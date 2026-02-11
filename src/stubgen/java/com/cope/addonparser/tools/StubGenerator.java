package com.cope.addonparser.tools;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class StubGenerator {
    private static final Map<String, String> SUPER_OVERRIDES = Map.ofEntries(
        Map.entry("net/minecraft/class_2338", "net/minecraft/class_2382"),
        Map.entry("net/minecraft/class_2846", "net/minecraft/class_2596"),
        Map.entry("net/minecraft/class_1291", "net/minecraft/class_6880"),
        Map.entry("net/minecraft/class_1887", "net/minecraft/class_5321"),
        Map.entry("net/minecraft/class_2753", "net/minecraft/class_2754"),
        Map.entry("net/minecraft/class_1269$class_9859", "net/minecraft/class_1269"),
        Map.entry("meteordevelopment/meteorclient/gui/WindowScreen", "net/minecraft/class_437"),
        Map.entry("meteordevelopment/meteorclient/gui/tabs/TabScreen", "net/minecraft/class_437"),
        Map.entry("meteordevelopment/meteorclient/gui/tabs/WindowTabScreen", "meteordevelopment/meteorclient/gui/tabs/TabScreen")
    );

    private static final Set<String> INTERFACE_OVERRIDES = Set.of(
        "net/minecraft/class_4013",
        "net/minecraft/class_2596",
        "net/minecraft/class_6880",
        "net/minecraft/class_1269"
    );

    private record Args(Path inputDir, Path outputDir, Path manualClassList) {
    }

    private record ClassEntry(String internalName, byte[] bytes) {
    }

    private record FieldRefUse(String owner, String name, String desc, boolean isStatic) {
    }

    private record MethodRefUse(String owner, String name, String desc, boolean isStatic, boolean isInterfaceCall) {
    }

    private static final class ParsedClass {
        final String internalName;
        final Set<String> classRefs = new LinkedHashSet<>();
        final List<FieldRefUse> fieldUses = new ArrayList<>();
        final List<MethodRefUse> methodUses = new ArrayList<>();

        ParsedClass(String internalName) {
            this.internalName = internalName;
        }
    }

    private record MemberKey(String name, String desc) {
    }

    private static final class StubField {
        final String name;
        final String desc;
        boolean isStatic;

        StubField(String name, String desc, boolean isStatic) {
            this.name = name;
            this.desc = desc;
            this.isStatic = isStatic;
        }
    }

    private static final class StubMethod {
        final String name;
        final String desc;
        boolean isStatic;
        boolean isInterfaceCall;

        StubMethod(String name, String desc, boolean isStatic, boolean isInterfaceCall) {
            this.name = name;
            this.desc = desc;
            this.isStatic = isStatic;
            this.isInterfaceCall = isInterfaceCall;
        }
    }

    private static final class StubModel {
        final String internalName;
        boolean isInterface;
        final Map<MemberKey, StubField> fields = new TreeMap<>(Comparator.comparing(MemberKey::name).thenComparing(MemberKey::desc));
        final Map<MemberKey, StubMethod> methods = new TreeMap<>(Comparator.comparing(MemberKey::name).thenComparing(MemberKey::desc));
        final Set<String> ctors = new LinkedHashSet<>();

        StubModel(String internalName) {
            this.internalName = internalName;
        }
    }

    private static final class ManualClassConfig {
        final Set<String> exactClasses;
        final Set<String> prefixes;

        ManualClassConfig(Set<String> exactClasses, Set<String> prefixes) {
            this.exactClasses = exactClasses;
            this.prefixes = prefixes;
        }
    }

    private StubGenerator() {
    }

    public static void main(String[] argv) throws Exception {
        Args args = parseArgs(argv);
        if (args == null) {
            System.err.println("Usage: StubGenerator --input-dir <dir> --output-dir <dir> [--manual-class-list <file>]");
            System.exit(2);
            return;
        }

        ManualClassConfig manual = loadManualClasses(args.manualClassList);
        regenerateStubs(args.inputDir, args.outputDir, manual);
    }

    private static void regenerateStubs(Path inputDir, Path outputDir, ManualClassConfig manual) throws Exception {
        if (Files.exists(outputDir)) deleteRecursively(outputDir);
        Files.createDirectories(outputDir);

        List<ClassEntry> classEntries = new ArrayList<>();
        Set<String> definedInJars = new HashSet<>();

        try (var jarStream = Files.list(inputDir)) {
            List<Path> jars = jarStream
                .filter(path -> Files.isRegularFile(path) && path.getFileName().toString().toLowerCase().endsWith(".jar"))
                .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase()))
                .toList();

            for (Path jar : jars) readJarClasses(jar, classEntries, definedInJars);
        }

        Map<String, StubModel> models = new HashMap<>();
        for (ClassEntry classEntry : classEntries) {
            ParsedClass parsed = parseClass(classEntry.bytes);
            if (parsed == null) continue;

            for (String ref : parsed.classRefs) {
                if (shouldSkipClass(ref, definedInJars, manual.exactClasses, manual.prefixes)) continue;
                ensureModel(models, ref);
            }

            for (FieldRefUse use : parsed.fieldUses) {
                if (shouldSkipClass(use.owner, definedInJars, manual.exactClasses, manual.prefixes)) continue;
                StubModel model = ensureModel(models, use.owner);
                MemberKey key = new MemberKey(use.name, use.desc);
                StubField field = model.fields.get(key);
                if (field == null) model.fields.put(key, new StubField(use.name, use.desc, use.isStatic));
                else field.isStatic |= use.isStatic;
            }

            for (MethodRefUse use : parsed.methodUses) {
                if (shouldSkipClass(use.owner, definedInJars, manual.exactClasses, manual.prefixes)) continue;
                StubModel model = ensureModel(models, use.owner);

                if ("<init>".equals(use.name)) {
                    model.ctors.add(use.desc);
                    continue;
                }

                MemberKey key = new MemberKey(use.name, use.desc);
                StubMethod method = model.methods.get(key);
                if (method == null) {
                    model.methods.put(key, new StubMethod(use.name, use.desc, use.isStatic, use.isInterfaceCall));
                } else {
                    method.isStatic |= use.isStatic;
                    method.isInterfaceCall |= use.isInterfaceCall;
                }

                if (use.isInterfaceCall) model.isInterface = true;
            }
        }

        Map<String, StubModel> filtered = new TreeMap<>();
        for (Map.Entry<String, StubModel> entry : models.entrySet()) {
            String internal = entry.getKey();
            if (internal == null || internal.isEmpty()) continue;
            if (internal.startsWith("[") || !internal.contains("/")) continue;
            if (shouldSkipClass(internal, definedInJars, manual.exactClasses, manual.prefixes)) continue;
            filtered.put(internal, entry.getValue());
        }

        for (Map.Entry<String, StubModel> entry : filtered.entrySet()) {
            String source = renderStub(entry.getValue());
            Path target = outputDir.resolve(entry.getKey() + ".java");
            if (target.getParent() != null) Files.createDirectories(target.getParent());
            Files.writeString(target, source, StandardCharsets.UTF_8);
        }

        System.out.println("Generated " + filtered.size() + " stub classes into " + outputDir);
    }

    private static void readJarClasses(Path jarPath, List<ClassEntry> outClasses, Set<String> definedInJars) {
        try (ZipFile zip = new ZipFile(jarPath.toFile())) {
            zip.stream()
                .filter(entry -> !entry.isDirectory() && entry.getName().endsWith(".class"))
                .forEach(entry -> readZipClass(zip, entry, outClasses, definedInJars));
        } catch (Throwable ignored) {
        }
    }

    private static void readZipClass(ZipFile zip, ZipEntry entry, List<ClassEntry> outClasses, Set<String> definedInJars) {
        String internal = entry.getName().substring(0, entry.getName().length() - ".class".length());
        definedInJars.add(internal);
        try (InputStream in = zip.getInputStream(entry)) {
            byte[] bytes = in.readAllBytes();
            outClasses.add(new ClassEntry(internal, bytes));
        } catch (Throwable ignored) {
        }
    }

    private static ParsedClass parseClass(byte[] classBytes) {
        try {
            ClassReader reader = new ClassReader(classBytes);
            ParsedClass parsed = new ParsedClass(reader.getClassName());

            reader.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    if (superName != null) parsed.classRefs.add(superName);
                    if (interfaces != null) {
                        for (String iface : interfaces) {
                            if (iface != null) parsed.classRefs.add(iface);
                        }
                    }
                }

                @Override
                public org.objectweb.asm.FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                    collectFromTypeDescriptor(descriptor, parsed.classRefs);
                    return super.visitField(access, name, descriptor, signature, value);
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    collectFromMethodDescriptor(descriptor, parsed.classRefs);
                    if (exceptions != null) {
                        for (String ex : exceptions) {
                            if (ex != null) parsed.classRefs.add(ex);
                        }
                    }

                    return new MethodVisitor(Opcodes.ASM9) {
                        @Override
                        public void visitFieldInsn(int opcode, String owner, String fieldName, String fieldDescriptor) {
                            boolean isStatic = opcode == Opcodes.GETSTATIC || opcode == Opcodes.PUTSTATIC;
                            parsed.fieldUses.add(new FieldRefUse(owner, fieldName, fieldDescriptor, isStatic));
                            if (owner != null) parsed.classRefs.add(owner);
                            collectFromTypeDescriptor(fieldDescriptor, parsed.classRefs);
                        }

                        @Override
                        public void visitMethodInsn(int opcode, String owner, String methodName, String methodDescriptor, boolean isInterface) {
                            boolean isStatic = opcode == Opcodes.INVOKESTATIC;
                            parsed.methodUses.add(new MethodRefUse(owner, methodName, methodDescriptor, isStatic, isInterface));
                            if (owner != null) parsed.classRefs.add(owner);
                            collectFromMethodDescriptor(methodDescriptor, parsed.classRefs);
                        }

                        @Override
                        public void visitTypeInsn(int opcode, String type) {
                            if (type != null && !type.isBlank()) {
                                if (type.startsWith("[")) collectFromTypeDescriptor(type, parsed.classRefs);
                                else parsed.classRefs.add(type);
                            }
                        }

                        @Override
                        public void visitLdcInsn(Object value) {
                            if (value instanceof Type t) collectFromAsmType(t, parsed.classRefs);
                        }

                        @Override
                        public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
                            collectFromMethodDescriptor(descriptor, parsed.classRefs);
                            if (bootstrapMethodHandle != null) {
                                if (bootstrapMethodHandle.getOwner() != null) parsed.classRefs.add(bootstrapMethodHandle.getOwner());
                                collectFromMethodDescriptor(bootstrapMethodHandle.getDesc(), parsed.classRefs);
                            }
                            if (bootstrapMethodArguments != null) {
                                for (Object arg : bootstrapMethodArguments) {
                                    if (arg instanceof Type t) collectFromAsmType(t, parsed.classRefs);
                                    if (arg instanceof Handle h) {
                                        if (h.getOwner() != null) parsed.classRefs.add(h.getOwner());
                                        collectFromMethodDescriptor(h.getDesc(), parsed.classRefs);
                                    }
                                }
                            }
                        }

                        @Override
                        public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
                            collectFromTypeDescriptor(descriptor, parsed.classRefs);
                        }
                    };
                }
            }, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

            return parsed;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void collectFromMethodDescriptor(String descriptor, Set<String> out) {
        if (descriptor == null || descriptor.isBlank()) return;
        collectFromAsmType(Type.getMethodType(descriptor), out);
    }

    private static void collectFromTypeDescriptor(String descriptor, Set<String> out) {
        if (descriptor == null || descriptor.isBlank()) return;
        collectFromAsmType(Type.getType(descriptor), out);
    }

    private static void collectFromAsmType(Type type, Set<String> out) {
        if (type == null) return;
        switch (type.getSort()) {
            case Type.OBJECT -> out.add(type.getInternalName());
            case Type.ARRAY -> collectFromAsmType(type.getElementType(), out);
            case Type.METHOD -> {
                for (Type arg : type.getArgumentTypes()) collectFromAsmType(arg, out);
                collectFromAsmType(type.getReturnType(), out);
            }
            default -> {
            }
        }
    }

    private static StubModel ensureModel(Map<String, StubModel> models, String internalName) {
        StubModel model = models.get(internalName);
        if (model != null) return model;
        StubModel created = new StubModel(internalName);
        if (INTERFACE_OVERRIDES.contains(internalName)) created.isInterface = true;
        models.put(internalName, created);
        return created;
    }

    private static boolean shouldSkipClass(String internalName, Set<String> definedInJars, Set<String> manualClasses, Set<String> manualPrefixes) {
        if (internalName == null || internalName.isBlank() || internalName.startsWith("[")) return true;
        if (internalName.startsWith("java/")
            || internalName.startsWith("javax/")
            || internalName.startsWith("jdk/")
            || internalName.startsWith("sun/")
            || internalName.startsWith("com/sun/")
            || internalName.startsWith("kotlin/")
            || internalName.startsWith("scala/")) return true;
        if ("module-info".equals(internalName) || internalName.endsWith("/module-info")) return true;
        if (definedInJars.contains(internalName)) return true;
        if (manualClasses.contains(internalName)) return true;
        for (String prefix : manualPrefixes) {
            if (internalName.startsWith(prefix)) return true;
        }
        return false;
    }

    private static ManualClassConfig loadManualClasses(Path path) {
        if (path == null || !Files.exists(path)) return new ManualClassConfig(Set.of(), Set.of());

        Set<String> exact = new HashSet<>();
        Set<String> prefixes = new HashSet<>();
        try {
            for (String raw : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String normalized = line.replace('.', '/');
                if (normalized.endsWith("*")) prefixes.add(normalized.substring(0, normalized.length() - 1));
                else exact.add(normalized);
            }
        } catch (Throwable ignored) {
        }
        return new ManualClassConfig(exact, prefixes);
    }

    private static String renderStub(StubModel model) {
        String javaName = internalToJavaType(model.internalName);
        String pkg = "";
        String simple = javaName;
        int lastDot = javaName.lastIndexOf('.');
        if (lastDot > 0) {
            pkg = javaName.substring(0, lastDot);
            simple = javaName.substring(lastDot + 1);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("// AUTO-GENERATED FILE. DO NOT EDIT.\n");
        if (!pkg.isEmpty()) sb.append("package ").append(pkg).append(";\n\n");

        sb.append("@SuppressWarnings({\"all\", \"unchecked\"})\n");
        if (model.isInterface) {
            sb.append("public interface ").append(simple).append(" {\n");
        } else {
            String superName = SUPER_OVERRIDES.get(model.internalName);
            if (superName != null) {
                String superJava = internalToJavaType(superName);
                if (INTERFACE_OVERRIDES.contains(superName)) {
                    sb.append("public class ").append(simple).append(" implements ").append(superJava).append(" {\n");
                } else {
                    sb.append("public class ").append(simple).append(" extends ").append(superJava).append(" {\n");
                }
            } else {
                sb.append("public class ").append(simple).append(" {\n");
            }
        }

        Set<String> seenFieldNames = new HashSet<>();
        for (StubField field : model.fields.values()) {
            if (!seenFieldNames.add(field.name)) continue;
            String javaType = typeToJava(Type.getType(field.desc));
            String mods = field.isStatic ? "public static" : "public";
            if (model.isInterface && !field.isStatic) mods = "public static";

            if (field.isStatic || model.isInterface) {
                sb.append("    ").append(mods).append(" ").append(javaType).append(" ").append(field.name)
                    .append(" = ").append(fieldInitExpr(javaType)).append(";\n");
            } else {
                sb.append("    ").append(mods).append(" ").append(javaType).append(" ").append(field.name).append(";\n");
            }
        }

        if (!model.fields.isEmpty()) sb.append('\n');

        if (!model.isInterface) {
            Set<String> ctorDescs = new LinkedHashSet<>(model.ctors);
            ctorDescs.add("()V");
            List<String> sortedCtors = new ArrayList<>(ctorDescs);
            sortedCtors.sort(String::compareTo);
            for (String desc : sortedCtors) {
                Type methodType = Type.getMethodType(desc);
                String params = renderParams(methodType.getArgumentTypes());
                sb.append("    public ").append(simple).append("(").append(params).append(") {\n");
                sb.append("    }\n\n");
            }
        }

        Set<String> seenMethodSigs = new HashSet<>();
        for (StubMethod method : model.methods.values()) {
            if ("<init>".equals(method.name) || "<clinit>".equals(method.name)) continue;
            Type mt = Type.getMethodType(method.desc);
            Type[] args = mt.getArgumentTypes();
            String sigKey = method.name + "|" + renderArgSig(args);
            if (!seenMethodSigs.add(sigKey)) continue;

            if ("getClass".equals(method.name) && args.length == 0) continue;
            if (model.isInterface && ("toString".equals(method.name) || "hashCode".equals(method.name))) continue;
            if (model.isInterface && "equals".equals(method.name) && args.length == 1 && "java.lang.Object".equals(typeToJava(args[0])))
                continue;

            String ret = typeToJava(mt.getReturnType());
            String params = renderParams(args);

            if (model.isInterface) {
                if (method.isStatic) sb.append("    static ").append(ret).append(" ").append(method.name).append("(").append(params).append(") {\n");
                else sb.append("    default ").append(ret).append(" ").append(method.name).append("(").append(params).append(") {\n");
            } else {
                sb.append("    public");
                if (method.isStatic) sb.append(" static");
                sb.append(" ").append(ret).append(" ").append(method.name).append("(").append(params).append(") {\n");
            }

            String body = defaultReturnExpr(ret);
            if (!body.isEmpty()) sb.append("        ").append(body).append("\n");
            sb.append("    }\n\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    private static String renderArgSig(Type[] args) {
        StringBuilder sb = new StringBuilder();
        for (Type arg : args) {
            if (!sb.isEmpty()) sb.append(',');
            sb.append(typeToJava(arg));
        }
        return sb.toString();
    }

    private static String renderParams(Type[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(typeToJava(args[i])).append(" p").append(i);
        }
        return sb.toString();
    }

    private static String internalToJavaType(String internalName) {
        return internalName.replace('/', '.');
    }

    private static String typeToJava(Type type) {
        return switch (type.getSort()) {
            case Type.VOID -> "void";
            case Type.BOOLEAN -> "boolean";
            case Type.CHAR -> "char";
            case Type.BYTE -> "byte";
            case Type.SHORT -> "short";
            case Type.INT -> "int";
            case Type.FLOAT -> "float";
            case Type.LONG -> "long";
            case Type.DOUBLE -> "double";
            case Type.ARRAY -> typeToJava(type.getElementType()) + "[]";
            case Type.OBJECT -> internalToJavaType(type.getInternalName());
            default -> "java.lang.Object";
        };
    }

    private static String defaultReturnExpr(String javaType) {
        return switch (javaType) {
            case "void" -> "";
            case "boolean" -> "return false;";
            case "byte" -> "return (byte) 0;";
            case "char" -> "return (char) 0;";
            case "short" -> "return (short) 0;";
            case "int" -> "return 0;";
            case "long" -> "return 0L;";
            case "float" -> "return 0.0f;";
            case "double" -> "return 0.0d;";
            default ->
                "return (" + javaType + ") com.cope.addonparser.stubs.StubSupport.defaultValue(" + javaType + ".class);";
        };
    }

    private static String fieldInitExpr(String javaType) {
        return switch (javaType) {
            case "boolean" -> "false";
            case "byte" -> "(byte) 0";
            case "char" -> "(char) 0";
            case "short" -> "(short) 0";
            case "int" -> "0";
            case "long" -> "0L";
            case "float" -> "0.0f";
            case "double" -> "0.0d";
            default ->
                "(" + javaType + ") com.cope.addonparser.stubs.StubSupport.defaultValue(" + javaType + ".class)";
        };
    }

    private static Args parseArgs(String[] args) {
        Map<String, String> values = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (!arg.startsWith("--")) return null;
            if (i + 1 >= args.length) return null;
            values.put(arg, args[++i]);
        }

        String input = values.get("--input-dir");
        String output = values.get("--output-dir");
        String manual = values.get("--manual-class-list");
        if (input == null || output == null) return null;

        Path manualPath = manual == null ? null : Path.of(manual);
        return new Args(Path.of(input), Path.of(output), manualPath);
    }

    private static void deleteRecursively(Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.deleteIfExists(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
