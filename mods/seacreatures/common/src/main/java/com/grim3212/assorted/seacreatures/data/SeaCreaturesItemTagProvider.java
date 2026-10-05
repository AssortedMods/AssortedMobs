package com.grim3212.assorted.seacreatures.data;

import com.grim3212.assorted.lib.data.LibItemTagProvider;
import com.grim3212.assorted.seacreatures.api.SeaCreaturesTags;
import com.grim3212.assorted.seacreatures.common.item.SeaCreaturesItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SeaCreaturesItemTagProvider extends LibItemTagProvider {

    public SeaCreaturesItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> appender, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        appender.apply(SeaCreaturesTags.Items.SEAL_FOOD).addTag(ItemTags.FISHES);
        appender.apply(SeaCreaturesTags.Items.REPAIRS_SHELL_ARMOR).add(key(SeaCreaturesItems.SEA_SHELL.get()));
        appender.apply(ItemTags.SWORDS).add(key(SeaCreaturesItems.NARWHAL_SWORD.get()));
        appender.apply(ItemTags.SHOVELS).add(key(SeaCreaturesItems.SHELL_SHOVEL.get()));
        appender.apply(ItemTags.HEAD_ARMOR).add(key(SeaCreaturesItems.SHELL_HELMET.get()));
        appender.apply(ItemTags.CHEST_ARMOR).add(key(SeaCreaturesItems.SHELL_CHESTPLATE.get()));
        appender.apply(ItemTags.LEG_ARMOR).add(key(SeaCreaturesItems.SHELL_LEGGINGS.get()));
        appender.apply(ItemTags.FOOT_ARMOR).add(key(SeaCreaturesItems.SHELL_BOOTS.get()));
    }

    private static ResourceKey<Item> key(Item item) {
        return BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow();
    }
}
