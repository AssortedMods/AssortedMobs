package com.grim3212.assorted.mobs.common.entity;

import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.api.MobsTags;
import com.grim3212.assorted.mobs.common.entity.ai.StayWhileOpenGoal;
import com.grim3212.assorted.mobs.common.entity.ai.WildAvoidPlayerGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A chest on legs, a mimic that would rather run than bite. It turns up inside mineshafts, temples
 * and the other structures in {@code MobsTags.Structures.SPAWNS_TREASURE_MOBS}, never out on the
 * surface, with a chest of what that place's own chests hold, rolled when it appears. It drops the
 * lot when it dies. Tempt one with gold nuggets and feed
 * it a few to tame it; a tame one follows its owner, sits when told to, and opens as a chest for
 * its owner when they sneak, standing still while it is open.
 */
public class TreasureMob extends TamableAnimal {

    /** What its chest holds when it turns up outside any structure with a table of its own. */
    public static final ResourceKey<LootTable> CHEST_LOOT = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "chests/treasure_mob"));

    private static final double WILD_HEALTH = 8.0D;
    private static final double TAME_HEALTH = 16.0D;
    /** Five minutes, the longest an untamed one lingers once no player is near. */
    private static final int WILD_LIFETIME = 2400;

    private final TreasureChest chest = new TreasureChest(this);
    @Nullable
    private TemptGoal temptGoal;

    public TreasureMob(EntityType<? extends TreasureMob> type, Level level) {
        super(type, level);
        this.setTame(false, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, WILD_HEALTH).add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    public static final double SPAWN_SPACING = 32.0D;

    public static boolean checkTreasureMobSpawnRules(EntityType<TreasureMob> type, LevelAccessor level, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return Mob.checkMobSpawnRules(type, level, spawnReason, pos, random) && !hasWildOneNear(level, pos);
    }

    /** Tame ones don't count. */
    public static boolean hasWildOneNear(LevelAccessor level, BlockPos pos) {
        return !level.getEntitiesOfClass(TreasureMob.class, new AABB(pos).inflate(SPAWN_SPACING), mob -> !mob.isTame()).isEmpty();
    }

    /** NaturalSpawner leaves these out of the category count, so only wild ones fill the cap of 4. */
    @Override
    public boolean requiresCustomPersistence() {
        return this.isTame();
    }

    /** Animal's rule needs grass or light 12+, which no structure interior has. */
    @Override
    public boolean checkSpawnRules(LevelAccessor level, EntitySpawnReason spawnReason) {
        return true;
    }

    @Override
    protected void registerGoals() {
        // Not scared off by the player moving or turning: aiming at it to feed it would end the
        // temptation, and taming only takes while tempted. The avoid goal ranks below so the
        // nuggets win, as the ocelot's does.
        this.temptGoal = new TemptGoal(this, 0.6D, stack -> stack.is(MobsTags.Items.TREASURE_MOB_TEMPT_ITEMS), false);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new StayWhileOpenGoal(this));
        this.goalSelector.addGoal(3, this.temptGoal);
        this.goalSelector.addGoal(4, new WildAvoidPlayerGoal(this));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 4.0F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    public TreasureChest getChest() {
        return this.chest;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
        this.fillChest(level.getLevel());
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
    }

    /**
     * The table for one found in {@code structure}, {@code assortedmobs:chests/treasure_mob/<namespace>/<path>}.
     * A datapack that adds a structure to the spawn tag gives it its own loot by adding this table.
     */
    public static ResourceKey<LootTable> chestLootFor(ResourceKey<Structure> structure) {
        Identifier id = structure.identifier();
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "chests/treasure_mob/" + id.getNamespace() + "/" + id.getPath()));
    }

    /**
     * The table for its chest at {@code pos}: the one for the structure it is standing in, so it
     * carries what that place's chests would, or {@link #CHEST_LOOT} outside one or for a structure
     * with no table.
     */
    public static ResourceKey<LootTable> chestLootAt(ServerLevel level, BlockPos pos) {
        StructureStart start = level.structureManager().getStructureWithPieceAt(pos, structure -> true);
        if (start.isValid()) {
            Optional<ResourceKey<LootTable>> table = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getResourceKey(start.getStructure())
                    .map(TreasureMob::chestLootFor)
                    .filter(key -> level.getServer().reloadableRegistries().getLootTable(key) != LootTable.EMPTY);
            if (table.isPresent()) {
                return table.get();
            }
        }
        return CHEST_LOOT;
    }

    private void fillChest(ServerLevel level) {
        LootTable table = level.getServer().reloadableRegistries().getLootTable(chestLootAt(level, this.blockPosition()));
        LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, this.position()).create(LootContextParamSets.CHEST);
        table.fill(this.chest, params, this.random.nextLong());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame()) {
            if (this.isOwnedBy(player)) {
                if (!this.level().isClientSide()) {
                    if (player.isSecondaryUseActive()) {
                        player.openMenu(new SimpleMenuProvider((id, inventory, opener) -> ChestMenu.threeRows(id, inventory, this.chest), this.getDisplayName()));
                    } else {
                        this.setOrderedToSit(!this.isOrderedToSit());
                        this.jumping = false;
                        this.navigation.stop();
                    }
                }
                return InteractionResult.SUCCESS;
            }
        } else if (!this.level().isClientSide() && stack.is(MobsTags.Items.TREASURE_MOB_TEMPT_ITEMS)
                && (this.temptGoal == null || this.temptGoal.isRunning()) && player.distanceToSqr(this) < 9.0D) {
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
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, (byte) 7);
        } else {
            this.level().broadcastEntityEvent(this, (byte) 6);
        }
    }

    @Override
    protected void applyTamingSideEffects() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.isTame() ? TAME_HEALTH : WILD_HEALTH);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (this.isInvulnerableTo(level, source)) {
            return false;
        }

        this.setOrderedToSit(false);
        return super.hurtServer(level, source, damage);
    }

    /** The chest is what it drops, loot or not, as a chest's contents are. */
    @Override
    protected void dropEquipment(ServerLevel level) {
        super.dropEquipment(level);
        Containers.dropContents(level, this, this.chest);
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return !this.isTame() && this.tickCount > WILD_LIFETIME;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        ContainerHelper.saveAllItems(output, this.chest.getItems());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        ContainerHelper.loadAllItems(input, this.chest.getItems());
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }
}
