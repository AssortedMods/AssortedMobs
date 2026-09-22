package com.grim3212.assorted.mobs.common.entity;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.mobs.MobsCommonMod;
import com.grim3212.assorted.mobs.api.MobsTags;
import com.grim3212.assorted.mobs.config.MobsCommonConfig;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
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

        MobsCommonConfig config = MobsCommonMod.COMMON_CONFIG;
        Services.WORLD_GEN.addSpawnToBiomes((key, biome) -> biome.is(MobsTags.Biomes.SPAWNS_ICE_PIXIES) && config.icePixieEnabled.get() && config.icePixieWeight.get() > 0, MobsEntities.ICE_PIXIE, config.icePixieWeight::get, 1, 3);
        Services.WORLD_GEN.addSpawnToBiomes((key, biome) -> biome.is(MobsTags.Biomes.SPAWNS_PARABUZZIES) && config.eightBitMobsEnabled.get() && config.parabuzzyWeight.get() > 0, MobsEntities.PARABUZZY, config.parabuzzyWeight::get, 1, 4);
        Services.WORLD_GEN.addSpawnToBiomes((key, biome) -> biome.is(MobsTags.Biomes.SPAWNS_SEALS) && config.seaCreaturesEnabled.get() && config.sealWeight.get() > 0, MobsEntities.SEAL, config.sealWeight::get, 1, 3);
        Services.WORLD_GEN.addSpawnToBiomes((key, biome) -> biome.is(MobsTags.Biomes.SPAWNS_WALRUSES) && config.seaCreaturesEnabled.get() && config.walrusWeight.get() > 0, MobsEntities.WALRUS, config.walrusWeight::get, 1, 2);
        // Not twice over where a beach is one of its own biomes already, as the snowy beach is.
        Services.WORLD_GEN.addSpawnToBiomes((key, biome) -> biome.is(MobsTags.Biomes.SPAWNS_WALRUSES_RARELY) && !biome.is(MobsTags.Biomes.SPAWNS_WALRUSES) && config.seaCreaturesEnabled.get() && config.walrusBeachWeight.get() > 0, MobsEntities.WALRUS, config.walrusBeachWeight::get, 1, 2);
        Services.WORLD_GEN.addSpawnToBiomes((key, biome) -> biome.is(MobsTags.Biomes.SPAWNS_NARWHALS) && config.seaCreaturesEnabled.get() && config.narwhalWeight.get() > 0, MobsEntities.NARWHAL, config.narwhalWeight::get, 1, 2);
        Services.WORLD_GEN.addSpawnToBiomes((key, biome) -> biome.is(MobsTags.Biomes.SPAWNS_SEA_OTTERS) && config.seaCreaturesEnabled.get() && config.seaOtterWeight.get() > 0, MobsEntities.SEA_OTTER, config.seaOtterWeight::get, 3, 5);
        // Never on the surface: only inside mineshafts, strongholds and the like.
        Services.WORLD_GEN.addSpawnToStructures(MobsTags.Structures.SPAWNS_TREASURE_MOBS, MobsEntities.TREASURE_MOB, () -> config.treasureMobEnabled.get() ? config.treasureMobWeight.get() : 0, 1, 1);
    }
}
