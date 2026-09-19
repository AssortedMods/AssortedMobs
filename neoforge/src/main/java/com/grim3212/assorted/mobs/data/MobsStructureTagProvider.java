package com.grim3212.assorted.mobs.data;

import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.api.MobsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.concurrent.CompletableFuture;

/** Which structures each creature spawns in. A datapack moves them by editing these tags. */
public class MobsStructureTagProvider extends StructureTagsProvider {

    /** Assorted World's structures, optional so the tag still loads without it. */
    private static final String ASSORTED_WORLD = "assortedworld";

    public MobsStructureTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, Constants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(MobsTags.Structures.SPAWNS_TREASURE_MOBS)
                .add(BuiltinStructures.MINESHAFT, BuiltinStructures.MINESHAFT_MESA, BuiltinStructures.STRONGHOLD, BuiltinStructures.TRIAL_CHAMBERS,
                        BuiltinStructures.ANCIENT_CITY, BuiltinStructures.DESERT_PYRAMID, BuiltinStructures.JUNGLE_TEMPLE)
                .addOptional(assortedWorld("fountain"))
                .addOptional(assortedWorld("pyramid"))
                .addOptional(assortedWorld("snowball"))
                .addOptional(assortedWorld("water_dome"));
    }

    private static ResourceKey<Structure> assortedWorld(String name) {
        return ResourceKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath(ASSORTED_WORLD, name));
    }
}
