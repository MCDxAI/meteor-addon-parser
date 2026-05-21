<div align="center">

# addon-parser

**Headless Java scanner for Meteor Client addons**

![Java](https://img.shields.io/badge/Java-25-orange?style=flat) ![Version](https://img.shields.io/badge/Version-0.1.0-purple?style=flat)

Extract module metadata, settings, and configuration from addon JARs without launching Minecraft.

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

## Development

</div>

<div align="center">

Built with Java 25 and Gradle (Groovy DSL); bytecode analysis via ASM, JSON via Jackson/Gson.

| Task | Command |
| --- | --- |
| **Build** | `./gradlew build -PautoGenerateStubs -PparserProfile=<profile> --no-daemon` |
| **Test** | `./gradlew test -PautoGenerateStubs -PparserProfile=<profile> --no-daemon` |
| **Generate stubs** | `./gradlew generateStubs -PparserProfile=<profile>` |
| **Format** | `./gradlew spotlessApply --no-daemon` |

</div>
