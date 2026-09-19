package com.grim3212.assorted.mobs.common.entity;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.mobs.MobsCommonMod;
import com.grim3212.assorted.mobs.api.MobsTags;
import com.grim3212.assorted.mobs.config.MobsCommonConfig;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Attributes, where each creature may spawn, and which biomes or structures it spawns in. Those are
 * the mod's biome and structure tags, so a datapack can move them. The part switches and weights are
 * read as a world loads its biomes, and for structures on every spawn attempt.
 */
public class MobsSpawns {

    public static void init() {
        Services.PLATFORM.registerEntityAttributes(MobsEntities.ICE_PIXIE, IcePixie::createAttributes);
        Services.PLATFORM.registerEntityAttributes(MobsEntities.TREASURE_MOB, TreasureMob::createAttributes);
        Services.PLATFORM.registerEntityAttributes(MobsEntities.BOBOMB, Bobomb::createAttributes);
        Services.PLATFORM.registerEntityAttributes(MobsEntities.PARABUZZY, Parabuzzy::createAttributes);

        // Pixies are out in daylight too, which is most of what makes them unlike other monsters.
        Services.PLATFORM.registerSpawnPlacement(MobsEntities.ICE_PIXIE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, IcePixie::checkIcePixieSpawnRules);
        Services.PLATFORM.registerSpawnPlacement(MobsEntities.TREASURE_MOB, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TreasureMob::checkTreasureMobSpawnRules);
        Services.PLATFORM.registerSpawnPlacement(MobsEntities.PARABUZZY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);

        MobsCommonConfig config = MobsCommonMod.COMMON_CONFIG;
        Services.WORLD_GEN.addSpawnToBiomes((key, biome) -> biome.is(MobsTags.Biomes.SPAWNS_ICE_PIXIES) && config.icePixieEnabled.get() && config.icePixieWeight.get() > 0, MobsEntities.ICE_PIXIE, config.icePixieWeight::get, 1, 3);
        Services.WORLD_GEN.addSpawnToBiomes((key, biome) -> biome.is(MobsTags.Biomes.SPAWNS_PARABUZZIES) && config.eightBitMobsEnabled.get() && config.parabuzzyWeight.get() > 0, MobsEntities.PARABUZZY, config.parabuzzyWeight::get, 1, 4);
        // Never on the surface: only inside mineshafts, strongholds and the like.
        Services.WORLD_GEN.addSpawnToStructures(MobsTags.Structures.SPAWNS_TREASURE_MOBS, MobsEntities.TREASURE_MOB, () -> config.treasureMobEnabled.get() ? config.treasureMobWeight.get() : 0, 1, 1);
    }
}
