package com.grim3212.assorted.mobs.common.item;

import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.api.MobsTags;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

/** Each material also needs {@code assets/assortedmobs/equipment/<name>.json}, or it is worn untextured; see MobsEquipmentAssetProvider. */
public final class MobsArmorMaterials {

    public static final ResourceKey<EquipmentAsset> SHELL_ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "shell"));

    /** Chainmail by another name, as it was in 1.2.5, for those with more otters than chains. */
    public static final ArmorMaterial SHELL = new ArmorMaterial(15,
            Map.of(ArmorType.BOOTS, 1, ArmorType.LEGGINGS, 4, ArmorType.CHESTPLATE, 5, ArmorType.HELMET, 2, ArmorType.BODY, 4),
            12, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, MobsTags.Items.REPAIRS_SHELL_ARMOR, SHELL_ASSET);

    private MobsArmorMaterials() {
    }
}
