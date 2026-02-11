package com.cope.addonparser;

import com.cope.addonparser.model.JarScanResult;
import com.cope.addonparser.scanner.AddonScanner;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FixtureScanTest {
    @Test
    void scansAllFixtureJars() throws Exception {
        Path jarDir = Path.of(System.getProperty("addonparser.fixtureJarsDir", "fixtures/addons/jars"));
        assertTrue(Files.isDirectory(jarDir), "Fixture jar directory missing: " + jarDir.toAbsolutePath());

        List<Path> jars = Files.list(jarDir)
            .filter(p -> Files.isRegularFile(p) && p.toString().toLowerCase().endsWith(".jar"))
            .sorted(Comparator.comparing(p -> p.getFileName().toString().toLowerCase()))
            .toList();

        assertFalse(jars.isEmpty(), "No fixture jars found in " + jarDir.toAbsolutePath());

        Set<String> jarNames = jars.stream()
            .map(path -> path.getFileName().toString())
            .collect(Collectors.toSet());

        List<Path> sideEffectPaths = List.of(
            Path.of("TrouserStreak"),
            Path.of("nora-tweaks"),
            Path.of("nora-tweaks-categories.json")
        );
        Set<Path> initiallyMissingSideEffects = sideEffectPaths.stream()
            .filter(path -> !Files.exists(path))
            .collect(Collectors.toSet());

        Path runtimeTmpRoot = Path.of(System.getProperty("addonparser.runtimeTmpDir", Path.of("tmp", "addon-parser-runtime").toString()));
        boolean tmpRootMissingAtStart = !Files.exists(runtimeTmpRoot);

        assertTrue(jarNames.stream().anyMatch(name -> name.startsWith("Baritone-Controller--")), "Missing fixture jar for Baritone-Controller");
        assertTrue(jarNames.stream().anyMatch(name -> name.startsWith("mc-games--")), "Missing fixture jar for mc-games");
        assertTrue(jarNames.stream().anyMatch(name -> name.startsWith("meteor-satellite-addon--")), "Missing fixture jar for meteor-satellite-addon");
        assertTrue(jarNames.stream().anyMatch(name -> name.startsWith("meteor-translation-addon--")), "Missing fixture jar for meteor-translation-addon");
        assertTrue(jarNames.stream().anyMatch(name -> name.startsWith("Trouser-Streak--")), "Missing fixture jar for Trouser-Streak");

        List<String> failures = new ArrayList<>();
        try (AddonScanner scanner = new AddonScanner()) {
            for (Path jar : jars) {
                JarScanResult result = scanner.scan(jar);
                if (!result.success) {
                    failures.add(jar.getFileName() + " => " + String.join(" | ", result.errors));
                }
            }
        }

        assertTrue(failures.isEmpty(), "Fixture scan failures:\n" + String.join("\n", failures));

        for (Path sideEffectPath : initiallyMissingSideEffects) {
            assertFalse(Files.exists(sideEffectPath), "Scanner leaked side-effect artifact: " + sideEffectPath.toAbsolutePath());
        }

        boolean keepTmp = Boolean.parseBoolean(System.getProperty("addonparser.keepTmp", "false"));
        if (!keepTmp && tmpRootMissingAtStart && Files.isDirectory(runtimeTmpRoot)) {
            try (var stream = Files.list(runtimeTmpRoot)) {
                assertTrue(stream.findAny().isEmpty(), "Runtime tmp root should be empty after scans: " + runtimeTmpRoot.toAbsolutePath());
            }
        }
    }
}
