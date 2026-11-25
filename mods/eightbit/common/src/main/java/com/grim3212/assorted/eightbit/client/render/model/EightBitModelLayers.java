package com.grim3212.assorted.eightbit.client.render.model;

import com.grim3212.assorted.eightbit.Constants;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class EightBitModelLayers {

    public static final ModelLayerLocation BOBOMB = create("bobomb");
    public static final ModelLayerLocation PARABUZZY = create("parabuzzy");

    private static ModelLayerLocation create(String name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), "main");
    }
}
