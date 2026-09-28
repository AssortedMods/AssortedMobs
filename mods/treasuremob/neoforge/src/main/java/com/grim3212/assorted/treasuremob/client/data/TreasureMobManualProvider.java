package com.grim3212.assorted.treasuremob.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.treasuremob.Constants;
import com.grim3212.assorted.treasuremob.common.entity.TreasureMobEntities;
import com.grim3212.assorted.treasuremob.common.handlers.TreasureMobCreativeItems;
import com.grim3212.assorted.treasuremob.common.item.TreasureMobItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/**
 * This part's chapter of the Assorted Mobs section, which every part shares; the explicit chapter orders keep the
 * section's order whichever parts are installed. Right clicking a creature with the manual opens its page.
 */
public class TreasureMobManualProvider extends LibManualProvider {

    /** Every picture is the page's full width, 16:9, so the text under it keeps the rest of the page. */
    private static final int PICTURE_WIDTH = 152;
    private static final int PICTURE_HEIGHT = 86;

    public TreasureMobManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Constants.FAMILY_ID);
    }

    @Override
    protected void addChapters() {
        ChapterBuilder treasureMob = this.chapter("treasure_mob", 10);
        treasureMob.image("info", picture("treasure_mob"), 104, 96)
                .opens(TreasureMobEntities.TREASURE_MOB.get()).opens(TreasureMobItems.TREASURE_MOB_SPAWN_EGG.get());
        treasureMob.text("taming");
    }

    /** In-game screenshots of each creature where it lives, under {@code textures/gui/manual}. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}
