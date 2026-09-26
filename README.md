# Assorted Mobs

Adds an assortment of mobs to the game. Each group of mobs is also its own mod if you only want some of them.

- [Assorted Mobs](mods/mobs) has all of them in one download
- [Assorted Ice Pixie](mods/icepixie) adds ice pixies
- [Assorted Treasure Mob](mods/treasuremob) adds treasure mobs
- [Assorted 8-Bit Mobs](mods/eightbit) adds parabuzzies and Bob-ombs
- [Assorted Sea Creatures](mods/seacreatures) adds seals, walruses, narwhals and sea otters

Worlds made with Assorted Mobs 1.x work with any of these.

Requires [Assorted Lib](https://github.com/AssortedMods/AssortedLib). Branches are per Minecraft version and `26.2` is the current one.

## Issue Reporting

Please include the following

* Minecraft version
* NeoForge version, or Fabric Loader and Fabric API versions
* Which of these mods you have and their versions
* Assorted Lib version
* The full `latest.log`, and the crash report if the game crashed

## Building

You need JDK 25. Each mod is its own folder under `mods`. The build setup comes from
[AssortedBuild](https://github.com/AssortedMods/AssortedBuild) and `assortedbuild_version` in `gradle.properties`
picks the version.

To build against a local copy of Assorted Lib, publish it first.

```bash
cd ../AssortedLib && ./gradlew publishToMavenLocal
```

Some useful commands

```bash
./gradlew build                                  # build every mod
./gradlew :seacreatures:neoforge:runClient            # run one mod
./gradlew :all:neoforge:runClient                # run every mob together
./gradlew runGameTestServer runGameTest          # gametests on NeoForge and Fabric
./gradlew runClientData runServerData            # datagen
```

## License

[LGPL-3.0-only](LICENSE).
