package com.grim3212.assorted.mobs.common.entity.ai;

import com.grim3212.assorted.mobs.common.entity.AmphibiousAnimal;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/** Wanders whichever of the two it is in: a swim while it is afloat, a walk that keeps to dry land while it is not. */
public class AmphibiousStrollGoal extends RandomStrollGoal {

    private final double swimSpeedModifier;

    public AmphibiousStrollGoal(AmphibiousAnimal mob, double walkSpeedModifier, double swimSpeedModifier, int interval) {
        super(mob, walkSpeedModifier, interval);
        this.swimSpeedModifier = swimSpeedModifier;
    }

    @Override
    protected @Nullable Vec3 getPosition() {
        return this.mob.isInWater() ? BehaviorUtils.getRandomSwimmablePos(this.mob, 10, 7) : LandRandomPos.getPos(this.mob, 10, 7);
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.mob.isInWater() ? this.swimSpeedModifier : this.speedModifier);
    }
}
