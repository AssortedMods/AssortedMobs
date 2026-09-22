package com.grim3212.assorted.mobs.common.entity;

import com.grim3212.assorted.mobs.common.entity.ai.AmphibiousMoveControl;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * An animal as much at home in the water as out of it: the seal, the walrus and the sea otter. It
 * walks the shore, never far from the water it came out of, swims the way an axolotl does, and tires
 * of each in turn; see SeekWaterGoal and HaulOutGoal. In 1.2.5 the first two never went near water and the otter never left it.
 */
public abstract class AmphibiousAnimal extends Animal {

    /** How fast the client eases between its land and water poses, per tick. */
    private static final float POSE_EASE = 0.1F;
    private static final double WADING_DEPTH = 0.2D;
    private static final double BANK_HOP_CLEARANCE = 0.6D;
    private static final double BANK_HOP = 0.3D;
    private static final double SURFACING_LIFT = 0.02D;
    /** How far from the water it came out of it will wander, in blocks, and how much further it can be taken before it turns back. */
    public static final int SHORE_RANGE = 24;
    private static final int STRAYED_BEYOND = 8;
    private static final int SHORE_CHECK_INTERVAL = 20;
    /** One that has never seen water, out of an egg inland say, looks for some this often, this far. */
    private static final int SHORE_SEARCH_INTERVAL = 200;
    private static final int SHORE_SEARCH_HEIGHT = 6;

    private final UniformInt landTime;
    private final UniformInt waterTime;
    /** Ticks until it wants the other habitat. Not saved: a reload is as good a reason as any to change its mind. */
    private int habitatTicks;
    private boolean wasInWater;
    /** Where it was last in the water, which is the middle of where it keeps to ashore. */
    @Nullable
    private BlockPos shore;

    // Client only, for the model: 0 on land to 1 in the water, and the pitch it swims at.
    private float waterAmount;
    private float waterAmountO;
    private float swimPitch;
    private float swimPitchO;

