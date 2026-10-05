package com.grim3212.assorted.eightbit.client.data;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.lib.data.LibLanguageProvider;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A creature or item whose name is its id in title case needs no line here (see
 * {@link LibLanguageProvider}); the manual's keys are the Assorted Mobs section's, which every part shares.
 */
public class EightBitLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public EightBitLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedmobs", "Assorted Mobs");

        this.add("entity.assortedeightbit.bobomb", "Bob-omb");
        this.add("item.assortedeightbit.bobomb", "Bob-omb");
        this.add("item.assortedeightbit.parabuzzy_shell", "Parabuzzy's Shell");

        this.add("subtitles.assortedeightbit.entity.bobomb.ambient", "Bob-omb ticks");
        this.add("subtitles.assortedeightbit.entity.parabuzzy.ambient", "Parabuzzy buzzes");
        this.add("subtitles.assortedeightbit.entity.parabuzzy.hurt", "Parabuzzy hurts");
        this.add("subtitles.assortedeightbit.entity.parabuzzy.death", "Parabuzzy dies");

        this.add("tag.item.assortedeightbit.parabuzzy_tame_items", "Parabuzzy Treats");

        // The Assorted Mobs root, which every part with advancements writes the same.
        this.add("advancements.assortedmobs.root.title", "Assorted Mobs");
        this.add("advancements.assortedmobs.root.description", "Meet one of the creatures Assorted Mobs adds");
        this.add("tag.item.assortedmobs.opens_advancements_when_held", "Starts the Assorted Mobs Advancements");
        this.advancement("shell_game", "Shell Game", "Tame a parabuzzy");
        this.advancement("bobomb", "Short Fuse", "Build a Bob-omb from a parabuzzy's shell");

        this.addManual();
    }

    /** This part's chapters of the Assorted Mobs section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assortedmobs.title", "Assorted Mobs");
        this.add("manual.assortedmobs.description", "The ice pixie, the treasure mob, the two 8-bit creatures, and the sea creatures.");

        this.add("manual.assortedmobs.chapter.eight_bit", "8-Bit Mobs");
        this.add("manual.assortedmobs.chapter.eight_bit.parabuzzy.title", "Parabuzzy");
        this.add("manual.assortedmobs.chapter.eight_bit.parabuzzy",
                "Parabuzzies hover more than they fall. A fish may tame one.");
        this.add("manual.assortedmobs.chapter.eight_bit.perching.title", "On Your Head");
        this.add("manual.assortedmobs.chapter.eight_bit.perching",
                "Use a parabuzzy you tamed and it climbs onto your head. There it can slowly heal, and you will drift down as gently as it does and take no fall damage." + BREAK
                        + "Sneak on the ground to put it down.");
        this.add("manual.assortedmobs.chapter.eight_bit.bobomb.title", "Bob-omb");
        this.add("manual.assortedmobs.chapter.eight_bit.bobomb",
                "A Bob-omb you put down follows you, and when a monster hurts you it will walk up to it and light its fuse.");
        this.add("manual.assortedmobs.chapter.eight_bit.carrying.title", "Carrying One");
        this.add("manual.assortedmobs.chapter.eight_bit.carrying",
                "Be careful when hitting one yourself as its fuse will light." + BREAK
                        + "One will ride on your head too. Sneak and use one you put down with an empty hand to pick it back up.");
        this.add("manual.assortedmobs.chapter.eight_bit.crafting.title", "Making One");
        this.add("manual.assortedmobs.chapter.eight_bit.crafting",
                "A parabuzzy's shell under gunpowder and redstone makes a Bob-omb of your own.");
    }

    private void advancement(String name, String title, String description) {
        this.add("advancements." + Constants.MOD_ID + "." + name + ".title", title);
        this.add("advancements." + Constants.MOD_ID + "." + name + ".description", description);
    }
}
