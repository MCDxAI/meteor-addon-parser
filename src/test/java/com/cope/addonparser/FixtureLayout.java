package com.cope.addonparser;

import java.util.List;

/**
 * Resolves the fixture jar directory and the set of jars expected to exist for the active profile.
 */
final class FixtureLayout {
  private FixtureLayout() {}

  static String profile() {
    return System.getProperty("addonparser.profile", "26x").toLowerCase();
  }

  static String defaultJarDir() {
    return "fixtures/addons/jars/" + profile();
  }

  static List<String> expectedJarPrefixes() {
    if ("legacy".equals(profile())) {
      return List.of(
          "Baritone-Controller--",
          "MeteorPlus--",
          "Trouser-Streak--",
          "meteor-villager-roller--",
          "nerv-printer-addon--");
    }
    return List.of(
        "MeteorAdditions--",
        "Nora-Tweaks--",
        "meteor-addon-template--",
        "meteor-translation-addon--",
        "Seija-Printer--");
  }
}
