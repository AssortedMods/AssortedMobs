package com.grim3212.assorted.mobs.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/** Runs from any block in a tag within {@code range}, to somewhere none of them reaches. */
public class AvoidBlocksGoal extends Goal {

    private final PathfinderMob mob;
    private final double speedModifier;
    private final TagKey<Block> avoid;
    private final int range;
    @Nullable
    private Vec3 destination;

    public AvoidBlocksGoal(PathfinderMob mob, double speedModifier, TagKey<Block> avoid, int range) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.avoid = avoid;
        this.range = range;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /** How many blocks in the tag lie within {@code range} of {@code center} on every axis. */
    public static int count(Level level, BlockPos center, TagKey<Block> tag, int range) {
        int found = 0;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-range, -range, -range), center.offset(range, range, range))) {
            if (level.getBlockState(pos).is(tag)) {
                found++;
            }
        }
        return found;
    }

    @Override
    public boolean canUse() {
        if (count(this.mob.level(), this.mob.blockPosition(), this.avoid, this.range) == 0) {
            return false;
        }

        for (int attempt = 0; attempt < 10; attempt++) {
            Vec3 candidate = DefaultRandomPos.getPos(this.mob, 10, 3);
            if (candidate != null && count(this.mob.level(), BlockPos.containing(candidate), this.avoid, this.range) == 0) {
                this.destination = candidate;
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        if (this.destination != null) {
            this.mob.getNavigation().moveTo(this.destination.x, this.destination.y, this.destination.z, this.speedModifier);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    @Override
    public void stop() {
        this.destination = null;
    }
}
