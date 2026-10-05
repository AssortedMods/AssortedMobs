package com.grim3212.assorted.treasuremob.common.handlers;

import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.treasuremob.Constants;
import com.grim3212.assorted.treasuremob.common.item.TreasureMobItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

/** This part's share of the Assorted Mobs tab, which every part asks for and the first to load registers. */
public class TreasureMobCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = Families.tab(Constants.FAMILY_ID);
    /** Vanilla's own key for it is private. */
    private static final ResourceKey<CreativeModeTab> SPAWN_EGGS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("spawn_eggs"));

    public static void init() {
        // Every part's items, then every part's spawn eggs, as the tab was when this was all one mod.
        SharedCreativeTabs.add(TAB, 110, TreasureMobCreativeItems::spawnEggs);
        Services.PLATFORM.modifyCreativeTab(SPAWN_EGGS, TreasureMobCreativeItems::spawnEggs);
    }

    private static List<ItemStack> spawnEggs() {
        return stacks(TreasureMobItems.TREASURE_MOB_SPAWN_EGG);
    }

    @SafeVarargs
    private static List<ItemStack> stacks(IRegistryObject<Item>... items) {
        return Arrays.stream(items).map(item -> new ItemStack(item.get())).toList();
    }
}
