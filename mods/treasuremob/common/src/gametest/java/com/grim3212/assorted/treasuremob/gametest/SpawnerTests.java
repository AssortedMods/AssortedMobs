package com.grim3212.assorted.treasuremob.gametest;

import com.grim3212.assorted.lib.spawn.SpawnHabit;
import com.grim3212.assorted.lib.spawn.SpawnHabits;
import com.grim3212.assorted.treasuremob.Constants;
import com.grim3212.assorted.treasuremob.common.entity.TreasureMob;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.treasuremob.gametest.TreasureMobTestSupport.*;

/** The spawn habits themselves, asked to set this part's creatures down. */
final class SpawnerTests {

    private SpawnerTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("spawner_keeps_treasure_mobs_inside_their_structures", SpawnerTests::spawnerKeepsTreasureMobsInsideTheirStructures);
    }

    static SpawnHabit habit(String name) {
        SpawnHabit habit = SpawnHabits.get(Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        if (habit == null) {
            throw new IllegalStateException("spawn habit " + name + " did not load; loaded: " + SpawnHabits.all().keySet());
        }
        return habit;
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
