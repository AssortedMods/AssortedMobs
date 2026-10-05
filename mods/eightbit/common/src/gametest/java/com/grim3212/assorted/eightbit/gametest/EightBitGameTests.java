package com.grim3212.assorted.eightbit.gametest;

import net.minecraft.gametest.framework.GameTestHelper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted 8-Bit Mobs. The tests live in small {@code <Feature>Tests} classes;
 * this only lists them. Each name needs a matching {@code data/assortedeightbit/test_instance/<name>.json}.
 */
public final class EightBitGameTests {

    private EightBitGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        AssetTests.register(out);
        AliasTests.register(out);
        MigrationTests.register(out);
        SpawnTests.register(out);
        EightBitTests.register(out);
        FollowOwnerTests.register(out);
        ParabuzzyTests.register(out);
        FamilyTests.register(out);
    }
}
