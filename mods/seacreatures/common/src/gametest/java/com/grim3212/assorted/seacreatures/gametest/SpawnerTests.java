package com.grim3212.assorted.seacreatures.gametest;

import com.grim3212.assorted.lib.spawn.SpawnHabit;
import com.grim3212.assorted.lib.spawn.SpawnHabitBuilder;
import com.grim3212.assorted.lib.spawn.WaterSite;
import com.grim3212.assorted.seacreatures.api.SeaCreaturesTags;
import com.grim3212.assorted.seacreatures.common.entity.SeaCreaturesEntities;
import com.grim3212.assorted.seacreatures.common.entity.Seal;
import com.grim3212.assorted.seacreatures.common.entity.Walrus;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.seacreatures.gametest.SeaCreaturesTestSupport.*;

/** The spawn habits themselves, asked to set this part's creatures down. */
final class SpawnerTests {

    private SpawnerTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("spawner_sets_down_a_whole_herd_of_seals", SpawnerTests::spawnerSetsDownAWholeHerdOfSeals);
        out.accept("seals_and_walruses_share_one_herd_cap", SpawnerTests::sealsAndWalrusesShareOneHerdCap);
        out.accept("spawner_finds_the_top_of_the_water", SpawnerTests::spawnerFindsTheTopOfTheWater);
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
        return SpawnHabitBuilder.of(SeaCreaturesEntities.SEAL).onLand().distance(0, 48).group(2, 4).spread(3);
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
        Walrus one = helper.spawnWithNoFreeWill(SeaCreaturesEntities.WALRUS.get(), new BlockPos(1, 3, 1));
        Walrus two = helper.spawnWithNoFreeWill(SeaCreaturesEntities.WALRUS.get(), new BlockPos(7, 3, 7));
        SpawnHabit habit = seals().cap(8, 2, SeaCreaturesTags.EntityTypes.ICE_HERD).build();
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
}
