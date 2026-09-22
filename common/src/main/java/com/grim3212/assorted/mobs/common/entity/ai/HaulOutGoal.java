package com.grim3212.assorted.mobs.common.entity.ai;

import com.grim3212.assorted.mobs.common.entity.AmphibiousAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.Path;

import java.util.HashSet;
import java.util.Set;

/**
 * Takes an animal that has swum long enough up onto the nearest shore, floe or bank it can get to
 * and fit on. The nearest place to lie is not always one it can reach, and vanilla's goal would go
 * on choosing it for ever: one it has no path to, or gives up on, is crossed off until it is ashore.
 */
public class HaulOutGoal extends MoveToBlockGoal {

    private static final int GIVE_UP_TICKS = 300;
    /** Ticks before it looks again, after a look that found nowhere. Vanilla waits ten seconds and more, which is a long time to tread water. */
    private static final int RETRY_TICKS = 40;
    private static final int NO_LAND_RETRY_TICKS = 200;
    private static final int MAX_CROSSED_OFF = 64;
    private static final float NEAR_ENOUGH = 2.5F;

    private final AmphibiousAnimal animal;
    private final Set<BlockPos> crossedOff = new HashSet<>();

    public HaulOutGoal(AmphibiousAnimal animal, double speedModifier, int searchRange) {
        super(animal, speedModifier, searchRange, 4);
        this.animal = animal;
    }

    @Override
    public boolean canUse() {
        if (!this.animal.wantsLand()) {
            this.crossedOff.clear();
            return false;
        }
        // A countdown to the next look, or the look itself: only the look can come up empty handed.
        boolean looking = this.nextStartTick <= 0;
        if (!super.canUse()) {
            if (looking) {
                if (this.crossedOff.isEmpty()) {
                    // No land at all: out at sea. The next look is a long way off, as vanilla's is, a look being thousands of blocks.
                    this.nextStartTick = reducedTickDelay(NO_LAND_RETRY_TICKS + this.animal.getRandom().nextInt(NO_LAND_RETRY_TICKS));
                }
                // Nowhere left that it has not already given up on. From where it is now, one of those may do after all.
                this.crossedOff.clear();
            }
            return false;
        }

        // To beside it will do: the last step up a bank is often one the pathfinder will not plot, and the hop out of
        // the water makes anyway.
        Path path = this.animal.getNavigation().createPath(this.getMoveToTarget(), 1);
        if (path == null || !path.canReach() && path.getDistToTarget() > NEAR_ENOUGH) {
            this.crossOff(this.blockPos);
            return false;
        }
        return true;
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(RETRY_TICKS + mob.getRandom().nextInt(RETRY_TICKS));
    }

    @Override
    public boolean canContinueToUse() {
        if (this.isReachedTarget() || !this.isValidTarget(this.animal.level(), this.blockPos)) {
            return false;
        }
        if (this.tryTicks > GIVE_UP_TICKS) {
            this.crossOff(this.blockPos);
            return false;
        }
        return true;
    }

    /** Vanilla's goal leaves it swimming at wherever it was going, which for one it has given up on is a wall. */
    @Override
    public void stop() {
        super.stop();
        this.animal.getNavigation().stop();
    }

    private void crossOff(BlockPos pos) {
        if (this.crossedOff.size() >= MAX_CROSSED_OFF) {
            this.crossedOff.clear();
        }
        this.crossedOff.add(pos.immutable());
    }

    @Override
    public boolean shouldRecalculatePath() {
        return this.tryTicks % 80 == 0;
    }

    /** Something to lie on with dry room above it, as many blocks across as the animal is: a walrus does not fit on a ledge. */
    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        // Only where the world is loaded: asking after a block in a chunk that is not loads it, or makes it.
        if (this.crossedOff.contains(pos) || !level.hasChunkAt(pos)) {
            return false;
        }

        int across = Mth.ceil(this.animal.getBbWidth());
        for (BlockPos under : BlockPos.betweenClosed(pos, pos.offset(across - 1, 0, across - 1))) {
            BlockPos above = under.above();
            if (!level.getBlockState(under).isFaceSturdy(level, under, Direction.UP)
                    || !level.getFluidState(above).isEmpty() || !level.getBlockState(above).getCollisionShape(level, above).isEmpty()
                    || !level.getBlockState(above.above()).getCollisionShape(level, above.above()).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
