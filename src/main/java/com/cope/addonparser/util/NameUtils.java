package com.cope.addonparser.util;

/** Shared utility for name-to-title conversion used by both module and setting code. */
public final class NameUtils {
  private NameUtils() {}

  /**
   * Converts a kebab-case, snake_case, or mixed identifier into a Title Case display string.
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>{@code "auto-jump"} → {@code "Auto Jump"}
   *   <li>{@code "render_mode"} → {@code "Render Mode"}
   *   <li>{@code "ElytraFly"} → {@code "ElytraFly"} (single word, kept as-is)
   *   <li>{@code null} → {@code ""}
   * </ul>
   */
  public static String nameToTitle(String value) {
    if (value == null || value.isEmpty()) return "";
    String[] parts = value.replace('-', ' ').replace('_', ' ').split("\\s+");
    StringBuilder out = new StringBuilder();
    for (String part : parts) {
      if (part.isEmpty()) continue;
      if (!out.isEmpty()) out.append(' ');
      out.append(Character.toUpperCase(part.charAt(0)));
      if (part.length() > 1) out.append(part.substring(1));
    }
    return out.toString();
  }
}
