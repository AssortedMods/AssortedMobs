package com.grim3212.assorted.icepixie.client.data;

import com.grim3212.assorted.icepixie.Constants;
import com.grim3212.assorted.icepixie.common.entity.IcePixieEntities;
import com.grim3212.assorted.icepixie.common.handlers.IcePixieCreativeItems;
import com.grim3212.assorted.icepixie.common.item.IcePixieItems;
import com.grim3212.assorted.lib.data.LibManualProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/**
 * This part's chapter of the Assorted Mobs section, which every part shares; the explicit chapter orders keep the
 * section's order whichever parts are installed. Right clicking a creature with the manual opens its page.
 */
public class IcePixieManualProvider extends LibManualProvider {

    /** Every picture is the page's full width, 16:9, so the text under it keeps the rest of the page. */
    private static final int PICTURE_WIDTH = 152;
    private static final int PICTURE_HEIGHT = 86;

    public IcePixieManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Constants.FAMILY_ID);
    }

    @Override
    protected void addChapters() {
        ChapterBuilder icePixie = this.chapter("ice_pixie", 0);
        icePixie.image("info", picture("ice_pixie"), PICTURE_WIDTH, PICTURE_HEIGHT)
                .opens(IcePixieEntities.ICE_PIXIE.get()).opens(IcePixieItems.ICE_PIXIE_SPAWN_EGG.get());
        icePixie.text("fire");
    }

    /** In-game screenshots of each creature where it lives, under {@code textures/gui/manual}. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}
