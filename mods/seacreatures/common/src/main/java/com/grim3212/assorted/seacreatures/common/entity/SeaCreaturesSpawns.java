package com.grim3212.assorted.seacreatures.common.entity;

import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Attributes and spawn placements. Every creature is set down by Assorted Lib's spawn habits, from
 * data/assortedseacreatures/spawn_habit; the placements here are what a habit asks of each spot.
 */
public class SeaCreaturesSpawns {

    public static void init() {
        Services.PLATFORM.registerEntityAttributes(SeaCreaturesEntities.SEAL, Seal::createAttributes);
        Services.PLATFORM.registerEntityAttributes(SeaCreaturesEntities.WALRUS, Walrus::createAttributes);
        Services.PLATFORM.registerEntityAttributes(SeaCreaturesEntities.NARWHAL, Narwhal::createAttributes);
        Services.PLATFORM.registerEntityAttributes(SeaCreaturesEntities.SEA_OTTER, SeaOtter::createAttributes);

        Services.PLATFORM.registerSpawnPlacement(SeaCreaturesEntities.SEAL, ArcticSpawnPlacement.INSTANCE, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Seal::checkArcticSpawnRules);
        Services.PLATFORM.registerSpawnPlacement(SeaCreaturesEntities.WALRUS, ArcticSpawnPlacement.INSTANCE, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Seal::checkArcticSpawnRules);
        Services.PLATFORM.registerSpawnPlacement(SeaCreaturesEntities.NARWHAL, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        Services.PLATFORM.registerSpawnPlacement(SeaCreaturesEntities.SEA_OTTER, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SeaOtter::checkSeaOtterSpawnRules);
    }
}
