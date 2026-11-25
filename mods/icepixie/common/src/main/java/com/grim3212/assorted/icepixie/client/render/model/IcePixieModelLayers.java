package com.grim3212.assorted.icepixie.client.render.model;

import com.grim3212.assorted.icepixie.Constants;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class IcePixieModelLayers {

    public static final ModelLayerLocation ICE_PIXIE = create("ice_pixie");

    private static ModelLayerLocation create(String name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), "main");
    }
}
