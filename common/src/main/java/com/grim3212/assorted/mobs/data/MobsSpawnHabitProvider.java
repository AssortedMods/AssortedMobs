package com.grim3212.assorted.mobs.data;

import com.grim3212.assorted.lib.data.LibSpawnHabitProvider;
import com.grim3212.assorted.lib.spawn.SpawnHabit;
import com.grim3212.assorted.lib.spawn.SpawnHabitBuilder;
import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.api.MobsTags;
import com.grim3212.assorted.mobs.common.MobsParts;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import net.minecraft.data.PackOutput;

import java.util.function.BiConsumer;

/**
 * How every creature is set down: data/assortedmobs/spawn_habit. A datapack retunes any of it. The biome
 * lists could not keep the seals scarce on the ice, put most pixies down caves against the monster cap,
 * and could not reach inside a structure at all.
 */
public class MobsSpawnHabitProvider extends LibSpawnHabitProvider {

    /** How far around a spot the creatures already there are counted. */
    private static final int NEARBY = 64;
    private static final int LARGE_NEARBY = 128;
    /** Ticks between the spawner's looks; the caps bound what a shorter wait adds. */
    private static final int EVERY = 400;
    private static final int TRIES = 6;

    public MobsSpawnHabitProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addHabits(BiConsumer<String, SpawnHabit> out) {
        // A farm animal among farm animals: CREATURE still, and waits for the animal cap like a biome spawn would. Its
        // biomes are most of the overworld, so it seeds at a tenth of vanilla's pack rate to stay an occasional sight.
        out.accept("parabuzzy", SpawnHabitBuilder.of(MobsEntities.PARABUZZY).part(MobsParts.EIGHT_BIT).every(EVERY).tries(TRIES).chance(0.1D).seed(0.01D)
                .onLand().inBiomes(MobsTags.Biomes.SPAWNS_PARABUZZIES).group(1, 3).cap(LARGE_NEARBY, 5).mobCap().build());
        // Surface only: the land site is the heightmap, so no caves.
        out.accept("ice_pixie", SpawnHabitBuilder.of(MobsEntities.ICE_PIXIE).part(MobsParts.ICE_PIXIE).every(EVERY).tries(TRIES).chance(0.35D).seed(0.03D)
                .onLand().inBiomes(MobsTags.Biomes.SPAWNS_ICE_PIXIES).group(1, 3).cap(LARGE_NEARBY, 6).build());
        // Seals and walruses share one herd cap, by the ice_herd tag: so many of either together, whatever the mix.
        out.accept("seal", SpawnHabitBuilder.of(MobsEntities.SEAL).part(MobsParts.SEA_CREATURES).every(EVERY).tries(TRIES).chance(0.25D).seed(0.03D)
                .onLand().inBiomes(MobsTags.Biomes.SPAWNS_SEALS).group(2, 4).cap(NEARBY, 6, MobsTags.EntityTypes.ICE_HERD).build());
        out.accept("walrus", SpawnHabitBuilder.of(MobsEntities.WALRUS).part(MobsParts.SEA_CREATURES).every(EVERY).tries(TRIES).chance(0.12D).seed(0.015D)
                .onLand().inBiomes(MobsTags.Biomes.SPAWNS_WALRUSES).group(2, 3).cap(NEARBY, 6, MobsTags.EntityTypes.ICE_HERD).build());
        // Now and then on any beach or rocky shore; not where the shore is one of its own biomes already.
        out.accept("walrus_ashore", SpawnHabitBuilder.of(MobsEntities.WALRUS).part(MobsParts.SEA_CREATURES).every(EVERY).tries(TRIES).chance(0.02D).seed(0.004D)
                .onLand().inBiomes(MobsTags.Biomes.SPAWNS_WALRUSES_RARELY).notInBiomes(MobsTags.Biomes.SPAWNS_WALRUSES).group(2, 3).cap(NEARBY, 6, MobsTags.EntityTypes.ICE_HERD).build());
        out.accept("sea_otter", SpawnHabitBuilder.of(MobsEntities.SEA_OTTER).part(MobsParts.SEA_CREATURES).every(EVERY).tries(TRIES).chance(0.25D).seed(0.04D)
                .inWater().inBiomes(MobsTags.Biomes.SPAWNS_SEA_OTTERS).group(2, 4).cap(LARGE_NEARBY, 5).build());
        // Under the ice of the frozen seas as much as in open water; its rule wants water over it, so never at the very top.
        out.accept("narwhal", SpawnHabitBuilder.of(MobsEntities.NARWHAL).part(MobsParts.SEA_CREATURES).every(EVERY).tries(TRIES).chance(0.15D).seed(0.02D)
                .inWater(1, 6, true).inBiomes(MobsTags.Biomes.SPAWNS_NARWHALS).group(1, 2).cap(NEARBY, 3).build());
        // Inside a piece only, alone. Its spawn rule keeps it SPAWN_SPACING from other wild ones; tame ones are persistent, so the cap skips them.
        // Never seeded: a structure site cannot run while a chunk generates.
        out.accept("treasure_mob", SpawnHabitBuilder.of(MobsEntities.TREASURE_MOB).part(MobsParts.TREASURE_MOB).every(80).chance(0.5D)
                .inStructures(MobsTags.Structures.SPAWNS_TREASURE_MOBS).distance(24, 96).tries(24).capOfWild(LARGE_NEARBY, 2).build());
    }
}
