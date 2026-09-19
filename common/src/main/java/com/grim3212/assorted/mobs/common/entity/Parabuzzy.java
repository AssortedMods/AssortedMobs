package com.grim3212.assorted.mobs.common.entity;

import com.grim3212.assorted.mobs.api.MobsTags;
import com.grim3212.assorted.mobs.common.sounds.MobsSounds;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

/**
 * A winged shell that hovers rather than falls and hunts monsters. Wild ones turn on whatever hurts
 * them; fish tames one, food heals a tame one, and a tame one sits on a player's head, where it
 * heals itself and slows the player's falls to its own drift. The rarer colours are tougher and hit harder.
 */
public class Parabuzzy extends TamableAnimal implements PerchingMob {

    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Parabuzzy.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ANGRY = SynchedEntityData.defineId(Parabuzzy.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<OptionalInt> DATA_PERCHED_ON = SynchedEntityData.defineId(Parabuzzy.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);

    /** How much faster than falling it drifts down. */
    private static final double HOVER = 0.6D;
    /** One chance in this, each tick on a head, to heal a heart. */
    private static final int PERCHED_HEAL_CHANCE = 200;

    private final Perch perch = new Perch(this, DATA_PERCHED_ON);

    public Parabuzzy(EntityType<? extends Parabuzzy> type, Level level) {
        super(type, level);
        this.setTame(false, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, Variant.BLUE.health).add(Attributes.MOVEMENT_SPEED, 0.35D).add(Attributes.ATTACK_DAMAGE, Variant.BLUE.damage);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.45F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NonTameRandomTargetGoal<>(this, Mob.class, true, (target, level) -> Bobomb.isEnemy(target)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, Variant.BLUE.ordinal());
        builder.define(DATA_ANGRY, false);
        Perch.define(builder, DATA_PERCHED_ON);
    }

    @Override
    public Perch perch() {
        return this.perch;
    }

    public Variant getVariant() {
        return Variant.byId(this.entityData.get(DATA_VARIANT));
    }

    /** Sets the colour, with the health and bite that come with it, and heals it to the new full. */
    public void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant.ordinal());
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(variant.health);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(variant.damage);
        this.setHealth(this.getMaxHealth());
    }

    public boolean isAngry() {
        return this.entityData.get(DATA_ANGRY);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        this.entityData.set(DATA_ANGRY, target != null && !this.isTame());
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
        this.setVariant(Variant.pick(level.getRandom()));
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("variant", Variant.CODEC, this.getVariant());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        // The attributes the variant set are saved with the rest, so only the colour is restored.
        input.read("variant", Variant.CODEC).ifPresent(variant -> this.entityData.set(DATA_VARIANT, variant.ordinal()));
    }

    /** Only the blue ones shed a shell, and a shell is all there is to drop. */
    @Override
    protected boolean shouldDropLoot(ServerLevel level) {
        return this.getVariant().dropsShell && super.shouldDropLoot(level);
    }

    @Override
    public void aiStep() {
        if (!this.onGround() && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, HOVER, 1.0D));
        }

        super.aiStep();

        if (!this.level().isClientSide() && this.perch.isPerched() && this.getHealth() < this.getMaxHealth() && this.random.nextInt(PERCHED_HEAL_CHANCE) == 0) {
            this.heal(2.0F);
            this.level().broadcastEntityEvent(this, (byte) 7);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.perch.tick();

        // Whoever it sits on falls as gently as it does.
        Player carrier = this.perch.player();
        if (carrier != null) {
            if (!this.level().isClientSide()) {
                carrier.resetFallDistance();
            }
            // A player moves itself, so only the instance that owns its movement can slow it.
            if (carrier.isLocalInstanceAuthoritative() && isDescending(carrier)) {
                carrier.setDeltaMovement(carrier.getDeltaMovement().multiply(1.0D, HOVER, 1.0D));
            }
        }
    }

    private static boolean isDescending(Player carrier) {
        return !carrier.onGround() && carrier.getDeltaMovement().y < 0.0D && !carrier.isFallFlying() && !carrier.getAbilities().flying && !carrier.isInLiquid();
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float damageModifier, DamageSource damageSource) {
        return false;
    }

    /** Higher ground is better ground. */
    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return pos.getY();
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (this.isInvulnerableTo(level, source)) {
            return false;
        }

        this.setOrderedToSit(false);
        Entity attacker = source.getEntity();
        // Its shell turns most of a creature's bite; players and arrows get through.
        if (attacker != null && !(attacker instanceof Player) && !(source.getDirectEntity() instanceof AbstractArrow)) {
            damage = (damage + 1.0F) / 2.0F;
        }
        return super.hurtServer(level, source, damage);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean server = !this.level().isClientSide();

        if (this.isTame()) {
            FoodProperties food = stack.get(DataComponents.FOOD);
            if (food != null && this.getHealth() < this.getMaxHealth()) {
                if (server) {
                    stack.consume(1, player);
                    this.heal(food.nutrition());
                }
                return InteractionResult.SUCCESS;
            }

            if (this.perch.isOn(player)) {
                return InteractionResult.PASS;
            }

            if (server && !player.isSecondaryUseActive() && this.perch.perchOn(player)) {
                return InteractionResult.SUCCESS_SERVER;
            }

            if (server && this.isOwnedBy(player)) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.jumping = false;
                this.navigation.stop();
                this.setTarget(null);
                return InteractionResult.SUCCESS_SERVER;
            }
        } else if (server && stack.is(MobsTags.Items.PARABUZZY_TAME_ITEMS) && !this.isAngry()) {
            stack.consume(1, player);
            this.tryToTame(player);
            return InteractionResult.SUCCESS_SERVER;
        }

        return super.mobInteract(player, hand);
    }

    private void tryToTame(Player player) {
        if (this.random.nextInt(3) == 0) {
            this.tame(player);
            this.navigation.stop();
            this.setTarget(null);
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, (byte) 7);
        } else {
            this.level().broadcastEntityEvent(this, (byte) 6);
        }
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof Creeper || target instanceof Ghast) {
            return false;
        } else if (target instanceof Parabuzzy parabuzzy) {
            return !parabuzzy.isTame() || parabuzzy.getOwner() != owner;
        } else if (target instanceof Player targetPlayer && owner instanceof Player ownerPlayer && !ownerPlayer.canHarmPlayer(targetPlayer)) {
            return false;
        } else {
            return !(target instanceof AbstractHorse horse && horse.isTamed()) && !(target instanceof TamableAnimal animal && animal.isTame());
        }
    }

    @Override
    public boolean canBeLeashed() {
        return !this.isAngry() && super.canBeLeashed();
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
    protected boolean isImmobile() {
        return this.perch.isPerched() || super.isImmobile();
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
    protected SoundEvent getAmbientSound() {
        return MobsSounds.PARABUZZY_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return MobsSounds.PARABUZZY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MobsSounds.PARABUZZY_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    public enum Variant implements StringRepresentable {
        // A spiked one hits twice as hard as its plain colour.
        BLUE("blue", 30.0D, 2.0D, true),
        RED("red", 35.0D, 3.0D, false),
        BLUE_SPIKED("blue_spiked", 45.0D, BLUE.damage * 2.0D, true),
        RED_SPIKED("red_spiked", 50.0D, RED.damage * 2.0D, false);

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
        private static final Variant[] VALUES = values();

        private final String name;
        public final double health;
        public final double damage;
        public final boolean dropsShell;

        Variant(String name, double health, double damage, boolean dropsShell) {
            this.name = name;
            this.health = health;
            this.damage = damage;
            this.dropsShell = dropsShell;
        }

        public static Variant byId(int id) {
            return id >= 0 && id < VALUES.length ? VALUES[id] : BLUE;
        }

        /** Colour is a coin flip, and one in four of either colour is spiked. */
        public static Variant pick(RandomSource random) {
            boolean red = random.nextBoolean();
            if (random.nextInt(4) == 0) {
                return red ? RED_SPIKED : BLUE_SPIKED;
            }
            return red ? RED : BLUE;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
