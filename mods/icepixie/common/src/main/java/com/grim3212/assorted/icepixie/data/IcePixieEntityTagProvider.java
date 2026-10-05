package com.grim3212.assorted.icepixie.data;

import com.grim3212.assorted.icepixie.common.entity.IcePixieEntities;
import com.grim3212.assorted.lib.data.LibEntityTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class IcePixieEntityTagProvider extends LibEntityTagProvider {

    public IcePixieEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<EntityType<?>>, TagAppender<EntityType<?>>> tagger) {
        IcePixieAdvancements.openedWhenKilled(tagger).add(key(IcePixieEntities.ICE_PIXIE.get()));
    }

    private static ResourceKey<EntityType<?>> key(EntityType<?> type) {
        return BuiltInRegistries.ENTITY_TYPE.getResourceKey(type).orElseThrow();
    }
}
