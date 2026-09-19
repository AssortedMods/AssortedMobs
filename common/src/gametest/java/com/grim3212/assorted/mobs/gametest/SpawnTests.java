package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.MobsCommonMod;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
        out.accept("treasure_mobs_spawn_only_in_their_structures", SpawnTests::treasureMobsSpawnOnlyInTheirStructures);
        out.accept("creatures_have_spawn_placements", SpawnTests::creaturesHaveSpawnPlacements);
    }

    /** A living type with no attributes cannot even be constructed. */
    private static void creaturesSpawnWithTheirAttributes(GameTestHelper helper) {
        for (EntityType<? extends Mob> type : List.of(MobsEntities.ICE_PIXIE.get(), MobsEntities.TREASURE_MOB.get(), MobsEntities.BOBOMB.get(), MobsEntities.PARABUZZY.get())) {
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

        helper.assertTrue(spawnEntry(helper, Biomes.DESERT, MobCategory.MONSTER, MobsEntities.ICE_PIXIE.get()).isEmpty(), "ice pixies spawn in the desert");
        helper.assertTrue(spawnEntry(helper, Biomes.NETHER_WASTES, MobCategory.CREATURE, MobsEntities.PARABUZZY.get()).isEmpty(), "parabuzzies spawn in the nether");
        helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).listElementIds().forEach(biome ->
                helper.assertTrue(spawnEntry(helper, biome, MobCategory.CREATURE, MobsEntities.TREASURE_MOB.get()).isEmpty(), "treasure mobs spawn anywhere in " + biome.identifier()));
        helper.succeed();
    }

    /**
     * Through vanilla's own lookup of what may spawn at a position, {@code NaturalSpawner#mobsAt},
     * which each loader extends differently through AssortedLib: inside a desert pyramid the treasure
     * mob is among the creatures, at its configured weight, and just above the pyramid it is not. The
     * pyramid is only laid out, not built, and is taken out of the chunk again afterwards.
     */
    private static void treasureMobsSpawnOnlyInTheirStructures(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Holder.Reference<Structure> pyramid = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.DESERT_PYRAMID);
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        ChunkPos origin = ChunkPos.containing(helper.absolutePos(CENTRE));
        StructureStart start = pyramid.value().generate(pyramid, level.dimension(), level.registryAccess(), generator, generator.getBiomeSource(),
                level.getChunkSource().randomState(), level.getStructureManager(), level.getSeed(), origin, 0, level, biome -> true);
        helper.assertTrue(start.isValid(), "could not lay out a desert pyramid to test in");

        BoundingBox bounds = start.getPieces().getFirst().getBoundingBox();
        BlockPos inside = bounds.getCenter();
        BlockPos above = new BlockPos(inside.getX(), bounds.maxY() + 8, inside.getZ());
        ChunkAccess originChunk = level.getChunk(origin.x(), origin.z());
        originChunk.setStartForStructure(pyramid.value(), start);
        level.getChunk(inside).addReferenceForStructure(pyramid.value(), origin.pack());
        try {
            Optional<Weighted<MobSpawnSettings.SpawnerData>> entry = treasureMobAmong(mobsAt(helper, level, inside));
            helper.assertTrue(entry.isPresent(), "treasure mobs are not among the creatures inside a desert pyramid");
            helper.assertValueEqual(entry.get().weight(), MobsCommonMod.COMMON_CONFIG.treasureMobWeight.get(), "treasure mob spawn weight inside a desert pyramid");
            helper.assertTrue(treasureMobAmong(mobsAt(helper, level, above)).isEmpty(), "treasure mobs are among the creatures above a desert pyramid");
        } finally {
            originChunk.setStartForStructure(pyramid.value(), StructureStart.INVALID_START);
        }
        helper.succeed();
    }

    private static Optional<Weighted<MobSpawnSettings.SpawnerData>> treasureMobAmong(WeightedList<MobSpawnSettings.SpawnerData> spawns) {
        return spawns.unwrap().stream().filter(weighted -> weighted.value().type() == MobsEntities.TREASURE_MOB.get()).findFirst();
    }

    /** {@code NaturalSpawner#mobsAt} for creatures, private to vanilla, whose result both loaders extend. */
    @SuppressWarnings("unchecked")
    private static WeightedList<MobSpawnSettings.SpawnerData> mobsAt(GameTestHelper helper, ServerLevel level, BlockPos pos) {
        try {
            Method mobsAt = NaturalSpawner.class.getDeclaredMethod("mobsAt", ServerLevel.class, StructureManager.class, ChunkGenerator.class, MobCategory.class, BlockPos.class, Holder.class);
            mobsAt.setAccessible(true);
            return (WeightedList<MobSpawnSettings.SpawnerData>) mobsAt.invoke(null, level, level.structureManager(), level.getChunkSource().getGenerator(), MobCategory.CREATURE, pos, null);
        } catch (ReflectiveOperationException e) {
            throw helper.assertionException("could not ask NaturalSpawner#mobsAt: " + e);
        }
    }

    private static void creaturesHaveSpawnPlacements(GameTestHelper helper) {
        for (EntityType<?> type : List.of(MobsEntities.ICE_PIXIE.get(), MobsEntities.TREASURE_MOB.get(), MobsEntities.PARABUZZY.get())) {
            helper.assertTrue(SpawnPlacements.getPlacementType(type) == SpawnPlacementTypes.ON_GROUND,
                    type.getDescriptionId() + " has no spawn placement, so it would spawn in mid air or in water");
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
}
