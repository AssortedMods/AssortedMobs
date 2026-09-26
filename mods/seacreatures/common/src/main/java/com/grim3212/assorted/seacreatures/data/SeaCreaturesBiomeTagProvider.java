package com.grim3212.assorted.seacreatures.data;

import com.grim3212.assorted.lib.data.LibBiomeTagProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.seacreatures.api.SeaCreaturesTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class SeaCreaturesBiomeTagProvider extends LibBiomeTagProvider {

    private static final TagKey<Biome> IS_SNOWY = common("is_snowy");
    private static final TagKey<Biome> IS_BEACH = common("is_beach");
    private static final TagKey<Biome> IS_STONY_SHORES = common("is_stony_shores");
    private static final TagKey<Biome> IS_AQUATIC_ICY = common("is_aquatic_icy");
    private static final TagKey<Biome> IS_RIVER = common("is_river");
    private static final TagKey<Biome> IS_SHALLOW_OCEAN = common("is_shallow_ocean");

    public SeaCreaturesBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Biome>, TagAppender<Biome>> tagger) {
        // By the common tags, so another mod's snow and seas count. Not every snowy biome has water in it; the spawn rule
        // wants some close by, so a seal is never far up a frozen peak. Nothing tags oceans by temperature: the narwhal's
        // cold seas are the frozen ones by tag and vanilla's two cold ones by name, and a mod that wants narwhals in its
        // own cold sea adds it to spawns_narwhals.
        tagger.apply(SeaCreaturesTags.Biomes.SPAWNS_SEALS).addTag(IS_SNOWY).addTag(IS_AQUATIC_ICY);
        tagger.apply(SeaCreaturesTags.Biomes.SPAWNS_WALRUSES).addTag(SeaCreaturesTags.Biomes.SPAWNS_SEALS);
        // Any shore, sand or rock. #c:is_beach has vanilla's beaches in it.
        tagger.apply(SeaCreaturesTags.Biomes.SPAWNS_WALRUSES_RARELY).addTag(IS_BEACH).addTag(IS_STONY_SHORES);
        tagger.apply(SeaCreaturesTags.Biomes.SPAWNS_NARWHALS).addTag(IS_AQUATIC_ICY).add(Biomes.COLD_OCEAN).add(Biomes.DEEP_COLD_OCEAN);
        // The otter keeps to the coast: every river, and the shallow oceans, which are the ones that have one. The frozen
        // ones are in both tags; the spawn rule turns those down by their temperature, see SeaOtter#isWarmEnough.
        tagger.apply(SeaCreaturesTags.Biomes.SPAWNS_SEA_OTTERS).addTag(IS_RIVER).addTag(IS_SHALLOW_OCEAN);
    }

    private static TagKey<Biome> common(String name) {
        return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, name));
    }
}
