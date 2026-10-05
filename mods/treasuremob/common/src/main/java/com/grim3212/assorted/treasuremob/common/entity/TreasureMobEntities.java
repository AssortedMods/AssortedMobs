package com.grim3212.assorted.treasuremob.common.entity;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.treasuremob.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/** MISC, which vanilla's spawner neither spawns nor counts: its spawn habit sets it down and caps how many there are. */
public class TreasureMobEntities {

    public static final RegistryProvider<EntityType<?>> ENTITIES = RegistryProvider.create(Registries.ENTITY_TYPE, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    // The chest it carries is its loot, dropped whole when it dies.
    public static final IRegistryObject<EntityType<TreasureMob>> TREASURE_MOB = register("treasure_mob", EntityType.Builder.of(TreasureMob::new, MobCategory.MISC).noLootTable().sized(0.85F, 0.8F).clientTrackingRange(10));

    private static <T extends Entity> IRegistryObject<EntityType<T>> register(final String name, final EntityType.Builder<T> builder) {
        final ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return ENTITIES.register(name, () -> builder.build(key));
    }

    public static void init() {
    }
}
