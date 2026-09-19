package com.grim3212.assorted.mobs;

import com.grim3212.assorted.mobs.common.MobsParts;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import com.grim3212.assorted.mobs.common.entity.MobsSpawns;
import com.grim3212.assorted.mobs.common.handlers.MobsCreativeItems;
import com.grim3212.assorted.mobs.common.item.MobsItems;
import com.grim3212.assorted.mobs.common.sounds.MobsSounds;
import com.grim3212.assorted.mobs.config.MobsCommonConfig;

/**
 * Loader-agnostic startup. Both loader entry points call {@link #init()} and nothing else; anything
 * a loader needs beyond it goes through AssortedLib's {@code Services}.
 */
public class MobsCommonMod {

    public static final MobsCommonConfig COMMON_CONFIG = new MobsCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        MobsSounds.init();
        MobsEntities.init();
        MobsItems.init();
        MobsCreativeItems.init();
        MobsParts.init();
        MobsSpawns.init();
    }
}
