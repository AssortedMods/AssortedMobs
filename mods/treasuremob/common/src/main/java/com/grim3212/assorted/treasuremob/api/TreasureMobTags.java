package com.grim3212.assorted.treasuremob.api;

import com.grim3212.assorted.treasuremob.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.structure.Structure;

public class TreasureMobTags {

    public static class Items {
        /** What tames a treasure mob. */
        public static final TagKey<Item> TREASURE_MOB_TEMPT_ITEMS = create("treasure_mob_tempt_items");

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
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
