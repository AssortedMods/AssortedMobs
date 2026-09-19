package com.grim3212.assorted.mobs.common.entity.ai;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;

/** Wild ones keep their distance from players; a tame one has no reason to. */
public class WildAvoidPlayerGoal extends AvoidEntityGoal<Player> {

    private final TamableAnimal mob;

    public WildAvoidPlayerGoal(TamableAnimal mob) {
        super(mob, Player.class, 10.0F, 1.0D, 1.2D);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return !this.mob.isTame() && super.canUse();
    }
}
