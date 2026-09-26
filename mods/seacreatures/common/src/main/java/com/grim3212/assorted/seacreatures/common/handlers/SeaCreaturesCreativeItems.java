package com.grim3212.assorted.seacreatures.common.handlers;

import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.seacreatures.Constants;
import com.grim3212.assorted.seacreatures.Family;
import com.grim3212.assorted.seacreatures.common.item.SeaCreaturesItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

/** This part's share of the Assorted Mobs tab, which every part asks for and the first to load registers. */
public class SeaCreaturesCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = SharedCreativeTabs.tab(Identifier.fromNamespaceAndPath(Family.ID, "tab"), Family.ICONS);
    /** Vanilla's own key for it is private. */
    private static final ResourceKey<CreativeModeTab> SPAWN_EGGS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("spawn_eggs"));

    public static void init() {
        // Every part's items, then every part's spawn eggs, as the tab was when this was all one mod.
        SharedCreativeTabs.add(TAB, 20, () -> stacks(SeaCreaturesItems.NARWHAL_HORN, SeaCreaturesItems.NARWHAL_SWORD, SeaCreaturesItems.SEA_SHELL, SeaCreaturesItems.SHELL_HELMET, SeaCreaturesItems.SHELL_CHESTPLATE, SeaCreaturesItems.SHELL_LEGGINGS, SeaCreaturesItems.SHELL_BOOTS, SeaCreaturesItems.SHELL_SHOVEL));
        SharedCreativeTabs.add(TAB, 130, SeaCreaturesCreativeItems::spawnEggs);
        Services.PLATFORM.modifyCreativeTab(SPAWN_EGGS, SeaCreaturesCreativeItems::spawnEggs);
    }

    private static List<ItemStack> spawnEggs() {
        return stacks(SeaCreaturesItems.SEAL_SPAWN_EGG, SeaCreaturesItems.WALRUS_SPAWN_EGG, SeaCreaturesItems.NARWHAL_SPAWN_EGG, SeaCreaturesItems.SEA_OTTER_SPAWN_EGG);
    }

    @SafeVarargs
    private static List<ItemStack> stacks(IRegistryObject<Item>... items) {
        return Arrays.stream(items).map(item -> new ItemStack(item.get())).toList();
    }
}
