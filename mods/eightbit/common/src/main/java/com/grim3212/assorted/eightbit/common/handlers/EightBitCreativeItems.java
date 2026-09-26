package com.grim3212.assorted.eightbit.common.handlers;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.Family;
import com.grim3212.assorted.eightbit.common.item.EightBitItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

/** This part's share of the Assorted Mobs tab, which every part asks for and the first to load registers. */
public class EightBitCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = SharedCreativeTabs.tab(Identifier.fromNamespaceAndPath(Family.ID, "tab"), Family.ICONS);
    /** Vanilla's own key for it is private. */
    private static final ResourceKey<CreativeModeTab> SPAWN_EGGS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("spawn_eggs"));

    public static void init() {
        // Every part's items, then every part's spawn eggs, as the tab was when this was all one mod.
        SharedCreativeTabs.add(TAB, 10, () -> stacks(EightBitItems.BOBOMB, EightBitItems.PARABUZZY_SHELL));
        SharedCreativeTabs.add(TAB, 120, EightBitCreativeItems::spawnEggs);
        Services.PLATFORM.modifyCreativeTab(SPAWN_EGGS, EightBitCreativeItems::spawnEggs);
    }

    private static List<ItemStack> spawnEggs() {
        return stacks(EightBitItems.PARABUZZY_SPAWN_EGG);
    }

    @SafeVarargs
    private static List<ItemStack> stacks(IRegistryObject<Item>... items) {
        return Arrays.stream(items).map(item -> new ItemStack(item.get())).toList();
    }
}
