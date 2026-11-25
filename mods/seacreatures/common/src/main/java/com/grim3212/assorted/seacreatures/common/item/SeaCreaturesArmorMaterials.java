package com.grim3212.assorted.seacreatures.common.item;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.seacreatures.Constants;
import com.grim3212.assorted.seacreatures.api.SeaCreaturesTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;

/**
 * The armour materials, a registry on 1.21.1. Each one's texture is
 * {@code assets/assortedseacreatures/textures/models/armor/<name>_layer_1.png}, and {@code _layer_2.png} for the
 * leggings, or it is worn untextured.
 */
public final class SeaCreaturesArmorMaterials {

    public static final RegistryProvider<ArmorMaterial> ARMOR_MATERIALS = RegistryProvider.create(Registries.ARMOR_MATERIAL, Constants.MOD_ID);

    /** Chainmail's durability multiplier, which on 1.21.1 goes on each piece rather than the material. */
    public static final int SHELL_DURABILITY = 15;

    /** Chainmail by another name, as it was in 1.2.5, for those with more otters than chains. */
    public static final IRegistryObject<ArmorMaterial> SHELL = ARMOR_MATERIALS.register("shell", () -> new ArmorMaterial(defense(1, 4, 5, 2, 4), 12,
            SoundEvents.ARMOR_EQUIP_CHAIN, () -> Ingredient.of(SeaCreaturesTags.Items.REPAIRS_SHELL_ARMOR),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "shell"))), 0.0F, 0.0F));

    private SeaCreaturesArmorMaterials() {
    }

    private static EnumMap<ArmorItem.Type, Integer> defense(int boots, int leggings, int chestplate, int helmet, int body) {
        EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.BOOTS, boots);
        defense.put(ArmorItem.Type.LEGGINGS, leggings);
        defense.put(ArmorItem.Type.CHESTPLATE, chestplate);
        defense.put(ArmorItem.Type.HELMET, helmet);
        defense.put(ArmorItem.Type.BODY, body);
        return defense;
    }

    public static void init() {
    }
}
