package com.grim3212.assorted.treasuremob.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.common.hash.Hashing;
import com.grim3212.assorted.lib.data.CrossLoaderData;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Writes {@link TreasureMobChestLoot}. A 1.21.1 datagen run only holds the tables it writes, so its check would call
 * every vanilla and Assorted Structures chest these draw from unknown; those references, and only those, are let
 * through. 1.21.1's loot provider writes no load conditions either, so the Assorted Structures tables get NeoForge's
 * mod-loaded condition added to their json here, and {@link CrossLoaderData} adds Fabric's spelling of it.
 */
public class TreasureMobChestLootProvider extends LootTableProvider {

    private static final String UNKNOWN_TABLE = "Unknown loot table called ";

    private final TreasureMobChestLoot loot;

    private TreasureMobChestLootProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, TreasureMobChestLoot loot) {
        super(output, Set.of(), List.of(new SubProviderEntry(lookup -> loot, LootContextParamSets.CHEST)), registries);
        this.loot = loot;
    }

    public static TreasureMobChestLootProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        return new TreasureMobChestLootProvider(output, registries, new TreasureMobChestLoot());
    }

    @Override
    protected void validate(WritableRegistry<LootTable> registry, ValidationContext context, ProblemReporter.Collector problems) {
        ProblemReporter.Collector found = new ProblemReporter.Collector();
        super.validate(registry, new ValidationContext(found, LootContextParamSets.ALL_PARAMS, context.resolver()), found);
        Set<String> external = this.loot.external().stream().map(key -> UNKNOWN_TABLE + key.location()).collect(Collectors.toSet());
        found.get().forEach((where, problem) -> {
            if (!external.contains(problem)) {
                problems.report(where + ": " + problem);
            }
        });
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        CachedOutput crossLoader = CrossLoaderData.wrap(output);
        return super.run((path, data, hash) -> {
            if (!this.needsAssortedStructures(path.toString().replace('\\', '/'))) {
                crossLoader.writeIfNeeded(path, data, hash);
                return;
            }
            JsonObject original = JsonParser.parseString(new String(data, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject conditional = new JsonObject();
            JsonElement conditions = ICondition.LIST_CODEC.encodeStart(JsonOps.INSTANCE, List.of(new ModLoadedCondition(TreasureMobChestLoot.ASSORTED_STRUCTURES))).getOrThrow();
            conditional.add(ConditionalOps.DEFAULT_CONDITIONS_KEY, conditions);
            original.entrySet().forEach(entry -> conditional.add(entry.getKey(), entry.getValue()));
            byte[] written = conditional.toString().getBytes(StandardCharsets.UTF_8);
            crossLoader.writeIfNeeded(path, written, Hashing.sha256().hashBytes(written));
        });
    }

    private boolean needsAssortedStructures(String path) {
        for (ResourceKey<LootTable> key : this.loot.needsAssortedStructures()) {
            if (path.endsWith("/data/" + key.location().getNamespace() + "/loot_table/" + key.location().getPath() + ".json")) {
                return true;
            }
        }
        return false;
    }
}
