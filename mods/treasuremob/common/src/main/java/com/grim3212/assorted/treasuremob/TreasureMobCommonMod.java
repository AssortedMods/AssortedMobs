package com.grim3212.assorted.treasuremob;

import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.migration.AdvancementIcons;
import com.grim3212.assorted.lib.migration.MovedIds;
import com.grim3212.assorted.treasuremob.common.entity.TreasureMobEntities;
import com.grim3212.assorted.treasuremob.common.entity.TreasureMobSpawns;
import com.grim3212.assorted.treasuremob.common.handlers.TreasureMobCreativeItems;
import com.grim3212.assorted.treasuremob.common.item.TreasureMobItems;
import net.minecraft.resources.Identifier;

import java.util.Map;

/**
 * Loader-agnostic startup. Both loader entry points call {@link #init()} and nothing else; anything
 * a loader needs beyond it goes through AssortedLib's {@code Services}.
 */
public class TreasureMobCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "treasure_mob_spawn_egg"), 10)
                .manualOrder(70);

        TreasureMobEntities.init();
        TreasureMobItems.init();
        TreasureMobCreativeItems.init();
        TreasureMobSpawns.init();

        // Recipes and advancements unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
        // The advancement root every part shares: its icon is the first installed of the family's, its criteria renamed.
        Identifier root = Identifier.fromNamespaceAndPath(Constants.FAMILY_ID, "root");
        AdvancementIcons.register(root, () -> Families.icons(Constants.FAMILY_ID));
        MovedIds.renameCriteria(root, Map.of("tamed_treasure_mob", "tamed"));
    }
}
