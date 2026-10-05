package com.grim3212.assorted.icepixie.data;

import com.grim3212.assorted.icepixie.api.IcePixieTags;
import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class IcePixieBlockTagProvider extends LibBlockTagProvider {

    public IcePixieBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // Anything hot. A redstone torch is not a fire; a lantern is light, not heat.
        TagAppender<Block> repellents = appender.apply(IcePixieTags.Blocks.ICE_PIXIE_REPELLENTS).addTag(BlockTags.FIRE).addTag(BlockTags.CAMPFIRES)
                .add(key(Blocks.LAVA)).add(key(Blocks.MAGMA_BLOCK));
        for (Block torch : new Block[]{Blocks.TORCH, Blocks.WALL_TORCH, Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.COPPER_TORCH, Blocks.COPPER_WALL_TORCH}) {
            repellents.add(key(torch));
        }
    }

    private static ResourceKey<Block> key(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow();
    }
}
