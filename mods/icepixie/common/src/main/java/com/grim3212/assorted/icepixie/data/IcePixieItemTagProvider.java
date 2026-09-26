package com.grim3212.assorted.icepixie.data;

import com.grim3212.assorted.icepixie.api.IcePixieTags;
import com.grim3212.assorted.lib.data.LibItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class IcePixieItemTagProvider extends LibItemTagProvider {

    public IcePixieItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> appender, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        // Anything a player can hold that is on fire, or lights one.
        TagAppender<Item> weapons = appender.apply(IcePixieTags.Items.ICE_PIXIE_WEAPONS);
        for (Item item : new Item[]{Items.TORCH, Items.SOUL_TORCH, Items.COPPER_TORCH, Items.FLINT_AND_STEEL}) {
            weapons.add(key(item));
        }

        IcePixieAdvancements.itemTags(appender);
    }

    private static ResourceKey<Item> key(Item item) {
        return BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow();
    }
}
