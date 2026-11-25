package com.grim3212.assorted.seacreatures;

import com.grim3212.assorted.lib.data.ForgeBiomeTagProvider;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeEntityTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.lib.data.ForgeRegistryEntries;
import com.grim3212.assorted.seacreatures.client.data.SeaCreaturesItemModelProvider;
import com.grim3212.assorted.seacreatures.client.data.SeaCreaturesLanguageProvider;
import com.grim3212.assorted.seacreatures.client.data.SeaCreaturesManualProvider;
import com.grim3212.assorted.seacreatures.data.SeaCreaturesBiomeTagProvider;
import com.grim3212.assorted.seacreatures.data.SeaCreaturesBlockTagProvider;
import com.grim3212.assorted.seacreatures.data.SeaCreaturesEntityLoot;
import com.grim3212.assorted.seacreatures.data.SeaCreaturesEntityTagProvider;
import com.grim3212.assorted.seacreatures.data.SeaCreaturesItemTagProvider;
import com.grim3212.assorted.seacreatures.data.SeaCreaturesRecipes;
import com.grim3212.assorted.seacreatures.data.SeaCreaturesSpawnHabitProvider;
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
public class AssortedSeaCreaturesNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedSeaCreaturesNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherData);

        SeaCreaturesCommonMod.init();
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

        ForgeBlockTagProvider blockTagProvider = generator.addProvider(server, new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new SeaCreaturesBlockTagProvider(packOutput, lookupProvider)));
        generator.addProvider(server, new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, fileHelper, new SeaCreaturesItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        generator.addProvider(server, new ForgeBiomeTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new SeaCreaturesBiomeTagProvider(packOutput, lookupProvider)));
        generator.addProvider(server, new ForgeEntityTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new SeaCreaturesEntityTagProvider(packOutput, lookupProvider)));
        generator.addProvider(server, new SeaCreaturesSpawnHabitProvider(packOutput));
        ForgeRegistryEntries.of(Constants.MOD_ID).recipes(SeaCreaturesRecipes::new).loot(SeaCreaturesEntityLoot::new, LootContextParamSets.ENTITY).addTo(event);

        boolean client = event.includeClient();
        generator.addProvider(client, new SeaCreaturesItemModelProvider(packOutput, fileHelper));
        generator.addProvider(client, new SeaCreaturesLanguageProvider(packOutput));
        generator.addProvider(client, new SeaCreaturesManualProvider(packOutput));
    }
}
