package com.grim3212.assorted.mobs.common.entity;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.mobs.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * MISC for everything a spawn habit sets down: vanilla's spawner neither counts MISC against its caps
 * nor spawns it, and the habit's own cap is the population limit. The pixie stays MONSTER so it reads
 * as hostile and keeps off peaceful; the parabuzzy stays CREATURE, a farm animal among farm animals.
 */
public class MobsEntities {

    public static final RegistryProvider<EntityType<?>> ENTITIES = RegistryProvider.create(Registries.ENTITY_TYPE, Constants.MOD_ID);

    public static final IRegistryObject<EntityType<IcePixie>> ICE_PIXIE = register("ice_pixie", EntityType.Builder.of(IcePixie::new, MobCategory.MONSTER).sized(0.35F, 0.5F).eyeHeight(0.4425F).clientTrackingRange(8));
    public static final IRegistryObject<EntityType<IceCube>> ICE_CUBE = register("ice_cube", EntityType.Builder.<IceCube>of(IceCube::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
    // The chest it carries is its loot, dropped whole when it dies.
    public static final IRegistryObject<EntityType<TreasureMob>> TREASURE_MOB = register("treasure_mob", EntityType.Builder.of(TreasureMob::new, MobCategory.MISC).noLootTable().sized(0.85F, 0.8F).clientTrackingRange(10));
    // Only ever made from its item.
    public static final IRegistryObject<EntityType<Bobomb>> BOBOMB = register("bobomb", EntityType.Builder.of(Bobomb::new, MobCategory.MISC).noLootTable().fireImmune().sized(0.3F, 0.5F).clientTrackingRange(10));
    public static final IRegistryObject<EntityType<Parabuzzy>> PARABUZZY = register("parabuzzy", EntityType.Builder.of(Parabuzzy::new, MobCategory.CREATURE).sized(0.5F, 0.6F).clientTrackingRange(10));

    // The sea creatures
    public static final IRegistryObject<EntityType<Seal>> SEAL = register("seal", EntityType.Builder.of(Seal::new, MobCategory.MISC).sized(0.8F, 0.6F).eyeHeight(0.45F).clientTrackingRange(10));
    // Under a block across, though it is broader than that to look at: at a block or over, the pathfinder takes it for two
    // across, and will not plot a step from the water onto any shore whose edge it would overhang, which is all of them.
    public static final IRegistryObject<EntityType<Walrus>> WALRUS = register("walrus", EntityType.Builder.of(Walrus::new, MobCategory.MISC).sized(0.98F, 1.1F).eyeHeight(0.9F).clientTrackingRange(10));
    public static final IRegistryObject<EntityType<Narwhal>> NARWHAL = register("narwhal", EntityType.Builder.of(Narwhal::new, MobCategory.MISC).sized(1.3F, 0.85F).eyeHeight(0.45F).clientTrackingRange(10));
    public static final IRegistryObject<EntityType<SeaOtter>> SEA_OTTER = register("sea_otter", EntityType.Builder.of(SeaOtter::new, MobCategory.MISC).sized(0.6F, 0.45F).eyeHeight(0.3F).clientTrackingRange(8));

    private static <T extends Entity> IRegistryObject<EntityType<T>> register(final String name, final EntityType.Builder<T> builder) {
        final ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return ENTITIES.register(name, () -> builder.build(key));
    }

    public static void init() {
    }
}
