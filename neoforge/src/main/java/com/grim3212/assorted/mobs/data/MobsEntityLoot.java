package com.grim3212.assorted.mobs.data;

import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.item.MobsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.stream.Stream;

/**
 * What the creatures drop. The treasure mob and the Bob-omb have no table: one drops its chest, the
 * other drops nothing it was not already carrying. Lives in the NeoForge module because only
 * NeoForge's copy of the provider can be told to check this mod's creatures and no others.
 */
public class MobsEntityLoot extends EntityLootSubProvider {

    /** Not #minecraft:fishes, which has the cooked ones in it. */
    private static final TagKey<Item> RAW_FISH = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, "foods/raw_fish"));
    private static final TagKey<Item> FROST_RODS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, "rods/frost"));

    public MobsEntityLoot(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> Constants.MOD_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace()));
    }

    private LootTable.Builder upToTwo(Item item) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1)))));
    }

    /**
     * None to two of one kind of fish, more with looting, and cooked if what dropped it was on fire: what has nothing
     * to cook into comes as it is. Whatever is in #c:foods/raw_fish, which is where every mod puts its raw fish and both
     * loaders put vanilla's four: each as likely as the next, which is how a tag goes into a table.
     */
    private LootPool.Builder oceanFish() {
        return LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                .add(TagEntry.expandTag(RAW_FISH)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))));
    }

    @Override
    public void generate() {
        // Snowballs every time, ice now and then, the rarer kinds of ice more rarely still.
        this.add(MobsEntities.ICE_PIXIE.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.SNOWBALL)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1)))))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.5F, 0.1F))
                        .add(LootItem.lootTableItem(Items.ICE).setWeight(12).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        .add(LootItem.lootTableItem(Items.PACKED_ICE).setWeight(5))
                        .add(LootItem.lootTableItem(Items.BLUE_ICE).setWeight(1)))
                // Assorted Tools' frost rod, by its tag, so this is empty without Tools. Tools also
                // gives every monster killed in a snowy biome its own small chance at one.
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.05F, 0.02F))
                        .add(TagEntry.expandTag(FROST_RODS))));

        // The sea creatures. Every one of them is full of fish: any raw fish there is, by the common tag, so other mods'
        // fish turn up too. The otter's shells and the narwhal's horn, which are what those two are for, come on top of that.
        this.add(MobsEntities.SEAL.get(), LootTable.lootTable().withPool(this.oceanFish()));
        this.add(MobsEntities.WALRUS.get(), LootTable.lootTable().withPool(this.oceanFish()));
        this.add(MobsEntities.SEA_OTTER.get(), this.upToTwo(MobsItems.SEA_SHELL.get()).withPool(this.oceanFish()));
        this.add(MobsEntities.NARWHAL.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(MobsItems.NARWHAL_HORN.get())))
                .withPool(this.oceanFish()));

        // The red ones drop nothing at all; see Parabuzzy#shouldDropLoot.
        this.add(MobsEntities.PARABUZZY.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(MobsItems.PARABUZZY_SHELL.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))));
    }
}
