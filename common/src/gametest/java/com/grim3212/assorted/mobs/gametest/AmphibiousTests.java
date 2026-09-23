package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.common.entity.AmphibiousAnimal;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.entity.Narwhal;
import com.grim3212.assorted.mobs.common.entity.SeaOtter;
import com.grim3212.assorted.mobs.common.entity.Seal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
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
        out.accept("sea_creatures_stay_like_farm_animals", AmphibiousTests::seaCreaturesStayLikeFarmAnimals);
        out.accept("struck_seal_bolts_for_the_water", AmphibiousTests::struckSealBoltsForTheWater);
        out.accept("swimmers_climb_out_onto_the_bank", AmphibiousTests::swimmersClimbOutOntoTheBank);
        out.accept("hauled_out_seal_keeps_to_its_shore", AmphibiousTests::hauledOutSealKeepsToItsShore);
        out.accept("seals_spawn_by_the_water", AmphibiousTests::sealsSpawnByTheWater);
        out.accept("seals_and_walruses_spawn_on_snow_and_ice", AmphibiousTests::sealsAndWalrusesSpawnOnSnowAndIce);
        out.accept("sea_otters_spawn_in_the_water", AmphibiousTests::seaOttersSpawnInTheWater);
        out.accept("sea_otters_keep_out_of_the_cold", AmphibiousTests::seaOttersKeepOutOfTheCold);
        out.accept("narwhal_raises_its_tusk_at_the_surface", AmphibiousTests::narwhalRaisesItsTuskAtTheSurface);
        out.accept("sea_creatures_take_to_unlit_water", AmphibiousTests::seaCreaturesTakeToUnlitWater);
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

    /** Animal#removeWhenFarAway is false, so one led home to a shell farm stays there, as a cow does. The narwhal is a WaterAnimal and still goes. */
    private static void seaCreaturesStayLikeFarmAnimals(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        for (EntityType<? extends AmphibiousAnimal> type : List.of(MobsEntities.SEA_OTTER.get(), MobsEntities.SEAL.get(), MobsEntities.WALRUS.get())) {
            AmphibiousAnimal mob = type.create(level, EntitySpawnReason.NATURAL);
            helper.assertTrue(mob != null, type.getDescriptionId() + " could not be created");
            helper.assertFalse(mob.removeWhenFarAway(10000.0D), type.getDescriptionId() + " despawns once a player walks off");
            mob.discard();
        }
        Narwhal narwhal = MobsEntities.NARWHAL.get().create(level, EntitySpawnReason.NATURAL);
        helper.assertTrue(narwhal != null && narwhal.removeWhenFarAway(10000.0D), "a narwhal never despawns, so the cold seas fill with them");
        narwhal.discard();
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

    /** Animal's walk target value goes negative below light 12, and PathfinderMob#checkSpawnRules reads it: an otter could not be set down in shade. */
    private static void seaCreaturesTakeToUnlitWater(GameTestHelper helper) {
        for (BlockPos pos : BlockPos.betweenClosed(1, 1, 1, 5, 5, 5)) {
            helper.setBlock(pos, Blocks.STONE);
        }
        for (BlockPos pos : BlockPos.betweenClosed(2, 2, 2, 4, 4, 4)) {
            helper.setBlock(pos, Blocks.WATER);
        }
        ServerLevel level = helper.getLevel();
        BlockPos deep = helper.absolutePos(new BlockPos(3, 3, 3));

        helper.succeedWhen(() -> {
            helper.assertValueEqual(level.getMaxLocalRawBrightness(deep), 0, "light in the sealed pool");
            // The narwhal is a WaterAnimal, so it had PathfinderMob's neutral value already; these three are Animals.
            for (EntityType<? extends AmphibiousAnimal> type : List.of(MobsEntities.SEA_OTTER.get(), MobsEntities.SEAL.get(), MobsEntities.WALRUS.get())) {
                AmphibiousAnimal mob = type.create(level, EntitySpawnReason.NATURAL);
                helper.assertTrue(mob != null, type.getDescriptionId() + " could not be created");
                mob.snapTo(deep.getX() + 0.5D, deep.getY(), deep.getZ() + 0.5D, 0.0F, 0.0F);
                helper.assertTrue(mob.getWalkTargetValue(deep, level) >= 0.0F, type.getDescriptionId() + " will not path into unlit water");
                helper.assertTrue(mob.checkSpawnRules(level, EntitySpawnReason.NATURAL), type.getDescriptionId() + " turns down unlit water");
                // Wanting no water, the sea is worth no more than the ice, so wandering ashore stays put.
                helper.assertValueEqual(mob.wantsWater(), false, type.getDescriptionId() + " in the water wants water");
                helper.assertValueEqual(mob.getWalkTargetValue(deep.above(4), level), mob.getWalkTargetValue(deep, level), type.getDescriptionId() + " prefers water while it is not after any");
                mob.discard();
            }
        });
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

    /** Vanilla's two questions of a spot, as both spawners ask them. */
    private static boolean spawnsAt(GameTestHelper helper, EntityType<?> type, BlockPos spot) {
        return SpawnPlacements.isSpawnPositionOk(type, helper.getLevel(), helper.absolutePos(spot))
                && SpawnPlacements.checkSpawnRules(type, helper.getLevel(), EntitySpawnReason.NATURAL, helper.absolutePos(spot), helper.getLevel().getRandom());
    }

    /**
     * The whole placement, as the spawner asks it: the ground, the room above it, and the rule. On plain ice above all,
     * which vanilla lets only a polar bear stand on; on snow blocks and packed ice; and on the grass under a snow layer,
     * where the layer is what the spawner lands in. Not in powder snow, which is a hole.
     */
    private static void sealsAndWalrusesSpawnOnSnowAndIce(GameTestHelper helper) {
        BlockPos spot = new BlockPos(2, 2, 4);
        // Three east and one down, the nearest place the rule looks: not so far that a neighbouring test's pool is what it finds.
        helper.setBlock(spot.east(3).below(), Blocks.WATER);
        for (EntityType<?> type : List.of(MobsEntities.SEAL.get(), MobsEntities.WALRUS.get())) {
            for (Block ground : List.of(Blocks.ICE, Blocks.PACKED_ICE, Blocks.SNOW_BLOCK, Blocks.GRASS_BLOCK)) {
                helper.setBlock(spot.below(), ground);
                helper.setBlock(spot, ground == Blocks.GRASS_BLOCK ? Blocks.SNOW : Blocks.AIR);
                helper.assertTrue(spawnsAt(helper, type, spot), type.getDescriptionId() + " will not spawn on " + ground.getName().getString());
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
        // Daylight test environment: PathfinderMob#checkSpawnRules is the light-based walk value, and the Fabric runner's
        // world may be at night. succeedWhen, because the light engine lags the blocks just placed.
        helper.succeedWhen(() -> {
            helper.assertTrue(otter.checkSpawnRules(level, EntitySpawnReason.NATURAL), "a new otter turns its own spawn down");
            helper.assertTrue(otter.checkSpawnObstruction(level), "a new otter takes the water it is spawned in for an obstruction");
        });
    }

    /**
     * The spawn rule's own question of a biome, asked of the biomes themselves: the frozen river and oceans are in the
     * tags the otter spawns by, and this is what keeps it out of them. The cold ocean is as warm as any other.
     */
    private static void seaOttersKeepOutOfTheCold(GameTestHelper helper) {
        HolderLookup.RegistryLookup<Biome> biomes = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        for (ResourceKey<Biome> warm : List.of(Biomes.RIVER, Biomes.OCEAN, Biomes.COLD_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN)) {
            helper.assertTrue(SeaOtter.isWarmEnough(biomes.getOrThrow(warm)), "an otter turns down " + warm.identifier());
        }
        // Not the deep frozen ocean, which is as warm at heart as the cold one and only frozen at the top; it is no shallow sea, so it is not in the tag at all.
        for (ResourceKey<Biome> cold : List.of(Biomes.FROZEN_RIVER, Biomes.FROZEN_OCEAN)) {
            helper.assertFalse(SeaOtter.isWarmEnough(biomes.getOrThrow(cold)), "an otter spawns in " + cold.identifier());
        }
        helper.succeed();
    }
}
