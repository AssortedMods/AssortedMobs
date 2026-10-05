package com.grim3212.assorted.eightbit.gametest;

import com.grim3212.assorted.eightbit.common.entity.Bobomb;
import com.grim3212.assorted.eightbit.common.entity.EightBitEntities;
import com.grim3212.assorted.eightbit.common.entity.Parabuzzy;
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
 * What a headless server cannot see: the Bob-omb and every colour of parabuzzy survive render state extraction, a creature on a player's head is drawn on it, and a parabuzzy there slows the player's own fall. Run with {@code ./gradlew :eightbit:fabric:runClientGameTest};
 * it exits non-zero on a failure.
 */
public class EightBitClientGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getConnection().waitForChunksRender();
            lineUp(context, world, List.of(EightBitEntities.BOBOMB.get(), EightBitEntities.PARABUZZY.get(), EightBitEntities.PARABUZZY.get(), EightBitEntities.PARABUZZY.get(), EightBitEntities.PARABUZZY.get()));
            context.takeScreenshot("assortedeightbit_lineup");
            perchedIsDrawnOnTheHead(context, world);
        }
    }

    /** A Bob-omb on a player's head, on the client, is pinned there rather than riding, and drawn on the head. */
    private static void perchedIsDrawnOnTheHead(ClientGameTestContext context, TestSingleplayerContext world) {
        int perched = world.getServer().computeOnServer(server -> {
            ServerLevel level = server.overworld();
            ServerPlayer player = server.getPlayerList().getPlayers().get(0);
            Bobomb onHead = EightBitEntities.BOBOMB.get().create(level, EntitySpawnReason.COMMAND);
            onHead.snapTo(player.getX() + 1.0D, player.getY(), player.getZ());
            level.addFreshEntity(onHead);
            onHead.perch().perchOn(player);
            return onHead.getId();
        });

        context.waitFor(client -> client.level.getEntity(perched) instanceof Bobomb bobomb && bobomb.perch().player() != null);
        context.waitTicks(10);
        context.runOnClient(client -> {
            EntityRenderState state = client.getEntityRenderDispatcher().extractEntity(client.level.getEntity(perched), 1.0F);
            double headY = client.player.getY() + client.player.getBbHeight();
            if (Math.abs(state.y - headY) > 1.0E-4D || Math.abs(state.x - client.player.getX()) > 1.0E-4D) {
                throw new AssertionError("a Bob-omb on the player's head is drawn at " + state.x + ", " + state.y + ", not on the head at " + client.player.getX() + ", " + headY);
            }
        });

        context.runOnClient(client -> client.options.setCameraType(CameraType.THIRD_PERSON_FRONT));
        context.waitTicks(5);
        context.takeScreenshot("assortedeightbit_perched");
        context.runOnClient(client -> client.options.setCameraType(CameraType.FIRST_PERSON));

        parabuzzySlowsTheFall(context, world, perched);
    }

    /**
     * Stands each of {@code types} in a row in front of the player and has the client extract its render state, which is
     * where a renderer or model that cannot draw it throws. Returns the ids of what it set down.
     */
    private static List<Integer> lineUp(ClientGameTestContext context, TestSingleplayerContext world, List<EntityType<? extends Mob>> types) {
        List<Integer> row = world.getServer().computeOnServer(server -> {
                ServerLevel level = server.overworld();
                level.getGameRules().set(GameRules.ADVANCE_TIME, false, server);
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set noon");
                ServerPlayer player = server.getPlayerList().getPlayers().get(0);
                player.snapTo(player.getX(), player.getY(), player.getZ(), 0.0F, 15.0F);

                List<Integer> ids = new ArrayList<>();
                for (int i = 0; i < types.size(); i++) {
                    Mob mob = types.get(i).create(level, EntitySpawnReason.COMMAND);
                    mob.snapTo(player.getX() + (i - types.size() / 2) * 1.2D, player.getY(), player.getZ() + 5.0D, 180.0F, 0.0F);
                    mob.setYHeadRot(180.0F);
                    mob.setYBodyRot(180.0F);
                    mob.setNoAi(true);
                    if (mob instanceof Parabuzzy parabuzzy) {
                        parabuzzy.setVariant(Parabuzzy.Variant.values()[i % Parabuzzy.Variant.values().length]);
                    }
                    level.addFreshEntity(mob);
                    ids.add(mob.getId());
                }
                return ids;
        });

        context.waitFor(client -> row.stream().allMatch(id -> client.level.getEntity(id) != null));
        context.waitTicks(10);
        context.runOnClient(client -> row.forEach(id -> client.getEntityRenderDispatcher().extractEntity(client.level.getEntity(id), 1.0F)));
        return row;
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
            Parabuzzy parabuzzy = EightBitEntities.PARABUZZY.get().create(level, EntitySpawnReason.COMMAND);
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
