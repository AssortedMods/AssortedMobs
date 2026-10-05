package com.grim3212.assorted.seacreatures.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/** What this part's creatures drop. */
public class SeaCreaturesLootTableProvider extends LootTableProvider {

    public SeaCreaturesLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(new SubProviderEntry(SeaCreaturesEntityLoot::new, LootContextParamSets.ENTITY)), registries);
    }
}
