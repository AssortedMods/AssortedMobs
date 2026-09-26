package com.grim3212.assorted.seacreatures.gametest;

import com.grim3212.assorted.seacreatures.common.entity.SeaCreaturesEntities;
import com.grim3212.assorted.seacreatures.common.entity.SeaOtter;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * What a headless server cannot see: every sea creature survives render state extraction, side on, and generating a
 * real frozen ocean reads no chunk it may not. Run with {@code ./gradlew :seacreatures:fabric:runClientGameTest};
 * it exits non-zero on a failure.
 */
public class SeaCreaturesClientGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getConnection().waitForChunksRender();

            List<Integer> row = world.getServer().computeOnServer(server -> {
                ServerLevel level = server.overworld();
                level.getGameRules().set(GameRules.ADVANCE_TIME, false, server);
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set noon");
                ServerPlayer player = server.getPlayerList().getPlayers().get(0);
                player.snapTo(player.getX(), player.getY(), player.getZ(), 0.0F, 15.0F);

                List<Integer> ids = new ArrayList<>();
                // The sea creatures, to the north and side on, for a picture of their own.
                List<EntityType<? extends Mob>> animals = List.of(SeaCreaturesEntities.SEAL.get(), SeaCreaturesEntities.WALRUS.get(), SeaCreaturesEntities.SEA_OTTER.get(), SeaCreaturesEntities.NARWHAL.get());
                for (int i = 0; i < animals.size(); i++) {
                    Mob mob = animals.get(i).create(level, EntitySpawnReason.COMMAND);
                    boolean narwhal = i == animals.size() - 1;
                    // The narwhal hangs in the air over the rest, as it would in water: on the ground the walrus hides it.
                    mob.snapTo(player.getX() + (narwhal ? 0.0D : (i - 1.0D) * 4.5D), player.getY() + (narwhal ? 2.5D : 0.0D), player.getZ() - (narwhal ? 8.0D : 6.0D), 90.0F, 0.0F);
                    mob.setYHeadRot(90.0F);
                    mob.setYBodyRot(90.0F);
                    mob.setNoAi(true);
                    if (mob instanceof SeaOtter otter) {
                        // On its back in a puddle let into the floor, at the depth it floats at. In the middle
                        // of it: the row is spaced in halves, and half in the turf beside it, it suffocates.
                        BlockPos puddle = otter.blockPosition().below();
                        level.setBlockAndUpdate(puddle, Blocks.WATER.defaultBlockState());
                        otter.snapTo(puddle.getX() + 0.5D, puddle.getY() + 0.58D, puddle.getZ() + 0.5D, 90.0F, 0.0F);
                        otter.setFloating(true);
                    }
                    level.addFreshEntity(mob);
                    ids.add(mob.getId());
                }

                return ids;
            });

            context.waitFor(client -> row.stream().allMatch(id -> client.level.getEntity(id) != null));
            context.waitTicks(10);
            context.runOnClient(client -> row.forEach(id -> client.getEntityRenderDispatcher().extractEntity(client.level.getEntity(id), 1.0F)));
            world.getServer().runOnServer(server -> {
                ServerPlayer player = server.getPlayerList().getPlayers().get(0);
                player.connection.teleport(player.getX(), player.getY(), player.getZ(), 180.0F, 10.0F);
            });
            context.waitTicks(10);
            context.takeScreenshot("assortedseacreatures_sea_creatures");
        }

        worldgenKeepsToItsChunk(context);
    }

    /**
     * A world that is frozen ocean all over, made for real: the one place the seal and the walrus are placed as the
     * terrain is, and asked whether there is water near. The flat test worlds never generate anything, so nothing
     * else here or on the server would notice a spawn rule that looks outside the chunk being made.
     */
    private static void worldgenKeepsToItsChunk(ClientGameTestContext context) {
        UnsafeTerrainReadWatch watch = UnsafeTerrainReadWatch.install();
        try (TestSingleplayerContext frozen = context.worldBuilder().adjustSettings(creator -> {
            // Off in a test world, for the sake of tests that count what is there. Here it is the point.
            creator.getGameRules().set(GameRules.SPAWN_MOBS, true, null);
            creator.updateDimensions((registries, dimensions) -> {
                Holder<Biome> biome = registries.lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.FROZEN_OCEAN);
                Holder<NoiseGeneratorSettings> noise = registries.lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(NoiseGeneratorSettings.OVERWORLD);
                return dimensions.replaceOverworldGenerator(registries, new NoiseBasedChunkGenerator(new FixedBiomeSource(biome), noise));
            });
        }).create()) {
            frozen.getConnection().waitForChunksRender();
            context.waitTicks(100);
        } finally {
            watch.remove();
        }

        if (watch.count() > 0) {
            throw new AssertionError(watch.count() + " unsafe terrain reads while a frozen ocean generated, the first: " + watch.first());
        }
    }
}
