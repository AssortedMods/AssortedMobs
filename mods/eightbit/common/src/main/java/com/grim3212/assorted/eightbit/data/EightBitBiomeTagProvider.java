package com.grim3212.assorted.eightbit.data;

import com.grim3212.assorted.eightbit.api.EightBitTags;
import com.grim3212.assorted.lib.data.LibBiomeTagProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class EightBitBiomeTagProvider extends LibBiomeTagProvider {

    private static final TagKey<Biome> IS_PLAINS = common("is_plains");
    private static final TagKey<Biome> IS_SWAMP = common("is_swamp");

    public EightBitBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Biome>, TagAppender<Biome>> tagger) {
        tagger.apply(EightBitTags.Biomes.SPAWNS_PARABUZZIES)
                .addTag(IS_PLAINS).addTag(BiomeTags.IS_FOREST).addTag(BiomeTags.IS_SAVANNA).addTag(BiomeTags.IS_HILL)
                .addTag(BiomeTags.IS_JUNGLE).addTag(BiomeTags.IS_BADLANDS).addTag(IS_SWAMP)
                .add(Biomes.MEADOW).add(Biomes.CHERRY_GROVE);
    }

    private static TagKey<Biome> common(String name) {
        return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, name));
    }
}
