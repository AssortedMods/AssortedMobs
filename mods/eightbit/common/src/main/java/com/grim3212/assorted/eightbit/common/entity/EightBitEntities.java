package com.grim3212.assorted.eightbit.common.entity;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.Family;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/** The parabuzzy stays CREATURE, a farm animal among farm animals; the Bob-omb is MISC. */
public class EightBitEntities {

    public static final RegistryProvider<EntityType<?>> ENTITIES = RegistryProvider.create(Registries.ENTITY_TYPE, Constants.MOD_ID).aliasFrom(Family.ID);

    // Only ever made from its item.
    public static final IRegistryObject<EntityType<Bobomb>> BOBOMB = register("bobomb", EntityType.Builder.of(Bobomb::new, MobCategory.MISC).noLootTable().fireImmune().sized(0.3F, 0.5F).clientTrackingRange(10));
    public static final IRegistryObject<EntityType<Parabuzzy>> PARABUZZY = register("parabuzzy", EntityType.Builder.of(Parabuzzy::new, MobCategory.CREATURE).sized(0.5F, 0.6F).clientTrackingRange(10));

    private static <T extends Entity> IRegistryObject<EntityType<T>> register(final String name, final EntityType.Builder<T> builder) {
        final ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return ENTITIES.register(name, () -> builder.build(key));
    }

    public static void init() {
    }
}
