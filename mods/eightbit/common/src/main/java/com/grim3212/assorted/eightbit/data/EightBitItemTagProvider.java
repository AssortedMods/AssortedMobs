package com.grim3212.assorted.eightbit.data;

import com.grim3212.assorted.eightbit.api.EightBitTags;
import com.grim3212.assorted.eightbit.common.item.EightBitItems;
import com.grim3212.assorted.lib.data.LibItemTagProvider;
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

public class EightBitItemTagProvider extends LibItemTagProvider {

    public EightBitItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> appender, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        appender.apply(EightBitTags.Items.PARABUZZY_TAME_ITEMS).addTag(ItemTags.FISHES);

        EightBitAdvancements.itemTags(appender).add(key(EightBitItems.PARABUZZY_SHELL.get()));
    }

    private static ResourceKey<Item> key(Item item) {
        return BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow();
    }
}
