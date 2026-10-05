package com.grim3212.assorted.eightbit;

import com.grim3212.assorted.eightbit.client.data.EightBitItemModelProvider;
import com.grim3212.assorted.eightbit.client.data.EightBitLanguageProvider;
import com.grim3212.assorted.eightbit.client.data.EightBitManualProvider;
import com.grim3212.assorted.eightbit.data.EightBitAdvancements;
import com.grim3212.assorted.eightbit.data.EightBitBiomeTagProvider;
import com.grim3212.assorted.eightbit.data.EightBitBlockTagProvider;
import com.grim3212.assorted.eightbit.data.EightBitEntityTagProvider;
import com.grim3212.assorted.eightbit.data.EightBitItemTagProvider;
import com.grim3212.assorted.eightbit.data.EightBitLootTableProvider;
import com.grim3212.assorted.eightbit.data.EightBitRecipes;
import com.grim3212.assorted.eightbit.data.EightBitSpawnHabitProvider;
import com.grim3212.assorted.lib.data.ForgeBiomeTagProvider;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeEntityTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedEightBitNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedEightBitNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        EightBitCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ForgeBlockTagProvider blockTagProvider = event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new EightBitBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, new EightBitItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        event.addProvider(new ForgeBiomeTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new EightBitBiomeTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeEntityTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new EightBitEntityTagProvider(packOutput, lookupProvider)));
        event.addProvider(new EightBitSpawnHabitProvider(packOutput));
        // Recipe providers are not data providers any more - the Runner owns the output.
        event.addProvider(new EightBitRecipes.Runner(packOutput, lookupProvider));
        event.addProvider(new EightBitLootTableProvider(packOutput, lookupProvider));
        event.addProvider(new AdvancementProvider(packOutput, lookupProvider, List.of(new EightBitAdvancements())));
    }

    /** Client datagen: models, the language file and the manual, written into common for both loaders. */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new EightBitItemModelProvider(packOutput));
        event.addProvider(new EightBitLanguageProvider(packOutput));
        event.addProvider(new EightBitManualProvider(packOutput));
    }
}
