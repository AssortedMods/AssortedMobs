package com.grim3212.assorted.icepixie;

import com.grim3212.assorted.icepixie.client.data.IcePixieItemModelProvider;
import com.grim3212.assorted.icepixie.client.data.IcePixieLanguageProvider;
import com.grim3212.assorted.icepixie.client.data.IcePixieManualProvider;
import com.grim3212.assorted.icepixie.data.IcePixieAdvancements;
import com.grim3212.assorted.icepixie.data.IcePixieBiomeTagProvider;
import com.grim3212.assorted.icepixie.data.IcePixieBlockTagProvider;
import com.grim3212.assorted.icepixie.data.IcePixieEntityTagProvider;
import com.grim3212.assorted.icepixie.data.IcePixieItemTagProvider;
import com.grim3212.assorted.icepixie.data.IcePixieLootTableProvider;
import com.grim3212.assorted.icepixie.data.IcePixieSpawnHabitProvider;
import com.grim3212.assorted.lib.data.ForgeBiomeTagProvider;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeEntityTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
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
public class AssortedIcePixieNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedIcePixieNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        IcePixieCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ForgeBlockTagProvider blockTagProvider = event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new IcePixieBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, new IcePixieItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        event.addProvider(new ForgeBiomeTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new IcePixieBiomeTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeEntityTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new IcePixieEntityTagProvider(packOutput, lookupProvider)));
        event.addProvider(new IcePixieSpawnHabitProvider(packOutput));
        event.addProvider(new IcePixieLootTableProvider(packOutput, lookupProvider));
        event.addProvider(new AdvancementProvider(packOutput, lookupProvider, List.of(new IcePixieAdvancements())));
    }

    /** Client datagen: models, the language file and the manual, written into common for both loaders. */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new IcePixieItemModelProvider(packOutput));
        event.addProvider(new IcePixieLanguageProvider(packOutput));
        event.addProvider(new IcePixieManualProvider(packOutput));
    }
}
