<div align="center">

# addon-parser

**Headless Java scanner for Meteor Client addons**

![Java](https://img.shields.io/badge/Java-25-orange?style=flat) ![Gradle](https://img.shields.io/badge/Gradle-9.4.1-02303A?style=flat) ![Version](https://img.shields.io/badge/Version-0.1.0-purple?style=flat)

Extract module metadata, settings, and configuration from addon JARs without launching Minecraft.

</div>

<div align="center">

## What It Does

</div>

<div align="center">

| | |
| --- | --- |
| **Purpose** | Loads Meteor Client addons in a headless JVM, runs their initialization code, and dumps structured metadata to JSON |
| **Output** | Per-JAR JSON with addon info, modules, categories, descriptions, and settings (including default values) |
| **Scan Modes** | `LEGACY` runs in-process for speed • `ISOLATED` forks a worker JVM for untrusted JARs |
| **Profiles** | `26x` for Mojmap / Meteor 0.6.x • `legacy` for 1.21.x Yarn/intermediary addons |

</div>

<div align="center">

## Quick Start

</div>

<div align="center">

| Step | Command |
| --- | --- |
| **Prerequisites** | Java 25 JDK (Gradle wrapper is included) |
| **Clone** | `git clone <repo-url> && cd addon-parser` |
| **Generate stubs** | `./gradlew generateStubs -PparserProfile=26x` |
| **Run tests** | `./gradlew test -PautoGenerateStubs -PparserProfile=26x --no-daemon` |
| **Scan addons** | `./gradlew run -PparserProfile=26x --args="--input fixtures/addons/jars/26x --output output/26x --profile 26x"` |

</div>

<div align="center">

Fixture JARs for both profiles are checked in under `fixtures/addons/jars/`, so the test and scan commands work out of the box.

</div>

<div align="center">

## CLI

</div>

<div align="center">

| Argument | Description |
| --- | --- |
| `--input` | Path to an addon JAR or a directory of JARs |
| `--output` | Output directory for per-JAR JSON files (default: `output`) |
| `--summary` | Path for the aggregate summary JSON (default: `<output>/summary.json`) |
| `--mode` | `LEGACY` (in-process) or `ISOLATED` (forked worker JVM) |
| `--profile` | `26x` or `legacy`; falls back to `addonparser.profile` or `-PparserProfile` |

</div>

```bash
./gradlew run -PparserProfile=26x --args="--input fixtures/addons/jars/26x --output output/26x --summary output/26x/summary.json --profile 26x"
./gradlew run -PparserProfile=legacy --args="--input fixtures/addons/jars/legacy --output output/legacy --summary output/legacy/summary.json --profile legacy --mode ISOLATED"
```

<div align="center">

## Features

</div>

<div align="center">

| Feature | Description |
| --- | --- |
| **Headless scanning** | No Minecraft, no GUI, no game tick loop — just the JVM |
| **Fabric mod parsing** | Reads `fabric.mod.json` and locates `MeteorAddon` entrypoints |
| **Runtime emulation** | Stubs for Meteor Client systems: Modules, Settings, HUD, Commands |
| **Two scan modes** | In-process for speed, or a forked worker JVM for isolation |
| **Profile support** | Separate compatibility shim sets for 26x Mojmap and legacy Yarn addons |
| **Mapping normalization** | Resolves obfuscated and intermediary names to readable ones where possible |
| **Sandboxed writes** | Addon runtime writes are confined to `tmp/addon-parser-runtime` and cleaned up by default |
| **Stub generation** | Auto-generates Java stubs from addon bytecode, including nested class refs |
| **CI coverage** | GitHub Actions runs both profiles against the checked-in fixture JARs |

</div>

<div align="center">

## Profiles

</div>

<div align="center">

| Profile | Fixtures | Count | Notes |
| --- | --- | --- | --- |
| **26x** | `fixtures/addons/jars/26x` | 9 jars | Mojmap-oriented Meteor 0.6.x / Minecraft 26.x; default profile |
| **legacy** | `fixtures/addons/jars/legacy` | 24 jars | 1.21.x Yarn / intermediary addons |

</div>

<div align="center">

## Output

</div>

<div align="center">

| File | Contents |
| --- | --- |
| **`{jarName}.json`** | `jarName` / `jarPath`, `success`, `errors` / `warnings`, `addons[]`, `modules[]` |
| **`summary.json`** | Aggregate stats across every scanned JAR |

</div>

<div align="center">

Each `ModuleDump` includes:

</div>

<div align="center">

| Field | Description |
| --- | --- |
| `name` | Display name |
| `category` | Meteor category (Combat, Render, Misc, ...) |
| `description` | Module description |
| `aliases` | Alternative command names |
| `className` | Fully qualified class name |
| `settings` | `SettingDump[]` with name, type, and default value |
| `settingGroups` | Nested setting group structure |

</div>

<div align="center">

## Tech Stack

</div>

<div align="center">

| Category | Dependencies |
| --- | --- |
| **Language** | Java 25, Gradle 9.4.1 (Groovy DSL) |
| **JSON** | Jackson 2.18.3, Gson 2.11.0 |
| **Bytecode** | ASM 9.9 with ASM Commons |
| **Logging** | SLF4J 2.0.16, Log4j 2.24.3 |
| **Minecraft** | Brigadier 1.0.18, Mixin 0.8.5 |
| **Testing** | JUnit Jupiter 5.11.4 |
| **Formatting** | Spotless 8.4.0 with google-java-format 1.35.0 |

</div>

<div align="center">

## Development

</div>

<div align="center">

| Task | Command |
| --- | --- |
| **Build (26x)** | `./gradlew build -PautoGenerateStubs -PparserProfile=26x --no-daemon` |
| **Build (legacy)** | `./gradlew build -PautoGenerateStubs -PparserProfile=legacy --no-daemon` |
| **Test (26x)** | `./gradlew test -PautoGenerateStubs -PparserProfile=26x --no-daemon` |
| **Test (legacy)** | `./gradlew test -PautoGenerateStubs -PparserProfile=legacy --no-daemon` |
| **Format check** | `./gradlew spotlessCheck --no-daemon` |
| **Format apply** | `./gradlew spotlessApply --no-daemon` |
| **Generate stubs** | `./gradlew generateStubs -PparserProfile=<profile>` |
| **Verify stubs** | `./gradlew verifyGeneratedStubs -PparserProfile=<profile>` |

</div>

<div align="center">

## Project Layout

</div>

<div align="center">

| Directory | Contents |
| --- | --- |
| `src/main/java/com/cope/addonparser/` | Scanner core, CLI, models, profile selection, utilities |
| `src/main/java/meteordevelopment/` | Shared Meteor Client emulation layer |
| `src/profile-26x/java/` | 26x / Mojmap compatibility shims |
| `src/profile-legacy/java/` | Legacy 1.21.x / Yarn compatibility shims |
| `src/generated/java/` | Auto-generated stub classes (per profile) |
| `src/stubgen/java/` | Stub generator implementation |
| `src/test/java/` | JUnit tests, including profile-aware fixture scans |
| `fixtures/addons/jars/` | Checked-in fixture JARs for both profiles |
| `.github/workflows/` | CI configuration |

</div>

<div align="center">

## Configuration

</div>

<div align="center">

| Property | Description | Default |
| --- | --- | --- |
| `addonparser.profile` | Runtime mapping profile (`26x` or `legacy`) | `26x` |
| `addonparser.fixtureJarsDir` | Override the test fixture directory | `fixtures/addons/jars/<profile>` |
| `addonparser.runtimeTmpDir` | Temp directory for sandboxed runtime artifacts | `tmp/addon-parser-runtime` |
| `addonparser.keepTmp` | Keep temp artifacts after scan for debugging | `false` |
| `addonparser.meteorFolder` | Override the emulated Meteor folder | managed per scan |
| `addonparser.yarnAutoDownload` | Allow the legacy resolver to download missing Yarn mappings | `true` |
| `addonparser.yarnMappingsVersions` | Specific Yarn mapping versions to use | auto-detected |
| `addonparser.mappingsDir` | Cached mappings directory | `mappings` |
| `addonparser.yarnMappingsJar` | Explicit list of Yarn mapping JARs | unset |

</div>

<div align="center">

## CI

</div>

<div align="center">

| Stage | Description |
| --- | --- |
| **Setup** | Java 25, Gradle 9.4.1, with Gradle cache enabled |
| **Matrix** | `26x` and `legacy` profiles run independently |
| **Stubs** | Auto-generated via `-PautoGenerateStubs` |
| **Test** | JUnit suite against checked-in fixture JARs |
| **Scan** | CLI runs per profile, writing `output/poc-scan/summary.json` |
| **Reports** | Gradle test reports uploaded on failure |

</div>
