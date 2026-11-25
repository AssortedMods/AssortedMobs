package com.grim3212.assorted.eightbit.gametest;

import com.google.gson.JsonParser;
import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.common.entity.EightBitEntities;
import com.grim3212.assorted.eightbit.common.item.EightBitItems;
import com.grim3212.assorted.eightbit.common.sounds.EightBitSounds;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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
        for (IRegistryObject<EntityType<?>> entity : EightBitEntities.ENTITIES.getEntries()) {
            // The id a chunk saved an entity under, read back the way the world reads it.
            CompoundTag saved = new CompoundTag();
            saved.putString("id", old(entity.getId()).toString());
            helper.assertValueEqual(EntityType.by(saved).orElse(null), entity.get(),
                    "the entity saved as " + old(entity.getId()));
        }

        for (IRegistryObject<Item> item : EightBitItems.ITEMS.getEntries()) {
            ItemStack stack = ItemStack.CODEC.parse(helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE),
                    JsonParser.parseString("{\"id\": \"" + old(item.getId()) + "\", \"count\": 1}")).getOrThrow();
            helper.assertTrue(stack.is(item.get()), "a stack saved as " + old(item.getId()) + " reads back as " + stack);
        }

        for (IRegistryObject<SoundEvent> sound : EightBitSounds.SOUNDS.getEntries()) {
            helper.assertValueEqual(BuiltInRegistries.SOUND_EVENT.get(old(sound.getId())), sound.get(), "the sound saved as " + old(sound.getId()));
        }
        helper.succeed();
    }

    private static ResourceLocation old(ResourceLocation id) {
        return ResourceLocation.fromNamespaceAndPath(Constants.FAMILY_ID, id.getPath());
    }
}
