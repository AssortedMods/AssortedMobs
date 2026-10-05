package com.grim3212.assorted.treasuremob;

import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeEntityTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.treasuremob.client.data.TreasureMobItemModelProvider;
import com.grim3212.assorted.treasuremob.client.data.TreasureMobLanguageProvider;
import com.grim3212.assorted.treasuremob.client.data.TreasureMobManualProvider;
import com.grim3212.assorted.treasuremob.data.TreasureMobAdvancements;
import com.grim3212.assorted.treasuremob.data.TreasureMobBlockTagProvider;
import com.grim3212.assorted.treasuremob.data.TreasureMobEntityTagProvider;
import com.grim3212.assorted.treasuremob.data.TreasureMobItemTagProvider;
import com.grim3212.assorted.treasuremob.data.TreasureMobLootTableProvider;
import com.grim3212.assorted.treasuremob.data.TreasureMobSpawnHabitProvider;
import com.grim3212.assorted.treasuremob.data.TreasureMobStructureTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedTreasureMobNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedTreasureMobNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        TreasureMobCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ForgeBlockTagProvider blockTagProvider = event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new TreasureMobBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, new TreasureMobItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        event.addProvider(new TreasureMobStructureTagProvider(packOutput, lookupProvider));
        event.addProvider(new ForgeEntityTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new TreasureMobEntityTagProvider(packOutput, lookupProvider)));
        event.addProvider(new TreasureMobSpawnHabitProvider(packOutput));
        event.addProvider(new TreasureMobLootTableProvider(packOutput, lookupProvider));
        event.addProvider(new AdvancementProvider(packOutput, lookupProvider, List.of(new TreasureMobAdvancements())));
    }

    /** Client datagen: models, the language file and the manual, written into common for both loaders. */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new TreasureMobItemModelProvider(packOutput));
        event.addProvider(new TreasureMobLanguageProvider(packOutput));
        event.addProvider(new TreasureMobManualProvider(packOutput));
    }
}
