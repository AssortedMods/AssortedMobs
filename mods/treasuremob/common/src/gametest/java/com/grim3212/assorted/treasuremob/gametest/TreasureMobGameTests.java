package com.grim3212.assorted.treasuremob.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Mob;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Treasure Mob. The tests live in small {@code <Feature>Tests} classes;
 * this only lists them. Each name needs a matching {@code data/assortedtreasuremob/test_instance/<name>.json}.
 */
public final class TreasureMobGameTests {

    private TreasureMobGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        AssetTests.register(out);
        AliasTests.register(out);
        SpawnTests.register(out);
        TreasureMobTests.register(out);
        FollowOwnerTests.register(out);
        SpawnerTests.register(out);
    }
}
