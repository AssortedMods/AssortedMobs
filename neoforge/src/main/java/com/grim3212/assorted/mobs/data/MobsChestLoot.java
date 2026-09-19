package com.grim3212.assorted.mobs.data;

import com.grim3212.assorted.mobs.common.entity.TreasureMob;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.function.BiConsumer;

/**
 * What a treasure mob's chest is filled with when it appears. Inside a structure it is one of that
 * structure's own chests, found by {@link TreasureMob#chestLootFor}; anywhere else, from bread to
 * diamonds. Lives in the NeoForge module for the mod-loaded condition on Assorted World's tables,
 * which {@code CrossLoaderData} gives Fabric's spelling as well.
 */
public class MobsChestLoot implements LootTableSubProvider {

    private static final String ASSORTED_WORLD = "assortedworld";

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

        structure(output, BuiltinStructures.MINESHAFT, BuiltInLootTables.ABANDONED_MINESHAFT);
        structure(output, BuiltinStructures.MINESHAFT_MESA, BuiltInLootTables.ABANDONED_MINESHAFT);
        structure(output, BuiltinStructures.STRONGHOLD, BuiltInLootTables.STRONGHOLD_CORRIDOR, BuiltInLootTables.STRONGHOLD_CROSSING, BuiltInLootTables.STRONGHOLD_LIBRARY);
        // The chests and barrels about the halls, not the vaults' rewards, which take a key.
        structure(output, BuiltinStructures.TRIAL_CHAMBERS, BuiltInLootTables.TRIAL_CHAMBERS_CORRIDOR, BuiltInLootTables.TRIAL_CHAMBERS_SUPPLY,
                BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION, BuiltInLootTables.TRIAL_CHAMBERS_ENTRANCE);
        structure(output, BuiltinStructures.ANCIENT_CITY, BuiltInLootTables.ANCIENT_CITY);
        structure(output, BuiltinStructures.DESERT_PYRAMID, BuiltInLootTables.DESERT_PYRAMID);
        structure(output, BuiltinStructures.JUNGLE_TEMPLE, BuiltInLootTables.JUNGLE_TEMPLE);
        structure(output, BuiltinStructures.WOODLAND_MANSION, BuiltInLootTables.WOODLAND_MANSION);
        structure(output, BuiltinStructures.BASTION_REMNANT, BuiltInLootTables.BASTION_TREASURE, BuiltInLootTables.BASTION_OTHER,
                BuiltInLootTables.BASTION_BRIDGE, BuiltInLootTables.BASTION_HOGLIN_STABLE);
        structure(output, BuiltinStructures.FORTRESS, BuiltInLootTables.NETHER_BRIDGE);
        structure(output, BuiltinStructures.END_CITY, BuiltInLootTables.END_CITY_TREASURE);

        // The snowball has no chests, so a treasure mob there carries the usual loot.
        assortedWorld(output, "fountain", "chests/fountain");
        assortedWorld(output, "pyramid", "chests/pyramid");
        assortedWorld(output, "water_dome", "chests/water_dome/cobblestone", "chests/water_dome/iron", "chests/water_dome/glowstone", "chests/water_dome/obsidian");
    }

    /** One of {@code chests}, picked evenly, as if the mob had wandered off with one of them. */
    @SafeVarargs
    private static LootTable.Builder oneOf(ResourceKey<LootTable>... chests) {
        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1));
        for (ResourceKey<LootTable> chest : chests) {
            pool.add(NestedLootTable.lootTableReference(chest));
        }
        return LootTable.lootTable().withPool(pool);
    }

    @SafeVarargs
    private static void structure(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output, ResourceKey<Structure> structure, ResourceKey<LootTable>... chests) {
        output.accept(TreasureMob.chestLootFor(structure), oneOf(chests));
    }

    /** Only loaded with Assorted World, whose chest tables these draw from. */
    private static void assortedWorld(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output, String structure, String... chests) {
        ResourceKey<LootTable>[] tables = new ResourceKey[chests.length];
        for (int i = 0; i < chests.length; i++) {
            tables[i] = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(ASSORTED_WORLD, chests[i]));
        }
        ResourceKey<Structure> key = ResourceKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath(ASSORTED_WORLD, structure));
        output.accept(TreasureMob.chestLootFor(key), oneOf(tables).withCondition(new ModLoadedCondition(ASSORTED_WORLD)));
    }

    private static void entry(LootPool.Builder pool, Item item, int weight, int min, int max) {
        pool.add(LootItem.lootTableItem(item).setWeight(weight).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max))));
    }
}
