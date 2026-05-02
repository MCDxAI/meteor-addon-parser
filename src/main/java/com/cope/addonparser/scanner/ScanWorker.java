package com.cope.addonparser.scanner;

import com.cope.addonparser.model.JarScanResult;
import com.cope.addonparser.profile.MappingProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintStream;
import java.nio.file.Path;

/**
 * Entry point for the isolated worker JVM process. Scans a single jar using {@link AddonScanner}
 * and writes the result as JSON to stdout.
 *
 * <p>Usage: {@code java ... com.cope.addonparser.scanner.ScanWorker <jar-path> [--profile
 * legacy|26x]}
 */
public final class ScanWorker {
  private ScanWorker() {}

  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Usage: ScanWorker <jar-path> [--profile legacy|26x]");
      System.exit(2);
      return;
    }

    Path jarPath = null;
    MappingProfile profile = MappingProfile.fromSystemProperty();
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if ("--profile".equals(arg) && i + 1 < args.length) {
        profile = MappingProfile.fromString(args[++i]);
      } else if (jarPath == null) {
        jarPath = Path.of(arg);
      }
    }

    if (jarPath == null) {
      System.err.println("Usage: ScanWorker <jar-path> [--profile legacy|26x]");
      System.exit(2);
      return;
    }

    System.setProperty(MappingProfile.SYSTEM_PROPERTY, profile.cliValue());

    // Redirect stdout early so addon code can't pollute the JSON output
    PrintStream originalOut = System.out;
    System.setOut(System.err);

    JarScanResult result;

    try (AddonScanner scanner = new AddonScanner(profile)) {
      result = scanner.scan(jarPath);
    } catch (Throwable t) {
      result = new JarScanResult();
      result.jarName = jarPath.getFileName().toString();
      result.jarPath = jarPath.toString();
      result.success = false;
      result.errors.add("Worker scan failure: " + t.getMessage());
    }

    try {
      ObjectMapper mapper = new ObjectMapper();
      String json = mapper.writeValueAsString(result);
      originalOut.println(json);
      originalOut.flush();
    } catch (Throwable t) {
      System.err.println("Failed to serialize scan result: " + t.getMessage());
      System.exit(1);
    }
  }
}
