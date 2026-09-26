# Assorted Mobs

Ice pixies, treasure mimics, Bob-ombs, parabuzzies, and four sea creatures: seals, walruses,
narwhals and sea otters. The first four are the creatures of the Grim World part of the old
[Grim Pack](https://github.com/grim3212/grim-pack), brought forward from 1.12. All of it is rebuilt on the
modern APIs.

Each part is its own mod, and Assorted Mobs is all of them in one download:

| Mod | Directory | What it adds |
| --- | --- | --- |
| [Assorted Mobs](mods/mobs) | `mods/mobs/` | Every one below, nested in one jar. No code of its own. |
| [Assorted Ice Pixie](mods/icepixie) | `mods/icepixie/` | The ice pixie. |
| [Assorted Treasure Mob](mods/treasuremob) | `mods/treasuremob/` | The treasure mob. |
| [Assorted 8-Bit Mobs](mods/eightbit) | `mods/eightbit/` | The parabuzzy and the Bob-omb. |
| [Assorted Sea Creatures](mods/seacreatures) | `mods/seacreatures/` | Seals, walruses, narwhals and sea otters. |

Whichever are installed share one Assorted Mobs creative tab, manual section and advancement tab.
Worlds from Assorted Mobs 1.x load in any of them.

Requires [Assorted Lib](https://github.com/AssortedMods/AssortedLib). Branches are per Minecraft version; `26.2`
is the current one.

## Issue Reporting

Please include the following

* Minecraft version
* Loader and its version — NeoForge, or Fabric Loader together with Fabric API
* Which of these mods, and their versions
* Assorted Lib version
* The full `latest.log`, plus the crash report if the game crashed

## Building

JDK 25 and the bundled Gradle wrapper. Each mod is a directory under `mods/`, laid out like a
one-mod repository: its own `gradle.properties`, `README.md` and `CHANGELOG.md`, and `common/`,
`fabric/`, `neoforge/`. `assorted_mods` in the root `gradle.properties` lists them; the root file
holds only what they share. `mods/mobs` is the bundle: `bundled_mods` in its `gradle.properties`
names the mods its jar carries.

How the build works - the Minecraft and loader versions, the runs, the tests, publishing - lives in
[AssortedBuild](https://github.com/AssortedMods/AssortedBuild), pinned by `assortedbuild_version` in
`gradle.properties`. This repository only says what the mods are.

Assorted Lib is consumed as a Maven artifact. To build against an unreleased one, publish it first:

```bash
cd ../AssortedLib && ./gradlew publishToMavenLocal
```

Then from this repository - a task named alone runs in every mod, `:<dir>:` picks one:

```bash
./gradlew build                                   # every mod; jars land in mods/<dir>/<module>/build/libs
./gradlew :seacreatures:neoforge:runClient        # one mod on its own
./gradlew :all:neoforge:runClient                 # every creature in one game (:all:fabric too)
./gradlew runGameTestServer                       # headless gametests on NeoForge, non-zero exit on failure
./gradlew runGameTest                             # and on Fabric
./gradlew :eightbit:fabric:runClientGameTest      # draws its creatures in a real client; opens a game window
./gradlew runClientData runServerData             # datagen
./gradlew :mobs:publishMods -PdryRun=true         # rehearse the bundle's release
```

Generated resources are committed. The NeoForge datagen writes them for both loaders; they are
regenerated, never hand-edited.

## License

[LGPL-3.0-only](LICENSE).
