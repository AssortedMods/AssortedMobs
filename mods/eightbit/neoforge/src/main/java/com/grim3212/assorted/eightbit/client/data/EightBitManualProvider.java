package com.grim3212.assorted.eightbit.client.data;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.common.entity.EightBitEntities;
import com.grim3212.assorted.eightbit.common.handlers.EightBitCreativeItems;
import com.grim3212.assorted.eightbit.common.item.EightBitItems;
import com.grim3212.assorted.lib.data.LibManualProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/**
 * This part's chapter of the Assorted Mobs section, which every part shares; the explicit chapter orders keep the
 * section's order whichever parts are installed. Right clicking a creature with the manual opens its page.
 */
public class EightBitManualProvider extends LibManualProvider {

    /** Every picture is the page's full width, 16:9, so the text under it keeps the rest of the page. */
    private static final int PICTURE_WIDTH = 152;
    private static final int PICTURE_HEIGHT = 86;

    public EightBitManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Constants.FAMILY_ID);
    }

    @Override
    protected void addChapters() {
        ChapterBuilder eightBit = this.chapter("eight_bit", 20);
        eightBit.image("parabuzzy", picture("parabuzzy"), PICTURE_WIDTH, PICTURE_HEIGHT)
                .opens(EightBitEntities.PARABUZZY.get()).opens(EightBitItems.PARABUZZY_SHELL.get(), EightBitItems.PARABUZZY_SPAWN_EGG.get());
        eightBit.text("perching");
        eightBit.image("bobomb", picture("bobomb"), PICTURE_WIDTH, PICTURE_HEIGHT)
                .opens(EightBitEntities.BOBOMB.get()).opens(EightBitItems.BOBOMB.get());
        eightBit.text("carrying");
        eightBit.recipes("crafting", EightBitItems.BOBOMB.get());
    }

    /** In-game screenshots of each creature where it lives, under {@code textures/gui/manual}. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}
