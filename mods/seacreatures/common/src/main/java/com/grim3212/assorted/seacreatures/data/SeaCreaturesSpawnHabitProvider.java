package com.grim3212.assorted.seacreatures.data;

import com.grim3212.assorted.lib.data.LibSpawnHabitProvider;
import com.grim3212.assorted.lib.spawn.SpawnHabit;
import com.grim3212.assorted.lib.spawn.SpawnHabitBuilder;
import com.grim3212.assorted.seacreatures.Constants;
import com.grim3212.assorted.seacreatures.api.SeaCreaturesTags;
import com.grim3212.assorted.seacreatures.common.entity.SeaCreaturesEntities;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.biome.Biomes;

import java.util.function.BiConsumer;

/** How this part's creatures are set down: data/assortedseacreatures/spawn_habit. A datapack retunes any of it. */
public class SeaCreaturesSpawnHabitProvider extends LibSpawnHabitProvider {

    /** How far around a spot the creatures already there are counted. */
    private static final int NEARBY = 64;
    private static final int LARGE_NEARBY = 128;
    /** Ticks between the spawner's looks; the caps bound what a shorter wait adds. */
    private static final int EVERY = 400;
    private static final int TRIES = 6;

    public SeaCreaturesSpawnHabitProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addHabits(BiConsumer<String, SpawnHabit> out) {
        // Seals and walruses share one herd cap, by the ice_herd tag: so many of either together, whatever the mix.
        out.accept("seal", SpawnHabitBuilder.of(SeaCreaturesEntities.SEAL).every(EVERY).tries(TRIES).chance(0.25D).seed(0.03D)
                .onLand().inBiomes(SeaCreaturesTags.Biomes.SPAWNS_SEALS).group(2, 4).cap(NEARBY, 6, SeaCreaturesTags.EntityTypes.ICE_HERD).build());
        out.accept("walrus", SpawnHabitBuilder.of(SeaCreaturesEntities.WALRUS).every(EVERY).tries(TRIES).chance(0.12D).seed(0.015D)
                .onLand().inBiomes(SeaCreaturesTags.Biomes.SPAWNS_WALRUSES).group(2, 3).cap(NEARBY, 6, SeaCreaturesTags.EntityTypes.ICE_HERD).build());
        // Now and then on any beach or rocky shore; not where the shore is one of its own biomes already.
        out.accept("walrus_ashore", SpawnHabitBuilder.of(SeaCreaturesEntities.WALRUS).every(EVERY).tries(TRIES).chance(0.02D).seed(0.004D)
                .onLand().inBiomes(SeaCreaturesTags.Biomes.SPAWNS_WALRUSES_RARELY).notInBiomes(SeaCreaturesTags.Biomes.SPAWNS_WALRUSES).group(2, 3).cap(NEARBY, 6, SeaCreaturesTags.EntityTypes.ICE_HERD).build());
        out.accept("sea_otter", SpawnHabitBuilder.of(SeaCreaturesEntities.SEA_OTTER).every(EVERY).tries(TRIES).chance(0.25D).seed(0.04D)
                .inWater().inBiomes(SeaCreaturesTags.Biomes.SPAWNS_SEA_OTTERS).group(2, 4).cap(LARGE_NEARBY, 5).build());
        // Under the ice of the frozen seas as much as in open water; its rule wants water over it, so never at the very top.
        out.accept("narwhal", SpawnHabitBuilder.of(SeaCreaturesEntities.NARWHAL).every(EVERY).tries(TRIES).chance(0.15D).seed(0.02D)
                .inWater(1, 6, true).inBiomes(SeaCreaturesTags.Biomes.SPAWNS_NARWHALS).group(1, 2).cap(NEARBY, 3).build());
    }
}
