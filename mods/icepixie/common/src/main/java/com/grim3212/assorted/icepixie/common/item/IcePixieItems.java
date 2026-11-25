package com.grim3212.assorted.icepixie.common.item;

import com.grim3212.assorted.icepixie.Constants;
import com.grim3212.assorted.icepixie.common.entity.IcePixieEntities;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Function;
import java.util.function.Supplier;

public class IcePixieItems {

    private static final int UNTINTED = 0xFFFFFF;

    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<Item> ICE_PIXIE_SPAWN_EGG = spawnEgg("ice_pixie_spawn_egg", IcePixieEntities.ICE_PIXIE::get);

    private static IRegistryObject<Item> spawnEgg(String name, Supplier<? extends EntityType<? extends Mob>> type) {
        // Entity types register before items, so the type exists by the time this runs. The egg's texture is
        // already coloured, and white leaves the tint 1.21.1 lays over a spawn egg's layers as it is. NeoForge
        // deprecates this constructor for its own DeferredSpawnEggItem, which the common code cannot name, and the type
        // it would defer is already registered.
        @SuppressWarnings("deprecation")
        IRegistryObject<Item> egg = register(name, props -> new SpawnEggItem(type.get(), UNTINTED, UNTINTED, props));
        return egg;
    }

    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<Item.Properties, ? extends T> factory) {
        return ITEMS.register(name, () -> factory.apply(new Item.Properties()));
    }

    public static void init() {
    }
}
