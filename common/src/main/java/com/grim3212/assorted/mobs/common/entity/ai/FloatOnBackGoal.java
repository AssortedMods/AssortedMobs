package com.grim3212.assorted.mobs.common.entity.ai;

import com.grim3212.assorted.mobs.common.entity.SeaOtter;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * What a sea otter does in the water when it is doing nothing else: comes up, rolls over and lies
 * on its back at the surface, paddling a little way now and then.
 */
public class FloatOnBackGoal extends Goal {

    /** How much of it lies under the water, as a share of its height: a fixed depth is deeper than a pup is tall. */
    private static final double FLOAT_FRACTION = 2.0D / 3.0D;
    private static final double SETTLED_FRACTION = 1.0D / 3.0D;
    private static final double RISE_SPEED = 0.06D;
    private static final int DRIFT_INTERVAL = 160;
    private static final int DRIFT_RANGE = 5;
    private static final double DRIFT_SPEED = 0.3D;

    private final SeaOtter otter;
    /** Whether the path the otter is following is the one {@link #drift()} set, rather than another goal's. */
    private boolean drifting;

    public FloatOnBackGoal(SeaOtter otter) {
        this.otter = otter;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return this.otter.isInWater();
    }

    @Override
    public void start() {
        this.drifting = false;
    }

    @Override
    public void stop() {
        this.otter.setFloating(false);
        this.otter.getNavigation().stop();
        this.drifting = false;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.otter.getNavigation().isDone()) {
            this.drifting = false;
        } else if (!this.drifting) {
            // Another goal is swimming it somewhere, a pup after its mother above all: FollowParentGoal holds no movement
            // flag, so it paths alongside this one. Holding the otter at the surface then fights the path every tick and
            // it shivers. Let it swim, upright, until the path is done.
            this.otter.setFloating(false);
            return;
        }

        double height = this.otter.getBbHeight();
        double floatDepth = height * FLOAT_FRACTION;
        double depth = this.otter.getFluidHeight(FluidTags.WATER);
        boolean submerged = depth >= height;
        // Nothing sinks it or floats it in the water, so this is all that holds it at the surface.
        double lift = submerged ? RISE_SPEED : Mth.clamp((depth - floatDepth) * 0.2D, -0.04D, 0.04D);
        Vec3 movement = this.otter.getDeltaMovement();
        this.otter.setDeltaMovement(movement.x, lift, movement.z);
        this.otter.setFloating(!submerged && Math.abs(depth - floatDepth) < height * SETTLED_FRACTION);

        if (this.otter.isFloating() && this.otter.getNavigation().isDone() && this.otter.getRandom().nextInt(DRIFT_INTERVAL) == 0) {
            this.drift();
        }
    }

    /** Along the surface only: a way down is a dive, and that is the stroll goal's business. */
    private void drift() {
        Level level = this.otter.level();
        BlockPos surface = BlockPos.containing(this.otter.getX(), this.otter.getY() + this.otter.getBbHeight() * FLOAT_FRACTION - 0.1D, this.otter.getZ());
        BlockPos target = surface.offset(this.otter.getRandom().nextInt(DRIFT_RANGE * 2 + 1) - DRIFT_RANGE, 0, this.otter.getRandom().nextInt(DRIFT_RANGE * 2 + 1) - DRIFT_RANGE);
        if (level.isLoaded(target) && level.getFluidState(target).is(FluidTags.WATER) && level.getBlockState(target.above()).isAir()) {
            this.drifting = this.otter.getNavigation().moveTo(target.getX() + 0.5D, this.otter.getY(), target.getZ() + 0.5D, DRIFT_SPEED);
        }
    }
}
