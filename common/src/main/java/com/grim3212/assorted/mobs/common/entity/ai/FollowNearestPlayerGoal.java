package com.grim3212.assorted.mobs.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Keeps up with whichever of {@code players} is nearest: an owner alone, or everyone for a mob with
 * none. It sets off once that player is {@code startDistance} away, up to {@code range}, and stops at
 * {@code stopDistance}, or as soon as it has something to fight. A player {@code teleportsTo} accepts,
 * an owner, is followed from any distance and teleported to from {@link #TELEPORT_DISTANCE}, as
 * {@code FollowOwnerGoal} does for a tame animal.
 */
public class FollowNearestPlayerGoal extends Goal {

    private static final int REPATH_INTERVAL = 10;
    /** {@code TamableAnimal#shouldTryTeleportToOwner}'s distance. */
    public static final double TELEPORT_DISTANCE = 12.0D;

    private final PathfinderMob mob;
    private final Supplier<List<? extends Player>> players;
    private final Predicate<Player> teleportsTo;
    private final double speedModifier;
    private final float startDistance;
    private final float stopDistance;
    private final float range;
    @Nullable
    private Player player;
    private int timeToRecalcPath;

    public FollowNearestPlayerGoal(PathfinderMob mob, Supplier<List<? extends Player>> players, Predicate<Player> teleportsTo, double speedModifier, float startDistance, float stopDistance, float range) {
        this.mob = mob;
        this.players = players;
        this.teleportsTo = teleportsTo;
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

        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Player player : this.players.get()) {
            double distance = this.mob.distanceToSqr(player);
            boolean inRange = distance <= this.range * this.range || this.teleportsTo.test(player);
            if (!player.isSpectator() && inRange && distance < nearestDistance) {
                nearest = player;
                nearestDistance = distance;
            }
        }
        if (nearest == null || nearestDistance < this.startDistance * this.startDistance) {
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

        boolean teleport = this.teleportsTo.test(this.player) && this.mob.distanceToSqr(this.player) >= TELEPORT_DISTANCE * TELEPORT_DISTANCE;
        if (!teleport) {
            this.mob.getLookControl().setLookAt(this.player, 10.0F, this.mob.getMaxHeadXRot());
        }

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(REPATH_INTERVAL);
            if (teleport) {
                this.teleportNear(this.player.blockPosition());
            } else {
                this.mob.getNavigation().moveTo(this.player, this.speedModifier);
            }
        }
    }

    // TamableAnimal#teleportToAroundBlockPos and what it calls, which are private to tame animals.

    private void teleportNear(BlockPos target) {
        for (int attempt = 0; attempt < 10; attempt++) {
            int xd = this.mob.getRandom().nextIntBetweenInclusive(-3, 3);
            int zd = this.mob.getRandom().nextIntBetweenInclusive(-3, 3);
            if (Math.abs(xd) >= 2 || Math.abs(zd) >= 2) {
                BlockPos pos = target.offset(xd, this.mob.getRandom().nextIntBetweenInclusive(-1, 1), zd);
                if (this.canTeleportTo(pos)) {
                    this.mob.snapTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, this.mob.getYRot(), this.mob.getXRot());
                    this.mob.getNavigation().stop();
                    return;
                }
            }
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        if (WalkNodeEvaluator.getPathTypeStatic(this.mob, pos) != PathType.WALKABLE
                || this.mob.level().getBlockState(pos.below()).getBlock() instanceof LeavesBlock) {
            return false;
        }
        return this.mob.level().noCollision(this.mob, this.mob.getBoundingBox().move(pos.subtract(this.mob.blockPosition())));
    }
}
