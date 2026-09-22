package com.grim3212.assorted.mobs.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.common.MobsParts;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.item.MobsItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

/**
 * This mod's section of the instruction manual. Each chapter hangs off the part it documents, so a
 * disabled part takes its pages with it. Right clicking a creature with the manual opens its page.
 */
public class MobsManualProvider extends LibManualProvider {

    public MobsManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addChapters() {
        this.section(70, MobsItems.BOBOMB.get());

        ChapterBuilder icePixie = this.chapter("ice_pixie").whenPartEnabled(MobsParts.ICE_PIXIE);
        icePixie.image("info", picture("ice_pixie"), 104, 104)
                .opens(MobsEntities.ICE_PIXIE.get()).opens(MobsItems.ICE_PIXIE_SPAWN_EGG.get());
        icePixie.text("fire");

        ChapterBuilder treasureMob = this.chapter("treasure_mob").whenPartEnabled(MobsParts.TREASURE_MOB);
        treasureMob.image("info", picture("treasure_mob"), 104, 96)
                .opens(MobsEntities.TREASURE_MOB.get()).opens(MobsItems.TREASURE_MOB_SPAWN_EGG.get());
        treasureMob.text("taming");

        ChapterBuilder eightBit = this.chapter("eight_bit").whenPartEnabled(MobsParts.EIGHT_BIT);
        eightBit.image("parabuzzy", picture("parabuzzy"), 104, 100)
                .opens(MobsEntities.PARABUZZY.get()).opens(MobsItems.PARABUZZY_SHELL.get(), MobsItems.PARABUZZY_SPAWN_EGG.get());
        eightBit.text("perching");
        eightBit.recipes("bobomb", MobsItems.BOBOMB.get())
                .opens(MobsEntities.BOBOMB.get()).opens(MobsItems.BOBOMB.get(), MobsItems.BOBOMB_SPAWN_EGG.get());

        ChapterBuilder seaCreatures = this.chapter("sea_creatures").whenPartEnabled(MobsParts.SEA_CREATURES);
        seaCreatures.text("seal").opens(MobsEntities.SEAL.get(), MobsEntities.WALRUS.get()).opens(MobsItems.SEAL_SPAWN_EGG.get(), MobsItems.WALRUS_SPAWN_EGG.get());
        seaCreatures.recipes("narwhal", MobsItems.NARWHAL_SWORD.get())
                .opens(MobsEntities.NARWHAL.get()).opens(MobsItems.NARWHAL_HORN.get(), MobsItems.NARWHAL_SWORD.get(), MobsItems.NARWHAL_SPAWN_EGG.get());
        seaCreatures.recipes("sea_otter", MobsItems.SHELL_HELMET.get(), MobsItems.SHELL_CHESTPLATE.get(), MobsItems.SHELL_LEGGINGS.get(), MobsItems.SHELL_BOOTS.get(), MobsItems.SHELL_SHOVEL.get())
                .opens(MobsEntities.SEA_OTTER.get()).opens(MobsItems.SEA_SHELL.get(), MobsItems.SHELL_HELMET.get(), MobsItems.SHELL_CHESTPLATE.get(), MobsItems.SHELL_LEGGINGS.get(),
                        MobsItems.SHELL_BOOTS.get(), MobsItems.SHELL_SHOVEL.get(), MobsItems.SEA_OTTER_SPAWN_EGG.get());
    }

    /** The Grim Pack screenshots under {@code textures/gui/manual}, cropped and sized to leave room for the text. */
    private static Identifier picture(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/manual/" + name + ".png");
    }
}
