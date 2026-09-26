package com.grim3212.assorted.icepixie.client.data;

import com.grim3212.assorted.icepixie.Constants;
import com.grim3212.assorted.lib.data.LibLanguageProvider;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A creature or item whose name is its id in title case needs no line here (see
 * {@link LibLanguageProvider}); the manual's keys are the Assorted Mobs section's, which every part shares.
 */
public class IcePixieLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public IcePixieLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedmobs", "Assorted Mobs");

        this.add("tag.item.assortedicepixie.ice_pixie_weapons", "Ice Pixie Weapons");

        // The Assorted Mobs root, which every part with advancements writes the same.
        this.add("advancements.assortedmobs.root.title", "Assorted Mobs");
        this.add("advancements.assortedmobs.root.description", "Meet one of the creatures Assorted Mobs adds");
        this.add("tag.item.assortedmobs.opens_advancements_when_held", "Opens the Assorted Mobs Advancements");
        this.advancement("rare_encounter", "Rare Encounter", "Kill an ice pixie");

        this.addManual();
    }

    /** This part's chapters of the Assorted Mobs section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assortedmobs.title", "Assorted Mobs");
        this.add("manual.assortedmobs.description", "The ice pixie, the treasure mob, the two 8-bit creatures, and the sea creatures.");

        this.add("manual.assortedmobs.chapter.ice_pixie", "Ice Pixie");
        this.add("manual.assortedmobs.chapter.ice_pixie.info.title", "Ice Pixie");
        this.add("manual.assortedmobs.chapter.ice_pixie.info",
                "Ice pixies live in snowy places and come out by day as well as by night. They throw ice at any player they see, and move faster on ice and snow.");
        this.add("manual.assortedmobs.chapter.ice_pixie.fire.title", "Only Fire");
        this.add("manual.assortedmobs.chapter.ice_pixie.fire",
                "Nothing hurts an ice pixie unless you are holding a torch or flint and steel when you hit it." + BREAK
                        + "Anything hot nearby burns will burn them too.");
    }

    private void advancement(String name, String title, String description) {
        this.add("advancements." + Constants.MOD_ID + "." + name + ".title", title);
        this.add("advancements." + Constants.MOD_ID + "." + name + ".description", description);
    }
}
