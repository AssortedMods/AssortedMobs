package com.grim3212.assorted.icepixie.data;

import com.grim3212.assorted.icepixie.Constants;
import com.grim3212.assorted.icepixie.api.IcePixieTags;
import com.grim3212.assorted.icepixie.common.entity.IcePixieEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.stream.Stream;

/**
 * What this part's creatures drop. Lives in the NeoForge module because only NeoForge's copy of the provider can be
 * told to check this mod's creatures and no others.
 */
public class IcePixieEntityLoot extends EntityLootSubProvider {

    public IcePixieEntityLoot(LootTableSubProvider.Context context) {
        super(FeatureFlags.REGISTRY.allFlags(), context);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> Constants.MOD_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace()));
    }

    @Override
    public void generate() {
        // Snowballs every time, ice now and then, the rarer kinds of ice more rarely still.
        this.add(IcePixieEntities.ICE_PIXIE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(Items.SNOWBALL)
                                .apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 3)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.enchantments, ContextFloatProviders.between(0.0F, 1.0F)))))
                .withPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1))
                        .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.enchantments, 0.5F, 0.1F))
                        .add(LootItem.lootTableItem(Items.ICE).setWeight(12).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 2))))
                        .add(LootItem.lootTableItem(Items.PACKED_ICE).setWeight(5))
                        .add(LootItem.lootTableItem(Items.BLUE_ICE).setWeight(1)))
                // Assorted Tools' frost rod, by its tag, so this is empty without Tools. Tools also
                // gives every monster killed in a snowy biome its own small chance at one.
                .withPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.enchantments, 0.05F, 0.02F))
                        .add(TagEntry.expandTag(this.items.getOrThrow(IcePixieTags.Items.FROST_RODS)))));
    }
}
