package com.grim3212.assorted.mobs.common;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.mobs.MobsCommonMod;

/**
 * Grim World's three creature subparts, and the new sea creatures. A disabled part still registers its creatures and items; it
 * loses its natural spawns, its recipes, its creative tab entries and its manual chapters.
 */
public class MobsParts {

    public static final String ICE_PIXIE = "ice_pixie";
    public static final String TREASURE_MOB = "treasure_mob";
    public static final String EIGHT_BIT = "eight_bit";
    public static final String SEA_CREATURES = "sea_creatures";

    public static void init() {
        Services.CONDITIONS.registerPartCondition(ICE_PIXIE, () -> MobsCommonMod.COMMON_CONFIG.icePixieEnabled.get());
        Services.CONDITIONS.registerPartCondition(TREASURE_MOB, () -> MobsCommonMod.COMMON_CONFIG.treasureMobEnabled.get());
        Services.CONDITIONS.registerPartCondition(EIGHT_BIT, () -> MobsCommonMod.COMMON_CONFIG.eightBitMobsEnabled.get());
        Services.CONDITIONS.registerPartCondition(SEA_CREATURES, () -> MobsCommonMod.COMMON_CONFIG.seaCreaturesEnabled.get());
    }
}
