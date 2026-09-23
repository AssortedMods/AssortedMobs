package com.grim3212.assorted.mobs.data;

import com.grim3212.assorted.lib.data.LibEntityTagProvider;
import com.grim3212.assorted.mobs.api.MobsTags;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class MobsEntityTagProvider extends LibEntityTagProvider {

    public MobsEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<EntityType<?>>, TagAppender<EntityType<?>>> tagger) {
        // The spawn habits' shared herd cap.
        tagger.apply(MobsTags.EntityTypes.ICE_HERD).add(key(MobsEntities.SEAL.get())).add(key(MobsEntities.WALRUS.get()));
    }

    private static ResourceKey<EntityType<?>> key(EntityType<?> type) {
        return BuiltInRegistries.ENTITY_TYPE.getResourceKey(type).orElseThrow();
    }
}
