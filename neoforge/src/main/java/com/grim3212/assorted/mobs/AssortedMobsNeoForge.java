package com.grim3212.assorted.mobs;

import com.grim3212.assorted.lib.data.ForgeBiomeTagProvider;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.mobs.client.data.MobsItemModelProvider;
import com.grim3212.assorted.mobs.client.data.MobsLanguageProvider;
import com.grim3212.assorted.mobs.client.data.MobsManualProvider;
import com.grim3212.assorted.mobs.data.MobsAdvancements;
import com.grim3212.assorted.mobs.data.MobsBiomeTagProvider;
import com.grim3212.assorted.mobs.data.MobsBlockTagProvider;
import com.grim3212.assorted.mobs.data.MobsChestLoot;
import com.grim3212.assorted.mobs.data.MobsEntityLoot;
import com.grim3212.assorted.mobs.data.MobsItemTagProvider;
import com.grim3212.assorted.mobs.data.MobsRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedMobsNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedMobsNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        MobsCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ForgeBlockTagProvider blockTagProvider = event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new MobsBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, new MobsItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        event.addProvider(new ForgeBiomeTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new MobsBiomeTagProvider(packOutput, lookupProvider)));
        // Recipe providers are not data providers any more - the Runner owns the output.
        event.addProvider(new MobsRecipes.Runner(packOutput, lookupProvider));
        event.addProvider(new LootTableProvider(packOutput, Collections.emptySet(), List.of(
                new LootTableProvider.SubProviderEntry(MobsEntityLoot::new, LootContextParamSets.ENTITY),
                new LootTableProvider.SubProviderEntry(MobsChestLoot::new, LootContextParamSets.CHEST)), lookupProvider));
        event.addProvider(new AdvancementProvider(packOutput, lookupProvider, List.of(new MobsAdvancements())));
    }

    /** Client datagen: models, the language file and the manual, written into common for both loaders. */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new MobsItemModelProvider(packOutput));
        event.addProvider(new MobsLanguageProvider(packOutput));
        event.addProvider(new MobsManualProvider(packOutput));
    }
}
