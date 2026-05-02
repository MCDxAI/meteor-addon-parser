package com.cope.addonparser.profile;

import java.util.Locale;

public enum MappingProfile {
  LEGACY,
  MOJMAP_26X;

  public static final String SYSTEM_PROPERTY = "addonparser.profile";

  public static MappingProfile fromString(String value) {
    if (value == null || value.isBlank()) return MOJMAP_26X;
    return switch (value.trim().toLowerCase(Locale.ROOT)) {
      case "legacy", "1.21", "1.21.x", "yarn" -> LEGACY;
      case "26x", "26", "26.1", "mojmap", "mojmap_26x" -> MOJMAP_26X;
      default -> throw new IllegalArgumentException(
          "Unknown mapping profile: " + value + " (expected 'legacy' or '26x')");
    };
  }

  public static MappingProfile fromSystemProperty() {
    return fromString(System.getProperty(SYSTEM_PROPERTY));
  }

  public String cliValue() {
    return this == LEGACY ? "legacy" : "26x";
  }
}
