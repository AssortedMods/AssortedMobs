package com.grim3212.assorted.seacreatures.common.item;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.seacreatures.Constants;
import com.grim3212.assorted.seacreatures.Family;
import com.grim3212.assorted.seacreatures.common.entity.SeaCreaturesEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Function;
import java.util.function.Supplier;

public class SeaCreaturesItems {

    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<Item> NARWHAL_HORN = register("narwhal_horn", Item::new);
    // As good as stone, which is what the 1.2.5 sword and shovel were.
    public static final IRegistryObject<Item> NARWHAL_SWORD = register("narwhal_sword", props -> new Item(props.sword(ToolMaterial.STONE, 3.0F, -2.4F)));
    public static final IRegistryObject<Item> SEA_SHELL = register("sea_shell", Item::new);
    public static final IRegistryObject<Item> SHELL_HELMET = register("shell_helmet", props -> new Item(props.humanoidArmor(SeaCreaturesArmorMaterials.SHELL, ArmorType.HELMET)));
    public static final IRegistryObject<Item> SHELL_CHESTPLATE = register("shell_chestplate", props -> new Item(props.humanoidArmor(SeaCreaturesArmorMaterials.SHELL, ArmorType.CHESTPLATE)));
    public static final IRegistryObject<Item> SHELL_LEGGINGS = register("shell_leggings", props -> new Item(props.humanoidArmor(SeaCreaturesArmorMaterials.SHELL, ArmorType.LEGGINGS)));
    public static final IRegistryObject<Item> SHELL_BOOTS = register("shell_boots", props -> new Item(props.humanoidArmor(SeaCreaturesArmorMaterials.SHELL, ArmorType.BOOTS)));
    public static final IRegistryObject<Item> SHELL_SHOVEL = register("shell_shovel", props -> new ShovelItem(ToolMaterial.STONE, 1.5F, -3.0F, props));

    public static final IRegistryObject<Item> SEAL_SPAWN_EGG = spawnEgg("seal_spawn_egg", SeaCreaturesEntities.SEAL::get);
    public static final IRegistryObject<Item> WALRUS_SPAWN_EGG = spawnEgg("walrus_spawn_egg", SeaCreaturesEntities.WALRUS::get);
    public static final IRegistryObject<Item> NARWHAL_SPAWN_EGG = spawnEgg("narwhal_spawn_egg", SeaCreaturesEntities.NARWHAL::get);
    public static final IRegistryObject<Item> SEA_OTTER_SPAWN_EGG = spawnEgg("sea_otter_spawn_egg", SeaCreaturesEntities.SEA_OTTER::get);

    private static IRegistryObject<Item> spawnEgg(String name, Supplier<? extends EntityType<?>> type) {
        // Entity types register before items, so the type exists by the time this runs.
        return register(name, props -> new SpawnEggItem(props.spawnEgg(type.get())));
    }

    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<Item.Properties, ? extends T> factory) {
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return ITEMS.register(name, () -> factory.apply(new Item.Properties().setId(key)));
    }

    public static void init() {
    }
}
