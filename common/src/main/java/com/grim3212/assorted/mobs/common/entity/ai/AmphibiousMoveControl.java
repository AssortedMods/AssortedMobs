package com.grim3212.assorted.mobs.common.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

/**
 * Vanilla's SmoothSwimmingMoveControl with the water speed taken off the movement attribute. There
 * the attribute goes in twice, so an animal slow on land is slower still at sea, and one goal's
 * speed modifier counts for its square.
 */
public class AmphibiousMoveControl extends MoveControl<Mob> {

    private static final int MAX_TURN_X = 85;
    private static final int MAX_TURN_Y = 10;

    private final float waterSpeed;

    /** @param waterSpeed what it puts on each tick swimming at a goal's speed of 1; it tops out at nine times that */
    public AmphibiousMoveControl(Mob mob, float waterSpeed) {
        super(mob);
        this.waterSpeed = waterSpeed;
    }

    @Override
    public void tick() {
        if (this.operation != MoveControl.Operation.MOVE_TO || this.mob.getNavigation().isDone()) {
            this.mob.setSpeed(0.0F);
            this.mob.setXxa(0.0F);
            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
            return;
        }

        double xd = this.wantedX - this.mob.getX();
        double yd = this.wantedY - this.mob.getY();
        double zd = this.wantedZ - this.mob.getZ();
        if (xd * xd + yd * yd + zd * zd < 2.5000003E-7F) {
            this.mob.setZza(0.0F);
            return;
        }

        float yRotD = (float) (Mth.atan2(zd, xd) * Mth.RAD_TO_DEG) - 90.0F;
        this.mob.setYRot(this.rotlerp(this.mob.getYRot(), yRotD, MAX_TURN_Y));
        this.mob.yBodyRot = this.mob.getYRot();
        this.mob.yHeadRot = this.mob.getYRot();

        if (this.mob.isInWater()) {
            this.mob.setSpeed((float) (this.waterSpeed * this.speedModifier));
            double flat = Math.sqrt(xd * xd + zd * zd);
            if (Math.abs(yd) > 1.0E-5F || flat > 1.0E-5F) {
                float xRotD = Mth.clamp(Mth.wrapDegrees(-(float) (Mth.atan2(yd, flat) * Mth.RAD_TO_DEG)), -MAX_TURN_X, MAX_TURN_X);
                this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), xRotD, 5.0F));
            }

            this.mob.zza = Mth.cos(this.mob.getXRot() * Mth.DEG_TO_RAD);
            this.mob.yya = -Mth.sin(this.mob.getXRot() * Mth.DEG_TO_RAD);
        } else {
            // Slowed through a turn, so it does not skate round corners.
            float leftToTurn = Math.abs(Mth.wrapDegrees(this.mob.getYRot() - yRotD));
            float turning = 1.0F - Mth.clamp((leftToTurn - 10.0F) / 50.0F, 0.0F, 1.0F);
            this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)) * turning);
        }
    }
}
