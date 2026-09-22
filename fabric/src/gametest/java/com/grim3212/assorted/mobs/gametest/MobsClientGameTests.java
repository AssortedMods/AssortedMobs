package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.common.entity.Bobomb;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.entity.Parabuzzy;
import com.grim3212.assorted.mobs.common.entity.SeaOtter;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.CameraType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.ArrayList;
import java.util.List;

/**
 * What a headless server cannot see: every creature survives render state extraction, a creature on
 * a player's head is drawn on it, on the client, where it is pinned rather than riding, and a
 * parabuzzy there slows the player's own fall, and that generating a real world reads no chunk it may not. Run with {@code ./gradlew :fabric:runClientGameTest};
 * it exits non-zero on a failure.
 */
public class MobsClientGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getConnection().waitForChunksRender();

            record Placed(List<Integer> row, int perched) {
            }

            Placed placed = world.getServer().computeOnServer(server -> {
                ServerLevel level = server.overworld();
                level.getGameRules().set(GameRules.ADVANCE_TIME, false, server);
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set noon");
                ServerPlayer player = server.getPlayerList().getPlayers().get(0);
                player.snapTo(player.getX(), player.getY(), player.getZ(), 0.0F, 15.0F);

                List<Integer> row = new ArrayList<>();
                List<EntityType<? extends Mob>> types = List.of(MobsEntities.ICE_PIXIE.get(), MobsEntities.TREASURE_MOB.get(), MobsEntities.BOBOMB.get(),
                        MobsEntities.PARABUZZY.get(), MobsEntities.PARABUZZY.get(), MobsEntities.PARABUZZY.get(), MobsEntities.PARABUZZY.get());
                for (int i = 0; i < types.size(); i++) {
                    Mob mob = types.get(i).create(level, EntitySpawnReason.COMMAND);
                    mob.snapTo(player.getX() + (i - types.size() / 2) * 1.2D, player.getY(), player.getZ() + 5.0D, 180.0F, 0.0F);
                    mob.setYHeadRot(180.0F);
                    mob.setYBodyRot(180.0F);
                    mob.setNoAi(true);
                    if (mob instanceof Parabuzzy parabuzzy) {
                        parabuzzy.setVariant(Parabuzzy.Variant.values()[i - 3]);
                    }
                    level.addFreshEntity(mob);
                    row.add(mob.getId());
                }

                // The sea creatures, to the north and side on, for a picture of their own.
                List<EntityType<? extends Mob>> animals = List.of(MobsEntities.SEAL.get(), MobsEntities.WALRUS.get(), MobsEntities.SEA_OTTER.get(), MobsEntities.NARWHAL.get());
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
                    row.add(mob.getId());
                }

                Bobomb onHead = MobsEntities.BOBOMB.get().create(level, EntitySpawnReason.COMMAND);
                onHead.snapTo(player.getX() + 1.0D, player.getY(), player.getZ());
                level.addFreshEntity(onHead);
                onHead.perch().perchOn(player);
                return new Placed(row, onHead.getId());
            });

            context.waitFor(client -> placed.row().stream().allMatch(id -> client.level.getEntity(id) != null)
                    && client.level.getEntity(placed.perched()) instanceof Bobomb bobomb && bobomb.perch().player() != null);
            context.waitTicks(10);

            context.runOnClient(client -> {
                for (int id : placed.row()) {
                    Entity entity = client.level.getEntity(id);
                    client.getEntityRenderDispatcher().extractEntity(entity, 1.0F);
                }

                Entity perched = client.level.getEntity(placed.perched());
                EntityRenderState state = client.getEntityRenderDispatcher().extractEntity(perched, 1.0F);
                double headY = client.player.getY() + client.player.getBbHeight();
                if (Math.abs(state.y - headY) > 1.0E-4D || Math.abs(state.x - client.player.getX()) > 1.0E-4D) {
                    throw new AssertionError("a Bob-omb on the player's head is drawn at " + state.x + ", " + state.y + ", not on the head at " + client.player.getX() + ", " + headY);
                }
            });

            context.waitTicks(10);
            context.takeScreenshot("assortedmobs_lineup");
            world.getServer().runOnServer(server -> {
                ServerPlayer player = server.getPlayerList().getPlayers().get(0);
                player.connection.teleport(player.getX(), player.getY(), player.getZ(), 180.0F, 10.0F);
            });
            context.waitTicks(10);
            context.takeScreenshot("assortedmobs_sea_creatures");
            world.getServer().runOnServer(server -> {
                ServerPlayer player = server.getPlayerList().getPlayers().get(0);
                player.connection.teleport(player.getX(), player.getY(), player.getZ(), 0.0F, 15.0F);
            });
            context.waitTicks(10);
            context.runOnClient(client -> client.options.setCameraType(CameraType.THIRD_PERSON_FRONT));
            context.waitTicks(5);
            context.takeScreenshot("assortedmobs_perched");
            context.runOnClient(client -> client.options.setCameraType(CameraType.FIRST_PERSON));

            parabuzzySlowsTheFall(context, world, placed.perched());
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

    /**
     * A player's fall is simulated on its own client, so this is the only place the parabuzzy's drift
     * can be seen: dropped from high up, a player carrying one is still airborne and falling slowly.
     */
    private static void parabuzzySlowsTheFall(ClientGameTestContext context, TestSingleplayerContext world, int bobomb) {
        double start = world.getServer().computeOnServer(server -> {
            ServerLevel level = server.overworld();
            ServerPlayer player = server.getPlayerList().getPlayers().get(0);
            level.getEntity(bobomb).discard();

            double y = player.getY() + 60.0D;
            player.teleportTo(player.getX(), y, player.getZ());
            Parabuzzy parabuzzy = MobsEntities.PARABUZZY.get().create(level, EntitySpawnReason.COMMAND);
            parabuzzy.snapTo(player.getX(), y, player.getZ());
            level.addFreshEntity(parabuzzy);
            parabuzzy.perch().perchOn(player);
            return y;
        });

        context.waitFor(client -> client.player.getY() > start - 1.0D && client.level.getEntitiesOfClass(Parabuzzy.class,
                client.player.getBoundingBox().inflate(1.0D, 2.0D, 1.0D), parabuzzy -> parabuzzy.perch().isOn(client.player)).size() == 1);
        context.waitTicks(20);

        context.runOnClient(client -> {
            double dropped = start - client.player.getY();
            double speed = client.player.getDeltaMovement().y;
            if (client.player.onGround() || dropped > 8.0D || speed < -0.3D) {
                throw new AssertionError("a player carrying a parabuzzy fell " + dropped + " blocks in 20 ticks and is falling at " + speed + " a tick");
            }
        });

        world.getServer().runOnServer(server -> {
            ServerPlayer player = server.getPlayerList().getPlayers().get(0);
            server.overworld().getEntitiesOfClass(Parabuzzy.class, player.getBoundingBox().inflate(1.0D, 2.0D, 1.0D)).forEach(Entity::discard);
            player.teleportTo(player.getX(), start - 60.0D, player.getZ());
        });
        context.waitTicks(5);
    }
}
