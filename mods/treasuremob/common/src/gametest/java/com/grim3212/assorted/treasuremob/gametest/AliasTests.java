package com.grim3212.assorted.treasuremob.gametest;

import com.google.gson.JsonParser;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.treasuremob.Constants;
import com.grim3212.assorted.treasuremob.common.entity.TreasureMobEntities;
import com.grim3212.assorted.treasuremob.common.item.TreasureMobItems;
import com.mojang.serialization.JsonOps;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** A world saved when this was all one mod, Assorted Mobs, still has these creatures, items and sounds in it. */
final class AliasTests {

    private AliasTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("assortedmobs_ids_still_load", AliasTests::assortedmobsIdsStillLoad);
    }

    private static void assortedmobsIdsStillLoad(GameTestHelper helper) {
        for (IRegistryObject<EntityType<?>> entity : TreasureMobEntities.ENTITIES.getEntries()) {
            // The id a chunk saved an entity under, read back the way the world reads it.
            CompoundTag saved = new CompoundTag();
            saved.putString("id", old(entity.getId()).toString());
            helper.assertValueEqual(EntityType.by(saved).orElse(null), entity.get(),
                    "the entity saved as " + old(entity.getId()));
        }

        for (IRegistryObject<Item> item : TreasureMobItems.ITEMS.getEntries()) {
            ItemStack stack = ItemStack.CODEC.parse(helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE),
                    JsonParser.parseString("{\"id\": \"" + old(item.getId()) + "\", \"count\": 1}")).getOrThrow();
            helper.assertTrue(stack.is(item.get()), "a stack saved as " + old(item.getId()) + " reads back as " + stack);
        }
        helper.succeed();
    }

    private static ResourceLocation old(ResourceLocation id) {
        return ResourceLocation.fromNamespaceAndPath(Constants.FAMILY_ID, id.getPath());
    }
}