    /**
     * @param landTime  ticks it stays ashore before it wants a swim
     * @param waterTime ticks it swims before it wants to haul out
     */
    protected AmphibiousAnimal(EntityType<? extends AmphibiousAnimal> type, Level level, float waterSpeed, UniformInt landTime, UniformInt waterTime) {
        super(type, level);
        this.landTime = landTime;
        this.waterTime = waterTime;
        this.habitatTicks = landTime.sample(this.random);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.moveControl = new AmphibiousMoveControl(this, waterSpeed);
        this.lookControl = new SmoothSwimmingLookControl(this, 20);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    /** Has it want the other habitat now, not when its time here next runs out. */
    public void tireOfHabitat() {
        this.habitatTicks = 0;
    }

    public boolean wantsWater() {
        return !this.isInWater() && this.habitatTicks <= 0;
    }

    public boolean wantsLand() {
        return this.isInWater() && this.habitatTicks <= 0;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            return;
        }

        // Arrived somewhere, not just passing: afloat, or stood on dry ground. In the air between the two, as it is for a moment
        // each time it hops at a bank, it has not changed its mind about where it was going.
        boolean inWater = this.isInWater();
        boolean arrived = inWater || this.onGround();
        if (arrived && inWater != this.wasInWater) {
            this.wasInWater = inWater;
            this.habitatTicks = (inWater ? this.waterTime : this.landTime).sample(this.random);
        } else if (this.habitatTicks > 0) {
            this.habitatTicks--;
        }

        if (this.tickCount % SHORE_CHECK_INTERVAL == 0) {
            this.keepToTheShore(inWater);
        }

        // What wants out comes up first: land is never down there, a bank can only be hopped from the surface, and the pathfinder
        // lets nothing climb through water that touches a block, so from the bottom of a channel there may be no way up it can plot.
        if (this.wantsLand() && this.isUnderWater()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, SURFACING_LIFT, 0.0D));
        }
    }

    /**
     * Ashore it keeps within {@link #SHORE_RANGE} of the water it left, by way of the home every wandering goal already
     * respects, and one taken further than that wants the water again. Not while it is on a lead, which is a home of its own.
     */
    private void keepToTheShore(boolean inWater) {
        if (inWater) {
            this.shore = this.blockPosition();
            if (!this.isLeashed()) {
                this.clearHome();
            }
            return;
        }

        if (this.shore != null && this.level().hasChunkAt(this.shore) && !this.level().getFluidState(this.shore).is(FluidTags.WATER)) {
            this.shore = null;
        }
        if (this.shore == null && this.tickCount % SHORE_SEARCH_INTERVAL == 0) {
            // Only where the world is already loaded: asking after a block in a chunk that is not loads it, or makes it.
            this.shore = BlockPos.findClosestMatch(this.blockPosition(), SHORE_RANGE, SHORE_SEARCH_HEIGHT, pos -> this.level().hasChunkAt(pos) && this.level().getFluidState(pos).is(FluidTags.WATER)).orElse(null);
        }
        if (this.shore == null || this.isLeashed()) {
            return;
        }

        this.setHomeTo(this.shore, SHORE_RANGE);
        if (!this.shore.closerToCenterThan(this.position(), SHORE_RANGE + STRAYED_BEYOND)) {
            this.habitatTicks = 0;
        }
    }

    /** Where it was last in the water, if it ever has been and it is still water. */
    public @Nullable BlockPos getShore() {
        return this.shore;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.storeNullable("shore", BlockPos.CODEC, this.shore);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.shore = input.read("shore", BlockPos.CODEC).orElse(null);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.waterAmountO = this.waterAmount;
            this.swimPitchO = this.swimPitch;
            this.waterAmount = Mth.clamp(this.waterAmount + (this.isSwimmingPose() ? POSE_EASE : -POSE_EASE), 0.0F, 1.0F);
            this.swimPitch += (this.getXRot() - this.swimPitch) * 0.2F;
        }
    }

    /** Afloat, and not just wading: in a puddle or a trickle it is still on its feet. */
    protected boolean isSwimmingPose() {
        return this.isInWater() && this.getFluidHeight(FluidTags.WATER) > WADING_DEPTH;
    }

    /** 0 on land to 1 in the water. Client only. */
    public float getWaterAmount(float partialTick) {
        return Mth.lerp(partialTick, this.waterAmountO, this.waterAmount);
    }

    /** Its pitch in degrees, eased so a dive is a curve and not a snap. Client only. */
    public float getSwimPitch(float partialTick) {
        return Mth.lerp(partialTick, this.swimPitchO, this.swimPitch);
    }

    /** No gravity and no sinking: it goes where it swims, as an axolotl does. */
    @Override
    protected void travelInWater(Vec3 input, double baseGravity, boolean isFalling, double oldY) {
        this.moveRelative(this.getSpeed(), input);
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));

        // Vanilla's jumpOutOfFluid, which is private and went with the method this replaces: up against a bank with room
        // over it, a kick upwards. Without it nothing afloat gets over the lip of a shore, since only what stands can step up.
        Vec3 movement = this.getDeltaMovement();
        if (this.horizontalCollision && this.isFree(movement.x, movement.y + BANK_HOP_CLEARANCE - this.getY() + oldY, movement.z)) {
            this.setDeltaMovement(movement.x, BANK_HOP, movement.z);
        }
    }

    /**
     * Not vanilla's, which refuses any spawn with water in its box: that is for land animals, and the otter is spawned
     * in the water, so it never spawned at all. What the water animals and the axolotl do instead.
     */
    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public int getMaxHeadXRot() {
        return this.isInWater() ? 1 : super.getMaxHeadXRot();
    }

    @Override
    public int getMaxHeadYRot() {
        return this.isInWater() ? 1 : super.getMaxHeadYRot();
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }
}
