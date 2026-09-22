package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.common.entity.AmphibiousAnimal;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.entity.Narwhal;
import com.grim3212.assorted.mobs.common.entity.SeaOtter;
import com.grim3212.assorted.mobs.common.entity.Seal;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.mobs.gametest.MobsTestSupport.*;

/** The seal, the walrus and the sea otter in and out of the water, and the narwhal that never leaves it. */
final class AmphibiousTests {

    private AmphibiousTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("sea_otter_floats_on_its_back", AmphibiousTests::seaOtterFloatsOnItsBack);
        out.accept("fed_sea_otter_stays", AmphibiousTests::fedSeaOtterStays);
        out.accept("struck_seal_bolts_for_the_water", AmphibiousTests::struckSealBoltsForTheWater);
        out.accept("swimmers_climb_out_onto_the_bank", AmphibiousTests::swimmersClimbOutOntoTheBank);
        out.accept("hauled_out_seal_keeps_to_its_shore", AmphibiousTests::hauledOutSealKeepsToItsShore);
        out.accept("seals_spawn_by_the_water", AmphibiousTests::sealsSpawnByTheWater);
        out.accept("seals_and_walruses_spawn_on_snow_and_ice", AmphibiousTests::sealsAndWalrusesSpawnOnSnowAndIce);
        out.accept("sea_otters_spawn_in_the_water", AmphibiousTests::seaOttersSpawnInTheWater);
        out.accept("narwhal_raises_its_tusk_at_the_surface", AmphibiousTests::narwhalRaisesItsTuskAtTheSurface);
    }

    /** Glass from {@code x} to the far wall, one block in from the rest, filled {@code depth} deep with water. */
    private static void pool(GameTestHelper helper, int x, int depth) {
        for (BlockPos pos : BlockPos.betweenClosed(x, 1, 1, 7, depth, 7)) {
            boolean wall = pos.getX() == x || pos.getX() == 7 || pos.getZ() == 1 || pos.getZ() == 7;
            helper.setBlock(pos, wall ? Blocks.GLASS : Blocks.WATER);
        }
    }

    /** Let go at the bottom of three blocks of water: it has to come up, find the surface and settle there. */
    private static void seaOtterFloatsOnItsBack(GameTestHelper helper) {
        pool(helper, 1, 3);
        SeaOtter otter = helper.spawn(MobsEntities.SEA_OTTER.get(), CENTRE);

        helper.succeedWhen(() -> {
            helper.assertTrue(otter.isFloating(), "the otter is not on its back");
            helper.assertTrue(otter.isInWater() && !otter.isUnderWater(), "an otter on its back is not at the surface");
        });
    }

    /** It despawns with the squid unless someone has taken an interest in it. */
    private static void fedSeaOtterStays(GameTestHelper helper) {
        ServerPlayer player = standingPlayer(helper, CENTRE.north(2));
        // Not helper.spawn, which makes whatever it spawns persistent itself.
        SeaOtter otter = MobsEntities.SEA_OTTER.get().create(helper.getLevel(), EntitySpawnReason.NATURAL);
        otter.snapTo(helper.absoluteVec(Vec3.atBottomCenterOf(CENTRE)));
        otter.setNoAi(true);
        helper.getLevel().addFreshEntity(otter);
        helper.assertTrue(otter.removeWhenFarAway(0.0D) && !otter.isPersistenceRequired(), "a wild otter never despawns");
        helper.assertTrue(otter.isFood(new ItemStack(Items.COD)), "an otter turns down fish");

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.COD));
        otter.mobInteract(player, InteractionHand.MAIN_HAND);
        helper.assertTrue(otter.isPersistenceRequired(), "an otter that was fed may still despawn");
        helper.succeed();
    }

    /** Stone to the west, a pool to the east, and a seal on the stone that has just been hit. */
    private static void struckSealBoltsForTheWater(GameTestHelper helper) {
        for (BlockPos pos : BlockPos.betweenClosed(1, 1, 1, 3, 1, 7)) {
            helper.setBlock(pos, Blocks.STONE);
        }
        pool(helper, 3, 1);
        // The pool's near wall is the shore.
        for (BlockPos pos : BlockPos.betweenClosed(3, 1, 2, 3, 1, 6)) {
            helper.setBlock(pos, Blocks.STONE);
        }

        ServerPlayer player = standingPlayer(helper, new BlockPos(1, 2, 1));
        Seal seal = helper.spawn(MobsEntities.SEAL.get(), new BlockPos(2, 2, 4));

        // Not on its first tick: a blow then carries the same timestamp as no blow at all.
        helper.startSequence()
                .thenIdle(5)
                .thenExecute(() -> seal.hurtServer(helper.getLevel(), seal.damageSources().playerAttack(player), 1.0F))
                .thenWaitUntil(() -> helper.assertTrue(seal.isInWater(), "the seal is still ashore"))
                .thenSucceed();
    }

    /** From the bottom of five blocks of water: up to just under the surface, and no further. */
    private static void narwhalRaisesItsTuskAtTheSurface(GameTestHelper helper) {
        pool(helper, 1, 5);
        Narwhal narwhal = helper.spawn(MobsEntities.NARWHAL.get(), CENTRE);
        narwhal.raiseTusk();

        helper.succeedWhen(() -> {
            helper.assertTrue(narwhal.isRaisingTusk(), "the narwhal has not raised its tusk");
            helper.assertTrue(narwhal.isUnderWater(), "a narwhal raising its tusk has come right out of the water");
        });
    }

    /**
     * Three blocks of water with a stone bank beside it, its top the usual tenth of a block above the
     * surface: nothing to a walker, and a wall to anything afloat that is not lifted over it. Each is sent
     * by its own haul-out goal, so this is also that it keeps wanting to leave while it bumps at the bank.
     */
    private static void swimmersClimbOutOntoTheBank(GameTestHelper helper) {
        for (BlockPos pos : BlockPos.betweenClosed(1, 1, 1, 2, 3, 7)) {
            helper.setBlock(pos, Blocks.STONE);
        }
        pool(helper, 2, 3);
        for (BlockPos pos : BlockPos.betweenClosed(2, 1, 2, 2, 3, 6)) {
            helper.setBlock(pos, Blocks.STONE);
        }

        List<AmphibiousAnimal> swimmers = List.of(
                helper.spawn(MobsEntities.SEAL.get(), new BlockPos(5, 2, 2)),
                helper.spawn(MobsEntities.WALRUS.get(), new BlockPos(5, 2, 4)),
                helper.spawn(MobsEntities.SEA_OTTER.get(), new BlockPos(5, 2, 6)));

        // By its own haul-out goal, as it would in the world. Once ashore counts: what it does after that is its own business.
        Set<AmphibiousAnimal> landed = new HashSet<>();
        helper.startSequence()
                .thenIdle(10)
                .thenExecute(() -> swimmers.forEach(AmphibiousAnimal::tireOfHabitat))
                .thenWaitUntil(() -> {
                    for (AmphibiousAnimal swimmer : swimmers) {
                        if (!swimmer.isInWater() && swimmer.onGround()) {
                            landed.add(swimmer);
                        }
                    }
                    for (AmphibiousAnimal swimmer : swimmers) {
                        helper.assertTrue(landed.contains(swimmer), swimmer.getType().getDescriptionId() + " has not got out of the water");
                    }
                })
                .thenSucceed();
    }

    /**
     * Out of the pool and onto the stone: it knows the water it left, keeps to within range of it, and still
     * does after a reload. Carried off further than that, it wants the water again at once.
     */
    private static void hauledOutSealKeepsToItsShore(GameTestHelper helper) {
        for (BlockPos pos : BlockPos.betweenClosed(1, 1, 1, 2, 3, 7)) {
            helper.setBlock(pos, Blocks.STONE);
        }
        pool(helper, 2, 3);
        for (BlockPos pos : BlockPos.betweenClosed(2, 1, 2, 2, 3, 6)) {
            helper.setBlock(pos, Blocks.STONE);
        }
        Seal seal = helper.spawn(MobsEntities.SEAL.get(), new BlockPos(5, 2, 4));

        helper.startSequence()
                .thenIdle(25)
                .thenExecute(() -> {
                    helper.assertTrue(seal.getShore() != null, "a seal in the water does not know where it is");
                    helper.assertFalse(seal.hasHome(), "a seal in the water is kept to a range");
                    seal.tireOfHabitat();
                })
                // Its next look at where it is, once it is ashore.
                .thenWaitUntil(() -> helper.assertTrue(!seal.isInWater() && seal.onGround() && seal.hasHome(), "the seal is not ashore and kept to its shore yet"))
                .thenExecute(() -> {
                    BlockPos shore = seal.getShore();
                    helper.assertTrue(shore != null && helper.getLevel().getFluidState(shore).is(FluidTags.WATER), "what a hauled out seal takes for its shore is not water");
                    helper.assertValueEqual(seal.getHomePosition(), shore, "where a hauled out seal keeps to");
                    helper.assertValueEqual(seal.getHomeRadius(), AmphibiousAnimal.SHORE_RANGE, "how far a hauled out seal keeps to its shore");
                    helper.assertFalse(seal.wantsWater(), "a seal that has only just hauled out wants back in");
                    helper.assertValueEqual(afterReload(helper, seal, MobsEntities.SEAL.get()).getShore(), shore, "its shore after a reload");

                    // Forty blocks up is as far from its shore as forty blocks inland.
                    seal.setNoGravity(true);
                    seal.snapTo(seal.getX(), seal.getY() + 40.0D, seal.getZ());
                })
                .thenWaitUntil(() -> helper.assertTrue(seal.wantsWater(), "a seal carried far from its shore is content to stay there"))
                .thenExecute(seal::discard)
                .thenSucceed();
    }

    /** Snow with the sea six blocks off will do, and the rule is asked at the block above the snow. */
    private static void sealsSpawnByTheWater(GameTestHelper helper) {
        BlockPos snow = new BlockPos(1, 1, 4);
        helper.setBlock(snow, Blocks.SNOW_BLOCK);
        helper.setBlock(snow.east(6), Blocks.WATER);
        helper.assertTrue(Seal.checkArcticSpawnRules(MobsEntities.SEAL.get(), helper.getLevel(), EntitySpawnReason.NATURAL, helper.absolutePos(snow.above()), helper.getLevel().getRandom()),
                "a seal will not spawn on snow six blocks from the water");
        helper.succeed();
    }

    /**
     * The whole placement, as the spawner asks it: the ground, the room above it, and the rule. On plain ice above all,
     * which vanilla lets only a polar bear stand on; on snow blocks and packed ice; and on the grass under a snow layer,
     * where the layer is what the spawner lands in. Not in powder snow, which is a hole.
     */
    private static void sealsAndWalrusesSpawnOnSnowAndIce(GameTestHelper helper) {
        BlockPos water = new BlockPos(6, 1, 4);
        helper.setBlock(water, Blocks.WATER);
        BlockPos spot = new BlockPos(2, 2, 4);
        for (EntityType<?> type : List.of(MobsEntities.SEAL.get(), MobsEntities.WALRUS.get())) {
            for (Block ground : List.of(Blocks.ICE, Blocks.PACKED_ICE, Blocks.SNOW_BLOCK, Blocks.GRASS_BLOCK)) {
                helper.setBlock(spot.below(), ground);
                helper.setBlock(spot, ground == Blocks.GRASS_BLOCK ? Blocks.SNOW : Blocks.AIR);
                helper.assertTrue(SpawnPlacements.isSpawnPositionOk(type, helper.getLevel(), helper.absolutePos(spot))
                        && SpawnPlacements.checkSpawnRules(type, helper.getLevel(), EntitySpawnReason.NATURAL, helper.absolutePos(spot), helper.getLevel().getRandom()),
                        type.getDescriptionId() + " will not spawn on " + ground.getName().getString());
            }
            helper.setBlock(spot.below(), Blocks.POWDER_SNOW);
            helper.setBlock(spot, Blocks.AIR);
            helper.assertFalse(SpawnPlacements.isSpawnPositionOk(type, helper.getLevel(), helper.absolutePos(spot)), type.getDescriptionId() + " spawns on powder snow");
        }
        helper.succeed();
    }

    /**
     * Every check the spawner makes of an otter, in the order it makes them: the placement and the two the new mob is
     * asked itself, at the top of the pool; and the rule, which reads no blocks, at sea level, which is far above the box.
     * The last of the mob's own is the one a land animal fails in water.
     */
    private static void seaOttersSpawnInTheWater(GameTestHelper helper) {
        pool(helper, 1, 3);
        ServerLevel level = helper.getLevel();
        BlockPos top = helper.absolutePos(CENTRE.above(2));
        EntityType<SeaOtter> type = MobsEntities.SEA_OTTER.get();

        helper.assertTrue(SpawnPlacements.isSpawnPositionOk(type, level, top), "the water is no place for an otter");
        BlockPos atSeaLevel = new BlockPos(top.getX(), level.getSeaLevel() - 1, top.getZ());
        helper.assertTrue(SpawnPlacements.checkSpawnRules(type, level, EntitySpawnReason.NATURAL, atSeaLevel, level.getRandom()), "an otter will not spawn just under sea level");
        SeaOtter otter = type.create(level, EntitySpawnReason.NATURAL);
        otter.snapTo(top.getX() + 0.5D, top.getY(), top.getZ() + 0.5D, 0.0F, 0.0F);
        helper.assertTrue(otter.checkSpawnRules(level, EntitySpawnReason.NATURAL), "a new otter turns its own spawn down");
        helper.assertTrue(otter.checkSpawnObstruction(level), "a new otter takes the water it is spawned in for an obstruction");
        helper.succeed();
    }
}
