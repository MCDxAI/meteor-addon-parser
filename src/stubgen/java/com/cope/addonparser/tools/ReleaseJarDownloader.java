package com.cope.addonparser.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.IOException;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class ReleaseJarDownloader {
  private static final String GITHUB_API = "https://api.github.com";
  private static final String USER_AGENT = "addon-parser-release-fetcher/1.0";
  private static final Path DEFAULT_ADDONS_DIR = Paths.get("ai_reference", "addons");
  private static final Path DEFAULT_JARS_DIR = Paths.get("fixtures", "addons", "jars");
  private static final Set<String> FAILURE_STATUSES =
      Set.of(
          "not_a_git_repo",
          "missing_origin",
          "non_github_or_unparsed_remote",
          "no_releases_found",
          "release_found_no_jar_assets",
          "failed_download");
  private static final List<String> SUMMARY_FIELDS =
      List.of(
          "addon_folder",
          "repo",
          "release_tag",
          "release_name",
          "release_url",
          "published_at",
          "releases_url",
          "asset_name",
          "asset_url",
          "downloaded_to",
          "status",
          "note");
  private static final List<Pattern> GITHUB_SLUG_PATTERNS =
      List.of(
          Pattern.compile("github\\.com[:/](?<slug>[^/]+/[^/.]+?)(?:\\.git)?$"),
          Pattern.compile("^https?://github\\.com/(?<slug>[^/]+/[^/.]+?)(?:\\.git)?/?$"),
          Pattern.compile("^git@github\\.com:(?<slug>[^/]+/[^/.]+?)(?:\\.git)?$"));
  private static final Gson GSON =
      new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

  private record Args(Path addonsDir, Path jarsDir, boolean failOnError) {}

  private record ApiJsonResult(JsonElement body, String error, int statusCode) {}

  private record DownloadResult(boolean ok, String error) {}

  private record ReleaseLookupResult(JsonObject release, String source, String error) {}

  private record SummaryEntry(
      String addonFolder,
      String repo,
      String releaseTag,
      String releaseName,
      String releaseUrl,
      String publishedAt,
      String releasesUrl,
      String assetName,
      String assetUrl,
      String downloadedTo,
      String status,
      String note) {
    Map<String, String> asMap() {
      LinkedHashMap<String, String> map = new LinkedHashMap<>();
      map.put("addon_folder", addonFolder);
      map.put("repo", repo);
      map.put("release_tag", releaseTag);
      map.put("release_name", releaseName);
      map.put("release_url", releaseUrl);
      map.put("published_at", publishedAt);
      map.put("releases_url", releasesUrl);
      map.put("asset_name", assetName);
      map.put("asset_url", assetUrl);
      map.put("downloaded_to", downloadedTo);
      map.put("status", status);
      map.put("note", note);
      return map;
    }
  }

  private ReleaseJarDownloader() {}

  public static void main(String[] argv) throws Exception {
    Args args = parseArgs(argv);
    if (args == null) {
      printUsage();
      System.exit(2);
      return;
    }

    System.exit(run(args));
  }

  private static void printUsage() {
    System.err.println(
        "Usage: ReleaseJarDownloader [--addons-dir <dir>] [--jars-dir <dir>] [--fail-on-error]");
  }

  private static Args parseArgs(String[] argv) {
    Path addonsDir = DEFAULT_ADDONS_DIR;
    Path jarsDir = DEFAULT_JARS_DIR;
    boolean failOnError = false;

    for (int i = 0; i < argv.length; i++) {
      String arg = argv[i];
      switch (arg) {
        case "--addons-dir":
          if (i + 1 >= argv.length) return null;
          addonsDir = Paths.get(argv[++i]);
          break;
        case "--jars-dir":
          if (i + 1 >= argv.length) return null;
          jarsDir = Paths.get(argv[++i]);
          break;
        case "--fail-on-error":
          failOnError = true;
          break;
        default:
          return null;
      }
    }

    return new Args(
        addonsDir.toAbsolutePath().normalize(), jarsDir.toAbsolutePath().normalize(), failOnError);
  }

  private static int run(Args args) throws IOException {
    if (!Files.isDirectory(args.addonsDir())) {
      System.err.println("addons directory not found: " + args.addonsDir());
      return 2;
    }
    Files.createDirectories(args.jarsDir());

    String token = resolveToken();
    HttpClient client =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    List<SummaryEntry> entries = new ArrayList<>();
    List<Path> repoDirs = listAddonDirs(args.addonsDir());
    for (Path addonDir : repoDirs) {
      String addonFolder = addonDir.getFileName().toString();
      Path gitDir = addonDir.resolve(".git");
      if (!Files.exists(gitDir)) {
        entries.add(entry(addonFolder, "", "", "", "", "", "", "", "", "", "not_a_git_repo", ""));
        continue;
      }

      String origin = getOriginUrl(addonDir);
      if (origin == null || origin.isBlank()) {
        entries.add(entry(addonFolder, "", "", "", "", "", "", "", "", "", "missing_origin", ""));
        continue;
      }

      String slug = parseGitHubSlug(origin);
      if (slug == null) {
        entries.add(
            entry(
                addonFolder,
                origin,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "non_github_or_unparsed_remote",
                ""));
        continue;
      }

      String releasesUrl = "https://github.com/" + slug + "/releases";
      ReleaseLookupResult releaseLookup = getLatestRelease(client, slug, token);
      JsonObject release = releaseLookup.release();
      if (release == null) {
        entries.add(
            entry(
                addonFolder,
                slug,
                "",
                "",
                "",
                "",
                releasesUrl,
                "",
                "",
                "",
                "no_releases_found",
                releaseLookup.error()));
        continue;
      }

      String releaseTag = stringValue(release, "tag_name");
      String releaseName = stringValue(release, "name");
      String releaseUrl = stringValue(release, "html_url");
      String publishedAt = stringValue(release, "published_at");

      JsonArray assets =
          release.has("assets") && release.get("assets").isJsonArray()
              ? release.getAsJsonArray("assets")
              : new JsonArray();
      List<JsonObject> jarAssets = new ArrayList<>();
      for (JsonElement assetElement : assets) {
        if (!assetElement.isJsonObject()) continue;
        JsonObject asset = assetElement.getAsJsonObject();
        String assetName = stringValue(asset, "name");
        if (assetName.toLowerCase().endsWith(".jar")) {
          jarAssets.add(asset);
        }
      }

      if (jarAssets.isEmpty()) {
        entries.add(
            entry(
                addonFolder,
                slug,
                releaseTag,
                releaseName,
                releaseUrl,
                publishedAt,
                releasesUrl,
                "",
                "",
                "",
                "release_found_no_jar_assets",
                "source=" + releaseLookup.source()));
        continue;
      }

      for (JsonObject asset : jarAssets) {
        String assetName = stringValue(asset, "name").trim();
        String assetUrl = stringValue(asset, "browser_download_url").trim();
        if (assetName.isEmpty() || assetUrl.isEmpty()) {
          entries.add(
              entry(
                  addonFolder,
                  slug,
                  releaseTag,
                  releaseName,
                  releaseUrl,
                  publishedAt,
                  releasesUrl,
                  assetName,
                  assetUrl,
                  "",
                  "failed_download",
                  "asset missing name/url"));
          continue;
        }

        String outName = sanitizeFileName(addonFolder + "--" + assetName);
        Path outPath = args.jarsDir().resolve(outName);
        DownloadResult download = downloadFile(client, assetUrl, outPath, token);
        String note =
            download.ok()
                ? "source=" + releaseLookup.source()
                : "source=" + releaseLookup.source() + ";error=" + download.error();
        entries.add(
            entry(
                addonFolder,
                slug,
                releaseTag,
                releaseName,
                releaseUrl,
                publishedAt,
                releasesUrl,
                assetName,
                assetUrl,
                download.ok() ? outPath.toString() : "",
                download.ok() ? "downloaded" : "failed_download",
                note));
      }
    }

    SummaryPaths summaryPaths = writeSummaryFiles(entries, args);
    List<String> summaryLines = buildSummaryLines(entries, args, summaryPaths);
    Files.writeString(
        summaryPaths.summaryTxt(), String.join("\n", summaryLines) + "\n", StandardCharsets.UTF_8);
    System.out.println(String.join("\n", summaryLines));

    boolean hasFailures =
        entries.stream().anyMatch(entry -> FAILURE_STATUSES.contains(entry.status()));
    if (args.failOnError() && hasFailures) return 1;
    return 0;
  }

  private static List<Path> listAddonDirs(Path addonsDir) throws IOException {
    try (Stream<Path> stream = Files.list(addonsDir)) {
      return stream
          .filter(Files::isDirectory)
          .filter(path -> !"jars".equalsIgnoreCase(path.getFileName().toString()))
          .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase()))
          .toList();
    }
  }

  private static String resolveToken() {
    String githubToken = System.getenv("GITHUB_TOKEN");
    if (githubToken != null && !githubToken.isBlank()) return githubToken;
    String ghToken = System.getenv("GH_TOKEN");
    if (ghToken != null && !ghToken.isBlank()) return ghToken;
    return null;
  }

  private static ApiJsonResult apiGetJson(HttpClient client, String path, String token) {
    HttpRequest.Builder requestBuilder =
        HttpRequest.newBuilder(URI.create(GITHUB_API + path))
            .timeout(Duration.ofSeconds(60))
            .header("Accept", "application/vnd.github+json")
            .header("User-Agent", USER_AGENT)
            .GET();
    if (token != null) requestBuilder.header("Authorization", "Bearer " + token);

    try {
      HttpResponse<String> response =
          client.send(
              requestBuilder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      int status = response.statusCode();
      String body = Optional.ofNullable(response.body()).orElse("");
      if (status >= 200 && status < 300) {
        JsonElement parsed = JsonParser.parseString(body);
        return new ApiJsonResult(parsed, null, status);
      }
      return new ApiJsonResult(null, "HTTP " + status + ": " + body, status);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return new ApiJsonResult(null, "interrupted", -1);
    } catch (Exception e) {
      return new ApiJsonResult(null, e.getMessage(), -1);
    }
  }

  private static DownloadResult downloadFile(
      HttpClient client, String url, Path outPath, String token) {
    HttpRequest.Builder requestBuilder =
        HttpRequest.newBuilder(URI.create(url))
            .timeout(Duration.ofSeconds(120))
            .header("User-Agent", USER_AGENT)
            .GET();
    if (token != null) requestBuilder.header("Authorization", "Bearer " + token);

    try {
      HttpResponse<byte[]> response =
          client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());
      int status = response.statusCode();
      if (status < 200 || status >= 300) {
        return new DownloadResult(false, "HTTP " + status);
      }

      byte[] bytes = response.body();
      if (bytes == null || bytes.length == 0) {
        return new DownloadResult(false, "downloaded file was empty");
      }

      Files.createDirectories(outPath.getParent());
      Path tmpPath = outPath.resolveSibling(outPath.getFileName().toString() + ".tmp");
      Files.write(tmpPath, bytes);
      Files.move(
          tmpPath, outPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      return new DownloadResult(true, "");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return new DownloadResult(false, "interrupted");
    } catch (Exception e) {
      return new DownloadResult(false, e.getMessage());
    }
  }

  private static String getOriginUrl(Path repoDir) {
    ProcessBuilder builder =
        new ProcessBuilder("git", "-C", repoDir.toString(), "remote", "get-url", "origin")
            .redirectErrorStream(true);
    try {
      Process process = builder.start();
      String output =
          new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
      int code = process.waitFor();
      if (code != 0 || output.isBlank()) return null;
      return output;
    } catch (Exception e) {
      return null;
    }
  }

  private static String parseGitHubSlug(String origin) {
    for (Pattern pattern : GITHUB_SLUG_PATTERNS) {
      Matcher matcher = pattern.matcher(origin);
      if (matcher.find()) {
        return matcher.group("slug");
      }
    }
    return null;
  }

  private static ReleaseLookupResult getLatestRelease(
      HttpClient client, String slug, String token) {
    ApiJsonResult latest = apiGetJson(client, "/repos/" + slug + "/releases/latest", token);
    if (latest.body() != null && latest.body().isJsonObject()) {
      return new ReleaseLookupResult(latest.body().getAsJsonObject(), "latest", "");
    }

    ApiJsonResult releases = apiGetJson(client, "/repos/" + slug + "/releases?per_page=1", token);
    if (releases.body() != null && releases.body().isJsonArray()) {
      JsonArray array = releases.body().getAsJsonArray();
      if (!array.isEmpty() && array.get(0).isJsonObject()) {
        return new ReleaseLookupResult(array.get(0).getAsJsonObject(), "releases_list_first", "");
      }
    }

    if (releases.error() != null && !releases.error().isBlank()) {
      return new ReleaseLookupResult(null, "", releases.error());
    }
    if (latest.error() != null && !latest.error().isBlank()) {
      return new ReleaseLookupResult(null, "", latest.error());
    }
    if (latest.statusCode() == 404) {
      return new ReleaseLookupResult(null, "", "no releases found");
    }
    return new ReleaseLookupResult(null, "", "unable to resolve latest release");
  }

  private static String sanitizeFileName(String value) {
    return value.replaceAll("[<>:\"/\\\\|?*]+", "_");
  }

  private static String stringValue(JsonObject object, String key) {
    if (!object.has(key) || object.get(key).isJsonNull()) return "";
    return object.get(key).getAsString();
  }

  private static SummaryEntry entry(
      String addonFolder,
      String repo,
      String releaseTag,
      String releaseName,
      String releaseUrl,
      String publishedAt,
      String releasesUrl,
      String assetName,
      String assetUrl,
      String downloadedTo,
      String status,
      String note) {
    return new SummaryEntry(
        addonFolder,
        repo,
        releaseTag,
        releaseName,
        releaseUrl,
        publishedAt,
        releasesUrl,
        assetName,
        assetUrl,
        downloadedTo,
        status,
        note);
  }

  private record SummaryPaths(Path summaryJson, Path summaryCsv, Path summaryTxt) {}

  private static SummaryPaths writeSummaryFiles(List<SummaryEntry> entries, Args args)
      throws IOException {
    Path summaryJson = args.jarsDir().resolve("release-summary.json");
    Path summaryCsv = args.jarsDir().resolve("release-summary.csv");
    Path summaryTxt = args.jarsDir().resolve("release-summary.txt");

    List<Map<String, String>> rows = entries.stream().map(SummaryEntry::asMap).toList();
    Files.writeString(summaryJson, GSON.toJson(rows), StandardCharsets.UTF_8);
    writeSummaryCsv(summaryCsv, rows);
    return new SummaryPaths(summaryJson, summaryCsv, summaryTxt);
  }

  private static void writeSummaryCsv(Path csvPath, List<Map<String, String>> rows)
      throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8)) {
      writer.write(String.join(",", SUMMARY_FIELDS));
      writer.newLine();
      for (Map<String, String> row : rows) {
        List<String> cells = new ArrayList<>(SUMMARY_FIELDS.size());
        for (String field : SUMMARY_FIELDS) {
          cells.add(escapeCsvCell(row.getOrDefault(field, "")));
        }
        writer.write(String.join(",", cells));
        writer.newLine();
      }
    }
  }

  private static String escapeCsvCell(String value) {
    boolean needsQuotes =
        value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
    if (!needsQuotes) return value;
    return "\"" + value.replace("\"", "\"\"") + "\"";
  }

  private static List<String> buildSummaryLines(
      List<SummaryEntry> entries, Args args, SummaryPaths summaryPaths) {
    int downloadedCount = 0;
    int noReleases = 0;
    int noJarAssets = 0;
    int failedDownloads = 0;
    int failureCount = 0;
    Set<String> repoFolders = new LinkedHashSet<>();
    for (SummaryEntry entry : entries) {
      repoFolders.add(entry.addonFolder());
      if ("downloaded".equals(entry.status())) downloadedCount++;
      if ("no_releases_found".equals(entry.status())) noReleases++;
      if ("release_found_no_jar_assets".equals(entry.status())) noJarAssets++;
      if ("failed_download".equals(entry.status())) failedDownloads++;
      if (FAILURE_STATUSES.contains(entry.status())) failureCount++;
    }

    List<String> lines = new ArrayList<>();
    lines.add("timestamp_utc=" + OffsetDateTime.now(ZoneOffset.UTC));
    lines.add("addons_dir=" + args.addonsDir());
    lines.add("jars_dir=" + args.jarsDir());
    lines.add("repos_seen=" + repoFolders.size());
    lines.add("downloaded_jars=" + downloadedCount);
    lines.add("no_releases=" + noReleases);
    lines.add("releases_without_jars=" + noJarAssets);
    lines.add("failed_downloads=" + failedDownloads);
    lines.add("failure_entries=" + failureCount);
    lines.add("summary_json=" + summaryPaths.summaryJson());
    lines.add("summary_csv=" + summaryPaths.summaryCsv());
    return lines;
  }
}
