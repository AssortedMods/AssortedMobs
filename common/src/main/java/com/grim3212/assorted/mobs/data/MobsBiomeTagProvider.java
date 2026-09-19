package com.grim3212.assorted.mobs.data;

import com.grim3212.assorted.lib.data.LibBiomeTagProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.mobs.api.MobsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/** Where each creature spawns. A datapack moves them by editing these tags. */
public class MobsBiomeTagProvider extends LibBiomeTagProvider {

    private static final TagKey<Biome> IS_SNOWY = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, "is_snowy"));

    public MobsBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Biome>, TagAppender<Biome>> tagger) {
        tagger.apply(MobsTags.Biomes.SPAWNS_ICE_PIXIES).addTag(IS_SNOWY);
        tagger.apply(MobsTags.Biomes.SPAWNS_PARABUZZIES).addTag(BiomeTags.IS_OVERWORLD);
    }
}
