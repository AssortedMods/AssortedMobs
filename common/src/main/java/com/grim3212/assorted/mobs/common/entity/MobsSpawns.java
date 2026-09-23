package com.grim3212.assorted.mobs.common.entity;

import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Attributes and spawn placements. Every creature is set down by Assorted Lib's spawn habits, from
 * data/assortedmobs/spawn_habit, written by MobsSpawnHabitProvider; the placements here are what a habit
 * asks of each spot.
 */
public class MobsSpawns {

    public static void init() {
        Services.PLATFORM.registerEntityAttributes(MobsEntities.ICE_PIXIE, IcePixie::createAttributes);
        Services.PLATFORM.registerEntityAttributes(MobsEntities.TREASURE_MOB, TreasureMob::createAttributes);
        Services.PLATFORM.registerEntityAttributes(MobsEntities.BOBOMB, Bobomb::createAttributes);
        Services.PLATFORM.registerEntityAttributes(MobsEntities.PARABUZZY, Parabuzzy::createAttributes);
        Services.PLATFORM.registerEntityAttributes(MobsEntities.SEAL, Seal::createAttributes);
        Services.PLATFORM.registerEntityAttributes(MobsEntities.WALRUS, Walrus::createAttributes);
        Services.PLATFORM.registerEntityAttributes(MobsEntities.NARWHAL, Narwhal::createAttributes);
        Services.PLATFORM.registerEntityAttributes(MobsEntities.SEA_OTTER, SeaOtter::createAttributes);

        // Pixies are out in daylight too, which is most of what makes them unlike other monsters.
        Services.PLATFORM.registerSpawnPlacement(MobsEntities.ICE_PIXIE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        Services.PLATFORM.registerSpawnPlacement(MobsEntities.TREASURE_MOB, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TreasureMob::checkTreasureMobSpawnRules);
        Services.PLATFORM.registerSpawnPlacement(MobsEntities.SEAL, ArcticSpawnPlacement.INSTANCE, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Seal::checkArcticSpawnRules);
        Services.PLATFORM.registerSpawnPlacement(MobsEntities.WALRUS, ArcticSpawnPlacement.INSTANCE, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Seal::checkArcticSpawnRules);
        Services.PLATFORM.registerSpawnPlacement(MobsEntities.NARWHAL, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        Services.PLATFORM.registerSpawnPlacement(MobsEntities.SEA_OTTER, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SeaOtter::checkSeaOtterSpawnRules);
        Services.PLATFORM.registerSpawnPlacement(MobsEntities.PARABUZZY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
    }
}
