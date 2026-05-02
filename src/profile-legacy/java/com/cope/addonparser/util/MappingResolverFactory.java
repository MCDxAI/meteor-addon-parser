package com.cope.addonparser.util;

import java.util.List;
import java.util.Map;

/** Legacy (1.21.x Yarn intermediary) profile bindings for ValueNormalizer. */
public final class MappingResolverFactory {
  private static final Map<String, List<String>> OWNER_HINTS =
      Map.of(
          "net.minecraft.class_1792", List.of("net.minecraft.class_1802"),
          "net.minecraft.class_2248", List.of("net.minecraft.class_2246"),
          "net.minecraft.class_3414", List.of("net.minecraft.class_3417"),
          "net.minecraft.class_1887", List.of("net.minecraft.class_1893"),
          "net.minecraft.class_5321", List.of("net.minecraft.class_1893"),
          "net.minecraft.class_1291", List.of("net.minecraft.class_1294"),
          "net.minecraft.class_6880", List.of("net.minecraft.class_1294"));

  private MappingResolverFactory() {}

  public static MappingResolver get() {
    return YarnMappingResolver.get();
  }

  public static Map<String, List<String>> ownerHints() {
    return OWNER_HINTS;
  }

  /** Per-simple-name registry-class fallbacks used when explicit hints are missing. */
  public static String fallbackOwnerForSimpleName(String simpleName) {
    return switch (simpleName) {
      case "class_1792" -> "net.minecraft.class_1802";
      case "class_2248" -> "net.minecraft.class_2246";
      default -> null;
    };
  }
}
