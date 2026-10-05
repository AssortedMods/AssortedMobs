package com.grim3212.assorted.treasuremob.data;

import com.grim3212.assorted.treasuremob.Constants;
import com.grim3212.assorted.treasuremob.api.TreasureMobTags;
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
public class TreasureMobStructureTagProvider extends StructureTagsProvider {

    /** The structures Assorted Structures adds, optional so the tag still loads without it. */
    private static final String ASSORTED_STRUCTURES = "assortedstructures";

    public TreasureMobStructureTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, Constants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(TreasureMobTags.Structures.SPAWNS_TREASURE_MOBS)
                .add(BuiltinStructures.MINESHAFT, BuiltinStructures.MINESHAFT_MESA, BuiltinStructures.STRONGHOLD, BuiltinStructures.TRIAL_CHAMBERS,
                        BuiltinStructures.ANCIENT_CITY, BuiltinStructures.DESERT_PYRAMID, BuiltinStructures.JUNGLE_TEMPLE, BuiltinStructures.WOODLAND_MANSION,
                        BuiltinStructures.BASTION_REMNANT, BuiltinStructures.FORTRESS, BuiltinStructures.END_CITY)
                .addOptional(assortedStructures("fountain"))
                .addOptional(assortedStructures("pyramid"))
                .addOptional(assortedStructures("snowball"))
                .addOptional(assortedStructures("water_dome"));
    }

    private static ResourceKey<Structure> assortedStructures(String name) {
        return ResourceKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath(ASSORTED_STRUCTURES, name));
    }
}
