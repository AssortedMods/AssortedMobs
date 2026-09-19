package com.grim3212.assorted.mobs.common.entity;

import com.grim3212.assorted.mobs.common.entity.ai.FollowNearestPlayerGoal;
import com.grim3212.assorted.mobs.common.item.MobsItems;
import com.grim3212.assorted.mobs.common.sounds.MobsSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;

import java.util.OptionalInt;

/**
 * A walking bomb that sides with players. It follows the nearest one, picks fights with monsters,
 * and goes off the moment one hits back, setting fire to every creature nearby. A player's hit only
 * lights its fuse. It will sit on a player's head, water puts it out for good, and an empty hand
 * while sneaking picks it back up as an item.
 */
public class Bobomb extends PathfinderMob implements PerchingMob {

    private static final EntityDataAccessor<Boolean> DATA_LIT = SynchedEntityData.defineId(Bobomb.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<OptionalInt> DATA_PERCHED_ON = SynchedEntityData.defineId(Bobomb.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);

    /** Ticks from being lit to going off. */
    public static final int FUSE = 50;
    public static final float EXPLOSION_RADIUS = 3.0F;
    /** How far the fire it starts reaches. */
    public static final double FIRE_RANGE = 4.0D;
    private static final int FIRE_SECONDS = 15;

    private final Perch perch = new Perch(this, DATA_PERCHED_ON);
    private int fuse = FUSE;

    public Bobomb(EntityType<? extends Bobomb> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 15.0D).add(Attributes.MOVEMENT_SPEED, 0.35D).add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    /** What the 8-bit mobs go after: monsters, but never each other. */
    public static boolean isEnemy(LivingEntity target) {
        return target instanceof Enemy && !(target instanceof Bobomb) && !(target instanceof Parabuzzy);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new FollowNearestPlayerGoal(this, 1.0D, 8.0F, 2.0F, 16.0F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, true, (target, level) -> isEnemy(target)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_LIT, false);
        Perch.define(builder, DATA_PERCHED_ON);
    }

    @Override
    public Perch perch() {
        return this.perch;
    }

    public boolean isLit() {
        return this.entityData.get(DATA_LIT);
    }

    public void light() {
        if (!this.isLit()) {
            this.entityData.set(DATA_LIT, true);
            this.playSound(SoundEvents.TNT_PRIMED, 1.0F, 1.0F);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.perch.tick();

        if (this.level() instanceof ServerLevel level) {
            if (this.isInWater()) {
                this.kill(level);
            } else if (this.isLit() && --this.fuse <= 0) {
                this.explode(level);
            }
        } else if (this.isAlive()) {
            this.fuseParticles();
        }
    }

    private void fuseParticles() {
        int flicker = this.random.nextInt(10);
        if (flicker <= 6) {
            this.level().addParticle(ParticleTypes.FLAME, this.getX(), this.getY() + 0.85D, this.getZ(), 0.01D, 0.01D, 0.01D);
        }
        if (flicker <= 2) {
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.8D, this.getZ(), 0.01D, 0.01D, 0.01D);
        }
    }

    public void explode(ServerLevel level) {
        if (this.isRemoved()) {
            return;
        }

        level.explode(this, this.getX(), this.getY(), this.getZ(), EXPLOSION_RADIUS, Level.ExplosionInteraction.MOB);
        for (Mob nearby : level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(FIRE_RANGE), mob -> mob != this)) {
            nearby.igniteForSeconds(FIRE_SECONDS);
        }
        this.discard();
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        Entity attacker = source.getEntity();
        if (attacker instanceof Bobomb || attacker instanceof Parabuzzy) {
            return false;
        }

        if (attacker instanceof Player) {
            this.light();
            return true;
        }

        if (attacker instanceof LivingEntity) {
            this.explode(level);
            return true;
        }

        return super.hurtServer(level, source, damage);
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!(this.level() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        if (!player.isSecondaryUseActive() && this.perch.perchOn(player)) {
            return InteractionResult.SUCCESS_SERVER;
        }

        if (player.getItemInHand(hand).isEmpty() && this.getTarget() == null) {
            level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY(0.5D), this.getZ(), 20, this.getBbWidth() / 2, this.getBbHeight() / 2, this.getBbWidth() / 2, 0.02D);
            this.spawnAtLocation(level, new ItemStack(MobsItems.BOBOMB.get()));
            this.discard();
            return InteractionResult.SUCCESS_SERVER;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected boolean isImmobile() {
        return this.isLit() || this.perch.isPerched() || super.isImmobile();
    }

    @Override
    protected void updateControlFlags() {
        super.updateControlFlags();
        if (this.perch.isPerched()) {
            this.goalSelector.setControlFlag(Goal.Flag.MOVE, false);
            this.goalSelector.setControlFlag(Goal.Flag.JUMP, false);
            this.goalSelector.setControlFlag(Goal.Flag.LOOK, false);
        }
        this.targetSelector.setControlFlag(Goal.Flag.TARGET, !this.perch.isPerched());
    }

    @Override
    public boolean isInWall() {
        return !this.perch.isPerched() && super.isInWall();
    }

    @Override
    public boolean isPushable() {
        return !this.perch.isPerched() && super.isPushable();
    }

    @Override
    protected void pushEntities() {
        if (!this.perch.isPerched()) {
            super.pushEntities();
        }
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float damageModifier, DamageSource damageSource) {
        return !this.perch.isPerched() && super.causeFallDamage(fallDistance, damageModifier, damageSource);
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return MobsSounds.BOBOMB_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }
}
