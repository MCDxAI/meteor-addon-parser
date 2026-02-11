package com.cope.addonparser.scanner;

import com.cope.addonparser.model.JarScanResult;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Runs addon scans in an isolated worker JVM process. Each scan forks a new JVM that loads and
 * executes addon code, communicating results as JSON over stdout. The worker process can be
 * independently terminated on timeout or failure, preventing untrusted addon code from affecting
 * the parent process.
 */
public class IsolatedScanner implements AutoCloseable {
  private static final long DEFAULT_TIMEOUT_SECONDS = 120;
  private static final String WORKER_MAIN = ScanWorker.class.getName();

  private final long timeoutSeconds;

  public IsolatedScanner() {
    this(DEFAULT_TIMEOUT_SECONDS);
  }

  public IsolatedScanner(long timeoutSeconds) {
    this.timeoutSeconds = timeoutSeconds;
  }

  public JarScanResult scan(Path jarPath) {
    Path absoluteJar = jarPath.toAbsolutePath().normalize();

    JarScanResult result = new JarScanResult();
    result.jarName = absoluteJar.getFileName().toString();
    result.jarPath = absoluteJar.toString();

    try {
      List<String> command = buildWorkerCommand(absoluteJar);
      ProcessBuilder pb = new ProcessBuilder(command);
      pb.redirectErrorStream(false);

      Process process = pb.start();

      StringBuilder stdout = new StringBuilder();
      StringBuilder stderr = new StringBuilder();

      Thread stdoutReader =
          new Thread(
              () -> {
                try (BufferedReader reader =
                    new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                  String line;
                  while ((line = reader.readLine()) != null) {
                    stdout.append(line).append('\n');
                  }
                } catch (Exception e) {
                  // Stream closed - expected on timeout
                }
              },
              "isolated-scanner-stdout");

      Thread stderrReader =
          new Thread(
              () -> {
                try (BufferedReader reader =
                    new BufferedReader(
                        new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                  String line;
                  while ((line = reader.readLine()) != null) {
                    stderr.append(line).append('\n');
                  }
                } catch (Exception e) {
                  // Stream closed - expected on timeout
                }
              },
              "isolated-scanner-stderr");

      stdoutReader.start();
      stderrReader.start();

      boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
      if (!finished) {
        process.destroyForcibly();
        result.success = false;
        result.errors.add("Worker process timed out after " + timeoutSeconds + "s");
        return result;
      }

      stdoutReader.join(5000);
      stderrReader.join(5000);

      int exitCode = process.exitValue();
      String output = stdout.toString().trim();

      if (output.isEmpty()) {
        result.success = false;
        result.errors.add(
            "Worker produced no output (exit="
                + exitCode
                + ")"
                + (stderr.length() > 0 ? " stderr: " + stderr.toString().trim() : ""));
        return result;
      }

      ObjectMapper mapper =
          new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      return mapper.readValue(output, JarScanResult.class);

    } catch (Exception e) {
      result.success = false;
      result.errors.add("Worker launch failure: " + e.getMessage());
      return result;
    }
  }

  private static List<String> buildWorkerCommand(Path jarPath) {
    List<String> cmd = new ArrayList<>();
    String javaHome = System.getProperty("java.home");
    String javaBin = Path.of(javaHome, "bin", "java").toString();
    cmd.add(javaBin);

    cmd.add("-noverify");
    cmd.add("-cp");
    cmd.add(System.getProperty("java.class.path"));

    // Forward relevant system properties
    String[] forwardedProps = {
      "addonparser.runtimeTmpDir",
      "addonparser.keepTmp",
      "addonparser.yarnAutoDownload",
      "addonparser.yarnMappingsVersions",
      "addonparser.yarnMappingsJar",
      "addonparser.mappingsDir",
      "addonparser.addonsSourceDir"
    };
    for (String prop : forwardedProps) {
      String value = System.getProperty(prop);
      if (value != null) {
        cmd.add("-D" + prop + "=" + value);
      }
    }

    cmd.add(WORKER_MAIN);
    cmd.add(jarPath.toString());

    return cmd;
  }

  @Override
  public void close() {
    // No persistent state to clean up
  }
}
