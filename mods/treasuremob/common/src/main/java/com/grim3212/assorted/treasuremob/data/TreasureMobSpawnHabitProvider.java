package com.grim3212.assorted.treasuremob.data;

import com.grim3212.assorted.lib.data.LibSpawnHabitProvider;
import com.grim3212.assorted.lib.spawn.SpawnHabit;
import com.grim3212.assorted.lib.spawn.SpawnHabitBuilder;
import com.grim3212.assorted.treasuremob.Constants;
import com.grim3212.assorted.treasuremob.api.TreasureMobTags;
import com.grim3212.assorted.treasuremob.common.entity.TreasureMobEntities;
import net.minecraft.data.PackOutput;

import java.util.function.BiConsumer;

/** How this part's creatures are set down: data/assortedtreasuremob/spawn_habit. A datapack retunes any of it. */
public class TreasureMobSpawnHabitProvider extends LibSpawnHabitProvider {

    /** How far around a spot the creatures already there are counted. */
    private static final int LARGE_NEARBY = 128;

    public TreasureMobSpawnHabitProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addHabits(BiConsumer<String, SpawnHabit> out) {
        // Inside a piece only, alone. Its spawn rule keeps it SPAWN_SPACING from other wild ones; tame ones are persistent, so the cap skips them.
        // Never seeded: a structure site cannot run while a chunk generates.
        out.accept("treasure_mob", SpawnHabitBuilder.of(TreasureMobEntities.TREASURE_MOB).every(80).chance(0.5D)
                .inStructures(TreasureMobTags.Structures.SPAWNS_TREASURE_MOBS).distance(24, 96).tries(24).capOfWild(LARGE_NEARBY, 2).build());
    }
}
