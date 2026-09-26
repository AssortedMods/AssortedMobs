package com.grim3212.assorted.seacreatures;

import com.grim3212.assorted.lib.data.ForgeBiomeTagProvider;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeEntityTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.seacreatures.client.data.SeaCreaturesEquipmentAssetProvider;
import com.grim3212.assorted.seacreatures.client.data.SeaCreaturesItemModelProvider;
import com.grim3212.assorted.seacreatures.client.data.SeaCreaturesLanguageProvider;
import com.grim3212.assorted.seacreatures.client.data.SeaCreaturesManualProvider;
import com.grim3212.assorted.seacreatures.data.SeaCreaturesBiomeTagProvider;
import com.grim3212.assorted.seacreatures.data.SeaCreaturesBlockTagProvider;
import com.grim3212.assorted.seacreatures.data.SeaCreaturesEntityTagProvider;
import com.grim3212.assorted.seacreatures.data.SeaCreaturesItemTagProvider;
import com.grim3212.assorted.seacreatures.data.SeaCreaturesLootTableProvider;
import com.grim3212.assorted.seacreatures.data.SeaCreaturesRecipes;
import com.grim3212.assorted.seacreatures.data.SeaCreaturesSpawnHabitProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedSeaCreaturesNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedSeaCreaturesNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        SeaCreaturesCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ForgeBlockTagProvider blockTagProvider = event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new SeaCreaturesBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, new SeaCreaturesItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        event.addProvider(new ForgeBiomeTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new SeaCreaturesBiomeTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeEntityTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new SeaCreaturesEntityTagProvider(packOutput, lookupProvider)));
        event.addProvider(new SeaCreaturesSpawnHabitProvider(packOutput));
        // Recipe providers are not data providers any more - the Runner owns the output.
        event.addProvider(new SeaCreaturesRecipes.Runner(packOutput, lookupProvider));
        event.addProvider(new SeaCreaturesLootTableProvider(packOutput, lookupProvider));
    }

    /** Client datagen: models, the language file and the manual, written into common for both loaders. */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new SeaCreaturesItemModelProvider(packOutput));
        event.addProvider(new SeaCreaturesEquipmentAssetProvider(packOutput));
        event.addProvider(new SeaCreaturesLanguageProvider(packOutput));
        event.addProvider(new SeaCreaturesManualProvider(packOutput));
    }
}
