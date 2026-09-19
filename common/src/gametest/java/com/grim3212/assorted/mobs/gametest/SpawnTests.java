package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.MobsCommonMod;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.entity.TreasureMob;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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
        out.accept("treasure_mobs_spawn_apart", SpawnTests::treasureMobsSpawnApart);
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

    /** Run 150 blocks up so other tests' treasure mobs are outside SPAWN_SPACING. */
    private static void treasureMobsSpawnApart(GameTestHelper helper) {
        helper.assertTrue(MobsEntities.TREASURE_MOB.get().getCategory() != MobCategory.CREATURE, "treasure mobs share the animals' spawn cap");

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
