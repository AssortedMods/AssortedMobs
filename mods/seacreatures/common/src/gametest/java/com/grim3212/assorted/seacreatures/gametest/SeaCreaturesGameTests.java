package com.grim3212.assorted.seacreatures.gametest;

import net.minecraft.gametest.framework.GameTestHelper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Sea Creatures. The tests live in small {@code <Feature>Tests} classes;
 * this only lists them. Each name needs a matching {@code data/assortedseacreatures/test_instance/<name>.json}.
 */
public final class SeaCreaturesGameTests {

    private SeaCreaturesGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        AssetTests.register(out);
        AliasTests.register(out);
        SpawnTests.register(out);
        AmphibiousTests.register(out);
        SeaCreatureTests.register(out);
        SpawnerTests.register(out);
        FamilyTests.register(out);
    }
}
