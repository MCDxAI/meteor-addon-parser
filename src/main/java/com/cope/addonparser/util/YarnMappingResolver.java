package com.cope.addonparser.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class YarnMappingResolver {
  private static final String MAPPINGS_URL_TEMPLATE =
      "https://maven.fabricmc.net/net/fabricmc/yarn/%s/yarn-%s-v2.jar";
  private static final String DEFAULT_MAPPINGS_DIR = "mappings";
  private static final String DEFAULT_ADDONS_SOURCE_DIR = "ai_reference/addons";
  private static final String DEFAULT_FALLBACK_VERSION = "1.21.11+build.4";
  private static final String TINY_ENTRY_PATH = "mappings/mappings.tiny";
  private static final Pattern VERSION_PATTERN =
      Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)\\+build\\.(\\d+)$");
  private static final Pattern JAR_VERSION_PATTERN =
      Pattern.compile("^yarn-(\\d+)\\.(\\d+)\\.(\\d+)\\+build\\.(\\d+)-v2\\.jar$");
  private static final Pattern[] VERSION_DISCOVERY_PATTERNS =
      new Pattern[] {
        Pattern.compile(
            "yarn[_-]mappings\\s*=\\s*['\\\"]?([0-9]+\\.[0-9]+\\.[0-9]+\\+build\\.[0-9]+)['\\\"]?")
      };

  private static final Set<String> VERSION_HINT_FILES =
      Set.of(
          "gradle.properties",
          "libs.versions.toml",
          "build.gradle",
          "build.gradle.kts",
          "stonecutter.gradle.kts");

  private static final YarnMappingResolver INSTANCE = new YarnMappingResolver();

  private final List<MappingIndex> indices = new ArrayList<>();
  private volatile boolean loaded = false;

  private YarnMappingResolver() {
    tryLoadMappings();
  }

  public static YarnMappingResolver get() {
    return INSTANCE;
  }

  public String mapClass(String classNameDot) {
    if (!loaded || classNameDot == null || classNameDot.isEmpty()) return null;
    String internal = classNameDot.replace('.', '/');
    for (MappingIndex index : indices) {
      String mapped = index.classMap().get(internal);
      if (mapped != null) return mapped.replace('/', '.');
    }
    return null;
  }

  public String mapField(String ownerClassDot, String fieldName) {
    if (!loaded || ownerClassDot == null || fieldName == null) return null;
    String ownerInternal = ownerClassDot.replace('.', '/');
    for (MappingIndex index : indices) {
      Map<String, String> ownerFields = index.fieldMap().get(ownerInternal);
      if (ownerFields == null) continue;
      String named = ownerFields.get(fieldName);
      if (named != null) return named;
    }
    return null;
  }

  public String mapSymbol(String ownerClassDot, String fieldName) {
    if (!loaded || ownerClassDot == null || fieldName == null) return null;
    String ownerInternal = ownerClassDot.replace('.', '/');
    for (MappingIndex index : indices) {
      Map<String, String> ownerFields = index.fieldMap().get(ownerInternal);
      if (ownerFields == null) continue;

      String namedField = ownerFields.get(fieldName);
      if (namedField == null) continue;

      String namedOwner = index.classMap().get(ownerInternal);
      String ownerOut = namedOwner == null ? ownerClassDot : namedOwner.replace('/', '.');
      return ownerOut + "." + namedField;
    }
    return null;
  }

  public boolean isLoaded() {
    return loaded;
  }

  private void tryLoadMappings() {
    maybeBootstrapMappings();
    List<Path> jarPaths = resolveMappingsJarPaths();
    for (Path jarPath : jarPaths) {
      MappingIndex index = loadIndex(jarPath);
      if (index != null) indices.add(index);
    }
    loaded = !indices.isEmpty();
  }

  private static void maybeBootstrapMappings() {
    String autoDownloadProp = System.getProperty("addonparser.yarnAutoDownload");
    if (autoDownloadProp != null && "false".equalsIgnoreCase(autoDownloadProp.trim())) return;

    Path mappingsDir = resolveMappingsDir();
    try {
      Files.createDirectories(mappingsDir);
    } catch (IOException e) {
      // Cannot create mappings directory - skip auto-download
      return;
    }

    Set<String> versions = discoverRequestedVersions();
    if (versions.isEmpty()) versions.add(DEFAULT_FALLBACK_VERSION);

    for (String version : versions) {
      if (!VERSION_PATTERN.matcher(version).matches()) continue;
      Path target = mappingsDir.resolve("yarn-" + version + "-v2.jar");
      if (Files.isRegularFile(target)) {
        try {
          if (Files.size(target) > 0) continue;
        } catch (IOException e) {
          // Cannot read file size - re-download to be safe
        }
      }
      downloadMappingJar(version, target);
    }
  }

  private static Set<String> discoverRequestedVersions() {
    LinkedHashSet<String> versions = new LinkedHashSet<>();

    String explicitVersions = System.getProperty("addonparser.yarnMappingsVersions");
    if (explicitVersions != null && !explicitVersions.isBlank()) {
      String[] parts = explicitVersions.split("[,;" + Pattern.quote(File.pathSeparator) + "]");
      for (String part : parts) {
        if (part == null) continue;
        String version = part.trim();
        if (VERSION_PATTERN.matcher(version).matches()) versions.add(version);
      }
    }

    Path addonsDir = resolveAddonsSourceDir();
    if (!Files.isDirectory(addonsDir)) return versions;

    try (Stream<Path> stream = Files.walk(addonsDir, 8)) {
      stream
          .filter(Files::isRegularFile)
          .filter(YarnMappingResolver::isVersionHintFile)
          .forEach(path -> versions.addAll(extractVersions(path)));
    } catch (IOException e) {
      // Addons source directory not walkable - return whatever versions we found
    }

    return versions;
  }

  private static boolean isVersionHintFile(Path path) {
    String file = path.getFileName().toString().toLowerCase();
    return VERSION_HINT_FILES.contains(file);
  }

  private static Set<String> extractVersions(Path file) {
    LinkedHashSet<String> versions = new LinkedHashSet<>();
    try {
      String text = Files.readString(file, StandardCharsets.UTF_8);
      for (Pattern pattern : VERSION_DISCOVERY_PATTERNS) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
          String version = matcher.group(1);
          if (version != null && VERSION_PATTERN.matcher(version).matches()) {
            versions.add(version);
          }
        }
      }
    } catch (IOException e) {
      // File not readable - skip version extraction
    }
    return versions;
  }

  private static void downloadMappingJar(String version, Path outPath) {
    String url = MAPPINGS_URL_TEMPLATE.formatted(version, version);
    HttpClient client =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    HttpRequest request =
        HttpRequest.newBuilder(URI.create(url))
            .timeout(Duration.ofSeconds(60))
            .header("User-Agent", "addon-parser/0.1")
            .GET()
            .build();

    try {
      HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
      int status = response.statusCode();
      if (status < 200 || status >= 300) return;

      byte[] bytes = response.body();
      if (bytes == null || bytes.length == 0) return;

      Files.createDirectories(outPath.getParent());
      Path tmp = outPath.resolveSibling(outPath.getFileName().toString() + ".tmp");
      Files.write(tmp, bytes);
      Files.move(tmp, outPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    } catch (IOException | InterruptedException e) {
      // Download failure is non-fatal - mappings will be unavailable for this version
    }
  }

  private static List<Path> resolveMappingsJarPaths() {
    List<Path> paths = new ArrayList<>();

    String explicit = System.getProperty("addonparser.yarnMappingsJar");
    if (explicit != null && !explicit.isBlank()) {
      String[] parts = explicit.split("[,;" + Pattern.quote(File.pathSeparator) + "]");
      for (String part : parts) {
        if (part == null || part.isBlank()) continue;
        Path path = Paths.get(part.trim());
        if (Files.isRegularFile(path)) paths.add(path);
      }
      if (!paths.isEmpty()) return dedupeAndSort(paths);
    }

    Path mappingsDir = resolveMappingsDir();
    if (Files.isDirectory(mappingsDir)) {
      try (Stream<Path> stream = Files.list(mappingsDir)) {
        stream
            .filter(path -> Files.isRegularFile(path))
            .filter(
                path -> {
                  String name = path.getFileName().toString();
                  return name.startsWith("yarn-") && name.endsWith("-v2.jar");
                })
            .forEach(paths::add);
      } catch (IOException e) {
        // Mappings directory listing failed - return empty paths
      }
    }

    return dedupeAndSort(paths);
  }

  private static Path resolveMappingsDir() {
    String prop = System.getProperty("addonparser.mappingsDir");
    if (prop != null && !prop.isBlank()) return Paths.get(prop);
    return Paths.get(DEFAULT_MAPPINGS_DIR);
  }

  private static Path resolveAddonsSourceDir() {
    String prop = System.getProperty("addonparser.addonsSourceDir");
    if (prop != null && !prop.isBlank()) return Paths.get(prop);
    return Paths.get(DEFAULT_ADDONS_SOURCE_DIR);
  }

  private static List<Path> dedupeAndSort(List<Path> input) {
    Map<String, Path> deduped = new HashMap<>();
    for (Path path : input) deduped.put(path.toAbsolutePath().normalize().toString(), path);

    List<Path> paths = new ArrayList<>(deduped.values());
    paths.sort(Comparator.comparing(YarnMappingResolver::versionRank).reversed());
    return paths;
  }

  private static VersionRank versionRank(Path path) {
    String name = path.getFileName().toString();
    Matcher matcher = JAR_VERSION_PATTERN.matcher(name);
    if (!matcher.matches()) return new VersionRank(0, 0, 0, 0);

    try {
      int major = Integer.parseInt(matcher.group(1));
      int minor = Integer.parseInt(matcher.group(2));
      int patch = Integer.parseInt(matcher.group(3));
      int build = Integer.parseInt(matcher.group(4));
      return new VersionRank(major, minor, patch, build);
    } catch (NumberFormatException e) {
      // Malformed version number in jar name - treat as lowest rank
      return new VersionRank(0, 0, 0, 0);
    }
  }

  private static MappingIndex loadIndex(Path jarPath) {
    try (ZipFile zip = new ZipFile(jarPath.toFile())) {
      ZipEntry entry = zip.getEntry(TINY_ENTRY_PATH);
      if (entry == null) return null;

      try (InputStream in = zip.getInputStream(entry);
          BufferedReader reader =
              new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
        return parseTiny(reader, jarPath.getFileName().toString());
      }
    } catch (IOException e) {
      // Jar file not readable or malformed - skip this mapping source
      return null;
    }
  }

  private static MappingIndex parseTiny(BufferedReader reader, String source) throws IOException {
    Map<String, String> classMap = new HashMap<>();
    Map<String, Map<String, String>> fieldMap = new HashMap<>();
    String line;
    String currentIntermediaryClass = null;

    while ((line = reader.readLine()) != null) {
      if (line.isEmpty()) continue;
      if (line.startsWith("tiny\t")) continue;

      if (line.startsWith("c\t")) {
        String[] parts = line.split("\t");
        if (parts.length < 3) {
          currentIntermediaryClass = null;
          continue;
        }

        String intermediary = parts[1];
        String named = parts[parts.length - 1];
        currentIntermediaryClass = intermediary;
        classMap.put(intermediary, named);
        continue;
      }

      if (line.startsWith("\tf\t")) {
        if (currentIntermediaryClass == null) continue;
        String[] parts = line.split("\t");
        if (parts.length < 5) continue;

        String intermediaryField = parts[3];
        String namedField = parts[parts.length - 1];
        if (intermediaryField.isEmpty() || namedField.isEmpty()) continue;

        fieldMap
            .computeIfAbsent(currentIntermediaryClass, ignored -> new HashMap<>())
            .putIfAbsent(intermediaryField, namedField);
      }
    }

    return new MappingIndex(source, classMap, fieldMap);
  }

  private record MappingIndex(
      String source, Map<String, String> classMap, Map<String, Map<String, String>> fieldMap) {}

  private record VersionRank(int major, int minor, int patch, int build)
      implements Comparable<VersionRank> {
    @Override
    public int compareTo(VersionRank other) {
      if (other == null) return 1;
      int c = Integer.compare(this.major, other.major);
      if (c != 0) return c;
      c = Integer.compare(this.minor, other.minor);
      if (c != 0) return c;
      c = Integer.compare(this.patch, other.patch);
      if (c != 0) return c;
      return Integer.compare(this.build, other.build);
    }
  }
}
