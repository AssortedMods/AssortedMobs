package com.grim3212.assorted.mobs.api;

import com.grim3212.assorted.mobs.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

public class MobsTags {

    public static class Blocks {
        /** Ice pixies take damage near these and run from them. */
        public static final TagKey<Block> ICE_PIXIE_REPELLENTS = create("ice_pixie_repellents");
        /** What a seal or a walrus will spawn on: ice and snow, and whatever other animals spawn on. */
        public static final TagKey<Block> SEALS_SPAWNABLE_ON = create("seals_spawnable_on");

        private static TagKey<Block> create(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Items {
        /** What a player has to be holding to hurt an ice pixie. */
        public static final TagKey<Item> ICE_PIXIE_WEAPONS = create("ice_pixie_weapons");
        /** What tames a treasure mob. */
        public static final TagKey<Item> TREASURE_MOB_TEMPT_ITEMS = create("treasure_mob_tempt_items");
        /** What tames a parabuzzy. */
        public static final TagKey<Item> PARABUZZY_TAME_ITEMS = create("parabuzzy_tame_items");
        /** What seals and walruses follow and breed for. */
        public static final TagKey<Item> SEAL_FOOD = create("seal_food");
        public static final TagKey<Item> REPAIRS_SHELL_ARMOR = create("repairs_shell_armor");

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> SPAWNS_ICE_PIXIES = create("spawns_ice_pixies");
        public static final TagKey<Biome> SPAWNS_PARABUZZIES = create("spawns_parabuzzies");
        public static final TagKey<Biome> SPAWNS_SEALS = create("spawns_seals");
        public static final TagKey<Biome> SPAWNS_WALRUSES = create("spawns_walruses");
        /** Where a walrus turns up now and then, away from the ice: any beach or rocky shore. One of its own biomes that is also here counts as its own. */
        public static final TagKey<Biome> SPAWNS_WALRUSES_RARELY = create("spawns_walruses_rarely");
        public static final TagKey<Biome> SPAWNS_NARWHALS = create("spawns_narwhals");
        public static final TagKey<Biome> SPAWNS_SEA_OTTERS = create("spawns_sea_otters");

        private static TagKey<Biome> create(String name) {
            return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Structures {
        /** Treasure mobs spawn only inside these, wherever one of their pieces is. */
        public static final TagKey<Structure> SPAWNS_TREASURE_MOBS = create("spawns_treasure_mobs");

        private static TagKey<Structure> create(String name) {
            return TagKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }
}
