package com.grim3212.assorted.mobs.common.handlers;

import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.MobsCommonMod;
import com.grim3212.assorted.mobs.common.item.MobsItems;
import com.grim3212.assorted.mobs.config.MobsCommonConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MobsCreativeItems {

    public static final RegistryProvider<CreativeModeTab> CREATIVE_TABS = RegistryProvider.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "tab"));
    /** Vanilla's own key for it is private. */
    private static final ResourceKey<CreativeModeTab> SPAWN_EGGS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("spawn_eggs"));

    // CreativeModeTab.Output is protected in vanilla, so the tab is registered empty and filled
    // through modifyCreativeTab. The builder is deprecated only by NeoForge's patches.
    @SuppressWarnings("deprecation")
    public static final IRegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("tab", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup." + Constants.MOD_ID))
            .icon(() -> new ItemStack(MobsItems.BOBOMB.get()))
            .build());

    private static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();
        MobsCommonConfig config = MobsCommonMod.COMMON_CONFIG;

        if (config.eightBitMobsEnabled.get()) {
            items.add(MobsItems.BOBOMB.get());
            items.add(MobsItems.PARABUZZY_SHELL.get());
        }
        getSpawnEggs().forEach(items::add);

        return items.getItems();
    }

    private static List<ItemStack> getSpawnEggs() {
        CreativeTabItems items = new CreativeTabItems();
        MobsCommonConfig config = MobsCommonMod.COMMON_CONFIG;

        if (config.icePixieEnabled.get()) {
            items.add(MobsItems.ICE_PIXIE_SPAWN_EGG.get());
        }
        if (config.treasureMobEnabled.get()) {
            items.add(MobsItems.TREASURE_MOB_SPAWN_EGG.get());
        }
        if (config.eightBitMobsEnabled.get()) {
            items.add(MobsItems.BOBOMB_SPAWN_EGG.get());
            items.add(MobsItems.PARABUZZY_SPAWN_EGG.get());
        }

        return items.getItems();
    }

    public static void init() {
        Services.PLATFORM.modifyCreativeTab(CREATIVE_TAB_KEY, MobsCreativeItems::getCreativeItems);
        Services.PLATFORM.modifyCreativeTab(SPAWN_EGGS, MobsCreativeItems::getSpawnEggs);
    }
}
