package com.grim3212.assorted.eightbit;

import com.grim3212.assorted.eightbit.client.data.EightBitItemModelProvider;
import com.grim3212.assorted.eightbit.client.data.EightBitLanguageProvider;
import com.grim3212.assorted.eightbit.client.data.EightBitManualProvider;
import com.grim3212.assorted.eightbit.data.EightBitAdvancements;
import com.grim3212.assorted.eightbit.data.EightBitBiomeTagProvider;
import com.grim3212.assorted.eightbit.data.EightBitBlockTagProvider;
import com.grim3212.assorted.eightbit.data.EightBitEntityLoot;
import com.grim3212.assorted.eightbit.data.EightBitEntityTagProvider;
import com.grim3212.assorted.eightbit.data.EightBitItemTagProvider;
import com.grim3212.assorted.eightbit.data.EightBitRecipes;
import com.grim3212.assorted.eightbit.data.EightBitSpawnHabitProvider;
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
public class AssortedEightBitNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedEightBitNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherData);

        EightBitCommonMod.init();
    }

    /** One event for both halves; the data run's --server and --client flags pick which providers run. */
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        ForgeBlockTagProvider blockTagProvider = generator.addProvider(event.includeServer(), new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new EightBitBlockTagProvider(packOutput, lookupProvider)));
        generator.addProvider(event.includeServer(), new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, fileHelper, new EightBitItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        generator.addProvider(event.includeServer(), new ForgeBiomeTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new EightBitBiomeTagProvider(packOutput, lookupProvider)));
        generator.addProvider(event.includeServer(), new ForgeEntityTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new EightBitEntityTagProvider(packOutput, lookupProvider)));
        generator.addProvider(event.includeServer(), new EightBitSpawnHabitProvider(packOutput));
        ForgeRegistryEntries.of(Constants.MOD_ID).recipes(EightBitRecipes::new)
                .advancements((registries, saver, existingFiles) -> new EightBitAdvancements().generate(registries, saver))
                .loot(EightBitEntityLoot::new, LootContextParamSets.ENTITY).addTo(event);

        generator.addProvider(event.includeClient(), new EightBitItemModelProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new EightBitLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new EightBitManualProvider(packOutput));
    }
}
