package com.grim3212.assorted.mobs.common.entity;

import com.grim3212.assorted.mobs.api.MobsTags;
import com.grim3212.assorted.mobs.common.entity.ai.AmphibiousStrollGoal;
import com.grim3212.assorted.mobs.common.entity.ai.FleeToWaterGoal;
import com.grim3212.assorted.mobs.common.entity.ai.FloatOnBackGoal;
import com.grim3212.assorted.mobs.common.entity.ai.HaulOutGoal;
import com.grim3212.assorted.mobs.common.entity.ai.SeekWaterGoal;
import com.grim3212.assorted.mobs.common.item.MobsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

/**
 * Lies on its back in rivers and along the coast, cracking open the sea shells that shell armour is
 * made of, and leaves one behind now and then. It dives to get about, and comes ashore for a while
 * when it has had enough of both.
 */
public class SeaOtter extends AmphibiousAnimal {

    private static final EntityDataAccessor<Boolean> FLOATING = SynchedEntityData.defineId(SeaOtter.class, EntityDataSerializers.BOOLEAN);
    private static final UniformInt LAND_TIME = TimeUtil.rangeOfSeconds(20, 60);
    private static final UniformInt WATER_TIME = TimeUtil.rangeOfSeconds(120, 300);
    /** A chicken's five to ten minutes an egg, but only the time spent afloat counts. */
    private static final UniformInt SHELL_TIME = TimeUtil.rangeOfSeconds(300, 600);
    private static final float FLOAT_EASE = 0.08F;
    private static final int SPAWN_DEPTH = 6;
    /** Vanilla's snow line. The frozen river and ocean are at 0; every other river and shallow sea, the cold ocean too, at 0.5. */
    private static final float MIN_TEMPERATURE = 0.15F;

    private int shellTime;

    // Client only, for the model: 0 the right way up to 1 on its back.
    private float floatAmount;
    private float floatAmountO;

    public SeaOtter(EntityType<? extends SeaOtter> type, Level level) {
        super(type, level, 0.02F, LAND_TIME, WATER_TIME);
        this.shellTime = SHELL_TIME.sample(this.random);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.STEP_HEIGHT, 1.0D);
    }

    /**
     * Near the top of the water, however little of it there is: a river is seldom three blocks deep. And not in a frozen
     * one: the biome tag it spawns by has every river and shallow sea in it, the frozen ones too, and those are the seal's.
     */
    public static boolean checkSeaOtterSpawnRules(EntityType<SeaOtter> type, LevelAccessor level, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return pos.getY() >= level.getSeaLevel() - SPAWN_DEPTH && pos.getY() <= level.getSeaLevel() && isWarmEnough(level.getBiome(pos));
    }

    /**
     * By the biome's own temperature, not the spot's: a frozen ocean's has noise in it that leaves the odd patch of it
     * unfrozen, and an otter has no more business in one of those than on the ice around it.
     */
    public static boolean isWarmEnough(Holder<Biome> biome) {
        return biome.value().getBaseTemperature() > MIN_TEMPERATURE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FLOATING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FleeToWaterGoal(this, 1.6D));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.2D, this::isFood, false));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new SeekWaterGoal(this, 1.0D, 24));
        this.goalSelector.addGoal(4, new HaulOutGoal(this, 1.0D, 12));
        this.goalSelector.addGoal(5, new AmphibiousStrollGoal(this, 1.0D, 1.0D, 240));
        this.goalSelector.addGoal(6, new FloatOnBackGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public boolean isFloating() {
        return this.entityData.get(FLOATING);
    }

    public void setFloating(boolean floating) {
        this.entityData.set(FLOATING, floating);
    }

    /** 0 the right way up to 1 on its back. Client only. */
    public float getFloatAmount(float partialTick) {
        return Mth.lerp(partialTick, this.floatAmountO, this.floatAmount);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.floatAmountO = this.floatAmount;
            this.floatAmount = Mth.clamp(this.floatAmount + (this.isFloating() ? FLOAT_EASE : -FLOAT_EASE), 0.0F, 1.0F);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level() instanceof ServerLevel level && this.isAlive() && !this.isBaby() && this.isFloating() && --this.shellTime <= 0) {
            this.playSound(SoundEvents.TURTLE_EGG_CRACK, 0.7F, 1.2F + this.random.nextFloat() * 0.3F);
            this.spawnAtLocation(level, MobsItems.SEA_SHELL.get());
            this.shellTime = SHELL_TIME.sample(this.random);
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(MobsTags.Items.SEAL_FOOD);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return MobsEntities.SEA_OTTER.get().create(level, EntitySpawnReason.BREEDING);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("ShellTime", this.shellTime);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        input.getInt("ShellTime").ifPresent(time -> this.shellTime = time);
    }
}
