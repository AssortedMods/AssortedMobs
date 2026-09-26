package com.grim3212.assorted.treasuremob.common.entity;

import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Attributes and spawn placements. Every creature is set down by Assorted Lib's spawn habits, from
 * data/assortedtreasuremob/spawn_habit; the placements here are what a habit asks of each spot.
 */
public class TreasureMobSpawns {

    public static void init() {
        Services.PLATFORM.registerEntityAttributes(TreasureMobEntities.TREASURE_MOB, TreasureMob::createAttributes);
        Services.PLATFORM.registerSpawnPlacement(TreasureMobEntities.TREASURE_MOB, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TreasureMob::checkTreasureMobSpawnRules);
    }
}
