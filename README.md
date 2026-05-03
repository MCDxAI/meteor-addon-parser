<div align="center">

# addon-parser

**Headless Java scanner for Meteor Client addons**

![Java](https://img.shields.io/badge/Java-25-orange?style=flat) ![Gradle](https://img.shields.io/badge/Gradle-9.4.1-02303A?style=flat) ![Version](https://img.shields.io/badge/Version-0.1.0-purple?style=flat)

**Extract module metadata, settings, and configuration from addon JARs without running Minecraft**

</div>

<div align="center">

## Overview

</div>

<div align="center">

| Capability | Details |
| --- | --- |
| **Purpose** | Emulates enough of Meteor Client's runtime to load addons, execute initialization code, and extract structured metadata without requiring the full Minecraft game or GUI |
| **Output** | Generates JSON files containing addon metadata, module information, settings with default values, categories, and descriptions |
| **Scan Modes** | **LEGACY** - Runs in-process for faster scans • **ISOLATED** - Spawns separate JVM worker processes for safer handling of untrusted JARs |
| **Mapping Profiles** | **26x** - Mojmap/Meteor 0.6.x addon fixtures • **legacy** - 1.21.x Yarn/intermediary addon fixtures |

</div>

<div align="center">

## Tech Stack

</div>

<div align="center">

| Category | Dependencies |
| --- | --- |
| **Language** | Java 25 with Gradle 9.4.1 (Groovy DSL build) |
| **JSON Processing** | Jackson 2.18.3 • Gson 2.11.0 |
| **Bytecode Analysis** | ASM 9.9 with ASM Commons |
| **Logging** | SLF4J 2.0.16 + Log4j 2.24.3 |
| **Minecraft Ecosystem** | Brigadier 1.0.18 • Mixin 0.8.5 |
| **Testing** | JUnit Jupiter 5.11.4 |
| **Code Quality** | Spotless 8.4.0 with google-java-format 1.35.0, ratcheted from `master` |

</div>

<div align="center">

## Features

</div>

<div align="center">

| Feature | Description |
| --- | --- |
| **Headless Scanning** | Parse addon JARs without launching Minecraft or any GUI components |
| **Fabric Mod Support** | Reads `fabric.mod.json` metadata and loads `MeteorAddon` entrypoints |
| **Runtime Emulation** | Provides stub implementations of Meteor Client systems: Modules, Settings, HUD, Commands |
| **Dual Scan Modes** | LEGACY mode for speed • ISOLATED mode for safety with untrusted jars |
| **Profile-Specific Compatibility** | Builds against either the 26x Mojmap shim set or the legacy 1.21.x/Yarn shim set |
| **Mapping Normalization** | Converts obfuscated/intermediary symbols into human-usable names where mappings are available |
| **Sandboxed Environment** | Contains addon runtime writes under `tmp/addon-parser-runtime` and cleans scan artifacts by default |
| **Stub Generation** | Auto-generates Java stub classes from addon bytecode, including nested JVM class references |
| **CI-Ready** | GitHub Actions runs a `[26x, legacy]` matrix against checked-in fixture jars |

</div>

<div align="center">

## Quick Start

</div>

<div align="center">

| Step | Instructions |
| --- | --- |
| **1. Prerequisites** | Java 25 JDK • Gradle wrapper included (`gradlew`, `gradlew.bat`) |
| **2. Clone** | `git clone <repo-url>`<br>`cd addon-parser` |
| **3. Fixtures** | Fixture jars are checked in under `fixtures/addons/jars/26x` and `fixtures/addons/jars/legacy` |
| **4. Generate Stubs** | `./gradlew generateStubs -PparserProfile=26x` or `./gradlew generateStubs -PparserProfile=legacy` |
| **5. Test** | `./gradlew test -PautoGenerateStubs -PparserProfile=26x --no-daemon`<br>`./gradlew test -PautoGenerateStubs -PparserProfile=legacy --no-daemon` |
| **6. Run Scanner** | `./gradlew run -PparserProfile=26x --args="--input fixtures/addons/jars/26x --output output/26x --summary output/26x/summary.json --profile 26x"` |

</div>

<div align="center">

## Profiles

</div>

<div align="center">

| Profile | Fixture Directory | Current Fixture Count | Notes |
| --- | --- | --- | --- |
| **26x** | `fixtures/addons/jars/26x` | 9 jars | Mojmap-oriented Meteor 0.6.x / Minecraft 26.x addon compatibility profile; default Gradle profile |
| **legacy** | `fixtures/addons/jars/legacy` | 24 jars | Legacy 1.21.x Yarn/intermediary compatibility profile |

</div>

<div align="center">

## CLI Usage

</div>

<div align="center">

| Argument | Description |
| --- | --- |
| `--input` | Path to addon JAR file or directory containing JARs |
| `--output` | Output directory for generated per-JAR JSON files; defaults to `output` |
| `--summary` | Optional path for aggregate summary JSON; defaults to `<output>/summary.json` |
| `--mode` | Scan mode: `legacy`/`LEGACY` for in-process scanning or `isolated`/`ISOLATED` for worker JVM scanning |
| `--profile` | Mapping/profile mode: `26x` or `legacy`; defaults from `addonparser.profile` and Gradle's `-PparserProfile` |

</div>
<div align="center">
<b>Examples:</b>
</div>

```bash
./gradlew run -PparserProfile=26x --args="--input fixtures/addons/jars/26x --output output/26x --summary output/26x/summary.json --profile 26x"
./gradlew run -PparserProfile=legacy --args="--input fixtures/addons/jars/legacy --output output/legacy --summary output/legacy/summary.json --profile legacy --mode ISOLATED"
```

<div align="center">

## Output Format

</div>

<div align="center">

| File | Contents |
| --- | --- |
| **Per-JAR JSON** | `{jarName}.json` containing:<br>&nbsp;&nbsp;&nbsp;• `jarName` / `jarPath` - File identification<br>&nbsp;&nbsp;&nbsp;• `success` - Boolean scan status<br>&nbsp;&nbsp;&nbsp;• `errors` / `warnings` - Diagnostic messages<br>&nbsp;&nbsp;&nbsp;• `addons` - Array of `AddonDump` objects<br>&nbsp;&nbsp;&nbsp;• `modules` - Array of `ModuleDump` objects |
| **Summary JSON** | `summary.json` with aggregate statistics across all scanned JARs |

</div>

<div align="center">
<b>Module metadata includes:</b>
</div>

<div align="center">

| Field | Description |
| --- | --- |
| `name` | Module display name |
| `category` | Meteor Client category (Combat, Render, Misc, etc.) |
| `description` | Module description text |
| `aliases` | Alternative command names |
| `className` | Fully qualified class name |
| `settings` | Array of `SettingDump` with name, type, and default value |
| `settingGroups` | Nested setting group structures |

</div>

<div align="center">

## Development Workflow

</div>

<div align="center">

| Task | Command |
| --- | --- |
| **Build 26x** | `./gradlew build -PautoGenerateStubs -PparserProfile=26x --no-daemon` |
| **Build Legacy** | `./gradlew build -PautoGenerateStubs -PparserProfile=legacy --no-daemon` |
| **Test 26x** | `./gradlew test -PautoGenerateStubs -PparserProfile=26x --no-daemon` |
| **Test Legacy** | `./gradlew test -PautoGenerateStubs -PparserProfile=legacy --no-daemon` |
| **Format Check** | `./gradlew spotlessCheck --no-daemon` |
| **Format Branch Changes** | `./gradlew spotlessApply --no-daemon` |
| **Generate Stubs** | `./gradlew generateStubs -PparserProfile=26x` or `./gradlew generateStubs -PparserProfile=legacy` |
| **Verify Stubs** | `./gradlew verifyGeneratedStubs -PparserProfile=26x` or `./gradlew verifyGeneratedStubs -PparserProfile=legacy` |
| **Generate JSON** | `./gradlew run -PparserProfile=26x --args="--input fixtures/addons/jars/26x --output output/26x --summary output/26x/summary.json --profile 26x"` |

</div>

<div align="center">

## Project Structure

</div>

<div align="center">

| Directory | Contents |
| --- | --- |
| `src/main/java/com/cope/addonparser/` | Core scanner logic, CLI, models, profile selection, and utilities |
| `src/main/java/meteordevelopment/` | Shared Meteor Client emulation layer |
| `src/profile-26x/java/` | 26x/Mojmap compatibility shims included when `-PparserProfile=26x` |
| `src/profile-legacy/java/` | Legacy 1.21.x/Yarn compatibility shims included when `-PparserProfile=legacy` |
| `src/generated/java/` | Profile-specific auto-generated stub classes from addon bytecode |
| `src/stubgen/java/` | Java stub generator implementation |
| `src/test/java/` | JUnit tests, including profile-aware fixture scanning |
| `fixtures/addons/jars/26x/` | Checked-in 26x addon fixture JARs |
| `fixtures/addons/jars/legacy/` | Checked-in legacy addon fixture JARs |
| `ai_reference/` | Optional local source reference material; ignored by Git and not used for fixture acquisition |
| `.github/workflows/` | CI pipeline configuration |

</div>

<div align="center">

## Configuration

</div>

<div align="center">

| Property | Description | Default |
| --- | --- | --- |
| `addonparser.profile` | Runtime mapping profile used by CLI/scanners: `26x` or `legacy` | `26x` |
| `addonparser.fixtureJarsDir` | Test fixture directory override | `fixtures/addons/jars/<profile>` |
| `addonparser.runtimeTmpDir` | Temporary directory for sandboxed runtime artifacts | `tmp/addon-parser-runtime` |
| `addonparser.keepTmp` | Keep temporary artifacts after scan for debugging | `false` |
| `addonparser.meteorFolder` | Optional override for the emulated Meteor folder | profile-managed per scan |
| `addonparser.yarnAutoDownload` | Legacy mapping resolver may auto-download missing Yarn mappings | `true` |
| `addonparser.yarnMappingsVersions` | Yarn mapping versions to use for legacy mapping resolution | auto-detected when available |
| `addonparser.mappingsDir` | Directory for cached mappings | `mappings` |
| `addonparser.yarnMappingsJar` | Explicit path list of Yarn mapping jars to load | unset |

</div>

<div align="center">

## CI Pipeline

</div>

<div align="center">

| Stage | Description |
| --- | --- |
| **Setup** | Uses Java 25 and Gradle 9.4.1 with Gradle cache enabled |
| **Matrix** | Runs both `26x` and `legacy` profiles independently |
| **Generate Stubs** | Auto-creates profile-specific stub classes with `-PautoGenerateStubs` |
| **Test** | Runs the JUnit suite against checked-in profile fixture jars |
| **Generate JSON** | Runs the CLI for each profile and writes `output/poc-scan/summary.json` |
| **Reports** | Uploads Gradle test reports on failure |

</div>
