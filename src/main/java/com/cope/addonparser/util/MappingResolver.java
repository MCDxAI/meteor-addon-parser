package com.cope.addonparser.util;

/**
 * Resolves obfuscated/intermediary names to human-readable names. Implementations are profile
 * specific: legacy (Yarn) maps {@code class_*} to mojang names; the 26x profile is identity
 * because Mojmap addons are already unobfuscated.
 */
public interface MappingResolver {
  /** Map a class name (dot-separated), or return null if no mapping exists. */
  String mapClass(String className);

  /** Map a static field reference {@code owner.name}, or return null. */
  String mapSymbol(String ownerClass, String fieldName);

  /** Map a single field name on the given owner, or return null. */
  String mapField(String ownerClass, String fieldName);
}
