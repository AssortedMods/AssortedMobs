package com.grim3212.assorted.icepixie.gametest;

import com.grim3212.assorted.icepixie.common.entity.IcePixieEntities;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.ArrayList;
import java.util.List;

/**
 * What a headless server cannot see: the ice pixie survives render state extraction. Run with {@code ./gradlew :icepixie:fabric:runClientGameTest};
 * it exits non-zero on a failure.
 */
public class IcePixieClientGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getConnection().waitForChunksRender();
            lineUp(context, world, List.of(IcePixieEntities.ICE_PIXIE.get()));
            context.takeScreenshot("assortedicepixie_ice_pixie");
        }
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
}
