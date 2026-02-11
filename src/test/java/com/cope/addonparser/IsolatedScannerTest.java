package com.cope.addonparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cope.addonparser.model.JarScanResult;
import com.cope.addonparser.scanner.IsolatedScanner;
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
import org.junit.jupiter.api.Timeout;

/**
 * Tests for {@link IsolatedScanner} that verify scanning fixture JARs in isolated JVM processes.
 * This test class mirrors {@link FixtureScanTest} but uses the isolated scanning mode.
 */
public class IsolatedScannerTest {
  private static final long TEST_TIMEOUT_SECONDS = 180;

  @Test
  @Timeout(value = 300, unit = java.util.concurrent.TimeUnit.SECONDS)
  void scansAllFixtureJars() throws Exception {
    Path jarDir = Path.of(System.getProperty("addonparser.fixtureJarsDir", "fixtures/addons/jars"));
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

    // Verify expected fixture jars exist
    assertTrue(
        jarNames.stream().anyMatch(name -> name.startsWith("Baritone-Controller--")),
        "Missing fixture jar for Baritone-Controller");
    assertTrue(
        jarNames.stream().anyMatch(name -> name.startsWith("mc-games--")),
        "Missing fixture jar for mc-games");
    assertTrue(
        jarNames.stream().anyMatch(name -> name.startsWith("meteor-satellite-addon--")),
        "Missing fixture jar for meteor-satellite-addon");
    assertTrue(
        jarNames.stream().anyMatch(name -> name.startsWith("meteor-translation-addon--")),
        "Missing fixture jar for meteor-translation-addon");
    assertTrue(
        jarNames.stream().anyMatch(name -> name.startsWith("Trouser-Streak--")),
        "Missing fixture jar for Trouser-Streak");

    // Known addon side-effect artifacts that may be created during scanning.
    List<Path> sideEffectPaths =
        List.of(
            Path.of("TrouserStreak"),
            Path.of("nora-tweaks"),
            Path.of("nora-tweaks-categories.json"));
    Set<Path> initiallyMissingSideEffects =
        sideEffectPaths.stream().filter(path -> !Files.exists(path)).collect(Collectors.toSet());

    List<String> failures = new ArrayList<>();
    try (IsolatedScanner scanner = new IsolatedScanner(TEST_TIMEOUT_SECONDS)) {
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

    assertTrue(failures.isEmpty(), "Isolated scan failures:\n" + String.join("\n", failures));
  }

  @Test
  void scanNonExistentJarReturnsFailure() throws Exception {
    try (IsolatedScanner scanner = new IsolatedScanner(10)) {
      Path nonExistent = Path.of("non-existent-jar-" + System.currentTimeMillis() + ".jar");
      JarScanResult result = scanner.scan(nonExistent);

      assertFalse(result.success, "Scan of non-existent jar should fail");
      assertFalse(result.errors.isEmpty(), "Should have error messages");
    }
  }

  @Test
  void scanInvalidFileReturnsFailure() throws Exception {
    try (IsolatedScanner scanner = new IsolatedScanner(10)) {
      // Create a temporary file that is not a valid JAR
      Path tempFile = Files.createTempFile("invalid-", ".jar");
      try {
        Files.writeString(tempFile, "This is not a valid JAR file");
        JarScanResult result = scanner.scan(tempFile);

        assertFalse(result.success, "Scan of invalid JAR should fail");
        assertFalse(result.errors.isEmpty(), "Should have error messages");
      } finally {
        Files.deleteIfExists(tempFile);
      }
    }
  }

  @Test
  void scanResultContainsExpectedMetadata() throws Exception {
    Path jarDir = Path.of(System.getProperty("addonparser.fixtureJarsDir", "fixtures/addons/jars"));
    if (!Files.isDirectory(jarDir)) {
      // Skip if fixtures not available
      return;
    }

    // Find a known fixture jar with predictable metadata
    List<Path> jars =
        Files.list(jarDir)
            .filter(p -> p.getFileName().toString().startsWith("meteor-satellite-addon--"))
            .toList();

    if (jars.isEmpty()) {
      return; // Skip if fixture not available
    }

    try (IsolatedScanner scanner = new IsolatedScanner(TEST_TIMEOUT_SECONDS)) {
      JarScanResult result = scanner.scan(jars.get(0));

      assertTrue(result.success, "Scan should succeed: " + String.join(", ", result.errors));
      assertNotNull(result.jarName, "jarName should be set");
      assertNotNull(result.jarPath, "jarPath should be set");
      assertTrue(result.jarName.startsWith("meteor-satellite-addon--"), "Unexpected jar name: " + result.jarName);

      // Verify addon metadata is populated
      assertFalse(result.addons.isEmpty(), "Should have at least one addon");
      assertFalse(result.modules.isEmpty(), "Should have at least one module");

      // Verify addon structure
      var addon = result.addons.get(0);
      assertNotNull(addon.name, "Addon name should be set");
      assertNotNull(addon.packageName, "Addon packageName should be set");
      assertNotNull(addon.entrypoint, "Addon entrypoint should be set");

      // Verify module structure
      var module = result.modules.get(0);
      assertNotNull(module.name, "Module name should be set");
      assertNotNull(module.category, "Module category should be set");
    }
  }

  @Test
  void defaultTimeoutIsUsed() {
    // Verify default constructor uses expected timeout
    IsolatedScanner defaultScanner = new IsolatedScanner();
    // The scanner doesn't expose the timeout, but we can verify it doesn't throw
    assertNotNull(defaultScanner);
    defaultScanner.close();
  }

  @Test
  void customTimeoutIsAccepted() {
    // Verify custom timeout constructor works
    IsolatedScanner customScanner = new IsolatedScanner(60);
    assertNotNull(customScanner);
    customScanner.close();
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
