package com.grim3212.assorted.treasuremob.gametest;

import com.grim3212.assorted.lib.spawn.SpawnHabit;
import com.grim3212.assorted.lib.spawn.SpawnHabits;
import com.grim3212.assorted.lib.spawn.SpawnSites;
import com.grim3212.assorted.treasuremob.Constants;
import com.grim3212.assorted.treasuremob.common.entity.TreasureMob;
import com.grim3212.assorted.treasuremob.common.entity.TreasureMobEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.treasuremob.gametest.TreasureMobTestSupport.*;

/** Where and how this part's creatures are set down, all by Assorted Lib's spawn habits. */
final class SpawnTests {

    private SpawnTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("creatures_spawn_with_their_attributes", SpawnTests::creaturesSpawnWithTheirAttributes);
        out.accept("creatures_are_added_to_their_biomes", SpawnTests::creaturesAreAddedToTheirBiomes);
        out.accept("creatures_have_spawn_habits", SpawnTests::creaturesHaveSpawnHabits);
        out.accept("creatures_have_spawn_placements", SpawnTests::creaturesHaveSpawnPlacements);
        out.accept("treasure_mobs_spawn_apart", SpawnTests::treasureMobsSpawnApart);
        out.accept("treasure_mobs_spawn_in_the_dark", SpawnTests::treasureMobsSpawnInTheDark);
    }

    /** Every habit the datagen writes, by name, with the entity each sets down. */
    private static final Map<String, EntityType<?>> HABITS = Map.of(
            "treasure_mob", TreasureMobEntities.TREASURE_MOB.get());

    /** A living type with no attributes cannot even be constructed. */
    private static void creaturesSpawnWithTheirAttributes(GameTestHelper helper) {
        for (EntityType<? extends Mob> type : List.of(TreasureMobEntities.TREASURE_MOB.get())) {
            Mob mob = helper.spawn(type, CENTRE, EntitySpawnReason.SPAWN_ITEM_USE);
            helper.assertTrue(mob.isAlive() && mob.getHealth() > 0, type.getDescriptionId() + " did not spawn alive");
            mob.discard();
        }
        helper.succeed();
    }

    /** Nothing of ours on any biome's spawn list: the habits are what set them down. */
    private static void creaturesAreAddedToTheirBiomes(GameTestHelper helper) {
        for (EntityType<?> type : HABITS.values()) {
            for (MobCategory category : MobCategory.values()) {
                helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).listElementIds().forEach(biome ->
                        helper.assertTrue(spawnEntry(helper, biome, category, type).isEmpty(), type.getDescriptionId() + " is on the biome spawn list of " + biome.identifier() + " as well as a habit"));
            }
        }
        helper.succeed();
    }

    /** Each habit loaded from data/assortedtreasuremob/spawn_habit, on with its mod installed, capped, and seeding new terrain where it can. */
    private static void creaturesHaveSpawnHabits(GameTestHelper helper) {
        HABITS.forEach((name, type) -> {
            SpawnHabit habit = SpawnHabits.get(Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
            helper.assertTrue(habit != null, "no spawn habit " + name + " loaded; loaded: " + SpawnHabits.all().keySet());
            helper.assertValueEqual(habit.entity(), type, "the creature of habit " + name);
            helper.assertTrue(habit.isEnabled(), "habit " + name + " is off");
            helper.assertTrue(habit.cap().isPresent(), "habit " + name + " has no cap");
            helper.assertTrue(habit.seed() > 0.0D || !habit.site().seedsAtGeneration(), "habit " + name + " never seeds new terrain");
        });
        helper.assertValueEqual(SpawnHabits.get(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "treasure_mob")).site().type(), SpawnSites.STRUCTURE, "treasure mob site");
        helper.assertValueEqual(TreasureMobEntities.TREASURE_MOB.get().getCategory(), MobCategory.MISC, "treasure mob category");
        helper.succeed();
    }

    /** A habit asks these placements of every spot, so a habit's creature needs one as much as a biome list's. */
    private static void creaturesHaveSpawnPlacements(GameTestHelper helper) {
        helper.assertTrue(SpawnPlacements.getPlacementType(TreasureMobEntities.TREASURE_MOB.get()) == SpawnPlacementTypes.ON_GROUND,
                "the treasure mob has no spawn placement, so it would spawn in mid air or in water");
        helper.succeed();
    }

    /** Run 150 blocks up so other tests' treasure mobs are outside SPAWN_SPACING. */
    private static void treasureMobsSpawnApart(GameTestHelper helper) {
        BlockPos aloft = CENTRE.above(150);
        BlockPos pos = helper.absolutePos(aloft);
        helper.assertFalse(TreasureMob.hasWildOneNear(helper.getLevel(), pos), "a wild treasure mob was found high over the box");
        TreasureMob tame = helper.spawnWithNoFreeWill(TreasureMobEntities.TREASURE_MOB.get(), aloft.east(2));
        tame.setTame(true, false);
        helper.assertFalse(TreasureMob.hasWildOneNear(helper.getLevel(), pos), "a tame treasure mob kept a wild one from spawning");
        helper.assertTrue(tame.requiresCustomPersistence(), "a tame treasure mob may despawn");
        helper.spawnWithNoFreeWill(TreasureMobEntities.TREASURE_MOB.get(), aloft.west(2));
        helper.assertTrue(TreasureMob.hasWildOneNear(helper.getLevel(), pos), "a wild treasure mob next door did not keep another from spawning");
        helper.succeed();
    }

    /** A dark cell and a lit one, so a retry asks the same thing again; waits for the light engine to settle before asking. */
    private static void treasureMobsSpawnInTheDark(GameTestHelper helper) {
        BlockPos glowing = new BlockPos(1, 1, 1);
        sealedCell(helper, CENTRE, false);
        sealedCell(helper, glowing, true);
        TreasureMob mob = helper.spawnWithNoFreeWill(TreasureMobEntities.TREASURE_MOB.get(), CENTRE);
        ServerLevel level = helper.getLevel();
        BlockPos dark = helper.absolutePos(CENTRE);
        BlockPos lit = helper.absolutePos(glowing);

        helper.succeedWhen(() -> {
            helper.assertValueEqual(level.getMaxLocalRawBrightness(mob.blockPosition()), 0, "light in the sealed cell");
            helper.assertTrue(mob.checkSpawnRules(level, EntitySpawnReason.NATURAL), "a treasure mob cannot spawn in the dark");
            // Its placement rule is the monsters' light check now, so a lit cell turns it down where a dark one does not.
            helper.assertTrue(Monster.isDarkEnoughToSpawn(level, dark, level.getRandom()), "the sealed cell is not dark enough for a monster");
            helper.assertTrue(level.getBrightness(LightLayer.BLOCK, lit) > 0, "the glowstone cell has no block light");
            helper.assertFalse(SpawnPlacements.checkSpawnRules(TreasureMobEntities.TREASURE_MOB.get(), level, EntitySpawnReason.NATURAL, lit, level.getRandom()),
                    "a treasure mob spawns in a lit room");
            mob.discard();
        });
    }

    /** Sealed stone, glowstone for a roof when it should be lit. */
    private static void sealedCell(GameTestHelper helper, BlockPos middle, boolean lit) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y <= 2; y++) {
                    boolean inside = x == 0 && z == 0 && (y == 0 || y == 1);
                    helper.setBlock(middle.offset(x, y, z), inside ? Blocks.AIR : lit && y == 2 ? Blocks.GLOWSTONE : Blocks.STONE);
                }
            }
        }
    }

    private static Optional<Weighted<MobSpawnSettings.SpawnerData>> spawnEntry(GameTestHelper helper, ResourceKey<Biome> biome, MobCategory category, EntityType<?> type) {
        Holder<Biome> holder = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(biome);
        return holder.value().getMobSettings().getMobs(category).unwrap().stream().filter(weighted -> weighted.value().type() == type).findFirst();
    }
}
