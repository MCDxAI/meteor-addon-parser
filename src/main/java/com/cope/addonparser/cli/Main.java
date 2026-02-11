package com.cope.addonparser.cli;

import com.cope.addonparser.model.JarScanResult;
import com.cope.addonparser.model.ScanSummary;
import com.cope.addonparser.scanner.AddonScanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        Args parsed = Args.parse(args);
        if (parsed == null) {
            printUsage();
            System.exit(2);
            return;
        }

        List<Path> jars = collectJars(parsed.input);
        if (jars.isEmpty()) {
            System.err.println("No jar files found in: " + parsed.input);
            System.exit(1);
            return;
        }

        Files.createDirectories(parsed.outputDir);

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        ScanSummary summary = new ScanSummary();
        summary.jarCount = jars.size();

        try (AddonScanner scanner = new AddonScanner()) {
            for (Path jar : jars) {
                JarScanResult result = scanner.scan(jar);

                String base = jar.getFileName().toString();
                Path outFile = parsed.outputDir.resolve(base + ".json");
                mapper.writeValue(outFile.toFile(), result);
                summary.outputFiles.add(outFile.getFileName().toString());

                if (result.success) {
                    summary.successCount++;
                    System.out.println("[OK] " + base + " modules=" + result.modules.size());
                } else {
                    summary.failureCount++;
                    summary.failedJars.add(base);
                    System.out.println("[FAIL] " + base + " errors=" + result.errors.size() + " modules=" + result.modules.size());
                }
            }
        }

        Path summaryFile = parsed.summaryFile != null
            ? parsed.summaryFile
            : parsed.outputDir.resolve("summary.json");
        Files.createDirectories(summaryFile.getParent());
        mapper.writeValue(summaryFile.toFile(), summary);

        System.out.println("Completed. jars=" + summary.jarCount + " ok=" + summary.successCount + " failed=" + summary.failureCount);
        System.out.println("Summary: " + summaryFile.toAbsolutePath());

        if (summary.failureCount > 0) {
            System.exit(1);
        }
    }

    private static List<Path> collectJars(Path input) throws Exception {
        List<Path> jars = new ArrayList<>();
        if (Files.isRegularFile(input) && input.toString().toLowerCase().endsWith(".jar")) {
            jars.add(input);
            return jars;
        }

        if (Files.isDirectory(input)) {
            try (var stream = Files.list(input)) {
                stream
                    .filter(path -> Files.isRegularFile(path) && path.toString().toLowerCase().endsWith(".jar"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase()))
                    .forEach(jars::add);
            }
        }

        return jars;
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar addon-parser.jar --input <jar-or-dir> [--output <dir>] [--summary <file>]");
    }

    private record Args(Path input, Path outputDir, Path summaryFile) {
        static Args parse(String[] args) {
            Path input = null;
            Path output = Paths.get("output");
            Path summary = null;

            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if ("--input".equals(arg) && i + 1 < args.length) {
                    input = Paths.get(args[++i]);
                } else if ("--output".equals(arg) && i + 1 < args.length) {
                    output = Paths.get(args[++i]);
                } else if ("--summary".equals(arg) && i + 1 < args.length) {
                    summary = Paths.get(args[++i]);
                } else {
                    return null;
                }
            }

            if (input == null) return null;
            return new Args(input, output, summary);
        }
    }
}
