package com.grim3212.assorted.seacreatures;

import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.migration.MovedIds;
import com.grim3212.assorted.seacreatures.common.entity.SeaCreaturesEntities;
import com.grim3212.assorted.seacreatures.common.entity.SeaCreaturesSpawns;
import com.grim3212.assorted.seacreatures.common.handlers.SeaCreaturesCreativeItems;
import com.grim3212.assorted.seacreatures.common.item.SeaCreaturesItems;
import com.grim3212.assorted.seacreatures.common.sounds.SeaCreaturesSounds;
import net.minecraft.resources.Identifier;

/**
 * Loader-agnostic startup. Both loader entry points call {@link #init()} and nothing else; anything
 * a loader needs beyond it goes through AssortedLib's {@code Services}.
 */
public class SeaCreaturesCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "narwhal_horn"), 30)
                .manualOrder(70);

        SeaCreaturesSounds.init();
        SeaCreaturesEntities.init();
        SeaCreaturesItems.init();
        SeaCreaturesCreativeItems.init();
        SeaCreaturesSpawns.init();

        // Recipes and advancements unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
    }
}
