# AGENTS.md

## Project Purpose

`addon-parser` is a headless Java scanner that emulates enough of Meteor Client to load addon jars, run addon initialization, and export module/setting data as JSON.

The design goal is CI-friendly execution with no Minecraft runtime or GUI dependencies.

## What Has Been Built

1. **Addon scanner runtime**
   - Entry: `src/main/java/com/cope/addonparser/cli/Main.java`
   - Core scan logic: `src/main/java/com/cope/addonparser/scanner/AddonScanner.java`
   - Isolated scan logic: `src/main/java/com/cope/addonparser/scanner/IsolatedScanner.java`
   - Output models: `src/main/java/com/cope/addonparser/model/*`

2. **Profile-aware Meteor emulation layer**
   - Shared Meteor systems/settings/hud/addon classes under:
     - `src/main/java/meteordevelopment/*`
   - 26x/Mojmap compatibility shims under:
     - `src/profile-26x/java/**`
   - Legacy 1.21.x/Yarn compatibility shims under:
     - `src/profile-legacy/java/**`
   - Gradle selects profile sources with `-PparserProfile=26x|legacy`.

3. **Stub generation (Java, not Python)**
   - Generator: `src/stubgen/java/com/cope/addonparser/tools/StubGenerator.java`
   - Generated sources: `src/generated/java/**`
   - Manual class exclusion list: `tools/manual_classes.txt`
   - Gradle task: `generateStubs` (JavaExec), profile-specific via fixture dir + shim source dir.
   - Nested JVM names such as `Outer$Inner` are emitted as nested Java source types inside `Outer.java` unless covered by handwritten manual classes.

4. **Compatibility classloading**
   - `src/main/java/com/cope/addonparser/util/ChildFirstClassLoader.java`
   - Handles legacy package/class descriptor drift generically.
   - Avoids addon-specific hardcoded bandaids.

5. **Value normalization + mappings**
   - `src/main/java/com/cope/addonparser/util/ValueNormalizer.java`
   - Profile resolver factory classes under `src/profile-26x/java/...` and `src/profile-legacy/java/...`
   - Legacy profile can use Yarn mappings for intermediary symbols.
   - 26x profile is Mojmap-oriented and mostly identity mapping.
   - Exports stable JSON-friendly values and prevents `ClassName@hex` leakage.

6. **Fixtures + regression tests**
   - Fixture layout helper: `src/test/java/com/cope/addonparser/FixtureLayout.java`
   - Fixture tests: `src/test/java/com/cope/addonparser/FixtureScanTest.java` and `src/test/java/com/cope/addonparser/IsolatedScannerTest.java`
   - 26x fixture jars live at: `fixtures/addons/jars/26x` (9 jars)
   - Legacy fixture jars live at: `fixtures/addons/jars/legacy` (24 jars)
   - Current baseline: both fixture profiles scan successfully.

7. **Runtime side-effect containment**
   - Scanner sandboxes addon runtime writes under `tmp/addon-parser-runtime/*` per scan and cleans artifacts after each scan.
   - Includes post-scan cleanup passes to catch delayed async writes from addon init threads.

8. **Code quality tooling**
   - Java toolchain: 25
   - Gradle wrapper: 9.4.1
   - Spotless: 8.4.0
   - google-java-format: 1.35.0
   - Spotless is ratcheted from `master` to avoid repo-wide formatting churn.

## Build and Run

Use the Gradle wrapper when possible.

1. **Run 26x tests**
   - `./gradlew test -PautoGenerateStubs -PparserProfile=26x --no-daemon`

2. **Run legacy tests**
   - `./gradlew test -PautoGenerateStubs -PparserProfile=legacy --no-daemon`

3. **Run 26x fixture test with debug output**
   - `./gradlew test --tests com.cope.addonparser.FixtureScanTest -PautoGenerateStubs -PparserProfile=26x --rerun-tasks --no-daemon --info --stacktrace`

4. **Run legacy fixture test with debug output**
   - `./gradlew test --tests com.cope.addonparser.FixtureScanTest -PautoGenerateStubs -PparserProfile=legacy --rerun-tasks --no-daemon --info --stacktrace`

5. **Generate 26x JSON output**
   - `./gradlew run -PautoGenerateStubs -PparserProfile=26x --no-daemon --args="--input fixtures/addons/jars/26x --output output/poc-scan-26x --summary output/poc-scan-26x/summary.json --profile 26x"`

