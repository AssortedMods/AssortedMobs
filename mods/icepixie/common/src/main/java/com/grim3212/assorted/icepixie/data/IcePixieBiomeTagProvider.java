package com.grim3212.assorted.icepixie.data;

import com.grim3212.assorted.icepixie.api.IcePixieTags;
import com.grim3212.assorted.lib.data.LibBiomeTagProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class IcePixieBiomeTagProvider extends LibBiomeTagProvider {

    private static final TagKey<Biome> IS_SNOWY = common("is_snowy");

    public IcePixieBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Biome>, TagAppender<Biome>> tagger) {
        tagger.apply(IcePixieTags.Biomes.SPAWNS_ICE_PIXIES).addTag(IS_SNOWY);
    }

    private static TagKey<Biome> common(String name) {
        return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, name));
    }
}
