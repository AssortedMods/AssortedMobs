package com.grim3212.assorted.icepixie.common.entity;

import com.grim3212.assorted.icepixie.Constants;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/** A MONSTER, so it reads as hostile and keeps off peaceful. Its spawn habit sets it down and caps how many there are. */
public class IcePixieEntities {

    public static final RegistryProvider<EntityType<?>> ENTITIES = RegistryProvider.create(Registries.ENTITY_TYPE, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<EntityType<IcePixie>> ICE_PIXIE = register("ice_pixie", EntityType.Builder.of(IcePixie::new, MobCategory.MONSTER).sized(0.35F, 0.5F).eyeHeight(0.4425F).clientTrackingRange(8));
    public static final IRegistryObject<EntityType<IceCube>> ICE_CUBE = register("ice_cube", EntityType.Builder.<IceCube>of(IceCube::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));

    private static <T extends Entity> IRegistryObject<EntityType<T>> register(final String name, final EntityType.Builder<T> builder) {
        final ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return ENTITIES.register(name, () -> builder.build(key));
    }

    public static void init() {
    }
}
