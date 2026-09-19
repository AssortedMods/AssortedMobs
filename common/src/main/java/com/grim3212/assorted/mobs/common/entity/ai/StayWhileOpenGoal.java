package com.grim3212.assorted.mobs.common.entity.ai;

import com.grim3212.assorted.mobs.common.entity.TreasureMob;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

/** Holds a treasure mob still, facing whoever is looking in, while its chest is open. */
public class StayWhileOpenGoal extends Goal {

    private final TreasureMob mob;

    public StayWhileOpenGoal(TreasureMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.mob.getChest().isOpen();
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        List<ContainerUser> viewers = this.mob.getChest().getEntitiesWithContainerOpen();
        if (!viewers.isEmpty()) {
            this.mob.getLookControl().setLookAt(viewers.getFirst().getLivingEntity());
        }
    }
}
