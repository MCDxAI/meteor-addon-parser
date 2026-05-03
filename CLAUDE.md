# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

Use the Gradle wrapper when possible.

```bash
./gradlew spotlessCheck --no-daemon
./gradlew test -PautoGenerateStubs -PparserProfile=26x --no-daemon
./gradlew test -PautoGenerateStubs -PparserProfile=legacy --no-daemon
./gradlew generateStubs -PparserProfile=26x
./gradlew generateStubs -PparserProfile=legacy
./gradlew verifyGeneratedStubs -PparserProfile=26x
./gradlew verifyGeneratedStubs -PparserProfile=legacy
./gradlew run -PautoGenerateStubs -PparserProfile=26x --args="--input fixtures/addons/jars/26x --output output/26x --summary output/26x/summary.json --profile 26x"
./gradlew run -PautoGenerateStubs -PparserProfile=legacy --args="--input fixtures/addons/jars/legacy --output output/legacy --summary output/legacy/summary.json --profile legacy"
```

Windows PowerShell equivalents use `./gradlew` or `.\gradlew`.

There is no `downloadLatestReleaseJars` task anymore. Fixture jars are checked in by profile.

## Architecture Overview

This is a headless Java scanner for Meteor Client addons. It emulates enough of Meteor Client's runtime to load addon JARs, execute their initialization code, and extract structured metadata (modules, settings, categories) as JSON without running Minecraft or any GUI.

### Core Scan Flow

1. **CLI Entry**: `com.cope.addonparser.cli.Main` parses args, selects a mapping profile, and invokes the scanner.
2. **AddonScanner**: In-process scan using `ChildFirstClassLoader` to isolate addon code.
3. **IsolatedScanner**: Forks a separate JVM worker process (`ScanWorker`) for safer handling of untrusted JARs.
4. Both modes produce `JarScanResult` containing `AddonDump` and `ModuleDump` objects.

### Key Packages and Directories

| Package / Directory | Purpose |
|---------------------|---------|
| `com.cope.addonparser.scanner` | Core scanning logic (`AddonScanner`, `IsolatedScanner`, `ScanWorker`) |
| `com.cope.addonparser.model` | Output DTOs (`JarScanResult`, `ModuleDump`, `SettingDump`, etc.) |
| `com.cope.addonparser.profile` | Profile selection (`26x` vs `legacy`) |
| `com.cope.addonparser.util` | Utilities (`ChildFirstClassLoader`, `ValueNormalizer`, `FabricModParser`, mapping interfaces) |
| `meteordevelopment.meteorclient.*` | Shared stub/emulation layer for Meteor Client APIs |
| `src/profile-26x/java/` | 26x/Mojmap compatibility shims included with `-PparserProfile=26x` |
| `src/profile-legacy/java/` | Legacy 1.21.x/Yarn compatibility shims included with `-PparserProfile=legacy` |
| `src/generated/java/` | Profile-specific auto-generated stub classes from addon bytecode |
| `src/stubgen/java/` | Java stub generation tooling |
| `fixtures/addons/jars/26x` | 26x checked-in fixture JARs |
| `fixtures/addons/jars/legacy` | Legacy checked-in fixture JARs |

### Scan Modes vs Mapping Profiles

Scan mode controls process isolation:

- **LEGACY**: Runs in-process, faster but shares JVM with addon code.
- **ISOLATED**: Spawns worker JVM per scan, safer for untrusted JARs, configurable timeout.

Mapping profile controls compatibility sources and name mapping:

- **26x**: Default profile; Mojmap-oriented Meteor 0.6.x / Minecraft 26.x fixture set.
- **legacy**: 1.21.x/Yarn/intermediary fixture set.

Gradle uses `-PparserProfile=26x|legacy`. The CLI uses `--profile 26x|legacy` and also reads `addonparser.profile`.

## Design Constraints

1. **No Python in project internals** - Build/runtime must remain Java.
2. **Fixtures are checked in** - CI uses `fixtures/addons/jars/26x` and `fixtures/addons/jars/legacy`; do not reintroduce automatic fixture downloading without a deliberate design change.
3. **`ai_reference/` is optional local context** - It may contain cloned Meteor/addon source for reference, but it is gitignored and not part of CI fixture acquisition.
4. **No addon-specific hacks** - Compatibility should be generic (descriptor/type adaptation, API parity, stub hierarchy), not package-name routing patches.
5. **Generated stubs must not overwrite manual classes** - Manual compatibility classes go in profile source dirs or must be listed in `tools/manual_classes.txt`.
6. **JSON must be stable** - Avoid `ClassName@hex` leakage; use enum names and structured values.
7. **Both fixture profiles are regression gates** - If either profile fails, fix compatibility/emulation until it passes.

## Runtime Properties

| Property | Description | Default |
|----------|-------------|---------|
| `addonparser.profile` | Runtime mapping profile (`26x` or `legacy`) | `26x` |
| `addonparser.fixtureJarsDir` | Test fixture directory override | `fixtures/addons/jars/<profile>` |
| `addonparser.runtimeTmpDir` | Temp dir for runtime artifacts | `tmp/addon-parser-runtime` |
| `addonparser.keepTmp` | Keep artifacts for debugging | `false` |
| `addonparser.meteorFolder` | Optional override for emulated Meteor folder | managed per scan |
| `addonparser.yarnAutoDownload` | Legacy mapping resolver may auto-download missing Yarn mappings | `true` |
| `addonparser.mappingsDir` | Cached mappings directory | `mappings` |
| `addonparser.yarnMappingsVersions` | Explicit Yarn mapping versions | unset |
| `addonparser.yarnMappingsJar` | Explicit mapping jar path list | unset |

## Tooling

- Java toolchain: 25
- Gradle wrapper: 9.4.1
- Spotless: 8.4.0
- google-java-format: 1.35.0
- Spotless is ratcheted from `master`; avoid broad formatting-only churn.

## When Modifying Compatibility

1. Reproduce with the relevant profile fixture test.
2. Prefer emulation API parity, generic loader descriptor reconciliation, generic stub hierarchy overrides, and profile-specific shims.
3. Avoid one-off addon routing rules unless absolutely impossible.
4. Re-run:
   - `./gradlew spotlessCheck --no-daemon`
   - `./gradlew test -PautoGenerateStubs -PparserProfile=26x --no-daemon`
   - `./gradlew test -PautoGenerateStubs -PparserProfile=legacy --no-daemon`
5. If scan behavior changed, also run profile-specific JSON generation and spot-check module counts, setting serialization quality, and mapping quality.
