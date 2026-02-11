package com.cope.addonparser.stubs;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

public final class StubSupport {
  private static final ThreadLocal<Set<Class<?>>> CONSTRUCTING =
      ThreadLocal.withInitial(HashSet::new);

  private StubSupport() {}

  public static Object defaultValue(Class<?> type) {
    if (type == null) return null;
    if (type == Object.class) return null;

    if (type.isPrimitive()) {
      if (type == boolean.class) return false;
      if (type == byte.class) return (byte) 0;
      if (type == short.class) return (short) 0;
      if (type == int.class) return 0;
      if (type == long.class) return 0L;
      if (type == float.class) return 0.0f;
      if (type == double.class) return 0.0d;
      if (type == char.class) return (char) 0;
      return null;
    }

    if (type.isArray()) {
      return Array.newInstance(type.getComponentType(), 0);
    }

    Set<Class<?>> constructing = CONSTRUCTING.get();
    if (constructing.contains(type)) return null;

    constructing.add(type);
    try {
      return createInstance(type);
    } finally {
      constructing.remove(type);
    }
  }

  private static Object createInstance(Class<?> type) {
    try {
      if (type.isInterface()) {
        InvocationHandler handler =
            new InvocationHandler() {
              @Override
              public Object invoke(Object proxy, Method method, Object[] args) {
                return defaultValue(method.getReturnType());
              }
            };

        return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler);
      }

      Constructor<?> ctor = type.getDeclaredConstructor();
      ctor.setAccessible(true);
      return ctor.newInstance();
    } catch (ReflectiveOperationException | SecurityException e) {
      // Type may not have a no-arg constructor or access is denied - return null default
      return null;
    }
  }
}
