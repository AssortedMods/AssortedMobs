package com.grim3212.assorted.seacreatures.api;

import com.grim3212.assorted.seacreatures.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class SeaCreaturesTags {

    public static class Blocks {
        /** What a seal or a walrus will spawn on: ice and snow, and whatever other animals spawn on. */
        public static final TagKey<Block> SEALS_SPAWNABLE_ON = create("seals_spawnable_on");

        private static TagKey<Block> create(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Items {
        /** What seals and walruses follow and breed for. */
        public static final TagKey<Item> SEAL_FOOD = create("seal_food");
        public static final TagKey<Item> REPAIRS_SHELL_ARMOR = create("repairs_shell_armor");

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class EntityTypes {
        /** Seals, walruses and polar bears: the spawn habits count these together, so many of any on the ice at once. */
        public static final TagKey<EntityType<?>> ICE_HERD = create("ice_herd");

        private static TagKey<EntityType<?>> create(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Biomes {
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
}
