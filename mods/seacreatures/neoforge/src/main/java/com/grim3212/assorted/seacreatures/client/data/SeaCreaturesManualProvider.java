package com.grim3212.assorted.seacreatures.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.seacreatures.Constants;
import com.grim3212.assorted.seacreatures.Family;
import com.grim3212.assorted.seacreatures.common.entity.SeaCreaturesEntities;
import com.grim3212.assorted.seacreatures.common.handlers.SeaCreaturesCreativeItems;
import com.grim3212.assorted.seacreatures.common.item.SeaCreaturesItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/**
 * This part's chapter of the Assorted Mobs section, which every part shares; the explicit chapter orders keep the
 * section's order whichever parts are installed. Right clicking a creature with the manual opens its page.
 */
public class SeaCreaturesManualProvider extends LibManualProvider {

    /** Every picture is the page's full width, 16:9, so the text under it keeps the rest of the page. */
    private static final int PICTURE_WIDTH = 152;
    private static final int PICTURE_HEIGHT = 86;

    public SeaCreaturesManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        ChapterBuilder seaCreatures = this.chapter("sea_creatures", 30);
        seaCreatures.image("seal", picture("seal"), PICTURE_WIDTH, PICTURE_HEIGHT)
                .opens(SeaCreaturesEntities.SEAL.get(), SeaCreaturesEntities.WALRUS.get()).opens(SeaCreaturesItems.SEAL_SPAWN_EGG.get(), SeaCreaturesItems.WALRUS_SPAWN_EGG.get());
        seaCreatures.text("temper");
        seaCreatures.image("narwhal", picture("narwhal"), PICTURE_WIDTH, PICTURE_HEIGHT)
                .opens(SeaCreaturesEntities.NARWHAL.get()).opens(SeaCreaturesItems.NARWHAL_SPAWN_EGG.get());
        seaCreatures.recipes("narwhal_sword", SeaCreaturesItems.NARWHAL_SWORD.get())
                .opens(SeaCreaturesItems.NARWHAL_HORN.get(), SeaCreaturesItems.NARWHAL_SWORD.get());
        seaCreatures.image("sea_otter", picture("sea_otter"), PICTURE_WIDTH, PICTURE_HEIGHT)
                .opens(SeaCreaturesEntities.SEA_OTTER.get()).opens(SeaCreaturesItems.SEA_OTTER_SPAWN_EGG.get());
        seaCreatures.recipes("shell_gear", SeaCreaturesItems.SHELL_HELMET.get(), SeaCreaturesItems.SHELL_CHESTPLATE.get(), SeaCreaturesItems.SHELL_LEGGINGS.get(), SeaCreaturesItems.SHELL_BOOTS.get(), SeaCreaturesItems.SHELL_SHOVEL.get())
                .opens(SeaCreaturesItems.SEA_SHELL.get(), SeaCreaturesItems.SHELL_HELMET.get(), SeaCreaturesItems.SHELL_CHESTPLATE.get(), SeaCreaturesItems.SHELL_LEGGINGS.get(),
                        SeaCreaturesItems.SHELL_BOOTS.get(), SeaCreaturesItems.SHELL_SHOVEL.get());
    }

    /** In-game screenshots of each creature where it lives, under {@code textures/gui/manual}. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}
