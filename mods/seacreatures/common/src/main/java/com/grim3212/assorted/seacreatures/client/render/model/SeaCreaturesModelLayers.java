package com.grim3212.assorted.seacreatures.client.render.model;

import com.grim3212.assorted.seacreatures.Constants;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

public class SeaCreaturesModelLayers {

    public static final ModelLayerLocation SEAL = create("seal");
    public static final ModelLayerLocation WALRUS = create("walrus");
    public static final ModelLayerLocation NARWHAL = create("narwhal");
    public static final ModelLayerLocation SEA_OTTER = create("sea_otter");

    private static ModelLayerLocation create(String name) {
        return new ModelLayerLocation(Identifier.fromNamespaceAndPath(Constants.MOD_ID, name), "main");
    }
}
