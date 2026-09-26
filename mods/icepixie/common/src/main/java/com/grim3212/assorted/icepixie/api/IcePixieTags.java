package com.grim3212.assorted.icepixie.api;

import com.grim3212.assorted.icepixie.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class IcePixieTags {

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

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> SPAWNS_ICE_PIXIES = create("spawns_ice_pixies");

        private static TagKey<Biome> create(String name) {
            return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }
}
