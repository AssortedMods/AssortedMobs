package com.grim3212.assorted.mobs.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.mobs.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A creature or item whose name is its id in title case needs
 * no line here (see {@link LibLanguageProvider}); these are the names that read differently, and
 * every key that is not a name.
 */
public class MobsLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public MobsLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedmobs", "Assorted Mobs");

        this.add("entity.assortedmobs.bobomb", "Bob-omb");
        this.add("item.assortedmobs.bobomb", "Bob-omb");
        this.add("item.assortedmobs.parabuzzy_shell", "Parabuzzy's Shell");

        this.add("subtitles.assortedmobs.entity.bobomb.ambient", "Bob-omb ticks");
        this.add("subtitles.assortedmobs.entity.parabuzzy.ambient", "Parabuzzy buzzes");
        this.add("subtitles.assortedmobs.entity.parabuzzy.hurt", "Parabuzzy hurts");
        this.add("subtitles.assortedmobs.entity.parabuzzy.death", "Parabuzzy dies");
        this.add("subtitles.assortedmobs.entity.seal.ambient", "Seal barks");
        this.add("subtitles.assortedmobs.entity.seal.hurt", "Seal hurts");
        this.add("subtitles.assortedmobs.entity.walrus.ambient", "Walrus bellows");
        this.add("subtitles.assortedmobs.entity.walrus.hurt", "Walrus hurts");

        this.add("tag.item.assortedmobs.ice_pixie_weapons", "Ice Pixie Weapons");
        this.add("tag.item.assortedmobs.treasure_mob_tempt_items", "Treasure Mob Lures");
        this.add("tag.item.assortedmobs.parabuzzy_tame_items", "Parabuzzy Treats");
        this.add("tag.item.assortedmobs.seal_food", "Seal Food");
        this.add("tag.item.assortedmobs.repairs_shell_armor", "Repairs Shell Armor");


        this.advancement("root", "Assorted Mobs", "Meet one of the creatures Assorted Mobs adds");
        this.advancement("rare_encounter", "Rare Encounter", "Kill an ice pixie");
        this.advancement("dont_kill_him", "Don't Kill Him", "Find a treasure mob in the wild and tame it");
        this.advancement("shell_game", "Shell Game", "Tame a parabuzzy");
        this.advancement("bobomb", "Short Fuse", "Build a Bob-omb from a parabuzzy's shell");

        this.addManual();
    }

    /** The chapters in {@code assets/assortedmobs/manual} name these keys. */
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

        this.add("manual.assortedmobs.chapter.treasure_mob", "Treasure Mob");
        this.add("manual.assortedmobs.chapter.treasure_mob.info.title", "Treasure Mob");
        this.add("manual.assortedmobs.chapter.treasure_mob.info",
                "Now and then a chest walks off on its own. It carries whatever it found, anything from bread to diamonds.");
        this.add("manual.assortedmobs.chapter.treasure_mob.taming.title", "Taming");
        this.add("manual.assortedmobs.chapter.treasure_mob.taming",
                "A wild one keeps away from players, but gold nuggets lure it in. Feed it nuggets and it may decide to stay." + BREAK
                        + "A tamed Treasure Mob follows you and sits when told to. Sneak and use it to open it as a chest.");

        this.add("manual.assortedmobs.chapter.eight_bit", "8-Bit Mobs");
        this.add("manual.assortedmobs.chapter.eight_bit.parabuzzy.title", "Parabuzzy");
        this.add("manual.assortedmobs.chapter.eight_bit.parabuzzy",
                "Parabuzzies hover more than they fall. A fish may tame one.");
        this.add("manual.assortedmobs.chapter.eight_bit.perching.title", "On Your Head");
        this.add("manual.assortedmobs.chapter.eight_bit.perching",
                "Use a tamed parabuzzy and it climbs onto your head. There it can slowly heal, and you will drift down as gently as it does and take no fall damage." + BREAK
                        + "Sneak on the ground to put it down.");
        this.add("manual.assortedmobs.chapter.eight_bit.bobomb.title", "Bob-omb");
        this.add("manual.assortedmobs.chapter.eight_bit.bobomb",
                "A Bob-omb you put down follows you, and when a monster hurts you it will walk up to it and light its fuse.");
        this.add("manual.assortedmobs.chapter.eight_bit.carrying.title", "Carrying One");
        this.add("manual.assortedmobs.chapter.eight_bit.carrying",
                "Be careful when hitting one yourself as its fuse will light." + BREAK
                        + "One will ride on your head too. Sneak and use one with an empty hand to pick it back up.");
        this.add("manual.assortedmobs.chapter.eight_bit.crafting.title", "Making One");
        this.add("manual.assortedmobs.chapter.eight_bit.crafting",
                "A parabuzzy's shell under gunpowder and redstone makes a Bob-omb of your own.");

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

    private void advancement(String name, String title, String description) {
        this.add("advancements." + Constants.MOD_ID + "." + name + ".title", title);
        this.add("advancements." + Constants.MOD_ID + "." + name + ".description", description);
    }
}
