package com.grim3212.assorted.seacreatures.data;

import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import com.grim3212.assorted.seacreatures.api.SeaCreaturesTags;
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

public class SeaCreaturesBlockTagProvider extends LibBlockTagProvider {

    public SeaCreaturesBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // Sand for the beaches and stone and gravel for the rocky shores: a walrus spawns on any of them, and a snowy beach is
        // as much sand as snow. Not down a cave for all that: the spawn rule wants daylight and water close by.
        appender.apply(SeaCreaturesTags.Blocks.SEALS_SPAWNABLE_ON).addTag(BlockTags.ANIMALS_SPAWNABLE_ON).addTag(BlockTags.ICE).addTag(BlockTags.SAND)
                .addTag(BlockTags.BASE_STONE_OVERWORLD).add(key(Blocks.GRAVEL)).add(key(Blocks.SNOW_BLOCK));
    }

    private static ResourceKey<Block> key(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow();
    }
}
