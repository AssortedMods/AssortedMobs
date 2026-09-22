package com.grim3212.assorted.mobs.data;

import com.grim3212.assorted.lib.data.LibItemTagProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.mobs.api.MobsTags;
import com.grim3212.assorted.mobs.common.item.MobsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MobsItemTagProvider extends LibItemTagProvider {

    private static final TagKey<Item> SEEDS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, "seeds"));

    public MobsItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> appender, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        // Anything a player can hold that is on fire, or lights one.
        TagAppender<Item> weapons = appender.apply(MobsTags.Items.ICE_PIXIE_WEAPONS);
        for (Item item : new Item[]{Items.TORCH, Items.SOUL_TORCH, Items.COPPER_TORCH, Items.FLINT_AND_STEEL}) {
            weapons.add(key(item));
        }

        appender.apply(MobsTags.Items.TREASURE_MOB_TEMPT_ITEMS).addTag(LibCommonTags.Items.NUGGETS_GOLD);
        appender.apply(MobsTags.Items.PARABUZZY_TAME_ITEMS).addTag(ItemTags.FISHES);

        appender.apply(MobsTags.Items.SEAL_FOOD).addTag(ItemTags.FISHES);
        appender.apply(MobsTags.Items.REPAIRS_SHELL_ARMOR).add(key(MobsItems.SEA_SHELL.get()));
        appender.apply(ItemTags.SWORDS).add(key(MobsItems.NARWHAL_SWORD.get()));
        appender.apply(ItemTags.SHOVELS).add(key(MobsItems.SHELL_SHOVEL.get()));
        appender.apply(ItemTags.HEAD_ARMOR).add(key(MobsItems.SHELL_HELMET.get()));
        appender.apply(ItemTags.CHEST_ARMOR).add(key(MobsItems.SHELL_CHESTPLATE.get()));
        appender.apply(ItemTags.LEG_ARMOR).add(key(MobsItems.SHELL_LEGGINGS.get()));
        appender.apply(ItemTags.FOOT_ARMOR).add(key(MobsItems.SHELL_BOOTS.get()));

    }

    private static ResourceKey<Item> key(Item item) {
        return BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow();
    }
}
