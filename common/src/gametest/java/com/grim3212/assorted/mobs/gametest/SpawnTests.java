package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.MobsCommonMod;
import com.grim3212.assorted.mobs.common.entity.ArcticSpawnPlacement;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.entity.TreasureMob;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.survivalPlayer;
import static com.grim3212.assorted.mobs.gametest.MobsTestSupport.*;

/**
 * That each creature can exist at all, and that the loaders were told where it lives. All three
 * go through AssortedLib, which does them differently on each loader.
 */
final class SpawnTests {

    private SpawnTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("creatures_spawn_with_their_attributes", SpawnTests::creaturesSpawnWithTheirAttributes);
        out.accept("creatures_are_added_to_their_biomes", SpawnTests::creaturesAreAddedToTheirBiomes);
        out.accept("treasure_mobs_spawn_only_in_their_structures",SpawnTests::treasureMobsSpawnOnlyInTheirStructures);
        out.accept("treasure_mobs_spawn_in_the_dark", SpawnTests::treasureMobsSpawnInTheDark);
        out.accept("treasure_mobs_have_their_own_category", SpawnTests::treasureMobsHaveTheirOwnCategory);
        out.accept("treasure_mobs_spawn_apart", SpawnTests::treasureMobsSpawnApart);
        out.accept("vanilla_spawns_a_treasure_mob_in_a_structure", SpawnTests::vanillaSpawnsATreasureMobInAStructure);
        out.accept("creatures_have_spawn_placements", SpawnTests::creaturesHaveSpawnPlacements);
    }

    /** A living type with no attributes cannot even be constructed. */
    private static void creaturesSpawnWithTheirAttributes(GameTestHelper helper) {
        for (EntityType<? extends Mob> type : List.of(MobsEntities.ICE_PIXIE.get(), MobsEntities.TREASURE_MOB.get(), MobsEntities.BOBOMB.get(), MobsEntities.PARABUZZY.get(),
                MobsEntities.SEAL.get(), MobsEntities.WALRUS.get(),
                MobsEntities.NARWHAL.get(), MobsEntities.SEA_OTTER.get())) {
            Mob mob = helper.spawn(type, CENTRE, EntitySpawnReason.SPAWN_ITEM_USE);
            helper.assertTrue(mob.isAlive() && mob.getHealth() > 0, type.getDescriptionId() + " did not spawn alive");
            mob.discard();
        }
        helper.succeed();
    }

    /** At the configured weight, which the config only knows once it has loaded. */
    private static void creaturesAreAddedToTheirBiomes(GameTestHelper helper) {
        assertSpawns(helper, Biomes.SNOWY_PLAINS, MobCategory.MONSTER, MobsEntities.ICE_PIXIE.get(), MobsCommonMod.COMMON_CONFIG.icePixieWeight.get());
        assertSpawns(helper, Biomes.PLAINS, MobCategory.CREATURE, MobsEntities.PARABUZZY.get(), MobsCommonMod.COMMON_CONFIG.parabuzzyWeight.get());
        // Named by id: no biome tag fits a meadow without the peaks coming too.
        assertSpawns(helper, Biomes.MEADOW, MobCategory.CREATURE, MobsEntities.PARABUZZY.get(), MobsCommonMod.COMMON_CONFIG.parabuzzyWeight.get());

        assertSpawns(helper, Biomes.SNOWY_PLAINS, MobCategory.CREATURE, MobsEntities.SEAL.get(), MobsCommonMod.COMMON_CONFIG.sealWeight.get());
        assertSpawns(helper, Biomes.FROZEN_OCEAN, MobCategory.CREATURE, MobsEntities.WALRUS.get(), MobsCommonMod.COMMON_CONFIG.walrusWeight.get());
        // On any beach or rocky shore, at a weight of its own; and once only on a snowy one, which was its biome already.
        assertSpawns(helper, Biomes.BEACH, MobCategory.CREATURE, MobsEntities.WALRUS.get(), MobsCommonMod.COMMON_CONFIG.walrusBeachWeight.get());
        assertSpawns(helper, Biomes.STONY_SHORE, MobCategory.CREATURE, MobsEntities.WALRUS.get(), MobsCommonMod.COMMON_CONFIG.walrusBeachWeight.get());
        assertSpawns(helper, Biomes.SNOWY_BEACH, MobCategory.CREATURE, MobsEntities.WALRUS.get(), MobsCommonMod.COMMON_CONFIG.walrusWeight.get());
        helper.assertValueEqual(spawnCount(helper, Biomes.SNOWY_BEACH, MobCategory.CREATURE, MobsEntities.WALRUS.get()), 1L, "walrus entries among a snowy beach's spawns");
        helper.assertTrue(spawnEntry(helper, Biomes.DESERT, MobCategory.CREATURE, MobsEntities.WALRUS.get()).isEmpty(), "walruses spawn in the desert");
        assertSpawns(helper, Biomes.DEEP_COLD_OCEAN, MobCategory.WATER_CREATURE, MobsEntities.NARWHAL.get(), MobsCommonMod.COMMON_CONFIG.narwhalWeight.get());
        assertSpawns(helper, Biomes.RIVER, MobCategory.WATER_CREATURE, MobsEntities.SEA_OTTER.get(), MobsCommonMod.COMMON_CONFIG.seaOtterWeight.get());
        helper.assertTrue(spawnEntry(helper, Biomes.WARM_OCEAN, MobCategory.WATER_CREATURE, MobsEntities.NARWHAL.get()).isEmpty(), "narwhals spawn in warm oceans");
        helper.assertTrue(spawnEntry(helper, Biomes.DESERT, MobCategory.CREATURE, MobsEntities.SEAL.get()).isEmpty(), "seals spawn in the desert");
        // By the common tags: a snowy taiga is snowy, a warm ocean is shallow, and a deep one is not coast.
        assertSpawns(helper, Biomes.SNOWY_TAIGA, MobCategory.CREATURE, MobsEntities.SEAL.get(), MobsCommonMod.COMMON_CONFIG.sealWeight.get());
        assertSpawns(helper, Biomes.FROZEN_RIVER, MobCategory.WATER_CREATURE, MobsEntities.NARWHAL.get(), MobsCommonMod.COMMON_CONFIG.narwhalWeight.get());
        assertSpawns(helper, Biomes.WARM_OCEAN, MobCategory.WATER_CREATURE, MobsEntities.SEA_OTTER.get(), MobsCommonMod.COMMON_CONFIG.seaOtterWeight.get());
        helper.assertTrue(spawnEntry(helper, Biomes.DEEP_OCEAN, MobCategory.WATER_CREATURE, MobsEntities.SEA_OTTER.get()).isEmpty(), "sea otters spawn out in the deep ocean");

        helper.assertTrue(spawnEntry(helper, Biomes.DESERT, MobCategory.MONSTER, MobsEntities.ICE_PIXIE.get()).isEmpty(), "ice pixies spawn in the desert");
        // Overworld, but in none of the tags spawns_parabuzzies names.
        helper.assertTrue(spawnEntry(helper, Biomes.SNOWY_PLAINS, MobCategory.CREATURE, MobsEntities.PARABUZZY.get()).isEmpty(), "parabuzzies spawn in snowy plains");
        // Farm animals live here, but taiga is the one such tag left out.
        helper.assertTrue(spawnEntry(helper, Biomes.TAIGA, MobCategory.CREATURE, MobsEntities.PARABUZZY.get()).isEmpty(), "parabuzzies spawn in taiga");
        helper.assertTrue(spawnEntry(helper, Biomes.NETHER_WASTES, MobCategory.CREATURE, MobsEntities.PARABUZZY.get()).isEmpty(), "parabuzzies spawn in the nether");
        helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).listElementIds().forEach(biome ->
                helper.assertTrue(spawnEntry(helper, biome, MobsEntities.TREASURE_MOB.get().getCategory(), MobsEntities.TREASURE_MOB.get()).isEmpty(), "treasure mobs spawn anywhere in " + biome.identifier()));
        helper.succeed();
    }

    /**
     * Through vanilla's own lookup of what may spawn at a position, {@code NaturalSpawner#mobsAt},
     * which each loader extends differently through AssortedLib: inside a desert pyramid the treasure
     * mob is among the creatures, at its configured weight, and just above the pyramid it is not.
     */
    private static void treasureMobsSpawnOnlyInTheirStructures(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        LaidOut pyramid = layOutStructure(helper, BuiltinStructures.DESERT_PYRAMID);

        Optional<Weighted<MobSpawnSettings.SpawnerData>> entry = treasureMobAmong(mobsAt(helper, level, pyramid.inside()));
        helper.assertTrue(entry.isPresent(), "treasure mobs are not among the creatures inside a desert pyramid");
        helper.assertValueEqual(entry.get().weight(), MobsCommonMod.COMMON_CONFIG.treasureMobWeight.get(), "treasure mob spawn weight inside a desert pyramid");
        helper.assertTrue(treasureMobAmong(mobsAt(helper, level, pyramid.above())).isEmpty(), "treasure mobs are among the creatures above a desert pyramid");
        helper.succeed();
    }

    /** The values each loader's enum extension gives the category; they are written out twice. */
    private static void treasureMobsHaveTheirOwnCategory(GameTestHelper helper) {
        MobCategory category = MobsEntities.TREASURE_MOB.get().getCategory();
        helper.assertValueEqual(category.name(), "ASSORTEDMOBS_TREASURE", "treasure mob category");
        helper.assertValueEqual(category.getName(), "assortedmobs:treasure", "category name");
        helper.assertValueEqual(category.getMaxInstancesPerChunk(), 4, "category cap");
        helper.assertTrue(category.isFriendly(), "the category does not spawn on peaceful");
        helper.assertFalse(category.isPersistent(), "the category only gets a spawn pass every 400 ticks");
        helper.assertValueEqual(category.getDespawnDistance(), 128, "category despawn distance");

        // Only wild ones count toward the cap.
        TreasureMob mob = helper.spawnWithNoFreeWill(MobsEntities.TREASURE_MOB.get(), CENTRE);
        helper.assertFalse(mob.requiresCustomPersistence(), "a wild treasure mob is left out of the category count");
        mob.setTame(true, false);
        helper.assertTrue(mob.requiresCustomPersistence(), "a tame treasure mob counts toward the category cap");
        mob.discard();
        helper.succeed();
    }

    /**
     * NaturalSpawner's own routine for one position, everything but the category caps: a desert
     * pyramid laid out in the sky with a floor in it, and a player 40 blocks off.
     */
    private static void vanillaSpawnsATreasureMobInAStructure(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        LaidOut pyramid = layOutStructure(helper, BuiltinStructures.DESERT_PYRAMID, 220);
        // Absolute: this far outside the box, the helper's relative positions do not map back reliably.
        BlockPos floor = pyramid.inside().below();
        BlockPos.betweenClosed(floor.offset(-7, 0, -7), floor.offset(7, 0, 7)).forEach(pos -> level.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState()));

        ServerPlayer player = survivalPlayer(helper);
        player.snapTo(pyramid.inside().getX() + 40.5D, pyramid.inside().getY(), pyramid.inside().getZ() + 0.5D);
        player.setNoGravity(true);
        player.setInvulnerable(true);

        AABB around = new AABB(pyramid.inside()).inflate(32.0D);
        for (int attempt = 0; attempt < 200 && level.getEntitiesOfClass(TreasureMob.class, around).isEmpty(); attempt++) {
            NaturalSpawner.spawnCategoryForPosition(MobsEntities.TREASURE_CATEGORY, level, pyramid.inside());
        }

        List<TreasureMob> spawned = level.getEntitiesOfClass(TreasureMob.class, around);
        BlockPos.betweenClosed(floor.offset(-7, 0, -7), floor.offset(7, 0, 7)).forEach(pos -> level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState()));
        helper.assertValueEqual(spawned.size(), 1, "treasure mobs vanilla spawned inside a desert pyramid");
        helper.assertFalse(spawned.getFirst().getChest().isEmpty(), "the spawned treasure mob has an empty chest");
        spawned.getFirst().discard();
        helper.succeed();
    }

    /** Run 150 blocks up so other tests' treasure mobs are outside SPAWN_SPACING. */
    private static void treasureMobsSpawnApart(GameTestHelper helper) {
        BlockPos aloft = CENTRE.above(150);
        BlockPos pos = helper.absolutePos(aloft);
        helper.assertFalse(TreasureMob.hasWildOneNear(helper.getLevel(), pos), "a wild treasure mob was found high over the box");
        TreasureMob tame = helper.spawnWithNoFreeWill(MobsEntities.TREASURE_MOB.get(), aloft.east(2));
        tame.setTame(true, false);
        helper.assertFalse(TreasureMob.hasWildOneNear(helper.getLevel(), pos), "a tame treasure mob kept a wild one from spawning");
        helper.spawnWithNoFreeWill(MobsEntities.TREASURE_MOB.get(), aloft.west(2));
        helper.assertTrue(TreasureMob.hasWildOneNear(helper.getLevel(), pos), "a wild treasure mob next door did not keep another from spawning");
        helper.succeed();
    }

    /** A sealed stone cell; waits for the light engine to darken it before asking. */
    private static void treasureMobsSpawnInTheDark(GameTestHelper helper) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y <= 2; y++) {
                    helper.setBlock(CENTRE.offset(x, y, z), x == 0 && z == 0 && (y == 0 || y == 1) ? Blocks.AIR : Blocks.STONE);
                }
            }
        }
        TreasureMob mob = helper.spawnWithNoFreeWill(MobsEntities.TREASURE_MOB.get(), CENTRE);

        helper.succeedWhen(() -> {
            helper.assertValueEqual(helper.getLevel().getMaxLocalRawBrightness(mob.blockPosition()), 0, "light in the sealed cell");
            helper.assertTrue(mob.checkSpawnRules(helper.getLevel(), EntitySpawnReason.NATURAL), "a treasure mob cannot spawn in the dark");
            mob.discard();
        });
    }

    private static Optional<Weighted<MobSpawnSettings.SpawnerData>> treasureMobAmong(WeightedList<MobSpawnSettings.SpawnerData> spawns) {
        return spawns.unwrap().stream().filter(weighted -> weighted.value().type() == MobsEntities.TREASURE_MOB.get()).findFirst();
    }

    /** Private NaturalSpawner#mobsAt, which both loaders extend through AssortedLib. */
    @SuppressWarnings("unchecked")
    private static WeightedList<MobSpawnSettings.SpawnerData> mobsAt(GameTestHelper helper, ServerLevel level, BlockPos pos) {
        try {
            Method mobsAt = NaturalSpawner.class.getDeclaredMethod("mobsAt", ServerLevel.class, StructureManager.class, ChunkGenerator.class, MobCategory.class, BlockPos.class, Holder.class);
            mobsAt.setAccessible(true);
            return (WeightedList<MobSpawnSettings.SpawnerData>) mobsAt.invoke(null, level, level.structureManager(), level.getChunkSource().getGenerator(), MobsEntities.TREASURE_MOB.get().getCategory(), pos, null);
        } catch (ReflectiveOperationException e) {
            throw helper.assertionException("could not ask NaturalSpawner#mobsAt: " + e);
        }
    }

    private static void creaturesHaveSpawnPlacements(GameTestHelper helper) {
        for (EntityType<?> type : List.of(MobsEntities.ICE_PIXIE.get(), MobsEntities.TREASURE_MOB.get(), MobsEntities.PARABUZZY.get())) {
            helper.assertTrue(SpawnPlacements.getPlacementType(type) == SpawnPlacementTypes.ON_GROUND,
                    type.getDescriptionId() + " has no spawn placement, so it would spawn in mid air or in water");
        }
        // On the ground too, by a placement of the mod's own that lets them onto plain ice; see AmphibiousTests for what it allows.
        for (EntityType<?> type : List.of(MobsEntities.SEAL.get(), MobsEntities.WALRUS.get())) {
            helper.assertTrue(SpawnPlacements.getPlacementType(type) == ArcticSpawnPlacement.INSTANCE, type.getDescriptionId() + " does not use the arctic spawn placement");
        }
        for (EntityType<?> type : List.of(MobsEntities.NARWHAL.get(), MobsEntities.SEA_OTTER.get())) {
            helper.assertTrue(SpawnPlacements.getPlacementType(type) == SpawnPlacementTypes.IN_WATER, type.getDescriptionId() + " is not placed in water, so it would spawn on the beach");
        }
        helper.succeed();
    }

    private static void assertSpawns(GameTestHelper helper, ResourceKey<Biome> biome, MobCategory category, EntityType<?> type, int weight) {
        Optional<Weighted<MobSpawnSettings.SpawnerData>> entry = spawnEntry(helper, biome, category, type);
        helper.assertTrue(entry.isPresent(), type.getDescriptionId() + " is not among the " + category.getName() + " spawns of " + biome.identifier());
        helper.assertValueEqual(entry.get().weight(), weight, type.getDescriptionId() + " spawn weight in " + biome.identifier());
    }

    private static Optional<Weighted<MobSpawnSettings.SpawnerData>> spawnEntry(GameTestHelper helper, ResourceKey<Biome> biome, MobCategory category, EntityType<?> type) {
        Holder<Biome> holder = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(biome);
        return holder.value().getMobSettings().getMobs(category).unwrap().stream().filter(weighted -> weighted.value().type() == type).findFirst();
    }

    private static long spawnCount(GameTestHelper helper, ResourceKey<Biome> biome, MobCategory category, EntityType<?> type) {
        Holder<Biome> holder = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(biome);
        return holder.value().getMobSettings().getMobs(category).unwrap().stream().filter(weighted -> weighted.value().type() == type).count();
    }
}
