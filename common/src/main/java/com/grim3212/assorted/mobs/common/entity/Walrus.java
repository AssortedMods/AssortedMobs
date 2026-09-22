package com.grim3212.assorted.mobs.common.entity;

import com.grim3212.assorted.mobs.api.MobsTags;
import com.grim3212.assorted.mobs.common.entity.ai.AmphibiousStrollGoal;
import com.grim3212.assorted.mobs.common.entity.ai.HaulOutGoal;
import com.grim3212.assorted.mobs.common.entity.ai.SeekWaterGoal;
import com.grim3212.assorted.mobs.common.sounds.MobsSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

/**
 * A great tusked lump of the ice and snow, and of the water under it. Left alone it is harmless; hit
 * one and every walrus in earshot charges, a good deal faster than it lumbers, and into the sea after
 * whoever did it. In 1.2.5 it carried its own copy of the zombie pigman's anger.
 */
public class Walrus extends AmphibiousAnimal implements NeutralMob {

    private static final UniformInt LAND_TIME = TimeUtil.rangeOfSeconds(90, 240);
    private static final UniformInt WATER_TIME = TimeUtil.rangeOfSeconds(40, 100);
    /** The 20 to 40 seconds the 1.2.5 herd stayed angry. */
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 40);
    private static final double CHARGE_SPEED = 1.6D;

    private long persistentAngerEndTime;
    @Nullable
    private EntityReference<LivingEntity> persistentAngerTarget;

    public Walrus(EntityType<? extends Walrus> type, Level level) {
        super(type, level, 0.018F, LAND_TIME, WATER_TIME);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 24.0D).add(Attributes.MOVEMENT_SPEED, 0.18D).add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.STEP_HEIGHT, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, CHARGE_SPEED, true));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, this::isFood, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new SeekWaterGoal(this, 1.0D, 24));
        this.goalSelector.addGoal(5, new HaulOutGoal(this, 1.0D, 16));
        this.goalSelector.addGoal(6, new AmphibiousStrollGoal(this, 1.0D, 1.0D, 160));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level() instanceof ServerLevel level) {
            this.updatePersistentAnger(level, true);
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        this.addPersistentAngerSaveData(output);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.readPersistentAngerSaveData(this.level(), input);
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setTimeToRemainAngry(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    public void setPersistentAngerEndTime(long endTime) {
        this.persistentAngerEndTime = endTime;
    }

    @Override
    public long getPersistentAngerEndTime() {
        return this.persistentAngerEndTime;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable EntityReference<LivingEntity> target) {
        this.persistentAngerTarget = target;
    }

    @Override
    public @Nullable EntityReference<LivingEntity> getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(MobsTags.Items.SEAL_FOOD);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return MobsEntities.WALRUS.get().create(level, EntitySpawnReason.BREEDING);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return MobsSounds.WALRUS_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return MobsSounds.WALRUS_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MobsSounds.WALRUS_HURT.get();
    }
}
