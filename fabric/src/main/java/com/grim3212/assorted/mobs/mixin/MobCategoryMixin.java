package com.grim3212.assorted.mobs.mixin;

import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Fabric enum extension (extend-enum in assortedmobs_fabric.accesswidener). Keep the values equal to
 * NeoForge's META-INF/enumextensions.json; treasure_mobs_have_their_own_category checks them.
 */
@Mixin(MobCategory.class)
enum MobCategoryMixin {
    ASSORTEDMOBS_TREASURE("assortedmobs:treasure", "ATM", 4, true, false, 128);

    @Shadow
    MobCategoryMixin(String name, String debugAbbreviation, int max, boolean isFriendly, boolean isPersistent, int despawnDistance) {
    }
}
