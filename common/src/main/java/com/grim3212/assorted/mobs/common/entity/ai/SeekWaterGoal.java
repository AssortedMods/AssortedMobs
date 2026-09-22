package com.grim3212.assorted.mobs.common.entity.ai;

import com.grim3212.assorted.mobs.common.entity.AmphibiousAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;

/** Takes an animal that has had enough of the shore, or strayed too far from it, back down to the water. */
public class SeekWaterGoal extends MoveToBlockGoal {

    private static final int GIVE_UP_TICKS = 1200;

    private final AmphibiousAnimal animal;

    public SeekWaterGoal(AmphibiousAnimal animal, double speedModifier, int searchRange) {
        super(animal, speedModifier, searchRange);
        this.animal = animal;
        // From the block it stands on, so the water beside an ice floe counts.
        this.verticalSearchStart = -1;
    }

    @Override
    public boolean canUse() {
        return this.animal.wantsWater() && super.canUse();
    }

    /** The water it came out of, if it remembers any: what is nearest at its own level may be none at all, up a bank. */
    @Override
    protected boolean findNearestBlock() {
        BlockPos shore = this.animal.getShore();
        if (shore != null && this.isValidTarget(this.animal.level(), shore)) {
            this.blockPos = shore;
            return true;
        }
        return super.findNearestBlock();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.animal.isInWater() && this.tryTicks <= GIVE_UP_TICKS && this.isValidTarget(this.animal.level(), this.blockPos);
    }

    @Override
    public boolean shouldRecalculatePath() {
        return this.tryTicks % 160 == 0;
    }

    /** Water it can get into from above: not what lies under the ice. */
    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        // Only where the world is loaded: asking after a block in a chunk that is not loads it, or makes it.
        return level.hasChunkAt(pos) && level.getFluidState(pos).is(FluidTags.WATER) && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty();
    }
}
