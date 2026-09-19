package com.grim3212.assorted.mobs.common.entity;

import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.api.MobsTags;
import com.grim3212.assorted.mobs.common.entity.ai.AvoidBlocksGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * A small hostile creature of snowy biomes that throws ice. Only fire hurts it: a player holding a
 * torch or flint and steel, or torches nearby, which it takes damage from and runs away from. It is
 * quicker on ice and snow.
 */
public class IcePixie extends Monster implements RangedAttackMob {

    /** How far a repellent reaches, on every axis. */
    public static final int REPELLENT_RANGE = 3;
    private static final int REPELLENT_CHECK_INTERVAL = 10;

    private static final Identifier ON_ICE_ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "ice_pixie_on_ice");
    private static final AttributeModifier ON_ICE = new AttributeModifier(ON_ICE_ID, 0.15D, AttributeModifier.Operation.ADD_VALUE);

    public IcePixie(EntityType<? extends IcePixie> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidBlocksGoal(this, 1.4D, MobsTags.Blocks.ICE_PIXIE_REPELLENTS, REPELLENT_RANGE));
        this.goalSelector.addGoal(4, new RangedAttackGoal(this, 1.0D, 20, 40, 10.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level() instanceof ServerLevel level) {
            this.updateIceSpeed();

            if (this.tickCount % REPELLENT_CHECK_INTERVAL == 0) {
                int repellents = AvoidBlocksGoal.count(level, this.blockPosition(), MobsTags.Blocks.ICE_PIXIE_REPELLENTS, REPELLENT_RANGE);
                if (repellents > 0) {
                    this.hurtServer(level, this.damageSources().generic(), repellents * 0.5F);
                }
            }
        }
    }

    private void updateIceSpeed() {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }

        boolean onIce = this.isOnIceOrSnow();
        if (onIce && !speed.hasModifier(ON_ICE_ID)) {
            speed.addTransientModifier(ON_ICE);
        } else if (!onIce && speed.hasModifier(ON_ICE_ID)) {
            speed.removeModifier(ON_ICE_ID);
        }
    }

    public boolean isOnIceOrSnow() {
        BlockPos pos = this.blockPosition();
        // A snow layer is the block the pixie stands in; ice and snow blocks are the one below.
        return this.level().getBlockState(pos).is(Blocks.SNOW)
                || this.level().getBlockState(pos.below()).is(BlockTags.ICE)
                || this.level().getBlockState(pos.below()).is(Blocks.SNOW_BLOCK);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || source.is(DamageTypes.GENERIC) || isArmedAgainst(source)) {
            return super.hurtServer(level, source, damage);
        }
        return false;
    }

    private static boolean isArmedAgainst(DamageSource source) {
        return source.getEntity() instanceof Player player && player.getMainHandItem().is(MobsTags.Items.ICE_PIXIE_WEAPONS);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        double dx = target.getX() - this.getX();
        double dy = target.getY(1.0D / 3.0D) - this.getEyeY();
        double dz = target.getZ() - this.getZ();
        double arc = Math.sqrt(dx * dx + dz * dz) * 0.2D;
        // Sharper the harder the world is.
        float inaccuracy = 14 - this.level().getDifficulty().getId() * 4;

        if (this.level() instanceof ServerLevel level) {
            ItemStack stack = new ItemStack(Items.ICE);
            Projectile.spawnProjectile(new IceCube(level, this, stack), level, stack, cube -> cube.shoot(dx, dy + arc, dz, 1.2F, inaccuracy));
        }

        this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SNOW_STEP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WOODEN_BUTTON_CLICK_ON;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOODEN_BUTTON_CLICK_OFF;
    }
}
