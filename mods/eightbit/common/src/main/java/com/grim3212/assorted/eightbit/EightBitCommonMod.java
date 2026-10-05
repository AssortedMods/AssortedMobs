package com.grim3212.assorted.eightbit;

import com.grim3212.assorted.eightbit.common.entity.EightBitEntities;
import com.grim3212.assorted.eightbit.common.entity.EightBitSpawns;
import com.grim3212.assorted.eightbit.common.handlers.EightBitCreativeItems;
import com.grim3212.assorted.eightbit.common.item.EightBitItems;
import com.grim3212.assorted.eightbit.common.sounds.EightBitSounds;
import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.migration.AdvancementIcons;
import com.grim3212.assorted.lib.migration.MovedIds;
import net.minecraft.resources.Identifier;

import java.util.Map;

/**
 * Loader-agnostic startup. Both loader entry points call {@link #init()} and nothing else; anything
 * a loader needs beyond it goes through AssortedLib's {@code Services}.
 */
public class EightBitCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "bobomb"), 40)
                .manualOrder(70);

        EightBitSounds.init();
        EightBitEntities.init();
        EightBitItems.init();
        EightBitCreativeItems.init();
        EightBitSpawns.init();

        // Recipes and advancements unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
        // The advancement root every part shares: its icon is the first installed of the family's, its criteria renamed.
        Identifier root = Identifier.fromNamespaceAndPath(Constants.FAMILY_ID, "root");
        AdvancementIcons.register(root, () -> Families.icons(Constants.FAMILY_ID));
        MovedIds.renameCriteria(root, Map.of("tamed_parabuzzy", "tamed", "has_parabuzzy_shell", "held"));
    }
}
