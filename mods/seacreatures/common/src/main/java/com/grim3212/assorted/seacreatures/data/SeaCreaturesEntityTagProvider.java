package com.grim3212.assorted.seacreatures.data;

import com.grim3212.assorted.lib.data.LibEntityTagProvider;
import com.grim3212.assorted.seacreatures.api.SeaCreaturesTags;
import com.grim3212.assorted.seacreatures.common.entity.SeaCreaturesEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class SeaCreaturesEntityTagProvider extends LibEntityTagProvider {

    public SeaCreaturesEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<EntityType<?>>, TagAppender<EntityType<?>>> tagger) {
        // The spawn habits' shared herd cap.
        tagger.apply(SeaCreaturesTags.EntityTypes.ICE_HERD).add(key(SeaCreaturesEntities.SEAL.get())).add(key(SeaCreaturesEntities.WALRUS.get()));
        // What lets them stay under as long as they like: on 1.21.1 a tag, not a method a creature can override.
        tagger.apply(EntityTypeTags.CAN_BREATHE_UNDER_WATER).add(key(SeaCreaturesEntities.SEAL.get())).add(key(SeaCreaturesEntities.WALRUS.get())).add(key(SeaCreaturesEntities.SEA_OTTER.get()));
    }

    private static ResourceKey<EntityType<?>> key(EntityType<?> type) {
        return BuiltInRegistries.ENTITY_TYPE.getResourceKey(type).orElseThrow();
    }
}
