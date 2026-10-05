package com.grim3212.assorted.eightbit.api;

import com.grim3212.assorted.eightbit.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;

public class EightBitTags {

    public static class Items {
        /** What tames a parabuzzy. */
        public static final TagKey<Item> PARABUZZY_TAME_ITEMS = create("parabuzzy_tame_items");

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> SPAWNS_PARABUZZIES = create("spawns_parabuzzies");

        private static TagKey<Biome> create(String name) {
            return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }
}
