package com.grim3212.assorted.eightbit.gametest;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.common.entity.EightBitEntities;
import com.grim3212.assorted.lib.spawn.SpawnHabit;
import com.grim3212.assorted.lib.spawn.SpawnHabits;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.eightbit.gametest.EightBitTestSupport.*;

/** Where and how this part's creatures are set down, all by Assorted Lib's spawn habits. */
final class SpawnTests {

    private SpawnTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("creatures_spawn_with_their_attributes", SpawnTests::creaturesSpawnWithTheirAttributes);
        out.accept("creatures_are_added_to_their_biomes", SpawnTests::creaturesAreAddedToTheirBiomes);
        out.accept("creatures_have_spawn_habits", SpawnTests::creaturesHaveSpawnHabits);
        out.accept("creatures_have_spawn_placements", SpawnTests::creaturesHaveSpawnPlacements);
    }

    /** Every habit the datagen writes, by name, with the entity each sets down. */
    private static final Map<String, EntityType<?>> HABITS = Map.of(
            "parabuzzy", EightBitEntities.PARABUZZY.get());

    /** A living type with no attributes cannot even be constructed. */
    private static void creaturesSpawnWithTheirAttributes(GameTestHelper helper) {
        for (EntityType<? extends Mob> type : List.of(EightBitEntities.BOBOMB.get(), EightBitEntities.PARABUZZY.get())) {
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

    /** Each habit loaded from data/assortedeightbit/spawn_habit, on with its mod installed, capped, and seeding new terrain where it can. */
    private static void creaturesHaveSpawnHabits(GameTestHelper helper) {
        HABITS.forEach((name, type) -> {
            SpawnHabit habit = SpawnHabits.get(Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
            helper.assertTrue(habit != null, "no spawn habit " + name + " loaded; loaded: " + SpawnHabits.all().keySet());
            helper.assertValueEqual(habit.entity(), type, "the creature of habit " + name);
            helper.assertTrue(habit.isEnabled(), "habit " + name + " is off");
            helper.assertTrue(habit.cap().isPresent(), "habit " + name + " has no cap");
            helper.assertTrue(habit.seed() > 0.0D || !habit.site().seedsAtGeneration(), "habit " + name + " never seeds new terrain");
        });
        // A farm animal among farm animals: it counts toward the animal cap and waits for it.
        helper.assertValueEqual(EightBitEntities.PARABUZZY.get().getCategory(), MobCategory.CREATURE, "parabuzzy category");
        helper.assertTrue(SpawnHabits.get(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "parabuzzy")).mobCap(), "the parabuzzy habit ignores the animal cap");
        helper.succeed();
    }

    /** A habit asks these placements of every spot, so a habit's creature needs one as much as a biome list's. */
    private static void creaturesHaveSpawnPlacements(GameTestHelper helper) {
        helper.assertTrue(SpawnPlacements.getPlacementType(EightBitEntities.PARABUZZY.get()) == SpawnPlacementTypes.ON_GROUND,
                "the parabuzzy has no spawn placement, so it would spawn in mid air or in water");
        helper.succeed();
    }

    private static Optional<Weighted<MobSpawnSettings.SpawnerData>> spawnEntry(GameTestHelper helper, ResourceKey<Biome> biome, MobCategory category, EntityType<?> type) {
        Holder<Biome> holder = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(biome);
        return holder.value().getMobSettings().getMobs(category).unwrap().stream().filter(weighted -> weighted.value().type() == type).findFirst();
    }
}
