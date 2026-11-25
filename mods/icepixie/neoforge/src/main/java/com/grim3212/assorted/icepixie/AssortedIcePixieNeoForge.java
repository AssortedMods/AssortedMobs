package com.grim3212.assorted.icepixie;

import com.grim3212.assorted.icepixie.client.data.IcePixieItemModelProvider;
import com.grim3212.assorted.icepixie.client.data.IcePixieLanguageProvider;
import com.grim3212.assorted.icepixie.client.data.IcePixieManualProvider;
import com.grim3212.assorted.icepixie.data.IcePixieAdvancements;
import com.grim3212.assorted.icepixie.data.IcePixieBiomeTagProvider;
import com.grim3212.assorted.icepixie.data.IcePixieBlockTagProvider;
import com.grim3212.assorted.icepixie.data.IcePixieEntityLoot;
import com.grim3212.assorted.icepixie.data.IcePixieEntityTagProvider;
import com.grim3212.assorted.icepixie.data.IcePixieItemTagProvider;
import com.grim3212.assorted.icepixie.data.IcePixieSpawnHabitProvider;
import com.grim3212.assorted.lib.data.ForgeBiomeTagProvider;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeEntityTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.lib.data.ForgeRegistryEntries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedIcePixieNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedIcePixieNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherData);

        IcePixieCommonMod.init();
    }

    /** One event for both halves; the data run's --server and --client flags pick which providers run. */
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        ForgeBlockTagProvider blockTagProvider = generator.addProvider(event.includeServer(), new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new IcePixieBlockTagProvider(packOutput, lookupProvider)));
        generator.addProvider(event.includeServer(), new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, fileHelper, new IcePixieItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        generator.addProvider(event.includeServer(), new ForgeBiomeTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new IcePixieBiomeTagProvider(packOutput, lookupProvider)));
        generator.addProvider(event.includeServer(), new ForgeEntityTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new IcePixieEntityTagProvider(packOutput, lookupProvider)));
        generator.addProvider(event.includeServer(), new IcePixieSpawnHabitProvider(packOutput));
        ForgeRegistryEntries.of(Constants.MOD_ID)
                .advancements((registries, saver, existingFiles) -> new IcePixieAdvancements().generate(registries, saver))
                .loot(IcePixieEntityLoot::new, LootContextParamSets.ENTITY).addTo(event);

        generator.addProvider(event.includeClient(), new IcePixieItemModelProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new IcePixieLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new IcePixieManualProvider(packOutput));
    }
}
