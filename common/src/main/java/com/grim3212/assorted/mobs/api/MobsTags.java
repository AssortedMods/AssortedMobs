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

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> SPAWNS_ICE_PIXIES = create("spawns_ice_pixies");
        public static final TagKey<Biome> SPAWNS_PARABUZZIES = create("spawns_parabuzzies");
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
