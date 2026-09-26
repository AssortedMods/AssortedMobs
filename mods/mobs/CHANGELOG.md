# Changelog

## 2.0.0

Assorted Mobs is now four mods, each published on its own, and this one carries all four.

- Assorted Ice Pixie, Assorted Treasure Mob, Assorted 8-Bit Mobs and Assorted Sea Creatures can be
  installed on their own. This mod is all of them in one jar, as before.
- The part switches in `assortedmobs-common.toml` are gone. To leave a creature out, install the
  mods you want instead of this one.
- Creatures, items and sounds have new ids, `assorted<part>:<name>`. Worlds from 1.x load them
  unchanged, whether with this mod or the ones it carries.
- Recipes, advancements and tags moved to the new ids too. A player's recipe book and advancement
  progress from 1.x carry over; `migration.carryOverMovedIds` in `assortedlib-common.toml` turns
  that off.
- Requires Assorted Lib 4.3.0.

## 1.0.0

Grim Pack's creatures from its Grim World part, ported from 1.12 and modernized. The ice pixie, the
treasure mob, and the two 8-bit mobs, the parabuzzy and the Bob-omb.

- Parabuzzies and Bob-ombs still sit on your head, though nothing may ride a player any more. They
  are pinned there instead, and sneaking on the ground puts them down.
- Sea Creatures seals, walruses, narwhals, and sea otters
- Requires Assorted Lib 4.2.0.
