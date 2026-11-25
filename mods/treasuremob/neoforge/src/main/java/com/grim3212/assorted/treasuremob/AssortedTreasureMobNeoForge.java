package com.grim3212.assorted.treasuremob;

import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeEntityTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.lib.data.ForgeRegistryEntries;
import com.grim3212.assorted.treasuremob.client.data.TreasureMobItemModelProvider;
import com.grim3212.assorted.treasuremob.client.data.TreasureMobLanguageProvider;
import com.grim3212.assorted.treasuremob.client.data.TreasureMobManualProvider;
import com.grim3212.assorted.treasuremob.data.TreasureMobAdvancements;
import com.grim3212.assorted.treasuremob.data.TreasureMobBlockTagProvider;
import com.grim3212.assorted.treasuremob.data.TreasureMobChestLootProvider;
import com.grim3212.assorted.treasuremob.data.TreasureMobEntityTagProvider;
import com.grim3212.assorted.treasuremob.data.TreasureMobItemTagProvider;
import com.grim3212.assorted.treasuremob.data.TreasureMobSpawnHabitProvider;
import com.grim3212.assorted.treasuremob.data.TreasureMobStructureTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedTreasureMobNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedTreasureMobNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherData);

        TreasureMobCommonMod.init();
    }

    /**
     * One event for both halves; the data run's --server and --client flags pick which providers run. The client
     * half writes models, the language file and the manual into common for both loaders.
     */
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        boolean server = event.includeServer();

        ForgeBlockTagProvider blockTagProvider = generator.addProvider(server, new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new TreasureMobBlockTagProvider(packOutput, lookupProvider)));
        generator.addProvider(server, new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, fileHelper, new TreasureMobItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        generator.addProvider(server, new TreasureMobStructureTagProvider(packOutput, lookupProvider, fileHelper));
        generator.addProvider(server, new ForgeEntityTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new TreasureMobEntityTagProvider(packOutput, lookupProvider)));
        generator.addProvider(server, new TreasureMobSpawnHabitProvider(packOutput));
        generator.addProvider(server, TreasureMobChestLootProvider.create(packOutput, lookupProvider));
        ForgeRegistryEntries.of(Constants.MOD_ID).advancements((registries, saver, existingFiles) -> new TreasureMobAdvancements().generate(registries, saver)).addTo(event);

        boolean client = event.includeClient();
        generator.addProvider(client, new TreasureMobItemModelProvider(packOutput, fileHelper));
        generator.addProvider(client, new TreasureMobLanguageProvider(packOutput));
        generator.addProvider(client, new TreasureMobManualProvider(packOutput));
    }
}
