package com.grim3212.assorted.icepixie.common.entity;

import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Attributes and spawn placements. Every creature is set down by Assorted Lib's spawn habits, from
 * data/assortedicepixie/spawn_habit; the placements here are what a habit asks of each spot.
 */
public class IcePixieSpawns {

    public static void init() {
        Services.PLATFORM.registerEntityAttributes(IcePixieEntities.ICE_PIXIE, IcePixie::createAttributes);

        // Pixies are out in daylight too, which is most of what makes them unlike other monsters.
        Services.PLATFORM.registerSpawnPlacement(IcePixieEntities.ICE_PIXIE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
    }
}
