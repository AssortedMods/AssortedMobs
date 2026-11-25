package com.grim3212.assorted.eightbit.client.render.entity;

import com.grim3212.assorted.eightbit.common.entity.PerchingMob;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Draws a perched mob where its carrier's head is this frame. The mob's own position is only
 * updated once a tick, and may be a tick behind the player's, so drawn from it the mob would slide
 * about on a walking player's head.
 */
final class PerchRendering {

    private PerchRendering() {
    }

    /** How far from where the mob would be drawn its carrier's head is this frame. */
    static <T extends Mob & PerchingMob> Vec3 offset(T mob, float partialTicks, Vec3 fallback) {
        Player carrier = mob.perch().player();
        if (carrier == null) {
            return fallback;
        }

        double x = Mth.lerp(partialTicks, carrier.xOld, carrier.getX()) - Mth.lerp(partialTicks, mob.xOld, mob.getX());
        double y = Mth.lerp(partialTicks, carrier.yOld, carrier.getY()) + carrier.getBbHeight() - Mth.lerp(partialTicks, mob.yOld, mob.getY());
        double z = Mth.lerp(partialTicks, carrier.zOld, carrier.getZ()) - Mth.lerp(partialTicks, mob.zOld, mob.getZ());
        return new Vec3(x, y, z);
    }

    /**
     * Runs {@code render} with the mob's body turned as its carrier's is this frame, and its head
     * turned along with it, so it still looks where it did relative to its body.
     */
    static <T extends Mob & PerchingMob> void withCarrierBody(T mob, Runnable render) {
        Player carrier = mob.perch().player();
        if (carrier == null) {
            render.run();
            return;
        }

        float bodyO = mob.yBodyRotO;
        float body = mob.yBodyRot;
        float headO = mob.yHeadRotO;
        float head = mob.yHeadRot;
        mob.yBodyRotO = carrier.yBodyRotO;
        mob.yBodyRot = carrier.yBodyRot;
        mob.yHeadRotO = headO + (carrier.yBodyRotO - bodyO);
        mob.yHeadRot = head + (carrier.yBodyRot - body);
        try {
            render.run();
        } finally {
            mob.yBodyRotO = bodyO;
            mob.yBodyRot = body;
            mob.yHeadRotO = headO;
            mob.yHeadRot = head;
        }
    }

    /** Whether the camera is the carrier's own eyes, where the mob would sit across the view. */
    static boolean hiddenFromCarrier(PerchingMob mob) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.options.getCameraType().isFirstPerson() && minecraft.getCameraEntity() instanceof Player camera && mob.perch().isOn(camera);
    }
}
