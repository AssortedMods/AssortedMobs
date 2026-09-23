package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.lib.spawn.SpawnHabit;
import com.grim3212.assorted.lib.spawn.SpawnHabitBuilder;
import com.grim3212.assorted.lib.spawn.SpawnHabits;
import com.grim3212.assorted.lib.spawn.WaterSite;
import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.api.MobsTags;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.entity.Seal;
import com.grim3212.assorted.mobs.common.entity.TreasureMob;
import com.grim3212.assorted.mobs.common.entity.Walrus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.mobs.gametest.MobsTestSupport.*;

/**
 * The mod's spawn habits, asked directly: the test server keeps spawn_mobs off. Through succeedWhen, not on
 * the tick the terrain was laid: PathfinderMob#checkSpawnRules reads the light, which lags fresh blocks.
 */
final class SpawnerTests {

    private SpawnerTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("spawner_sets_down_a_whole_herd_of_seals", SpawnerTests::spawnerSetsDownAWholeHerdOfSeals);
        out.accept("seals_and_walruses_share_one_herd_cap", SpawnerTests::sealsAndWalrusesShareOneHerdCap);
        out.accept("spawner_finds_the_top_of_the_water", SpawnerTests::spawnerFindsTheTopOfTheWater);
        out.accept("spawner_keeps_treasure_mobs_inside_their_structures", SpawnerTests::spawnerKeepsTreasureMobsInsideTheirStructures);
    }

    static SpawnHabit habit(String name) {
        SpawnHabit habit = SpawnHabits.get(Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        if (habit == null) {
            throw new IllegalStateException("spawn habit " + name + " did not load; loaded: " + SpawnHabits.all().keySet());
        }
        return habit;
    }

    /** Ice over water across the whole box, as a frozen ocean is: every block a spot with the sea just under it. */
    private static void iceSheet(GameTestHelper helper) {
        for (BlockPos pos : BlockPos.betweenClosed(0, 1, 0, 8, 1, 8)) {
            helper.setBlock(pos, Blocks.WATER);
        }
        for (BlockPos pos : BlockPos.betweenClosed(0, 2, 0, 8, 2, 8)) {
            helper.setBlock(pos, Blocks.ICE);
        }
    }

    /** No odds and no biome, so only what this test put on the ice counts; distance 0 because a test box has players in it. */
    private static SpawnHabitBuilder seals() {
        return SpawnHabitBuilder.of(MobsEntities.SEAL).onLand().distance(0, 48).group(2, 4).spread(3);
    }

    /** Two to four arrive together from one column. */
    private static void spawnerSetsDownAWholeHerdOfSeals(GameTestHelper helper) {
        iceSheet(helper);
        ServerLevel level = helper.getLevel();
        SpawnHabit habit = seals().build();
        helper.succeedWhen(() -> {
            int spawned = habit.spawnPackAt(level, helper.absolutePos(new BlockPos(4, 3, 4)), level.getRandom());
            helper.assertTrue(spawned >= 2 && spawned <= 4, "the spawner set down " + spawned + " seals, not a herd of two to four");
            List<Seal> onTheIce = level.getEntitiesOfClass(Seal.class, AABB.encapsulatingFullBlocks(helper.absolutePos(new BlockPos(0, 2, 0)), helper.absolutePos(new BlockPos(8, 5, 8))));
            helper.assertValueEqual(onTheIce.size(), spawned, "seals on the ice");
            for (Seal seal : onTheIce) {
                helper.assertValueEqual(seal.blockPosition().getY(), helper.absolutePos(new BlockPos(4, 3, 4)).getY(), "the height a seal was set down at");
                seal.discard();
            }
        });
    }

    /** The ice_herd tag: two walruses fill a seal cap of two, one walrus leaves room for a herd. The count comes before any spawning, so it answers on the first tick. */
    private static void sealsAndWalrusesShareOneHerdCap(GameTestHelper helper) {
        iceSheet(helper);
        ServerLevel level = helper.getLevel();
        BlockPos column = helper.absolutePos(new BlockPos(4, 3, 4));
        Walrus one = helper.spawnWithNoFreeWill(MobsEntities.WALRUS.get(), new BlockPos(1, 3, 1));
        Walrus two = helper.spawnWithNoFreeWill(MobsEntities.WALRUS.get(), new BlockPos(7, 3, 7));
        SpawnHabit habit = seals().cap(8, 2, MobsTags.EntityTypes.ICE_HERD).build();
        helper.assertValueEqual(habit.spawnPackAt(level, column, level.getRandom()), 0, "seals set down beside two walruses, with two of the herd the most allowed");
        two.discard();
        helper.succeedWhen(() -> {
            int spawned = habit.spawnPackAt(level, column, level.getRandom());
            helper.assertTrue(spawned >= 2, "no herd was set down beside one walrus, with two of the herd allowed: " + spawned);
            one.discard();
            level.getEntitiesOfClass(Seal.class, new AABB(column).inflate(8.0D)).forEach(Seal::discard);
        });
    }

    /** The water site: the topmost water block of a pool, and nothing where the column is dry. */
    private static void spawnerFindsTheTopOfTheWater(GameTestHelper helper) {
        for (BlockPos pos : BlockPos.betweenClosed(1, 1, 1, 7, 3, 7)) {
            boolean wall = pos.getX() == 1 || pos.getX() == 7 || pos.getZ() == 1 || pos.getZ() == 7;
            helper.setBlock(pos, wall ? Blocks.GLASS : Blocks.WATER);
        }
        ServerLevel level = helper.getLevel();
        BlockPos found = WaterSite.SURFACE.find(level, helper.absolutePos(new BlockPos(4, 1, 4)), level.getRandom());
        helper.assertTrue(found != null, "no water was found in a column of it");
        helper.assertValueEqual(found, helper.absolutePos(new BlockPos(4, 3, 4)), "where the water site put the otter");
        helper.assertTrue(level.getFluidState(found).is(FluidTags.WATER), "the water site found no water");
        helper.assertTrue(WaterSite.SURFACE.find(level, helper.absolutePos(new BlockPos(4, 1, 0)), level.getRandom()) == null, "the water site found water in a dry column");
        helper.succeed();
    }

    /** The loaded treasure_mob habit and a desert pyramid in the sky with a floor: the mob lands inside its piece with the pyramid's loot. Most heights are inside walls, so it takes a few asks. */
    private static void spawnerKeepsTreasureMobsInsideTheirStructures(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        LaidOut pyramid = layOutStructure(helper, BuiltinStructures.DESERT_PYRAMID, 220);
        // A sealed cell at the one column the habit is handed: the pyramid is only laid out, never built, and its rule wants the dark.
        BlockPos cell = pyramid.inside();
        BlockPos.betweenClosed(cell.offset(-1, -1, -1), cell.offset(1, 2, 1))
                .forEach(pos -> level.setBlockAndUpdate(pos, pos.getX() == cell.getX() && pos.getZ() == cell.getZ() && (pos.getY() == cell.getY() || pos.getY() == cell.getY() + 1)
                        ? Blocks.AIR.defaultBlockState() : Blocks.STONE.defaultBlockState()));

        helper.runBeforeTestEnd(() -> BlockPos.betweenClosed(cell.offset(-1, -1, -1), cell.offset(1, 2, 1))
                .forEach(pos -> level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())));

        SpawnHabit habit = habit("treasure_mob");
        Structure structure = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.DESERT_PYRAMID).value();
        // Not on the tick the cell was sealed: its rule wants the dark, and the light engine lags fresh blocks.
        helper.succeedWhen(() -> {
            helper.assertValueEqual(level.getMaxLocalRawBrightness(cell), 0, "light in the sealed cell");
            int spawned = 0;
            for (int asked = 0; asked < 200 && spawned == 0; asked++) {
                spawned = habit.spawnPackAt(level, cell, level.getRandom());
            }
            List<TreasureMob> found = level.getEntitiesOfClass(TreasureMob.class, new AABB(cell).inflate(32.0D));
            found.forEach(TreasureMob::discard);
            helper.assertValueEqual(spawned, 1, "treasure mobs the spawner set down in a desert pyramid");
            helper.assertValueEqual(found.size(), 1, "treasure mobs found in the pyramid");
            TreasureMob mob = found.getFirst();
            BoundingBox piece = level.structureManager().getStructureWithPieceAt(mob.blockPosition(), structure).getBoundingBox();
            helper.assertTrue(piece.isInside(mob.blockPosition()), "the treasure mob was set down outside the pyramid at " + mob.blockPosition());
            helper.assertFalse(mob.getChest().isEmpty(), "the spawned treasure mob has an empty chest");
        });
    }
}
