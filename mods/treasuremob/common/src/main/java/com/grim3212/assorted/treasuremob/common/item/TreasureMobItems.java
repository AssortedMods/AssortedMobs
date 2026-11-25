package com.grim3212.assorted.treasuremob.common.item;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.treasuremob.Constants;
import com.grim3212.assorted.treasuremob.common.entity.TreasureMobEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Function;
import java.util.function.Supplier;

public class TreasureMobItems {

    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<Item> TREASURE_MOB_SPAWN_EGG = spawnEgg("treasure_mob_spawn_egg", TreasureMobEntities.TREASURE_MOB::get);

    /**
     * Its sprite is the whole egg, drawn untinted: vanilla tints a spawn egg's first layer with its background colour,
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
