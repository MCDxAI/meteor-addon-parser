package com.cope.addonparser.scanner;

import com.cope.addonparser.model.AddonDump;
import com.cope.addonparser.model.FabricModMetadata;
import com.cope.addonparser.model.JarScanResult;
import com.cope.addonparser.model.ModuleDump;
import com.cope.addonparser.model.SettingDump;
import com.cope.addonparser.model.SettingGroupDump;
import com.cope.addonparser.profile.MappingProfile;
import com.cope.addonparser.util.ChildFirstClassLoader;
import com.cope.addonparser.util.FabricModParser;
import com.cope.addonparser.util.ValueNormalizer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class AddonScanner implements AutoCloseable {
  private static final String TMP_ROOT_PROPERTY = "addonparser.runtimeTmpDir";
  private static final String KEEP_TMP_PROPERTY = "addonparser.keepTmp";
  private static final Path DEFAULT_TMP_ROOT = Paths.get("tmp", "addon-parser-runtime");

  private final MappingProfile profile;

  public AddonScanner() {
    this(MappingProfile.fromSystemProperty());
  }

  public AddonScanner(MappingProfile profile) {
    this.profile = profile;
    System.setProperty(MappingProfile.SYSTEM_PROPERTY, profile.cliValue());
  }

  public MappingProfile profile() {
    return profile;
  }

  public JarScanResult scan(Path jarPath) {
    Path absoluteJar = jarPath.toAbsolutePath().normalize();

    JarScanResult result = new JarScanResult();
    result.jarName = absoluteJar.getFileName().toString();
    result.jarPath = absoluteJar.toString();

    FabricModMetadata metadata;
    try {
      metadata = FabricModParser.parse(absoluteJar);
    } catch (Throwable t) {
      result.success = false;
      result.errors.add("Failed to parse fabric.mod.json: " + t.getMessage());
      return result;
    }

    resetRuntime();
    RuntimeSandbox sandbox = RuntimeSandbox.open(absoluteJar);
    if (sandbox.openError() != null) {
      result.warnings.add(
          "Runtime sandbox setup failed; using current directory. " + sandbox.openError());
    }
    if (sandbox.isKeepingArtifacts()) {
      result.warnings.add("Runtime artifacts kept at: " + sandbox.scanDir().toAbsolutePath());
    }
    String sandboxEnterError = sandbox.enter();
    if (sandboxEnterError != null) {
      result.warnings.add(
          "Runtime sandbox activation failed; using current directory. " + sandboxEnterError);
    }

    URL jarUrl;
    try {
      jarUrl = absoluteJar.toUri().toURL();
    } catch (Throwable t) {
      result.success = false;
      result.errors.add("Invalid jar URL: " + t.getMessage());
      sandbox.exit();
      return result;
    }

    ClassLoader previous = Thread.currentThread().getContextClassLoader();
    try (ChildFirstClassLoader loader =
        new ChildFirstClassLoader(new URL[] {jarUrl}, AddonScanner.class.getClassLoader())) {
      Thread.currentThread().setContextClassLoader(loader);

      List<EntrypointHolder> loadedEntrypoints = new ArrayList<>();
      for (String rawEntrypoint : metadata.meteorEntrypoints) {
        String entrypoint = normalizeEntrypoint(rawEntrypoint);
        try {
          Class<?> entryClass = Class.forName(entrypoint, true, loader);
          Object instance = entryClass.getDeclaredConstructor().newInstance();

          if (!(instance instanceof MeteorAddon addon)) {
            result.errors.add("Entrypoint is not a MeteorAddon: " + rawEntrypoint);
            continue;
          }

          addon.name =
              metadata.name != null
                  ? metadata.name
                  : (metadata.id != null ? metadata.id : entrypoint);
          addon.authors = metadata.authors.toArray(new String[0]);
          AddonManager.ADDONS.add(addon);
          loadedEntrypoints.add(new EntrypointHolder(rawEntrypoint, addon));
        } catch (Throwable t) {
          result.errors.add("Entrypoint load failure (" + rawEntrypoint + "): " + rootMessage(t));
        }
      }

      try {
        Categories.init();
      } catch (Throwable t) {
        result.errors.add("Category registration failure: " + rootMessage(t));
      }

      for (EntrypointHolder holder : loadedEntrypoints) {
        try {
          holder.addon.onInitialize();
        } catch (Throwable t) {
          result.errors.add(
              "onInitialize failure (" + holder.rawEntrypoint + "): " + rootMessage(t));
        }
      }

      for (EntrypointHolder holder : loadedEntrypoints) {
        result.addons.add(dumpAddon(holder));
      }

      List<Module> modules = new ArrayList<>(Modules.get().getAll());
      modules.sort(
          Comparator.comparing((Module m) -> safe(m.category == null ? null : m.category.name))
              .thenComparing(m -> safe(m.name)));
      for (Module module : modules) {
        result.modules.add(dumpModule(module));
      }

      result.success = result.errors.isEmpty();
      if (!result.success && !result.modules.isEmpty()) {
        result.warnings.add("Partial scan completed with module data despite errors.");
      }

      return result;
    } catch (Throwable t) {
      result.success = false;
      result.errors.add("Scanner runtime failure: " + rootMessage(t));
      return result;
    } finally {
      Thread.currentThread().setContextClassLoader(previous);
      resetRuntime();
      sandbox.exit();
    }
  }

  private static void resetRuntime() {
    AddonManager.reset();
    Modules.reset();
    Systems.reset();
    Commands.reset();
    Hud.get().reset();
    ValueNormalizer.resetCache();
  }

  private static String normalizeEntrypoint(String raw) {
    if (raw == null) return "";
    int methodRef = raw.indexOf("::");
    return (methodRef >= 0 ? raw.substring(0, methodRef) : raw).trim();
  }

  private static AddonDump dumpAddon(EntrypointHolder holder) {
    MeteorAddon addon = holder.addon;
    AddonDump dump = new AddonDump();
    dump.entrypoint = holder.rawEntrypoint;
    dump.name = addon.name;
    dump.packageName = safe(addon.getPackage());
    dump.website = safe(addon.getWebsite());

    String[] authors = addon.authors == null ? new String[0] : addon.authors;
    dump.authors.addAll(Arrays.asList(authors));

    try {
      GithubRepo repo = addon.getRepo();
      if (repo != null) {
        dump.repoOwner = safe(repo.owner());
        dump.repoName = safe(repo.name());
        dump.repoBranch = safe(repo.branch());
        dump.repoCommit = safe(repo.commit());
      }
    } catch (RuntimeException e) {
      // GithubRepo accessor may not be implemented by all addons - safe to skip
    }

    return dump;
  }

  private static ModuleDump dumpModule(Module module) {
    ModuleDump dump = new ModuleDump();
    dump.className = module.getClass().getName();
    dump.category = module.category == null ? null : module.category.name;
    dump.name = module.name;
    dump.title = module.title;
    dump.description = module.description;
    dump.addonPackage = module.addon == null ? null : safe(module.addon.getPackage());
    dump.aliases.addAll(Arrays.asList(module.aliases == null ? new String[0] : module.aliases));

    for (SettingGroup group : module.settings.groups) {
      SettingGroupDump groupDump = new SettingGroupDump();
      groupDump.name = group.name;

      for (Setting<?> setting : group) {
        SettingDump settingDump = new SettingDump();
        settingDump.name = setting.name;
        settingDump.title = setting.title;
        settingDump.description = setting.description;
        settingDump.type = setting.getClass().getSimpleName();
        settingDump.defaultValue = ValueNormalizer.normalize(setting.getDefaultValue());
        settingDump.value = ValueNormalizer.normalize(setting.get());
        try {
          settingDump.visible = setting.isVisible();
        } catch (RuntimeException e) {
          // Visibility predicate may reference unavailable game state - default is fine
        }
        groupDump.settings.add(settingDump);
      }

      dump.groups.add(groupDump);
    }

    return dump;
  }

  private static String safe(String value) {
    return value == null ? "" : value;
  }

  private static String rootMessage(Throwable t) {
    Throwable cur = t;
    while (cur.getCause() != null && cur.getCause() != cur) cur = cur.getCause();
    String name = cur.getClass().getSimpleName();
    String msg = cur.getMessage();
    String base = msg == null ? name : name + ": " + msg;
    StackTraceElement[] trace = cur.getStackTrace();
    if (trace == null || trace.length == 0) return base;
    StackTraceElement top = trace[0];
    StackTraceElement interesting = top;
    for (StackTraceElement frame : trace) {
      String owner = frame.getClassName();
      if (owner.startsWith("java.")
          || owner.startsWith("javax.")
          || owner.startsWith("jdk.")
          || owner.startsWith("sun.")) continue;
      interesting = frame;
      break;
    }
    return base
        + " @"
        + interesting.getClassName()
        + "#"
        + interesting.getMethodName()
        + ":"
        + interesting.getLineNumber();
  }

  private record EntrypointHolder(String rawEntrypoint, MeteorAddon addon) {}

  private static Path resolveTmpRoot() {
    String configured = System.getProperty(TMP_ROOT_PROPERTY);
    if (configured == null || configured.isBlank()) return DEFAULT_TMP_ROOT;
    return Paths.get(configured);
  }

  private static boolean shouldKeepTmp() {
    String keep = System.getProperty(KEEP_TMP_PROPERTY);
    if (keep == null) return false;
    String normalized = keep.trim().toLowerCase(Locale.ROOT);
    return "true".equals(normalized)
        || "1".equals(normalized)
        || "yes".equals(normalized)
        || "on".equals(normalized);
  }

  private static String sanitizeFileName(String input) {
    if (input == null || input.isBlank()) return "scan";
    return input.replaceAll("[^a-zA-Z0-9._-]", "_");
  }

  private static void deleteRecursively(Path root) {
    if (root == null || !Files.exists(root)) return;
    try (Stream<Path> stream = Files.walk(root)) {
      List<Path> toDelete = stream.sorted(Comparator.reverseOrder()).toList();
      for (Path path : toDelete) {
        try {
          Files.deleteIfExists(path);
        } catch (IOException e) {
          // Best-effort deletion: file may be locked by another process
        }
      }
    } catch (IOException e) {
      // Walk failure: root may have been concurrently deleted
    }
  }

  @Override
  public void close() {
    try {
      ForkJoinPool.commonPool().awaitQuiescence(15, TimeUnit.SECONDS);
    } catch (Throwable t) {
      // Pool quiescence timeout - non-fatal, proceed with cleanup
    }
  }

  private static final class RuntimeSandbox {
    private final Path rootDir;
    private final Path scanDir;
    private final boolean keepArtifacts;
    private final String openError;

    private String previousUserDir;
    private File previousMeteorFolder;
    private boolean entered;

    private RuntimeSandbox(Path rootDir, Path scanDir, boolean keepArtifacts, String openError) {
      this.rootDir = rootDir;
      this.scanDir = scanDir;
      this.keepArtifacts = keepArtifacts;
      this.openError = openError;
    }

    static RuntimeSandbox open(Path jarPath) {
      Path root = resolveTmpRoot();
      boolean keep = shouldKeepTmp();
      try {
        Files.createDirectories(root);
        String jarBase = sanitizeFileName(jarPath.getFileName().toString());
        String suffix = Long.toUnsignedString(System.nanoTime(), 36);
        Path scan = root.resolve(jarBase + "-" + suffix);
        Files.createDirectories(scan);
        return new RuntimeSandbox(root, scan, keep, null);
      } catch (Throwable t) {
        return new RuntimeSandbox(root, null, keep, rootMessage(t));
      }
    }

    String openError() {
      return openError;
    }

    boolean isKeepingArtifacts() {
      return keepArtifacts && scanDir != null;
    }

    Path scanDir() {
      return scanDir;
    }

    String enter() {
      if (scanDir == null) return openError;
      try {
        previousUserDir = System.getProperty("user.dir");
        previousMeteorFolder = meteordevelopment.meteorclient.MeteorClient.FOLDER;
        String sandboxDir = scanDir.toAbsolutePath().normalize().toString();
        System.setProperty("user.dir", sandboxDir);
        meteordevelopment.meteorclient.MeteorClient.setFolder(scanDir.toFile());
        entered = true;
        return null;
      } catch (Throwable t) {
        if (previousUserDir != null) System.setProperty("user.dir", previousUserDir);
        if (previousMeteorFolder != null)
          meteordevelopment.meteorclient.MeteorClient.setFolder(previousMeteorFolder);
        return rootMessage(t);
      }
    }

    void exit() {
      if (entered) {
        if (previousUserDir != null) System.setProperty("user.dir", previousUserDir);
        meteordevelopment.meteorclient.MeteorClient.setFolder(
            previousMeteorFolder == null ? new File(".") : previousMeteorFolder);
        entered = false;
      }

      if (!keepArtifacts) {
        // Only delete scanner-owned paths under the runtime temp root
        deleteRecursively(scanDir);
        try {
          if (rootDir != null && Files.isDirectory(rootDir)) {
            try (Stream<Path> stream = Files.list(rootDir)) {
              if (stream.findAny().isEmpty()) Files.deleteIfExists(rootDir);
            }
          }
        } catch (IOException e) {
          // Non-fatal: temp root cleanup is best-effort
        }
      }
    }
  }
}
