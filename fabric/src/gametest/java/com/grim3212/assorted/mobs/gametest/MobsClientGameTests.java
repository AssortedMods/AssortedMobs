package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.common.entity.Bobomb;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.entity.Parabuzzy;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.CameraType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.ArrayList;
import java.util.List;

/**
 * What a headless server cannot see: every creature survives render state extraction, a creature on
 * a player's head is drawn on it, on the client, where it is pinned rather than riding, and a
 * parabuzzy there slows the player's own fall. Run with {@code ./gradlew :fabric:runClientGameTest};
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

            context.takeScreenshot("assortedmobs_lineup");
            context.runOnClient(client -> client.options.setCameraType(CameraType.THIRD_PERSON_FRONT));
            context.waitTicks(5);
            context.takeScreenshot("assortedmobs_perched");
            context.runOnClient(client -> client.options.setCameraType(CameraType.FIRST_PERSON));

            parabuzzySlowsTheFall(context, world, placed.perched());
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
