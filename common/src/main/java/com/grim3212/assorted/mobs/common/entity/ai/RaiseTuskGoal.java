package com.grim3212.assorted.mobs.common.entity.ai;

import com.grim3212.assorted.mobs.common.entity.Narwhal;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

/**
 * Now and then a narwhal comes up under open water, stands on its tail and holds its tusk out in
 * the air for a while, as the real ones do. The standing is the model's; this gets it there and
 * keeps it still.
 */
public class RaiseTuskGoal extends Goal {

    /** One go in this many ticks, on average. */
    private static final int INTERVAL = 600;
    private static final int MAX_RISE = 16;
    /** How far under the surface its middle is held: far enough that only the tusk comes out. */
    private static final double HOLD_DEPTH = 1.3D;
    private static final double ARRIVED_WITHIN = 0.6D;
    private static final int GIVE_UP_TICKS = 300;
    private static final int MIN_HOLD_TICKS = 60;
    private static final int MAX_HOLD_TICKS = 140;

    private final Narwhal narwhal;
    private boolean forceTrigger;
    private double holdY;
    private int ticks;
    private int holdTicks;

    public RaiseTuskGoal(Narwhal narwhal) {
        this.narwhal = narwhal;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public void trigger() {
        this.forceTrigger = true;
    }

    @Override
    public boolean canUse() {
        if (!this.narwhal.isInWater() || (!this.forceTrigger && this.narwhal.getRandom().nextInt(reducedTickDelay(INTERVAL)) != 0)) {
            return false;
        }

        this.forceTrigger = false;
        return this.findSurface();
    }

    /** Straight up, to air: not to the underside of the ice, and not where there is no depth to stand in. */
    private boolean findSurface() {
        Level level = this.narwhal.level();
        BlockPos.MutableBlockPos pos = this.narwhal.blockPosition().mutable();
        for (int i = 0; i < MAX_RISE && level.getFluidState(pos).is(FluidTags.WATER); i++) {
            pos.move(0, 1, 0);
        }

        if (!level.getBlockState(pos).isAir() || !level.getFluidState(pos.below(2)).is(FluidTags.WATER)) {
            return false;
        }

        this.holdY = pos.getY() - HOLD_DEPTH - this.narwhal.getBbHeight() / 2.0D;
        return true;
    }

    @Override
    public void start() {
        this.ticks = 0;
        this.holdTicks = 0;
        this.narwhal.getNavigation().moveTo(this.narwhal.getX(), this.holdY, this.narwhal.getZ(), 1.0D);
    }

    @Override
    public boolean canContinueToUse() {
        // A fright ends it without being asked: the panic goal outranks this one.
        return this.narwhal.isInWater() && (this.narwhal.isRaisingTusk() ? this.holdTicks > 0 : this.ticks < GIVE_UP_TICKS);
    }

    @Override
    public void stop() {
        this.narwhal.setRaisingTusk(false);
        this.narwhal.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.ticks++;
        if (this.narwhal.isRaisingTusk()) {
            this.holdTicks--;
            // Held where it is: nothing sinks it, but what it arrived with would carry it on up and out.
            this.narwhal.setDeltaMovement(this.narwhal.getDeltaMovement().multiply(0.6D, 0.0D, 0.6D).add(0.0D, (this.holdY - this.narwhal.getY()) * 0.1D, 0.0D));
        } else if (Math.abs(this.narwhal.getY() - this.holdY) < ARRIVED_WITHIN) {
            this.narwhal.getNavigation().stop();
            this.narwhal.setRaisingTusk(true);
            this.holdTicks = MIN_HOLD_TICKS + this.narwhal.getRandom().nextInt(MAX_HOLD_TICKS - MIN_HOLD_TICKS);
        } else if (this.narwhal.getNavigation().isDone()) {
            this.narwhal.getNavigation().moveTo(this.narwhal.getX(), this.holdY, this.narwhal.getZ(), 1.0D);
        }
    }
}
