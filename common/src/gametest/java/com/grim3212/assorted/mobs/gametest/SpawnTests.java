package com.grim3212.assorted.mobs.gametest;

import com.grim3212.assorted.mobs.MobsCommonMod;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.mobs.gametest.MobsTestSupport.*;

/**
 * That each creature can exist at all, and that the loaders were told where it lives. All three
 * go through AssortedLib, which does them differently on each loader.
 */
final class SpawnTests {

    private SpawnTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("creatures_spawn_with_their_attributes", SpawnTests::creaturesSpawnWithTheirAttributes);
        out.accept("creatures_are_added_to_their_biomes", SpawnTests::creaturesAreAddedToTheirBiomes);
        out.accept("creatures_have_spawn_placements", SpawnTests::creaturesHaveSpawnPlacements);
    }

    /** A living type with no attributes cannot even be constructed. */
    private static void creaturesSpawnWithTheirAttributes(GameTestHelper helper) {
        for (EntityType<? extends Mob> type : List.of(MobsEntities.ICE_PIXIE.get(), MobsEntities.TREASURE_MOB.get(), MobsEntities.BOBOMB.get(), MobsEntities.PARABUZZY.get())) {
            Mob mob = helper.spawn(type, CENTRE, EntitySpawnReason.SPAWN_ITEM_USE);
            helper.assertTrue(mob.isAlive() && mob.getHealth() > 0, type.getDescriptionId() + " did not spawn alive");
            mob.discard();
        }
        helper.succeed();
    }

    /** At the configured weight, which the config only knows once it has loaded. */
    private static void creaturesAreAddedToTheirBiomes(GameTestHelper helper) {
        assertSpawns(helper, Biomes.SNOWY_PLAINS, MobCategory.MONSTER, MobsEntities.ICE_PIXIE.get(), MobsCommonMod.COMMON_CONFIG.icePixieWeight.get());
        assertSpawns(helper, Biomes.PLAINS, MobCategory.CREATURE, MobsEntities.TREASURE_MOB.get(), MobsCommonMod.COMMON_CONFIG.treasureMobWeight.get());
        assertSpawns(helper, Biomes.PLAINS, MobCategory.CREATURE, MobsEntities.PARABUZZY.get(), MobsCommonMod.COMMON_CONFIG.parabuzzyWeight.get());

        helper.assertTrue(spawnEntry(helper, Biomes.DESERT, MobCategory.MONSTER, MobsEntities.ICE_PIXIE.get()).isEmpty(), "ice pixies spawn in the desert");
        helper.assertTrue(spawnEntry(helper, Biomes.NETHER_WASTES, MobCategory.CREATURE, MobsEntities.PARABUZZY.get()).isEmpty(), "parabuzzies spawn in the nether");
        helper.succeed();
    }

    private static void creaturesHaveSpawnPlacements(GameTestHelper helper) {
        for (EntityType<?> type : List.of(MobsEntities.ICE_PIXIE.get(), MobsEntities.TREASURE_MOB.get(), MobsEntities.PARABUZZY.get())) {
            helper.assertTrue(SpawnPlacements.getPlacementType(type) == SpawnPlacementTypes.ON_GROUND,
                    type.getDescriptionId() + " has no spawn placement, so it would spawn in mid air or in water");
        }
        helper.succeed();
    }

    private static void assertSpawns(GameTestHelper helper, ResourceKey<Biome> biome, MobCategory category, EntityType<?> type, int weight) {
        Optional<Weighted<MobSpawnSettings.SpawnerData>> entry = spawnEntry(helper, biome, category, type);
        helper.assertTrue(entry.isPresent(), type.getDescriptionId() + " is not among the " + category.getName() + " spawns of " + biome.identifier());
        helper.assertValueEqual(entry.get().weight(), weight, type.getDescriptionId() + " spawn weight in " + biome.identifier());
    }

    private static Optional<Weighted<MobSpawnSettings.SpawnerData>> spawnEntry(GameTestHelper helper, ResourceKey<Biome> biome, MobCategory category, EntityType<?> type) {
        Holder<Biome> holder = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(biome);
        return holder.value().getMobSettings().getMobs(category).unwrap().stream().filter(weighted -> weighted.value().type() == type).findFirst();
    }
}
