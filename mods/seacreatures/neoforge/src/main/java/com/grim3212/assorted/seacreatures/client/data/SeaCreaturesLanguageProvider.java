package com.grim3212.assorted.seacreatures.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.seacreatures.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A creature or item whose name is its id in title case needs no line here (see
 * {@link LibLanguageProvider}); the manual's keys are the Assorted Mobs section's, which every part shares.
 */
public class SeaCreaturesLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public SeaCreaturesLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedmobs", "Assorted Mobs");

        this.add("subtitles.assortedseacreatures.entity.seal.ambient", "Seal barks");
        this.add("subtitles.assortedseacreatures.entity.seal.hurt", "Seal hurts");
        this.add("subtitles.assortedseacreatures.entity.walrus.ambient", "Walrus bellows");
        this.add("subtitles.assortedseacreatures.entity.walrus.hurt", "Walrus hurts");

        this.add("tag.item.assortedseacreatures.seal_food", "Seal Food");
        this.add("tag.item.assortedseacreatures.repairs_shell_armor", "Repairs Shell Armor");

        this.addManual();
    }

    /** This part's chapters of the Assorted Mobs section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assortedmobs.title", "Assorted Mobs");
        this.add("manual.assortedmobs.description", "The ice pixie, the treasure mob, the two 8-bit creatures, and the sea creatures.");

        this.add("manual.assortedmobs.chapter.sea_creatures", "Sea Creatures");
        this.add("manual.assortedmobs.chapter.sea_creatures.seal.title", "Seal and Walrus");
        this.add("manual.assortedmobs.chapter.sea_creatures.seal",
                "Both out on the ice and snow, never far from the water, and slip into it if they get a chance. Both follow a fish and breed for one.");
        this.add("manual.assortedmobs.chapter.sea_creatures.temper.title", "Temper");
        this.add("manual.assortedmobs.chapter.sea_creatures.temper",
                "A seal harms nothing, and bolts for the sea if it is hit." + BREAK
                        + "A walrus is as peaceful until one is hit. Then every walrus in earshot charges.");
        this.add("manual.assortedmobs.chapter.sea_creatures.narwhal.title", "Narwhal");
        this.add("manual.assortedmobs.chapter.sea_creatures.narwhal", "Found in cold and frozen seas, where now and then one comes up and stands its tusk out of the water.");
        this.add("manual.assortedmobs.chapter.sea_creatures.narwhal_sword.title", "Narwhal Sword");
        this.add("manual.assortedmobs.chapter.sea_creatures.narwhal_sword", "A narwhal drops its horn, and two horns on a stick make a sword.");
        this.add("manual.assortedmobs.chapter.sea_creatures.sea_otter.title", "Sea Otter");
        this.add("manual.assortedmobs.chapter.sea_creatures.sea_otter", "Floats on its back in rivers and along milder coasts, dives to get about. Afloat it cracks open sea shells and leaves one behind every so often. I hear they like fish.");
        this.add("manual.assortedmobs.chapter.sea_creatures.shell_gear.title", "Shell Gear");
        this.add("manual.assortedmobs.chapter.sea_creatures.shell_gear", "Sea shells make a set of armor and a shovel.");
    }
}
