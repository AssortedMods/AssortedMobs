package com.grim3212.assorted.icepixie.gametest;

import com.grim3212.assorted.icepixie.Constants;
import com.grim3212.assorted.icepixie.common.item.IcePixieItems;
import com.grim3212.assorted.lib.family.Families;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** The Assorted Mobs advancement root is drawn with the family's first installed icon, which is this part's own when it is alone. */
final class MigrationTests {

    private MigrationTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("advancement_root_falls_back_to_the_ice_pixie", MigrationTests::advancementRootFallsBackToTheIcePixie);
    }

    private static void advancementRootFallsBackToTheIcePixie(GameTestHelper helper) {
        Identifier root = Identifier.fromNamespaceAndPath(Constants.FAMILY_ID, "root");
        Item drawn = helper.getLevel().getServer().getAdvancements().get(root).value().display().orElseThrow().icon().item().value();
        Item expected = Families.icon(Constants.FAMILY_ID).getItem();
        helper.assertTrue(drawn == expected, "the Assorted Mobs advancement root is drawn with " + drawn + ", not the family's first installed icon " + expected);
        // Beside a part whose icon ranks higher, as in a family's combined run, the root rightly shows that part's.
        helper.assertTrue(Families.members(Constants.FAMILY_ID).size() > 1 || drawn == IcePixieItems.ICE_PIXIE_SPAWN_EGG.get(),
                "the Assorted Mobs advancement root is not drawn with the ice pixie's egg, the first of the family's icons installed");
        helper.succeed();
    }
}
