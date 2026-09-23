package com.grim3212.assorted.mobs.data;

import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import com.grim3212.assorted.mobs.api.MobsTags;
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

public class MobsBlockTagProvider extends LibBlockTagProvider {

    public MobsBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // Anything hot. A redstone torch is not a fire; a lantern is light, not heat.
        TagAppender<Block> repellents = appender.apply(MobsTags.Blocks.ICE_PIXIE_REPELLENTS).addTag(BlockTags.FIRE).addTag(BlockTags.CAMPFIRES)
                .add(key(Blocks.LAVA)).add(key(Blocks.MAGMA_BLOCK));
        for (Block torch : new Block[]{Blocks.TORCH, Blocks.WALL_TORCH, Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.COPPER_TORCH, Blocks.COPPER_WALL_TORCH}) {
            repellents.add(key(torch));
        }
        // Sand for the beaches and stone and gravel for the rocky shores: a walrus spawns on any of them, and a snowy beach is
        // as much sand as snow. Not down a cave for all that: the spawn rule wants daylight and water close by.
        appender.apply(MobsTags.Blocks.SEALS_SPAWNABLE_ON).addTag(BlockTags.ANIMALS_SPAWNABLE_ON).addTag(BlockTags.ICE).addTag(BlockTags.SAND)
                .addTag(BlockTags.BASE_STONE_OVERWORLD).add(key(Blocks.GRAVEL)).add(key(Blocks.SNOW_BLOCK));
    }

    private static ResourceKey<Block> key(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow();
    }
}
