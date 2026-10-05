package com.grim3212.assorted.treasuremob.client.render.model;

import com.grim3212.assorted.treasuremob.Constants;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

public class TreasureMobModelLayers {

    public static final ModelLayerLocation TREASURE_MOB = create("treasure_mob");

    private static ModelLayerLocation create(String name) {
        return new ModelLayerLocation(Identifier.fromNamespaceAndPath(Constants.MOD_ID, name), "main");
    }
}
