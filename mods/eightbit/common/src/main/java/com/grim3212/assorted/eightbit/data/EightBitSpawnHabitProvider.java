package com.grim3212.assorted.eightbit.data;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.api.EightBitTags;
import com.grim3212.assorted.eightbit.common.entity.EightBitEntities;
import com.grim3212.assorted.lib.data.LibSpawnHabitProvider;
import com.grim3212.assorted.lib.spawn.SpawnHabit;
import com.grim3212.assorted.lib.spawn.SpawnHabitBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.biome.Biomes;

import java.util.function.BiConsumer;

/** How this part's creatures are set down: data/assortedeightbit/spawn_habit. A datapack retunes any of it. */
public class EightBitSpawnHabitProvider extends LibSpawnHabitProvider {

    /** How far around a spot the creatures already there are counted. */
    private static final int LARGE_NEARBY = 128;
    /** Ticks between the spawner's looks; the caps bound what a shorter wait adds. */
    private static final int EVERY = 400;
    private static final int TRIES = 6;

    public EightBitSpawnHabitProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addHabits(BiConsumer<String, SpawnHabit> out) {
        // A farm animal among farm animals: CREATURE still, and waits for the animal cap like a biome spawn would. Its
        // biomes are most of the overworld, so it seeds at a tenth of vanilla's pack rate to stay an occasional sight.
        out.accept("parabuzzy", SpawnHabitBuilder.of(EightBitEntities.PARABUZZY).every(EVERY).tries(TRIES).chance(0.1D).seed(0.01D)
                .onLand().inBiomes(EightBitTags.Biomes.SPAWNS_PARABUZZIES).group(1, 3).cap(LARGE_NEARBY, 5).mobCap().build());
    }
}
