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

    /** How much of it lies under the water, in blocks. */
    private static final double FLOAT_DEPTH = 0.3D;
    private static final double SETTLED_WITHIN = 0.15D;
    private static final double RISE_SPEED = 0.06D;
    private static final int DRIFT_INTERVAL = 160;
    private static final int DRIFT_RANGE = 5;
    private static final double DRIFT_SPEED = 0.3D;

    private final SeaOtter otter;

    public FloatOnBackGoal(SeaOtter otter) {
        this.otter = otter;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return this.otter.isInWater();
    }

    @Override
    public void stop() {
        this.otter.setFloating(false);
        this.otter.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        double depth = this.otter.getFluidHeight(FluidTags.WATER);
        boolean submerged = depth >= this.otter.getBbHeight();
        // Nothing sinks it or floats it in the water, so this is all that holds it at the surface.
        double lift = submerged ? RISE_SPEED : Mth.clamp((depth - FLOAT_DEPTH) * 0.2D, -0.04D, 0.04D);
        Vec3 movement = this.otter.getDeltaMovement();
        this.otter.setDeltaMovement(movement.x, lift, movement.z);
        this.otter.setFloating(!submerged && Math.abs(depth - FLOAT_DEPTH) < SETTLED_WITHIN);

        if (this.otter.isFloating() && this.otter.getNavigation().isDone() && this.otter.getRandom().nextInt(DRIFT_INTERVAL) == 0) {
            this.drift();
        }
    }

    /** Along the surface only: a way down is a dive, and that is the stroll goal's business. */
    private void drift() {
        Level level = this.otter.level();
        BlockPos surface = BlockPos.containing(this.otter.getX(), this.otter.getY() + FLOAT_DEPTH - 0.1D, this.otter.getZ());
        BlockPos target = surface.offset(this.otter.getRandom().nextInt(DRIFT_RANGE * 2 + 1) - DRIFT_RANGE, 0, this.otter.getRandom().nextInt(DRIFT_RANGE * 2 + 1) - DRIFT_RANGE);
        if (level.hasChunkAt(target) && level.getFluidState(target).is(FluidTags.WATER) && level.getBlockState(target.above()).isAir()) {
            this.otter.getNavigation().moveTo(target.getX() + 0.5D, this.otter.getY(), target.getZ() + 0.5D, DRIFT_SPEED);
        }
    }
}
