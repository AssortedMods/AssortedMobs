package com.grim3212.assorted.eightbit.common.entity;

import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Attributes and spawn placements. Every creature is set down by Assorted Lib's spawn habits, from
 * data/assortedeightbit/spawn_habit; the placements here are what a habit asks of each spot.
 */
public class EightBitSpawns {

    public static void init() {
        Services.PLATFORM.registerEntityAttributes(EightBitEntities.BOBOMB, Bobomb::createAttributes);
        Services.PLATFORM.registerEntityAttributes(EightBitEntities.PARABUZZY, Parabuzzy::createAttributes);
        Services.PLATFORM.registerSpawnPlacement(EightBitEntities.PARABUZZY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
    }
}
