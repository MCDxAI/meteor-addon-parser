package com.cope.addonparser.scanner;

import com.cope.addonparser.model.JarScanResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintStream;
import java.nio.file.Path;

/**
 * Entry point for the isolated worker JVM process. Scans a single jar using {@link AddonScanner}
 * and writes the result as JSON to stdout.
 *
 * <p>Usage: {@code java ... com.cope.addonparser.scanner.ScanWorker <jar-path>}
 */
public final class ScanWorker {
  private ScanWorker() {}

  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Usage: ScanWorker <jar-path>");
      System.exit(2);
      return;
    }

    // Redirect stdout early so addon code can't pollute the JSON output
    PrintStream originalOut = System.out;
    System.setOut(System.err);

    Path jarPath = Path.of(args[0]);
    JarScanResult result;

    try (AddonScanner scanner = new AddonScanner()) {
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
