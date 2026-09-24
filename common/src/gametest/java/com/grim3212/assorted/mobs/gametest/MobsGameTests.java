package com.grim3212.assorted.mobs.gametest;

import net.minecraft.gametest.framework.GameTestHelper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Mobs. The tests live in small {@code <Feature>Tests}
 * classes; this only lists them. Each name here needs a matching
 * {@code data/assortedmobs/test_instance/<name>.json}.
 */
public final class MobsGameTests {

    private MobsGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        AssetTests.register(out);
        SpawnTests.register(out);
        IcePixieTests.register(out);
        TreasureMobTests.register(out);
        EightBitTests.register(out);
        FollowOwnerTests.register(out);
        ParabuzzyTests.register(out);
        AmphibiousTests.register(out);
        SeaCreatureTests.register(out);
        SpawnerTests.register(out);
    }
}
