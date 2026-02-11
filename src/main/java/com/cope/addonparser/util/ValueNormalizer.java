package com.cope.addonparser.util;

import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class ValueNormalizer {
    private static final Pattern DEFAULT_OBJECT_TOSTRING = Pattern.compile("^.+@[0-9a-fA-F]+$");
    private static final Map<Object, String> OBJECT_SYMBOL_CACHE = Collections.synchronizedMap(new IdentityHashMap<>());
    private static final YarnMappingResolver YARN = YarnMappingResolver.get();
    private static final Map<String, List<String>> OWNER_HINTS = Map.of(
        "net.minecraft.class_1792", List.of("net.minecraft.class_1802"),
        "net.minecraft.class_2248", List.of("net.minecraft.class_2246"),
        "net.minecraft.class_3414", List.of("net.minecraft.class_3417"),
        "net.minecraft.class_1887", List.of("net.minecraft.class_1893"),
        "net.minecraft.class_5321", List.of("net.minecraft.class_1893"),
        "net.minecraft.class_1291", List.of("net.minecraft.class_1294"),
        "net.minecraft.class_6880", List.of("net.minecraft.class_1294")
    );

    private ValueNormalizer() {
    }

    public static Object normalize(Object value) {
        if (value == null) return null;

        if (value instanceof String || value instanceof Number || value instanceof Boolean) {
            return value;
        }

        if (value instanceof Enum<?> e) {
            return e.name();
        }

        if (value instanceof SettingColor sc) {
            return colorMap(sc.r, sc.g, sc.b, sc.a, sc.rainbow);
        }

        if (value instanceof Color c) {
            return colorMap(c.r, c.g, c.b, c.a, false);
        }

        if (value instanceof Keybind kb) {
            return kb.toString();
        }

        if (value instanceof Collection<?> collection) {
            List<Object> out = new ArrayList<>(collection.size());
            for (Object item : collection) out.add(normalize(item));
            return out;
        }

        if (value instanceof Map<?, ?> map) {
            Map<String, Object> out = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                out.put(String.valueOf(entry.getKey()), normalize(entry.getValue()));
            }
            return out;
        }

        Class<?> cls = value.getClass();
        if (cls.isArray()) {
            int len = Array.getLength(value);
            List<Object> out = new ArrayList<>(len);
            for (int i = 0; i < len; i++) out.add(normalize(Array.get(value, i)));
            return out;
        }

        if (cls.getName().startsWith("net.minecraft.")) {
            return normalizeMinecraftObject(value);
        }

        String asString = String.valueOf(value);
        if (DEFAULT_OBJECT_TOSTRING.matcher(asString).matches()) {
            Object enumLike = normalizeEnumLikeObject(value);
            if (enumLike != null) return enumLike;
            return normalizeOpaqueObject(value);
        }

        return asString;
    }

    private static Object normalizeMinecraftObject(Object value) {
        Map<String, Object> out = new LinkedHashMap<>();
        String className = value.getClass().getName();
        out.put("class", className);
        String classNamed = YARN.mapClass(className);
        if (classNamed != null) out.put("classNamed", classNamed);

        String symbol = resolveSymbol(value);
        if (symbol != null) out.put("symbol", symbol);
        String symbolNamed = toNamedSymbol(symbol);
        if (symbolNamed != null) out.put("symbolNamed", symbolNamed);
        out.put("id", Integer.toHexString(System.identityHashCode(value)));

        try {
            Field name = value.getClass().getField("name");
            out.put("name", String.valueOf(name.get(value)));
        } catch (Throwable ignored) {
        }

        return out;
    }

    private static String toNamedSymbol(String symbol) {
        if (symbol == null) return null;
        int split = symbol.lastIndexOf('.');
        if (split <= 0 || split >= symbol.length() - 1) return null;

        String owner = symbol.substring(0, split);
        String field = symbol.substring(split + 1);

        String symbolNamed = YARN.mapSymbol(owner, field);
        if (symbolNamed != null) return symbolNamed;

        String ownerNamed = YARN.mapClass(owner);
        String fieldNamed = YARN.mapField(owner, field);
        if (ownerNamed == null || fieldNamed == null) return null;
        return ownerNamed + "." + fieldNamed;
    }

    private static String resolveSymbol(Object value) {
        String cached = OBJECT_SYMBOL_CACHE.get(value);
        if (cached != null) return cached;

        Class<?> valueClass = value.getClass();
        Set<String> candidates = new LinkedHashSet<>(OWNER_HINTS.getOrDefault(valueClass.getName(), List.of()));

        // Fallback to nearby registry-like owner classes when we don't have explicit hints.
        String simple = valueClass.getSimpleName();
        if ("class_1792".equals(simple)) candidates.add("net.minecraft.class_1802");
        if ("class_2248".equals(simple)) candidates.add("net.minecraft.class_2246");

        ClassLoader loader = valueClass.getClassLoader();
        for (String ownerName : candidates) {
            String symbol = lookupStaticSymbol(ownerName, valueClass, value, loader);
            if (symbol != null) {
                OBJECT_SYMBOL_CACHE.put(value, symbol);
                return symbol;
            }
        }

        String fallback = valueClass.getName() + "#" + Integer.toHexString(System.identityHashCode(value));
        OBJECT_SYMBOL_CACHE.put(value, fallback);
        return fallback;
    }

    private static String lookupStaticSymbol(String ownerName, Class<?> valueClass, Object value, ClassLoader loader) {
        try {
            Class<?> owner = Class.forName(ownerName, true, loader);
            for (Field field : owner.getDeclaredFields()) {
                int mods = field.getModifiers();
                if (!Modifier.isStatic(mods)) continue;
                if (!field.getType().isAssignableFrom(valueClass) && !valueClass.isAssignableFrom(field.getType())) continue;

                field.setAccessible(true);
                Object fieldValue = field.get(null);
                if (fieldValue == value) return ownerName + "." + field.getName();
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    private static Object normalizeEnumLikeObject(Object value) {
        Class<?> cls = value.getClass();
        String symbol = lookupStaticSymbol(cls.getName(), cls, value, cls.getClassLoader());
        if (symbol == null) return null;

        try {
            cls.getMethod("valueOf", String.class);
            int split = symbol.lastIndexOf('.');
            if (split > 0 && split < symbol.length() - 1) {
                return symbol.substring(split + 1);
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    private static Object normalizeOpaqueObject(Object value) {
        Map<String, Object> out = new LinkedHashMap<>();
        Class<?> cls = value.getClass();
        out.put("class", cls.getName());

        String symbol = lookupStaticSymbol(cls.getName(), cls, value, cls.getClassLoader());
        if (symbol != null) out.put("symbol", symbol);
        out.put("id", Integer.toHexString(System.identityHashCode(value)));
        return out;
    }

    private static Map<String, Object> colorMap(int r, int g, int b, int a, boolean rainbow) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("r", r);
        out.put("g", g);
        out.put("b", b);
        out.put("a", a);
        out.put("rainbow", rainbow);
        return out;
    }
}
