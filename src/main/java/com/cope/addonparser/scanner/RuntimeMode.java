package com.cope.addonparser.scanner;

import java.util.Locale;

/**
 * Controls how addon code is executed during scanning.
 *
 * <ul>
 *   <li>{@link #ISOLATED} - Each scan runs in a separate worker JVM process. The worker can be
 *       independently terminated on timeout or failure. Recommended for untrusted addon jars.
 *   <li>{@link #LEGACY} - Addon code runs in-process with the scanner. This is faster but provides
 *       no isolation. Only use for trusted jars or debugging.
 * </ul>
 */
public enum RuntimeMode {
  ISOLATED,
  LEGACY;

  public static RuntimeMode fromString(String value) {
    if (value == null || value.isBlank()) return LEGACY;
    return switch (value.trim().toLowerCase(Locale.ROOT)) {
      case "legacy", "inprocess", "in-process" -> LEGACY;
      default -> ISOLATED;
    };
  }
}
