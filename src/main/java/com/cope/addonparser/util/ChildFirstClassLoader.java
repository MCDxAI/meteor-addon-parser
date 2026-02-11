package com.cope.addonparser.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChildFirstClassLoader extends URLClassLoader {
    private static final String MODERN_STAR_PREFIX = "org/meteordev/starscript/";
    private static final String LEGACY_STAR_PREFIX = "meteordevelopment/starscript/";

    private final ConcurrentMap<String, Class<?>> ownerClassCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<FieldKey, String> fieldDescriptorCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<MethodKey, MethodResolution> methodResolutionCache = new ConcurrentHashMap<>();
    private final ThreadLocal<Set<Object>> resolutionGuard = ThreadLocal.withInitial(HashSet::new);

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public ChildFirstClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> loaded = findLoadedClass(name);
            if (loaded == null) {
                if (isParentFirst(name)) {
                    loaded = getParent().loadClass(name);
                } else {
                    try {
                        loaded = findClass(name);
                    } catch (ClassNotFoundException ignored) {
                        loaded = getParent().loadClass(name);
                    }
                }
            }

            if (resolve) resolveClass(loaded);
            return loaded;
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String resourcePath = name.replace('.', '/') + ".class";
        URL resource = findResource(resourcePath);
        if (resource == null) throw new ClassNotFoundException(name);

        byte[] bytes;
        try (InputStream input = resource.openStream()) {
            bytes = input.readAllBytes();
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }

        byte[] transformed = applyCompatibilityRemaps(bytes);
        return defineClass(name, transformed, 0, transformed.length);
    }

    private byte[] applyCompatibilityRemaps(byte[] sourceBytes) {
        ClassReader reader = new ClassReader(sourceBytes);
        String currentClass = reader.getClassName();
        ClassWriter writer = new ClassWriter(reader, 0);
        Remapper remapper = new Remapper() {
            @Override
            public String map(String internalName) {
                if (internalName != null && internalName.startsWith(MODERN_STAR_PREFIX)) {
                    return LEGACY_STAR_PREFIX + internalName.substring(MODERN_STAR_PREFIX.length());
                }
                return internalName;
            }
        };

        ClassVisitor compatibilityVisitor = new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM9, methodVisitor) {
                    @Override
                    public void visitFieldInsn(int opcode, String owner, String fieldName, String fieldDescriptor) {
                        fieldDescriptor = resolveFieldDescriptor(currentClass, owner, fieldName, fieldDescriptor);
                        super.visitFieldInsn(opcode, owner, fieldName, fieldDescriptor);
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String methodName, String methodDescriptor, boolean isInterface) {
                        MethodResolution resolution = resolveMethod(currentClass, owner, methodName, methodDescriptor, opcode, isInterface);
                        super.visitMethodInsn(resolution.opcode(), resolution.owner(), methodName, resolution.descriptor(), resolution.isInterface());
                    }
                };
            }
        };

        ClassVisitor visitor = new ClassRemapper(compatibilityVisitor, remapper);
        reader.accept(visitor, 0);
        return writer.toByteArray();
    }

    private String resolveFieldDescriptor(String currentClass, String owner, String name, String descriptor) {
        FieldKey key = new FieldKey(owner, name, descriptor);
        String cached = fieldDescriptorCache.get(key);
        if (cached != null) return cached;

        Set<Object> guard = resolutionGuard.get();
        if (!guard.add(key)) return descriptor;

        try {
            Class<?> ownerClass = resolveOwnerClass(currentClass, owner);
            String resolved = descriptor;

            if (ownerClass != null && !hasExactField(ownerClass, name, descriptor)) {
                Set<String> candidates = collectFieldDescriptors(ownerClass, name);
                if (candidates.size() == 1) resolved = candidates.iterator().next();
            }

            fieldDescriptorCache.putIfAbsent(key, resolved);
            return resolved;
        } finally {
            guard.remove(key);
        }
    }

    private MethodResolution resolveMethod(String currentClass, String owner, String name, String descriptor, int opcode, boolean isInterface) {
        MethodKey key = new MethodKey(owner, name, descriptor, opcode, isInterface);
        MethodResolution cached = methodResolutionCache.get(key);
        if (cached != null) return cached;

        Set<Object> guard = resolutionGuard.get();
        if (!guard.add(key)) return new MethodResolution(owner, descriptor, opcode, isInterface);

        try {
            Class<?> ownerClass = resolveOwnerClass(currentClass, owner);
            if (ownerClass == null) {
                MethodResolution fallback = new MethodResolution(owner, descriptor, opcode, isInterface);
                methodResolutionCache.putIfAbsent(key, fallback);
                return fallback;
            }

            boolean runtimeInterface = ownerClass.isInterface();
            int adjustedOpcode = adjustInvokeOpcode(opcode, runtimeInterface);

            if (hasExactMethod(ownerClass, name, descriptor)) {
                MethodResolution exact = new MethodResolution(owner, descriptor, adjustedOpcode, runtimeInterface);
                methodResolutionCache.putIfAbsent(key, exact);
                return exact;
            }

            Set<String> candidates = collectMethodDescriptors(ownerClass, name, descriptor);
            if (candidates.size() == 1) {
                MethodResolution rewritten = new MethodResolution(owner, candidates.iterator().next(), adjustedOpcode, runtimeInterface);
                methodResolutionCache.putIfAbsent(key, rewritten);
                return rewritten;
            }

            MethodResolution passthrough = new MethodResolution(owner, descriptor, adjustedOpcode, runtimeInterface);
            methodResolutionCache.putIfAbsent(key, passthrough);
            return passthrough;
        } finally {
            guard.remove(key);
        }
    }

    private Class<?> resolveOwnerClass(String currentClass, String ownerInternal) {
        if (ownerInternal == null || ownerInternal.isEmpty() || ownerInternal.startsWith("[")) return null;
        if (ownerInternal.equals(currentClass)) return null;

        Class<?> cached = ownerClassCache.get(ownerInternal);
        if (cached != null) return cached;

        String ownerName = ownerInternal.replace('/', '.');
        Class<?> resolved = tryLoadClass(ownerName, getParent());
        if (resolved != null) ownerClassCache.put(ownerInternal, resolved);
        return resolved;
    }

    private static Class<?> tryLoadClass(String className, ClassLoader loader) {
        try {
            return Class.forName(className, false, loader);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Set<String> collectFieldDescriptors(Class<?> ownerClass, String fieldName) {
        LinkedHashSet<String> descriptors = new LinkedHashSet<>();
        Set<Class<?>> visited = new HashSet<>();
        ArrayDeque<Class<?>> queue = new ArrayDeque<>();
        queue.add(ownerClass);

        while (!queue.isEmpty()) {
            Class<?> current = queue.removeFirst();
            if (current == null || !visited.add(current)) continue;

            for (Field field : current.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) descriptors.add(Type.getDescriptor(field.getType()));
            }

            Class<?> superClass = current.getSuperclass();
            if (superClass != null) queue.addLast(superClass);
            queue.addAll(Arrays.asList(current.getInterfaces()));
        }

        return descriptors;
    }

    private static boolean hasExactField(Class<?> ownerClass, String fieldName, String descriptor) {
        return collectFieldDescriptors(ownerClass, fieldName).contains(descriptor);
    }

    private static Set<String> collectMethodDescriptors(Class<?> ownerClass, String methodName, String descriptor) {
        Type[] expectedArgs = Type.getArgumentTypes(descriptor);
        LinkedHashSet<String> matches = new LinkedHashSet<>();

        if ("<init>".equals(methodName)) {
            for (Constructor<?> constructor : ownerClass.getDeclaredConstructors()) {
                String ctorDescriptor = Type.getConstructorDescriptor(constructor);
                if (sameArgs(expectedArgs, Type.getArgumentTypes(ctorDescriptor))) {
                    matches.add(ctorDescriptor);
                }
            }
            return matches;
        }

        Set<Class<?>> visited = new HashSet<>();
        ArrayDeque<Class<?>> queue = new ArrayDeque<>();
        queue.add(ownerClass);

        while (!queue.isEmpty()) {
            Class<?> current = queue.removeFirst();
            if (current == null || !visited.add(current)) continue;

            for (Method method : current.getDeclaredMethods()) {
                if (!method.getName().equals(methodName)) continue;
                String methodDescriptor = Type.getMethodDescriptor(method);
                if (sameArgs(expectedArgs, Type.getArgumentTypes(methodDescriptor))) {
                    matches.add(methodDescriptor);
                }
            }

            Class<?> superClass = current.getSuperclass();
            if (superClass != null) queue.addLast(superClass);
            queue.addAll(Arrays.asList(current.getInterfaces()));
        }

        return matches;
    }

    private static boolean hasExactMethod(Class<?> ownerClass, String methodName, String descriptor) {
        Type[] expectedArgs = Type.getArgumentTypes(descriptor);
        Type expectedReturn = Type.getReturnType(descriptor);

        if ("<init>".equals(methodName)) {
            for (Constructor<?> constructor : ownerClass.getDeclaredConstructors()) {
                String ctorDescriptor = Type.getConstructorDescriptor(constructor);
                if (descriptor.equals(ctorDescriptor)) return true;
                if (sameArgs(expectedArgs, Type.getArgumentTypes(ctorDescriptor))) return true;
            }
            return false;
        }

        Set<Class<?>> visited = new HashSet<>();
        ArrayDeque<Class<?>> queue = new ArrayDeque<>();
        queue.add(ownerClass);

        while (!queue.isEmpty()) {
            Class<?> current = queue.removeFirst();
            if (current == null || !visited.add(current)) continue;

            for (Method method : current.getDeclaredMethods()) {
                if (!method.getName().equals(methodName)) continue;
                String methodDescriptor = Type.getMethodDescriptor(method);
                if (methodDescriptor.equals(descriptor)) return true;
                if (sameArgs(expectedArgs, Type.getArgumentTypes(methodDescriptor))
                    && expectedReturn.equals(Type.getReturnType(methodDescriptor))) {
                    return true;
                }
            }

            Class<?> superClass = current.getSuperclass();
            if (superClass != null) queue.addLast(superClass);
            queue.addAll(Arrays.asList(current.getInterfaces()));
        }

        return false;
    }

    private static boolean sameArgs(Type[] left, Type[] right) {
        if (left.length != right.length) return false;
        for (int i = 0; i < left.length; i++) {
            if (!left[i].equals(right[i])) return false;
        }
        return true;
    }

    private static int adjustInvokeOpcode(int opcode, boolean ownerIsInterface) {
        if (ownerIsInterface && opcode == Opcodes.INVOKEVIRTUAL) return Opcodes.INVOKEINTERFACE;
        if (!ownerIsInterface && opcode == Opcodes.INVOKEINTERFACE) return Opcodes.INVOKEVIRTUAL;
        return opcode;
    }

    private record FieldKey(String owner, String name, String descriptor) {
    }

    private record MethodKey(String owner, String name, String descriptor, int opcode, boolean isInterface) {
    }

    private record MethodResolution(String owner, String descriptor, int opcode, boolean isInterface) {
    }

    private boolean isParentFirst(String name) {
        return name.startsWith("java.")
            || name.startsWith("javax.")
            || name.startsWith("jdk.")
            || name.startsWith("sun.")
            || name.startsWith("org.meteordev.starscript.")
            || name.startsWith("meteordevelopment.starscript.")
            || name.startsWith("com.cope.addonparser.");
    }
}
