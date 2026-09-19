package com.grim3212.assorted.mobs.data;

import com.grim3212.assorted.mobs.common.entity.TreasureMob;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

/** What a treasure mob's chest is filled with when it appears, from bread to diamonds. */
public class MobsChestLoot implements LootTableSubProvider {

    public MobsChestLoot(HolderLookup.Provider registries) {
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        LootPool.Builder pool = LootPool.lootPool().setRolls(UniformGenerator.between(3, 5));
        entry(pool, Items.ARROW, 2, 1, 3);
        entry(pool, Items.GOLD_INGOT, 2, 1, 4);
        entry(pool, Items.APPLE, 2, 1, 4);
        entry(pool, Items.SLIME_BALL, 2, 1, 5);
        entry(pool, Items.SADDLE, 1, 1, 1);
        entry(pool, Items.CAKE, 2, 1, 1);
        entry(pool, Items.COOKIE, 2, 1, 4);
        entry(pool, Items.DIAMOND, 1, 1, 2);
        entry(pool, Items.STRING, 2, 1, 3);
        entry(pool, Items.IRON_INGOT, 2, 1, 4);
        entry(pool, Items.NAME_TAG, 1, 1, 2);
        entry(pool, Items.WHEAT, 2, 1, 4);
        entry(pool, Items.BREAD, 2, 1, 3);
        entry(pool, Items.COAL, 2, 1, 5);
        entry(pool, Items.REDSTONE, 1, 1, 8);

        output.accept(TreasureMob.CHEST_LOOT, LootTable.lootTable().withPool(pool));
    }

    private static void entry(LootPool.Builder pool, Item item, int weight, int min, int max) {
        pool.add(LootItem.lootTableItem(item).setWeight(weight).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max))));
    }
}
