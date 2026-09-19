package com.grim3212.assorted.mobs.common.item;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Function;
import java.util.function.Supplier;

public class MobsItems {

    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID);

    public static final IRegistryObject<Item> BOBOMB = register("bobomb", props -> new BobombItem(props.stacksTo(16)));
    public static final IRegistryObject<Item> PARABUZZY_SHELL = register("parabuzzy_shell", Item::new);

    public static final IRegistryObject<Item> ICE_PIXIE_SPAWN_EGG = spawnEgg("ice_pixie_spawn_egg", MobsEntities.ICE_PIXIE::get);
    public static final IRegistryObject<Item> TREASURE_MOB_SPAWN_EGG = spawnEgg("treasure_mob_spawn_egg", MobsEntities.TREASURE_MOB::get);
    public static final IRegistryObject<Item> BOBOMB_SPAWN_EGG = spawnEgg("bobomb_spawn_egg", MobsEntities.BOBOMB::get);
    public static final IRegistryObject<Item> PARABUZZY_SPAWN_EGG = spawnEgg("parabuzzy_spawn_egg", MobsEntities.PARABUZZY::get);

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
