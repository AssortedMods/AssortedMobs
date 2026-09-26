package com.grim3212.assorted.treasuremob.common.item;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.treasuremob.Constants;
import com.grim3212.assorted.treasuremob.Family;
import com.grim3212.assorted.treasuremob.common.entity.TreasureMobEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Function;
import java.util.function.Supplier;

public class TreasureMobItems {

    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<Item> TREASURE_MOB_SPAWN_EGG = spawnEgg("treasure_mob_spawn_egg", TreasureMobEntities.TREASURE_MOB::get);

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
