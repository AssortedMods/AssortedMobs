package com.grim3212.assorted.mobs.data;

import com.grim3212.assorted.lib.data.CrossLoaderData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * The mod's loot tables. Written through {@link CrossLoaderData} so the treasure tables for Assorted
 * World's structures carry Fabric's mod-loaded condition beside NeoForge's.
 */
public class MobsLootTableProvider extends LootTableProvider {

    /** Tables that only draw from other chests; see {@link #validate}. */
    private static final String TREASURE_TABLES = "chests/treasure_mob/";

    public MobsLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(MobsEntityLoot::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(MobsChestLoot::new, LootContextParamSets.CHEST)), registries);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return super.run(CrossLoaderData.wrap(cache));
    }

    /**
     * The treasure tables are left out: they point at vanilla's and Assorted World's chest tables,
     * which datagen only knows when it generates them itself, so every reference would be reported
     * missing. The game checks them against the real tables when it loads.
     */
    @Override
    protected void validate(WritableRegistry<LootTable> tables, ValidationContextSource validationContext, ProblemReporter.Collector problems) {
        tables.listElements()
                .filter(table -> !table.key().identifier().getPath().startsWith(TREASURE_TABLES))
                .forEach(table -> table.value().validate(validationContext.context(table.value().getParamSet())
                        .enterElement(new ProblemReporter.RootElementPathElement(table.key()), table.key())));
    }
}
