package com.cope.addonparser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cope.addonparser.model.JarScanResult;
import com.cope.addonparser.scanner.AddonScanner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class FixtureScanTest {
  @Test
  void scansAllFixtureJars() throws Exception {
    Path jarDir =
        Path.of(System.getProperty("addonparser.fixtureJarsDir", FixtureLayout.defaultJarDir()));
    assertTrue(
        Files.isDirectory(jarDir), "Fixture jar directory missing: " + jarDir.toAbsolutePath());

    List<Path> jars =
        Files.list(jarDir)
            .filter(p -> Files.isRegularFile(p) && p.toString().toLowerCase().endsWith(".jar"))
            .sorted(Comparator.comparing(p -> p.getFileName().toString().toLowerCase()))
            .toList();

    assertFalse(jars.isEmpty(), "No fixture jars found in " + jarDir.toAbsolutePath());

    Set<String> jarNames =
        jars.stream().map(path -> path.getFileName().toString()).collect(Collectors.toSet());

    // Known addon side-effect artifacts that may be created during scanning.
    // The scanner intentionally does NOT delete workspace-level artifacts (AP-004).
    // We track them here to clean up after the test.
    List<Path> sideEffectPaths =
        List.of(
            Path.of("TrouserStreak"),
            Path.of("nora-tweaks"),
            Path.of("nora-tweaks-categories.json"));
    Set<Path> initiallyMissingSideEffects =
        sideEffectPaths.stream().filter(path -> !Files.exists(path)).collect(Collectors.toSet());

    Path runtimeTmpRoot =
        Path.of(
            System.getProperty(
                "addonparser.runtimeTmpDir", Path.of("tmp", "addon-parser-runtime").toString()));
    boolean tmpRootMissingAtStart = !Files.exists(runtimeTmpRoot);

    for (String prefix : FixtureLayout.expectedJarPrefixes()) {
      assertTrue(
          jarNames.stream().anyMatch(name -> name.startsWith(prefix)),
          "Missing fixture jar with prefix '"
              + prefix
              + "' for profile "
              + FixtureLayout.profile());
    }

    List<String> failures = new ArrayList<>();
    try (AddonScanner scanner = new AddonScanner()) {
      for (Path jar : jars) {
        JarScanResult result = scanner.scan(jar);
        if (!result.success) {
          failures.add(jar.getFileName() + " => " + String.join(" | ", result.errors));
        }
      }
    } finally {
      // Clean up any addon side-effect artifacts created during the test
      for (Path sideEffectPath : initiallyMissingSideEffects) {
        deleteRecursively(sideEffectPath);
      }
    }

    assertTrue(failures.isEmpty(), "Fixture scan failures:\n" + String.join("\n", failures));

    boolean keepTmp = Boolean.parseBoolean(System.getProperty("addonparser.keepTmp", "false"));
    if (!keepTmp && tmpRootMissingAtStart && Files.isDirectory(runtimeTmpRoot)) {
      try (var stream = Files.list(runtimeTmpRoot)) {
        assertTrue(
            stream.findAny().isEmpty(),
            "Runtime tmp root should be empty after scans: " + runtimeTmpRoot.toAbsolutePath());
      }
    }
  }

  private static void deleteRecursively(Path root) {
    if (root == null || !Files.exists(root)) return;
    try (Stream<Path> stream = Files.walk(root)) {
      List<Path> toDelete = stream.sorted(Comparator.reverseOrder()).toList();
      for (Path path : toDelete) {
        try {
          Files.deleteIfExists(path);
        } catch (IOException e) {
          // Best-effort cleanup in test
        }
      }
    } catch (IOException e) {
      // Walk failure during test cleanup - non-fatal
    }
  }
}
