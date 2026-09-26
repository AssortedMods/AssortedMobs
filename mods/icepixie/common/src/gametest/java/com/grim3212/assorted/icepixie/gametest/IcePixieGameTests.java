package com.grim3212.assorted.icepixie.gametest;

import net.minecraft.gametest.framework.GameTestHelper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Ice Pixie. The tests live in small {@code <Feature>Tests} classes;
 * this only lists them. Each name needs a matching {@code data/assortedicepixie/test_instance/<name>.json}.
 */
public final class IcePixieGameTests {

    private IcePixieGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        AssetTests.register(out);
        AliasTests.register(out);
        MigrationTests.register(out);
        SpawnTests.register(out);
        IcePixieTests.register(out);
    }
}
