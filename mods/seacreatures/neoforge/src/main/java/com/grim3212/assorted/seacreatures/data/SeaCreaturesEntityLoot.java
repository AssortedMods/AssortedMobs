package com.grim3212.assorted.seacreatures.data;

import com.grim3212.assorted.seacreatures.Constants;
import com.grim3212.assorted.seacreatures.common.entity.SeaCreaturesEntities;
import com.grim3212.assorted.seacreatures.common.item.SeaCreaturesItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.stream.Stream;

/**
 * What this part's creatures drop. Lives in the NeoForge module because only NeoForge's copy of the provider can be
 * told to check this mod's creatures and no others.
 */
public class SeaCreaturesEntityLoot extends EntityLootSubProvider {

    public SeaCreaturesEntityLoot(LootTableSubProvider.Context context) {
        super(FeatureFlags.REGISTRY.allFlags(), context);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> Constants.MOD_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace()));
    }

    private LootTable.Builder upToTwo(Item item) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1))
                .add(LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(ContextIntProviders.between(0, 2)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.enchantments, ContextFloatProviders.between(0.0F, 1.0F)))));
    }

    /**
     * None to two of one kind of fish, more with looting, and cooked if what dropped it was on fire. The fish are named
     * one by one, at the given weights, the way the polar bear's are: not by #c:foods/raw_fish, which has the pufferfish
     * and the tropical fish in it, and no seal ever came up with one of those.
     */
    private LootPool.Builder fishOf(LootPoolEntryContainer.Builder<?>... fish) {
        LootPool.Builder pool = LootPool.lootPool().setRolls(ContextIntProviders.exactly(1));
        for (LootPoolEntryContainer.Builder<?> entry : fish) {
            pool.add(entry);
        }
        return pool;
    }

    private UniformContainerBase.Builder<?> fish(Item fish, int weight) {
        return LootItem.lootTableItem(fish).setWeight(weight)
                .apply(SetItemCountFunction.setCount(ContextIntProviders.between(0, 2)))
                .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.enchantments, ContextFloatProviders.between(0.0F, 1.0F)));
    }

    @Override
    public void generate() {
        // Each full of the fish of its own waters. The two of the ice mostly cod, as the polar bear is; the walrus, which
        // lives off the sea floor, cod alone; the otter, which is in the rivers too, salmon first. The otter's shells and
        // the narwhal's horn, which are what those two are for, come on top of that.
        this.add(SeaCreaturesEntities.SEAL.get(), LootTable.lootTable().withPool(this.fishOf(this.fish(Items.COD, 3), this.fish(Items.SALMON, 1))));
        this.add(SeaCreaturesEntities.WALRUS.get(), LootTable.lootTable().withPool(this.fishOf(this.fish(Items.COD, 1))));
        this.add(SeaCreaturesEntities.SEA_OTTER.get(), this.upToTwo(SeaCreaturesItems.SEA_SHELL.get()).withPool(this.fishOf(this.fish(Items.SALMON, 2), this.fish(Items.COD, 1))));
        this.add(SeaCreaturesEntities.NARWHAL.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).add(LootItem.lootTableItem(SeaCreaturesItems.NARWHAL_HORN.get())))
                .withPool(this.fishOf(this.fish(Items.COD, 3), this.fish(Items.SALMON, 1))));
    }
}
