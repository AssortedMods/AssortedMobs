package com.grim3212.assorted.mobs.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;

/** A panic that makes for the water if there is any near, where a seal is a good deal harder to catch. */
public class FleeToWaterGoal extends PanicGoal {

    private static final int WATER_SEARCH_RANGE = 10;

    public FleeToWaterGoal(PathfinderMob mob, double speedModifier) {
        super(mob, speedModifier, panicker -> DamageTypeTags.PANIC_CAUSES);
    }

    @Override
    protected boolean findRandomPosition() {
        if (!this.mob.isInWater()) {
            BlockPos water = this.lookForWater(this.mob.level(), this.mob, WATER_SEARCH_RANGE);
            if (water != null) {
                this.posX = water.getX() + 0.5D;
                this.posY = water.getY();
                this.posZ = water.getZ() + 0.5D;
                return true;
            }
        }
        return super.findRandomPosition();
    }
}
