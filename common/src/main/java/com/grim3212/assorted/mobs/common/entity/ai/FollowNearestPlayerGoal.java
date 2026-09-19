package com.grim3212.assorted.mobs.common.entity.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * Keeps up with whichever player is nearest, for a mob with no owner. It sets off once that player
 * is {@code startDistance} away, up to {@code range}, and stops at {@code stopDistance}, or as soon
 * as it has something to fight.
 */
public class FollowNearestPlayerGoal extends Goal {

    private static final int REPATH_INTERVAL = 10;

    private final PathfinderMob mob;
    private final double speedModifier;
    private final float startDistance;
    private final float stopDistance;
    private final float range;
    @Nullable
    private Player player;
    private int timeToRecalcPath;

    public FollowNearestPlayerGoal(PathfinderMob mob, double speedModifier, float startDistance, float stopDistance, float range) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.range = range;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getTarget() != null) {
            return false;
        }

        Player nearest = this.mob.level().getNearestPlayer(this.mob, this.range);
        if (nearest == null || nearest.isSpectator() || this.mob.distanceToSqr(nearest) < this.startDistance * this.startDistance) {
            return false;
        }

        this.player = nearest;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.player != null && !this.mob.getNavigation().isDone() && this.mob.getTarget() == null
                && this.mob.distanceToSqr(this.player) > this.stopDistance * this.stopDistance;
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.player = null;
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.player == null) {
            return;
        }

        this.mob.getLookControl().setLookAt(this.player, 10.0F, this.mob.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(REPATH_INTERVAL);
            this.mob.getNavigation().moveTo(this.player, this.speedModifier);
        }
    }
}
