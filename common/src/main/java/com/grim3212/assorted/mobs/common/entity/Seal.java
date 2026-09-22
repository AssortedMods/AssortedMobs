package com.grim3212.assorted.mobs.common.entity;

import com.grim3212.assorted.mobs.api.MobsTags;
import com.grim3212.assorted.mobs.common.entity.ai.AmphibiousStrollGoal;
import com.grim3212.assorted.mobs.common.entity.ai.FleeToWaterGoal;
import com.grim3212.assorted.mobs.common.entity.ai.HaulOutGoal;
import com.grim3212.assorted.mobs.common.entity.ai.SeekWaterGoal;
import com.grim3212.assorted.mobs.common.sounds.MobsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction8;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

/**
 * A seal, hauled out on the snow and ice until it fancies a swim. It harms nothing, follows anyone
 * holding a fish, and is full of them. On land it is slow; hit one and it bolts for the water, where it is not.
 */
public class Seal extends AmphibiousAnimal {

    private static final UniformInt LAND_TIME = TimeUtil.rangeOfSeconds(60, 150);
    private static final UniformInt WATER_TIME = TimeUtil.rangeOfSeconds(45, 120);

    public Seal(EntityType<? extends Seal> type, Level level) {
        super(type, level, 0.022F, LAND_TIME, WATER_TIME);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.18D).add(Attributes.STEP_HEIGHT, 1.0D);
    }

    /** How far off the water may be for one to spawn: well inside what it keeps to ashore. */
    private static final int[] WATER_WITHIN = {3, 6, 9, 12};
    /**
     * One spot in this many is taken, the rest passed over, as the ocelot's are. A weight only says how a pack is shared
     * out against the other animals, and on the frozen ocean there is only the polar bear to share with, while every
     * block of ice has the sea just under it: without this the ice was carpeted.
     */
    public static final int SPAWN_ODDS = 10;

    /**
     * On snow, ice or whatever other animals will spawn on, in daylight, by the water, and only now and then. Shared
     * with the walrus: in 1.2.5 both wanted snow underfoot and nothing else, which put them far inland.
     */
    public static boolean checkArcticSpawnRules(EntityType<? extends Animal> type, LevelAccessor level, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return random.nextInt(SPAWN_ODDS) == 0 && level.getBlockState(pos.below()).is(MobsTags.Blocks.SEALS_SPAWNABLE_ON) && isBrightEnoughToSpawn(level, pos) && isByTheWater(level, pos);
    }

    /**
     * A few looks, not a search: this is asked of every place a spawn is tried. At its feet or just under, where a shore's
     * water lies. Never outside the chunk that is being generated, where this is asked as the world is made: its neighbours
     * may have no terrain yet, and reaching for one from a worldgen thread waits on a chunk that is waiting on this one,
     * which hangs the game. Nor into a chunk that is not loaded, in a world that is running, which would load it.
     */
    private static boolean isByTheWater(LevelAccessor level, BlockPos pos) {
        ChunkPos generating = level instanceof WorldGenRegion region ? region.getCenter() : null;
        BlockPos.MutableBlockPos look = new BlockPos.MutableBlockPos();
        for (int distance : WATER_WITHIN) {
            for (Direction8 direction : Direction8.values()) {
                look.set(pos.getX() + direction.getStepX() * distance, pos.getY(), pos.getZ() + direction.getStepZ() * distance);
                if (generating != null ? !generating.equals(ChunkPos.containing(look)) : !AmphibiousAnimal.isLoaded(level, look)) {
                    continue;
                }
                for (int down = 1; down <= 2; down++) {
                    if (level.getFluidState(look.setY(pos.getY() - down)).is(FluidTags.WATER)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FleeToWaterGoal(this, 1.8D));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.2D, this::isFood, false));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new SeekWaterGoal(this, 1.0D, 24));
        this.goalSelector.addGoal(4, new HaulOutGoal(this, 1.0D, 16));
        this.goalSelector.addGoal(5, new AmphibiousStrollGoal(this, 1.0D, 1.0D, 120));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(MobsTags.Items.SEAL_FOOD);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return MobsEntities.SEAL.get().create(level, EntitySpawnReason.BREEDING);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return MobsSounds.SEAL_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return MobsSounds.SEAL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MobsSounds.SEAL_HURT.get();
    }
}