6. **Generate legacy JSON output**
   - `./gradlew run -PautoGenerateStubs -PparserProfile=legacy --no-daemon --args="--input fixtures/addons/jars/legacy --output output/poc-scan-legacy --summary output/poc-scan-legacy/summary.json --profile legacy"`

7. **Format check**
   - `./gradlew spotlessCheck --no-daemon`

## Mapping Behavior

`MappingProfile` selects profile behavior from `addonparser.profile`, Gradle's `-PparserProfile`, or the CLI `--profile` argument.

Profile names:

1. `26x` (default)
   - Mojmap-oriented profile.
   - Uses `src/profile-26x/java` compatibility sources.
   - Uses fixture jars from `fixtures/addons/jars/26x`.

2. `legacy`
   - 1.21.x/Yarn/intermediary profile.
   - Uses `src/profile-legacy/java` compatibility sources.
   - Uses fixture jars from `fixtures/addons/jars/legacy`.
   - Can load Yarn mappings from `mappings` and explicit mapping jars.

Useful system properties:

1. `addonparser.profile` (`26x` default)
2. `addonparser.fixtureJarsDir` (test override, default `fixtures/addons/jars/<profile>`)
3. `addonparser.yarnAutoDownload` (`true` default, legacy mapping behavior)
4. `addonparser.mappingsDir` (default `mappings`)
5. `addonparser.addonsSourceDir` (default `ai_reference/addons`, optional local reference context)
6. `addonparser.yarnMappingsVersions` (comma/semicolon/path-separator separated version list)
7. `addonparser.yarnMappingsJar` (explicit jar path list to load)

## Runtime Sandbox Properties

1. `addonparser.runtimeTmpDir` (default `tmp/addon-parser-runtime`)
2. `addonparser.keepTmp` (`false` default; set `true` to keep per-scan sandbox artifacts for debugging)
3. `addonparser.meteorFolder` (optional initial override for `MeteorClient.FOLDER`; scanner normally sets this per scan automatically)

## Invariants to Preserve

1. **No Python in project internals**
   - Build/runtime internals must remain Java.
   - Python scripts may exist only as optional workspace helpers.
   - There is no project fixture downloader task anymore.

2. **Fixtures are checked in by profile**
   - 26x fixture jars live under `fixtures/addons/jars/26x`.
   - Legacy fixture jars live under `fixtures/addons/jars/legacy`.
   - Do not reintroduce runtime fixture downloading into CI without a deliberate design change.

3. **Use `ai_reference/` only as local source context**
   - `ai_reference/` may contain cloned third-party repos and Meteor source context.
   - It is gitignored and not part of fixture acquisition or CI.
   - Treat it as useful local reference material when present, not committed project data.

4. **No addon-specific hacks**
   - Compatibility should be generic (descriptor/type adaptation, API parity, stub hierarchy), not package-name specific patches.

5. **Generated stubs must not overwrite manual classes**
   - Any handwritten compatibility class must be listed in `tools/manual_classes.txt` or otherwise included through manual source dirs.

6. **JSON must be stable and human-usable**
   - Avoid raw object identities (`@hex`).
   - Prefer enum names/symbol names and structured objects.

7. **Fixture tests are the regression gate**
   - If either profile fails, fix compatibility/emulation until it passes.
   - Always rerun both profile test commands before committing cross-profile changes.

## When Adding/Changing Compatibility

1. Reproduce with the relevant profile fixture test.
2. Prefer:
   - emulation API parity in manual Meteor classes
   - generic loader descriptor reconciliation
   - generic stub hierarchy/interface overrides
   - profile-specific shims in `src/profile-26x/java` or `src/profile-legacy/java`
3. Avoid one-off addon routing rules unless absolutely impossible.
4. Re-run:
   - `./gradlew spotlessCheck --no-daemon`
   - `./gradlew test -PautoGenerateStubs -PparserProfile=26x --no-daemon`
   - `./gradlew test -PautoGenerateStubs -PparserProfile=legacy --no-daemon`
   - profile-specific JSON generation if behavior changed
5. Spot-check output JSON for:
   - module count regressions
   - setting serialization quality
   - mapping resolution quality

## Known Practical Notes

1. File size differences between versioned addon JSONs can be legitimate if module sets differ by jar version.
2. `src/generated/java` is profile-specific; regenerate it with the profile you are compiling/testing.
3. `compileJava` only depends on `generateStubs` when `-PautoGenerateStubs` is set.
4. CI uses checked-in fixture jars and no longer downloads or clones fixtures.
5. `fixtures/addons/jars/legacy/release-summary.*` may exist locally as old provenance artifacts; they are not required for tests.
