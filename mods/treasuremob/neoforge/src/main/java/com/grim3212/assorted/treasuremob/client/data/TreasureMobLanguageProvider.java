package com.grim3212.assorted.treasuremob.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.treasuremob.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A creature or item whose name is its id in title case needs no line here (see
 * {@link LibLanguageProvider}); the manual's keys are the Assorted Mobs section's, which every part shares.
 */
public class TreasureMobLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public TreasureMobLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedmobs", "Assorted Mobs");

        this.add("tag.item.assortedtreasuremob.treasure_mob_tempt_items", "Treasure Mob Lures");

        // The Assorted Mobs root, which every part with advancements writes the same.
        this.add("advancements.assortedmobs.root.title", "Assorted Mobs");
        this.add("advancements.assortedmobs.root.description", "Meet one of the creatures Assorted Mobs adds");
        this.add("tag.item.assortedmobs.opens_advancements_when_held", "Starts the Assorted Mobs Advancements");
        this.advancement("dont_kill_him", "Don't Kill Him", "Find a treasure mob in the wild and tame it");

        this.addManual();
    }

    /** This part's chapters of the Assorted Mobs section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assortedmobs.title", "Assorted Mobs");
        this.add("manual.assortedmobs.description", "The ice pixie, the treasure mob, the two 8-bit creatures, and the sea creatures.");

        this.add("manual.assortedmobs.chapter.treasure_mob", "Treasure Mob");
        this.add("manual.assortedmobs.chapter.treasure_mob.info.title", "Treasure Mob");
        this.add("manual.assortedmobs.chapter.treasure_mob.info",
                "Now and then a chest walks off on its own. It carries whatever it found, anything from bread to diamonds.");
        this.add("manual.assortedmobs.chapter.treasure_mob.taming.title", "Taming");
        this.add("manual.assortedmobs.chapter.treasure_mob.taming",
                "A wild one keeps away from players, but gold nuggets lure it in. Feed it nuggets and it may decide to stay." + BREAK
                        + "A tamed Treasure Mob follows you and sits when told to. Sneak and use it to open it as a chest.");
    }

    private void advancement(String name, String title, String description) {
        this.add("advancements." + Constants.MOD_ID + "." + name + ".title", title);
        this.add("advancements." + Constants.MOD_ID + "." + name + ".description", description);
    }
}
