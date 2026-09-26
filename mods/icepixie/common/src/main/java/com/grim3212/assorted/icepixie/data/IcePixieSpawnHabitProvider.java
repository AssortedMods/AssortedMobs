package com.grim3212.assorted.icepixie.data;

import com.grim3212.assorted.icepixie.Constants;
import com.grim3212.assorted.icepixie.api.IcePixieTags;
import com.grim3212.assorted.icepixie.common.entity.IcePixieEntities;
import com.grim3212.assorted.lib.data.LibSpawnHabitProvider;
import com.grim3212.assorted.lib.spawn.SpawnHabit;
import com.grim3212.assorted.lib.spawn.SpawnHabitBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.biome.Biomes;

import java.util.function.BiConsumer;

/** How this part's creatures are set down: data/assortedicepixie/spawn_habit. A datapack retunes any of it. */
public class IcePixieSpawnHabitProvider extends LibSpawnHabitProvider {

    /** How far around a spot the creatures already there are counted. */
    private static final int LARGE_NEARBY = 128;
    /** Ticks between the spawner's looks; the caps bound what a shorter wait adds. */
    private static final int EVERY = 400;
    private static final int TRIES = 6;

    public IcePixieSpawnHabitProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addHabits(BiConsumer<String, SpawnHabit> out) {
        // Surface only: the land site is the heightmap, so no caves.
        out.accept("ice_pixie", SpawnHabitBuilder.of(IcePixieEntities.ICE_PIXIE).every(EVERY).tries(TRIES).chance(0.35D).seed(0.03D)
                .onLand().inBiomes(IcePixieTags.Biomes.SPAWNS_ICE_PIXIES).group(1, 3).cap(LARGE_NEARBY, 6).build());
    }
}
