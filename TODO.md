# TODO: Maven build review

Review of all 12 `pom.xml` files, the CI workflow and `build.sh`, done 2026-11-08.

Two problems were confirmed by running them:

- **The Checkstyle step can never fail.** It reports "0 violations" because every Google-style rule is only a warning. Counting warnings too, the 11 modules have about 20,000.
- **Building one module from its own folder fails.** `cd FormatBMP && mvn verify` stops with `Could not find resource …/FormatBMP/spotbugs-exclude-filter.xml`.

## What each POM does

**Root `pom.xml`**: the parent for all 11 modules (`packaging=pom`).

- **Project settings:** Java 25 (`maven.compiler.release`), UTF-8, a fixed `project.build.outputTimestamp` for reproducible jars, and `${revision}=3.7` as the version, made build-friendly by `flatten-maven-plugin`.
- **Dependency for every module:** `spotbugs-annotations`, compile-time only (`provided`).
- **Build steps, in order:**
  - **validate:** the enforcer requires Maven 3.9+ and JDK 25+. Checkstyle runs with Google rules.
  - **initialize:** git-commit-id writes `git.properties`.
  - **compile:** strict lint (`-Xlint:all,-requires-automatic`, deprecation warnings on).
  - **package:** the jar manifest gets a Versions section (version, git revision, branch), from buildnumber-maven-plugin. Every module also builds a javadoc jar (private members, UML diagrams) and a sources jar. jdeps checks that no JDK internals are used.
  - **verify:** SpotBugs, with failures turned on.
  - **Declared but inactive:** PMD (declared with no execution, so it never runs in a build), a PDF of the site (site phase only), and an explicit deploy step.
- **Site reports:** project info, combined javadoc, cross-referenced source (JXR), Checkstyle, SpotBugs, PMD with copy-paste detection (CPD), and Surefire test results.
- **Module list**, grouped as ready / in development / on hold.

**Library modules** (CommonLib and the 8 `Format*`): almost identical small POMs. Each sets an artifactId, its own version (BMP 1.1, CLASS 25.0, DEX 0.8 and so on) and depends on CommonLib. DEX also depends on FormatCLASS. CLASS, PDF and Web add JUnit for tests. CommonLib also adds an `Automatic-Module-Name` to its manifest.

**`BinaryInternalsViewer/pom.xml`** and **`BinaryInternalsWeb/pom.xml`**: the two applications.

- Each depends on all 8 format modules, listed with hard-coded versions.
- It copies its runtime jars to `target/libs`, writes a runnable manifest (main class plus a `libs/` class path) and zips everything into `/dist` with the assembly plugin.
- The Viewer's jar name includes a build timestamp.

## Suggestions, most important first

### 1. Make Checkstyle check something, or remove it

- [x] Adopt a ruleset that fails the build: the bundled `openjdk_checks.xml` (OpenJDK Java Style), used unchanged, with project exceptions in `checkstyle-suppressions.xml`.
- [x] Fix the temporary baseline (`EmptyLineSeparator`, `DeclarationOrder`, lines over 160 characters); no baseline is left.
- [ ] Decode the ICC profile header flags, device attributes and rendering intent (`FormatJPEG` `icc/Header.java`, bytes 44 - 67). They were `TODO` comments, which `openjdk_checks.xml` does not allow.

Permanent exceptions, each explained in `checkstyle-suppressions.xml`:

- Naming checks: names follow the file format specifications (`InnerClasses_attribute`, `u4`, `tableswitch`).
- `UnusedImports`: Checkstyle does not see references in Markdown doc comments (`///`, JEP 467).
- `NeedBraces` for arrow switch cases only (`case X -> value;`); braces stay required for `if`, `else`, `for`, `while` and `do`.
- `LineLength`: the limit is 160 instead of 100, by suppressing the messages for 101 - 160 characters.

### 2. Fix building a single module

- [ ] Add an empty `.mvn/` directory at the root, or better, add the Maven Wrapper (`mvn wrapper:wrapper`).

SpotBugs and Checkstyle find their files (`spotbugs-exclude-filter.xml`, `checkstyle-suppressions.xml`) through `${maven.multiModuleProjectDirectory}`. That is the directory where Maven started unless a `.mvn/` folder marks the project root, and this repo has none. The wrapper also pins the Maven version for CI and contributors.

### 3. CI improvements in `.github/workflows/ci.yml`

- [ ] Also run on `push` to `master`, not only pull requests.
- [ ] Add `cache: maven` to `setup-java` to cache downloaded dependencies.

### 4. `build.sh`

- [ ] `mvn dependency:purge-local-repository` re-downloads everything on every build, which is slow and rarely needed. Make it a flag.
- [ ] `versions:display-dependency-updates` lists betas and milestones. Add a small versions rules file in the POM to filter them out.

### 5. Cosmetic

- [ ] **Naming:** artifact IDs are mixed (`BinaryInternalsViewer` vs `binaryinternals.format.bmp`).
- [ ] **Descriptions:** the Viewer and Web POMs have none.
- [ ] **Whitespace:** trailing spaces in the ELF and JPEG POMs, and a line-wrapped `<outputDirectory>` in the Viewer.
- [ ] **Outside the POMs:** a `jvnet-nexus` certificate warning appears in every build. It doesn't come from these POMs (nor from `~/.m2/settings.xml`), so it's probably a repository declared inside some third-party dependency's POM. Harmless.

Suggested order: start with 2. It is a mechanical change the existing build checks can verify. The Checkstyle baseline in item 1 can be worked off one rule at a time.
