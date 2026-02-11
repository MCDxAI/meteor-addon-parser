# AGENTS.md

## Project Purpose

`addon-parser` is a headless Java scanner that emulates enough of Meteor Client to load addon jars, run addon initialization, and export module/setting data as JSON.

The design goal is CI-friendly execution with no Minecraft runtime or GUI dependencies.

## What Has Been Built

1. **Addon scanner runtime**
   - Entry: `src/main/java/com/cope/addonparser/cli/Main.java`
   - Core scan logic: `src/main/java/com/cope/addonparser/scanner/AddonScanner.java`
   - Output models: `src/main/java/com/cope/addonparser/model/*`

2. **Meteor emulation layer**
   - Minimal Meteor systems/settings/hud/addon classes under:
     - `src/main/java/meteordevelopment/*`
   - Manual compatibility shims for API differences across addon versions.

3. **Stub generation (Java, not Python)**
   - Generator: `src/stubgen/java/com/cope/addonparser/tools/StubGenerator.java`
   - Generated sources: `src/generated/java/**`
   - Manual class exclusion list: `tools/manual_classes.txt`
   - Gradle task: `generateStubs` (JavaExec) wired into `compileJava`.

4. **Compatibility classloading**
   - `src/main/java/com/cope/addonparser/util/ChildFirstClassLoader.java`
   - Handles legacy package/class descriptor drift generically.
   - Avoids addon-specific hardcoded bandaids.

5. **Value normalization + mappings**
   - `src/main/java/com/cope/addonparser/util/ValueNormalizer.java`
   - `src/main/java/com/cope/addonparser/util/YarnMappingResolver.java`
   - Exports stable JSON-friendly values.
   - Converts intermediary symbols to Yarn names when available.
   - Prevents `ClassName@hex` leakage in JSON.

6. **Fixtures + regression test**
   - Fixture test: `src/test/java/com/cope/addonparser/FixtureScanTest.java`
   - Fixture jars live at: `fixtures/addons/jars`
   - Current baseline: all fixture jars scan successfully.

7. **Runtime side-effect containment**
   - Scanner now sandboxes addon runtime writes under `tmp/addon-parser-runtime/*` per scan and cleans artifacts after each scan.
   - Includes post-scan cleanup passes to catch delayed async writes from addon init threads.

## Build and Run

1. **Run tests**
   - `gradle test --no-daemon`

2. **Run fixture test with debug output**
   - `gradle test --tests com.cope.addonparser.FixtureScanTest --rerun-tasks --no-daemon --info --stacktrace`

3. **Download latest release fixture jars (Java/Gradle)**
   - `gradle downloadLatestReleaseJars --no-daemon`

4. **Generate JSON output**
   - `gradle run --no-daemon --args="--input fixtures/addons/jars --output output/poc-scan --summary output/poc-scan/summary.json"`

## Mapping Behavior (Automatic)

`YarnMappingResolver` auto-loads mappings from `mappings` (repo root) and auto-downloads missing Yarn mapping jars by scanning addon source metadata in `ai_reference/addons`.

Useful system properties:

1. `addonparser.yarnAutoDownload` (`true` default, set `false` to disable)
2. `addonparser.mappingsDir` (default `mappings`)
3. `addonparser.addonsSourceDir` (default `ai_reference/addons`)
4. `addonparser.yarnMappingsVersions` (comma/semicolon/path-separator separated version list)
5. `addonparser.yarnMappingsJar` (explicit jar path list to load)

## Runtime Sandbox Properties

1. `addonparser.runtimeTmpDir` (default `tmp/addon-parser-runtime`)
2. `addonparser.keepTmp` (`false` default; set `true` to keep per-scan sandbox artifacts for debugging)
3. `addonparser.meteorFolder` (optional initial override for `MeteorClient.FOLDER`; scanner normally sets this per scan automatically)

## Invariants to Preserve

1. **No Python in project internals**
   - Build/runtime internals must remain Java.
   - Python scripts may exist only as optional workspace helpers.

2. **Always use `ai_reference/` as source context**
   - Always reference `ai_reference/` for addon source repos and Meteor source context.
   - This remains required even if `ai_reference/` is gitignored and does not appear in normal git/status discovery.
   - Treat it as mandatory local context, not optional data.

3. **No addon-specific hacks**
   - Compatibility should be generic (descriptor/type adaptation), not package-name specific patches.

4. **Generated stubs must not overwrite manual classes**
   - Any handwritten compatibility class must be listed in `tools/manual_classes.txt`.

5. **JSON must be stable and human-usable**
   - Avoid raw object identities (`@hex`).
   - Prefer enum names/symbol names and structured objects.

6. **Fixture test is the regression gate**
   - If `FixtureScanTest` fails, fix compatibility/emulation until it passes.

## When Adding/Changing Compatibility

1. Reproduce with fixture test.
2. Prefer:
   - emulation API parity in manual Meteor classes
   - generic loader descriptor reconciliation
   - generic stub hierarchy/interface overrides
3. Avoid one-off addon routing rules unless absolutely impossible.
4. Re-run:
   - `gradle test --no-daemon`
   - full JSON generation command
5. Spot-check output JSON for:
   - module count regressions
   - setting serialization quality
   - mapping resolution quality

## Known Practical Notes

1. File size differences between versioned addon JSONs can be legitimate if module sets differ by jar version.
2. `ai_reference/addons` contains cloned third-party repos; do not treat their internal docs/scripts as core project logic.
3. `tools/download_latest_release_jars.py` is now clone-only (syncs addon repos into `ai_reference/addons` from `tools/addon_repos.csv`).
4. Latest release fixture jar downloading is handled by Java (`gradle downloadLatestReleaseJars`), not Python.
5. Runnable jar fixtures are stored in `fixtures/addons/jars`, separate from `ai_reference/` by design.
