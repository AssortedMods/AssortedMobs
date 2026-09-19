package com.grim3212.assorted.mobs.data;

import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.item.MobsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.stream.Stream;

/**
 * What the creatures drop. The treasure mob and the Bob-omb have no table: one drops its chest, the
 * other drops nothing it was not already carrying. Lives in the NeoForge module because only
 * NeoForge's copy of the provider can be told to check this mod's creatures and no others.
 */
public class MobsEntityLoot extends EntityLootSubProvider {

    public MobsEntityLoot(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> Constants.MOD_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace()));
    }

    @Override
    public void generate() {
        this.add(MobsEntities.ICE_PIXIE.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 4))
                .add(LootItem.lootTableItem(Items.COD).setWeight(1))
                .add(LootItem.lootTableItem(Items.ICE).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                .add(LootItem.lootTableItem(Items.STICK).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))));

        // The red ones drop nothing at all; see Parabuzzy#shouldDropLoot.
        this.add(MobsEntities.PARABUZZY.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(MobsItems.PARABUZZY_SHELL.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))));
    }
}
