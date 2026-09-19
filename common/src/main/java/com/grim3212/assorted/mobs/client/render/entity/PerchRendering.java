package com.grim3212.assorted.mobs.client.render.entity;

import com.grim3212.assorted.mobs.common.entity.PerchingMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

/**
 * Draws a perched mob where its carrier's head is this frame. The mob's own position is only
 * updated once a tick, and may be a tick behind the player's, so drawn from it the mob would slide
 * about on a walking player's head.
 */
final class PerchRendering {

    private PerchRendering() {
    }

    static void extract(PerchingMob mob, LivingEntityRenderState state, float partialTicks) {
        Player carrier = mob.perch().player();
        if (carrier == null) {
            return;
        }

        state.x = Mth.lerp(partialTicks, carrier.xOld, carrier.getX());
        state.y = Mth.lerp(partialTicks, carrier.yOld, carrier.getY()) + carrier.getBbHeight();
        state.z = Mth.lerp(partialTicks, carrier.zOld, carrier.getZ());
        state.bodyRot = Mth.rotLerp(partialTicks, carrier.yBodyRotO, carrier.yBodyRot);
    }

    /** Whether the camera is the carrier's own eyes, where the mob would sit across the view. */
    static boolean hiddenFromCarrier(PerchingMob mob) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.options.getCameraType().isFirstPerson() && minecraft.getCameraEntity() instanceof Player camera && mob.perch().isOn(camera);
    }
}
