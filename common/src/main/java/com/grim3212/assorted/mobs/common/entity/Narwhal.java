package com.grim3212.assorted.mobs.common.entity;

import com.grim3212.assorted.mobs.common.entity.ai.RaiseTuskGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * The unicorn of the cold seas, and its horn makes a sword. It swims the way a dolphin does, wants
 * nothing of anyone, and now and then comes up to stand its tusk out of the water. The 1.2.5 mod
 * moved it with a squid's drifting and some riding code nothing ever called.
 */
public class Narwhal extends WaterAnimal {

    private static final EntityDataAccessor<Boolean> RAISING_TUSK = SynchedEntityData.defineId(Narwhal.class, EntityDataSerializers.BOOLEAN);
    private static final float TUSK_EASE = 0.06F;
    /** Degrees a tick it turns at which it is leaning right over. */
    private static final float FULL_BANK_TURN = 6.0F;
    private static final float MAX_BANK = 0.35F;

    /** Made in registerGoals, which the constructor above this one calls before any field here is set. */
    private RaiseTuskGoal raiseTuskGoal;

    // Client only, for the model: its pitch and the lean into a turn, both eased, and 0 swimming to 1 tusk up.
    private float swimPitch;
    private float swimPitchO;
    private float bank;
    private float bankO;
    private float tuskAmount;
    private float tuskAmountO;

    public Narwhal(EntityType<? extends Narwhal> type, Level level) {
        super(type, level);
        // No buoyancy from the control: nothing in travelInWater sinks it for that to hold up.
        this.moveControl = new SmoothSwimmingMoveControl<>(this, 85, 10, 0.02F, 0.1F, false);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return WaterAnimal.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RAISING_TUSK, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D, swimmer -> DamageTypeTags.PANIC_CAUSES));
        this.raiseTuskGoal = new RaiseTuskGoal(this);
        this.goalSelector.addGoal(3, this.raiseTuskGoal);
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    public boolean isRaisingTusk() {
        return this.entityData.get(RAISING_TUSK);
    }

    public void setRaisingTusk(boolean raising) {
        this.entityData.set(RAISING_TUSK, raising);
    }

    /** Has it go up and raise its tusk the next chance it gets, not whenever it next feels like it. */
    public void raiseTusk() {
        this.raiseTuskGoal.trigger();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.swimPitchO = this.swimPitch;
            this.bankO = this.bank;
            this.tuskAmountO = this.tuskAmount;
            this.swimPitch += (this.getXRot() - this.swimPitch) * 0.2F;
            float turn = Mth.wrapDegrees(this.yBodyRot - this.yBodyRotO);
            this.bank += (Mth.clamp(turn / FULL_BANK_TURN, -1.0F, 1.0F) * MAX_BANK - this.bank) * 0.15F;
            this.tuskAmount = Mth.clamp(this.tuskAmount + (this.isRaisingTusk() ? TUSK_EASE : -TUSK_EASE), 0.0F, 1.0F);
        }
    }

    /** Its pitch in degrees, eased so a dive is a curve and not a snap. Client only. */
    public float getSwimPitch(float partialTick) {
        return Mth.lerp(partialTick, this.swimPitchO, this.swimPitch);
    }

    /** How far it leans into the turn it is making, in radians. Client only. */
    public float getBank(float partialTick) {
        return Mth.lerp(partialTick, this.bankO, this.bank);
    }

    /** 0 swimming to 1 stood on its tail with its tusk in the air. Client only. */
    public float getTuskAmount(float partialTick) {
        return Mth.lerp(partialTick, this.tuskAmountO, this.tuskAmount);
    }

    @Override
    protected void travelInWater(Vec3 input, double baseGravity, boolean isFalling, double oldY) {
        this.moveRelative(this.getSpeed(), input);
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
    }
}
