package com.grim3212.assorted.mobs.config;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.mobs.Constants;

import java.util.function.Supplier;

/**
 * Every creature registers whether its part is on or not, so a world keeps loading the same way; a
 * part only gates its natural spawns, recipes, creative tab entries and manual chapters. See
 * {@code MobsParts}.
 */
public class MobsCommonConfig {

    public final Supplier<Boolean> icePixieEnabled;
    public final Supplier<Boolean> treasureMobEnabled;
    public final Supplier<Boolean> eightBitMobsEnabled;
    public final Supplier<Boolean> seaCreaturesEnabled;

    public final Supplier<Integer> icePixieWeight;
    public final Supplier<Integer> treasureMobWeight;
    public final Supplier<Integer> parabuzzyWeight;
    public final Supplier<Integer> sealWeight;
    public final Supplier<Integer> walrusWeight;
    public final Supplier<Integer> walrusBeachWeight;
    public final Supplier<Integer> narwhalWeight;
    public final Supplier<Integer> seaOtterWeight;


    public MobsCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        icePixieEnabled = builder.defineBoolean("parts.icePixieEnabled", true, "Set this to true if you would like ice pixies to spawn in snowy biomes and be found in the creative tab.");
        treasureMobEnabled = builder.defineBoolean("parts.treasureMobEnabled", true, "Set this to true if you would like treasure mobs to spawn and be found in the creative tab.");
        eightBitMobsEnabled = builder.defineBoolean("parts.eightBitMobsEnabled", true, "Set this to true if you would like parabuzzies to spawn, Bob-ombs to be craftable and both to be found in the creative tab.");

        seaCreaturesEnabled = builder.defineBoolean("parts.seaCreaturesEnabled", true, "Set this to true if you would like seals, walruses, narwhals and sea otters to spawn, what is made from them to be craftable, and all of it to be found in the creative tab.");

        icePixieWeight = builder.defineInteger("spawning.icePixieWeight", 39, 0, 1000, "How often ice pixies spawn in snowy biomes, against the other monsters there. Those add up to 515, so 39 is 7% of monsters. Set to 0 to stop them spawning.");
        treasureMobWeight = builder.defineInteger("spawning.treasureMobWeight", 5, 0, 1000, "How often treasure mobs spawn inside the structures in the assortedmobs:spawns_treasure_mobs structure tag. Set to 0 to stop them spawning.");
        parabuzzyWeight = builder.defineInteger("spawning.parabuzzyWeight", 8, 0, 1000, "How often parabuzzies spawn in the overworld, against the other animals there. A higher weight is more common. Set to 0 to stop them spawning.");
        sealWeight = builder.defineInteger("spawning.sealWeight", 1, 0, 1000, "How often seals spawn on the ice and snow, against the other animals there. A higher weight is more common. Set to 0 to stop them spawning.");
        walrusWeight = builder.defineInteger("spawning.walrusWeight", 1, 0, 1000, "How often walruses spawn on the ice and snow, against the other animals there. Set to 0 to stop them spawning.");
        walrusBeachWeight = builder.defineInteger("spawning.walrusBeachWeight", 1, 0, 1000, "How often walruses spawn on beaches and rocky shores away from the ice, in the biomes of the assortedmobs:spawns_walruses_rarely biome tag. Set to 0 to keep walruses to the ice and snow.");
        narwhalWeight = builder.defineInteger("spawning.narwhalWeight", 2, 0, 1000, "How often narwhals spawn in cold and frozen oceans. Set to 0 to stop them spawning.");
        seaOtterWeight = builder.defineInteger("spawning.seaOtterWeight", 2, 0, 1000, "How often sea otters spawn in rivers and the shallow oceans. Set to 0 to stop them spawning.");

        builder.setup();
    }
}
