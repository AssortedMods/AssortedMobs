package com.grim3212.assorted.icepixie.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/** What this part's creatures drop. */
public class IcePixieLootTableProvider extends LootTableProvider {

    public IcePixieLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(new SubProviderEntry(IcePixieEntityLoot::new, LootContextParamSets.ENTITY)), registries);
    }
}
