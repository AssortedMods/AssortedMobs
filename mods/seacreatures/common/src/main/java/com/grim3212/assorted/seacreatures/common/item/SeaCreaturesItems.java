package com.grim3212.assorted.seacreatures.common.item;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.seacreatures.Constants;
import com.grim3212.assorted.seacreatures.common.entity.SeaCreaturesEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

import java.util.function.Function;
import java.util.function.Supplier;

public class SeaCreaturesItems {

    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<Item> NARWHAL_HORN = register("narwhal_horn", Item::new);
    // As good as stone, which is what the 1.2.5 sword and shovel were.
    public static final IRegistryObject<Item> NARWHAL_SWORD = register("narwhal_sword", props -> new SwordItem(Tiers.STONE, props.attributes(SwordItem.createAttributes(Tiers.STONE, 3, -2.4F))));
    public static final IRegistryObject<Item> SEA_SHELL = register("sea_shell", Item::new);
    public static final IRegistryObject<Item> SHELL_HELMET = register("shell_helmet", props -> shellArmor(ArmorItem.Type.HELMET, props));
    public static final IRegistryObject<Item> SHELL_CHESTPLATE = register("shell_chestplate", props -> shellArmor(ArmorItem.Type.CHESTPLATE, props));
    public static final IRegistryObject<Item> SHELL_LEGGINGS = register("shell_leggings", props -> shellArmor(ArmorItem.Type.LEGGINGS, props));
    public static final IRegistryObject<Item> SHELL_BOOTS = register("shell_boots", props -> shellArmor(ArmorItem.Type.BOOTS, props));
    public static final IRegistryObject<Item> SHELL_SHOVEL = register("shell_shovel", props -> new ShovelItem(Tiers.STONE, props.attributes(DiggerItem.createAttributes(Tiers.STONE, 1.5F, -3.0F))));

    public static final IRegistryObject<Item> SEAL_SPAWN_EGG = spawnEgg("seal_spawn_egg", SeaCreaturesEntities.SEAL::get);
    public static final IRegistryObject<Item> WALRUS_SPAWN_EGG = spawnEgg("walrus_spawn_egg", SeaCreaturesEntities.WALRUS::get);
    public static final IRegistryObject<Item> NARWHAL_SPAWN_EGG = spawnEgg("narwhal_spawn_egg", SeaCreaturesEntities.NARWHAL::get);
    public static final IRegistryObject<Item> SEA_OTTER_SPAWN_EGG = spawnEgg("sea_otter_spawn_egg", SeaCreaturesEntities.SEA_OTTER::get);

    /**
     * The material's holder from the registry itself: Assorted Lib's IRegistryObject#asHolder casts the value to a
     * Holder on Fabric, which only works for a registry whose entries are their own holders, as items are.
     */
    private static ArmorItem shellArmor(ArmorItem.Type type, Item.Properties props) {
        return new ArmorItem(BuiltInRegistries.ARMOR_MATERIAL.getHolderOrThrow(SeaCreaturesArmorMaterials.SHELL.getResourceKey()), type, props.durability(type.getDurability(SeaCreaturesArmorMaterials.SHELL_DURABILITY)));
    }

    /**
     * Each sprite is the whole egg, drawn untinted: vanilla tints a spawn egg's first layer with its background colour,
     * so that is white, and the sprite has no second layer for the highlight. NeoForge deprecates this constructor
     * for its own DeferredSpawnEggItem, which common code cannot reach; vanilla's, which Fabric runs, is not.
     */
    @SuppressWarnings("deprecation")
    private static IRegistryObject<Item> spawnEgg(String name, Supplier<? extends EntityType<? extends Mob>> type) {
        // Entity types register before items on both loaders, so the type exists by the time this runs.
        return register(name, props -> new SpawnEggItem(type.get(), 0xFFFFFF, 0xFFFFFF, props));
    }

    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<Item.Properties, ? extends T> factory) {
        return ITEMS.register(name, () -> factory.apply(new Item.Properties()));
    }

    public static void init() {
    }
}
