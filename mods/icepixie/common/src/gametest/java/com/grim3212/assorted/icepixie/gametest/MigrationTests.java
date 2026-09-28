package com.grim3212.assorted.icepixie.gametest;

import com.grim3212.assorted.icepixie.Constants;
import com.grim3212.assorted.icepixie.common.item.IcePixieItems;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** With only the ice pixie installed, the Assorted Mobs advancement root still has an icon of its own. */
final class MigrationTests {

    private MigrationTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("advancement_root_falls_back_to_the_ice_pixie", MigrationTests::advancementRootFallsBackToTheIcePixie);
    }

    // The family's first two icons, the Bob-omb and the narwhal horn, are not installed here.
    private static void advancementRootFallsBackToTheIcePixie(GameTestHelper helper) {
        Identifier root = Identifier.fromNamespaceAndPath(Constants.FAMILY_ID, "root");
        helper.assertTrue(helper.getLevel().getServer().getAdvancements().get(root).value().display().orElseThrow().getIcon().item().value() == IcePixieItems.ICE_PIXIE_SPAWN_EGG.get(),
                "the Assorted Mobs advancement root is not drawn with the ice pixie's egg, the first of the family's icons installed");
        helper.succeed();
    }
}
