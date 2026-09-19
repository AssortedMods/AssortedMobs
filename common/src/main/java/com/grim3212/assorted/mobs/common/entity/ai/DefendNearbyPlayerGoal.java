package com.grim3212.assorted.mobs.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * {@code OwnerHurtByTargetGoal} for a mob that is not a {@code TamableAnimal}: it goes after whatever
 * last hurt one of {@code players} within {@code range}, if {@code canTarget} accepts it, the nearest
 * player's attacker first. {@code players} is an owner alone, or everyone for a mob with none. A
 * player remembers their attacker for 100 ticks after the hit, so that is how long the mob will
 * still answer it.
 */
public class DefendNearbyPlayerGoal extends TargetGoal {

    private final Supplier<List<? extends Player>> players;
    private final float range;
    private final Predicate<LivingEntity> canTarget;
    @Nullable
    private LivingEntity attacker;

    public DefendNearbyPlayerGoal(Mob mob, Supplier<List<? extends Player>> players, float range, Predicate<LivingEntity> canTarget) {
        super(mob, false);
        this.players = players;
        this.range = range;
        this.canTarget = canTarget;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        this.attacker = null;
        double nearest = Double.MAX_VALUE;
        for (Player player : this.players.get()) {
            double distance = this.mob.distanceToSqr(player);
            if (player.isSpectator() || distance > this.range * this.range || distance >= nearest) {
                continue;
            }

            LivingEntity hurtBy = player.getLastHurtByMob();
            if (hurtBy != null && this.canTarget.test(hurtBy) && this.canAttack(hurtBy, TargetingConditions.DEFAULT)) {
                this.attacker = hurtBy;
                nearest = distance;
            }
        }
        return this.attacker != null;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacker);
        super.start();
    }
}
