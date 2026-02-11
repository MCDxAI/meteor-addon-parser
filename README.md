<div align="center">

# addon-parser

**Headless Java scanner for Meteor Client addons**

![Java](https://img.shields.io/badge/Java-21-orange?style=flat) ![Gradle](https://img.shields.io/badge/Gradle-8.12.1-02303A?style=flat) ![Version](https://img.shields.io/badge/Version-0.1.0-purple?style=flat) ![License](https://img.shields.io/badge/License-MIT-green?style=flat)

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
| **Modes** | **LEGACY** – Runs in-process for faster scans • **ISOLATED** – Spawns separate JVM worker processes for safer handling of untrusted JARs |

</div>

<div align="center">

## Tech Stack

</div>

<div align="center">

| Category | Dependencies |
| --- | --- |
| **Language** | Java 21 with Gradle 8.12.1 (Kotlin DSL settings) |
| **JSON Processing** | Jackson 2.18.3 • Gson 2.11.0 |
| **Bytecode Analysis** | ASM 9.7.1 with ASM Commons |
| **Logging** | SLF4J 2.0.16 + Log4j 2.24.3 |
| **Minecraft Ecosystem** | Brigadier 1.0.18 • Mixin 0.8.5 |
| **Testing** | JUnit Jupiter 5.11.4 |
| **Code Quality** | Spotless 7.0.2 with Google Java Format |

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
| **Dual Scan Modes** | LEGACY mode for speed • ISOLATED mode for security with untrusted jars |
| **Yarn Mapping Resolution** | Auto-downloads and applies Yarn mappings for normalized class/method names |
| **Sandboxed Environment** | Contains side effects and prevents addon code from affecting the host system |
| **Stub Generation** | Auto-generates Java stub classes from addon bytecode for compilation |
| **CI-Ready** | GitHub Actions workflow with automatic fixture downloading and stub verification |

</div>

<div align="center">

## Quick Start

</div>

<div align="center">

| Step | Instructions |
| --- | --- |
| **1. Prerequisites** | Java 21 JDK • Gradle 8.x (or use wrapper) |
| **2. Clone** | `git clone <repo-url>`<br>`cd addon-parser` |
| **3. Download Fixtures** | `gradle downloadLatestReleaseJars --no-daemon` |
| **4. Generate Stubs** | `gradle generateStubs` (auto-runs during compile with `-PautoGenerateStubs`) |
| **5. Build** | `gradle build` |
| **6. Run Scanner** | `gradle run --args="--input fixtures/addons/jars --output output"` |

</div>

<div align="center">

## CLI Usage

</div>

<div align="center">

| Argument | Description |
| --- | --- |
| `--input` | Path to addon JAR file or directory containing JARs |
| `--output` | Output directory for generated JSON files |
| `--mode` | Scan mode: `LEGACY` (in-process) or `ISOLATED` (separate JVM) |
| `--verbose` | Enable detailed logging output |

</div>

**Example:**

```bash
gradle run --args="--input /path/to/addons --output ./results --mode ISOLATED"
```

<div align="center">

## Output Format

</div>

<div align="center">

| File | Contents |
| --- | --- |
| **Per-JAR JSON** | `{jarName}.json` containing:<br>&nbsp;&nbsp;&nbsp;• `jarName` / `jarPath` – File identification<br>&nbsp;&nbsp;&nbsp;• `success` – Boolean scan status<br>&nbsp;&nbsp;&nbsp;• `errors` / `warnings` – Diagnostic messages<br>&nbsp;&nbsp;&nbsp;• `addons` – Array of `AddonDump` objects<br>&nbsp;&nbsp;&nbsp;• `modules` – Array of `ModuleDump` objects |
| **Summary JSON** | `summary.json` with aggregate statistics across all scanned JARs |

</div>

**Module metadata includes:**

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
| **Build** | `gradle build` – Compile and package |
| **Test** | `gradle test --no-daemon` – Run test suite |
| **CI Test** | `gradle test --no-daemon -PautoGenerateStubs` – Auto-generate stubs before testing |
| **Format Code** | `gradle spotlessApply` – Apply Google Java Format |
| **Generate Stubs** | `gradle generateStubs` – Create stub classes from fixture JARs |
| **Verify Stubs** | `gradle verifyGeneratedStubs` – Check if generated stubs are current |
| **Download Fixtures** | `gradle downloadLatestReleaseJars --no-daemon` – Fetch latest addon releases |

</div>

<div align="center">

## Project Structure

</div>

<div align="center">

| Directory | Contents |
| --- | --- |
| `src/main/java/com/cope/addonparser/` | Core scanner logic and CLI |
| `src/main/java/meteordevelopment/` | Meteor Client emulation layer (stubs) |
| `src/generated/java/` | Auto-generated stub classes from addon bytecode |
| `src/stubgen/java/` | Stub generation tools |
| `src/test/java/` | Test suite (FixtureScanTest) |
| `fixtures/addons/jars/` | Test fixture addon JARs |
| `ai_reference/addons/` | Source repositories for fixtures |
| `tools/` | Python utilities for repo management |
| `.github/workflows/` | CI pipeline configuration |

</div>

<div align="center">

## Configuration

</div>

<div align="center">

| Property | Description | Default |
| --- | --- | --- |
| `addonparser.runtimeTmpDir` | Temporary directory for runtime artifacts | System temp |
| `addonparser.keepTmp` | Keep temporary artifacts after scan (debugging) | `false` |
| `addonparser.yarnAutoDownload` | Auto-download Yarn mappings | `true` |
| `addonparser.yarnMappingsVersions` | Yarn mapping versions to use | Latest |
| `addonparser.mappingsDir` | Directory for cached mappings | `./mappings` |

</div>

<div align="center">

## CI Pipeline

</div>

<div align="center">

| Stage | Description |
| --- | --- |
| **Setup** | Gradle 8.12.1 with cache enabled |
| **Download Fixtures** | Fetches latest addon JARs from GitHub releases |
| **Generate Stubs** | Auto-creates stub classes with `-PautoGenerateStubs` |
| **Test** | Runs `FixtureScanTest` against all fixture JARs |
| **Verify** | Confirms generated stubs match committed versions |

</div>
