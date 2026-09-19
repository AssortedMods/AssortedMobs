# Assorted Mobs

Adds ice pixies, treasure mimics, Bob-ombs and parabuzzies. These are the creatures of the Grim
World part of the old [Grim Pack](https://github.com/grim3212/grim-pack), brought forward from 1.12
and rebuilt on the modern APIs.

Requires [Assorted Lib](https://github.com/AssortedMods/AssortedLib). Branches are per Minecraft version; `26.2`
is the current one.

## Issue Reporting

Please include the following

* Minecraft version
* Loader and its version — NeoForge, or Fabric Loader together with Fabric API
* Assorted Mobs version
* Assorted Lib version
* The full `latest.log`, plus the crash report if the game crashed

## Building

JDK 25 and the bundled Gradle wrapper. `common/` holds the loader-agnostic code; both loader
modules compile those sources inline rather than depending on a common jar, so there is nothing to
install between them.

How the build works - the Minecraft and loader versions, the runs, the tests, publishing - lives in
[AssortedBuild](https://github.com/AssortedMods/AssortedBuild), pinned by `assortedbuild_version` in
`gradle.properties`. This repository only says what the mod is.

Assorted Lib is consumed as a Maven artifact. To build against an unreleased one, publish it first:

```bash
cd ../AssortedLib && ./gradlew publishToMavenLocal
```

Then from this repository:

```bash
./gradlew build                        # every module; jars land in <module>/build/libs
./gradlew :neoforge:runClient
./gradlew :fabric:runClient
./gradlew :neoforge:runGameTestServer  # headless gametests, non-zero exit on failure
./gradlew :fabric:runGameTest
./gradlew :fabric:runClientGameTest    # draws every creature in a real client; opens a game window
./gradlew :neoforge:runClientData      # datagen
./gradlew :neoforge:runServerData
```

Generated resources are committed. The NeoForge datagen writes them for both loaders; they are
regenerated, never hand-edited.

## License

[LGPL-3.0-only](LICENSE).
