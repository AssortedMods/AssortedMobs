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

    public MobsCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        icePixieEnabled = builder.defineBoolean("parts.icePixieEnabled", true, "Set this to true if you would like ice pixies to spawn in snowy biomes and be found in the creative tab.");
        treasureMobEnabled = builder.defineBoolean("parts.treasureMobEnabled", true, "Set this to true if you would like treasure mobs to spawn and be found in the creative tab.");
        eightBitMobsEnabled = builder.defineBoolean("parts.eightBitMobsEnabled", true, "Set this to true if you would like parabuzzies to spawn, Bob-ombs to be craftable and both to be found in the creative tab.");

        seaCreaturesEnabled = builder.defineBoolean("parts.seaCreaturesEnabled", true, "Set this to true if you would like seals, walruses, narwhals and sea otters to spawn, what is made from them to be craftable, and all of it to be found in the creative tab.");

        builder.setup();
    }
}
