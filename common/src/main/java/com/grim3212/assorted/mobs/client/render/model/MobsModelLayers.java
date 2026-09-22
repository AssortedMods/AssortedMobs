package com.grim3212.assorted.mobs.client.render.model;

import com.grim3212.assorted.mobs.Constants;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

public class MobsModelLayers {

    public static final ModelLayerLocation ICE_PIXIE = create("ice_pixie");
    public static final ModelLayerLocation TREASURE_MOB = create("treasure_mob");
    public static final ModelLayerLocation BOBOMB = create("bobomb");
    public static final ModelLayerLocation PARABUZZY = create("parabuzzy");
    public static final ModelLayerLocation SEAL = create("seal");
    public static final ModelLayerLocation WALRUS = create("walrus");
    public static final ModelLayerLocation NARWHAL = create("narwhal");
    public static final ModelLayerLocation SEA_OTTER = create("sea_otter");

    private static ModelLayerLocation create(String name) {
        return new ModelLayerLocation(Identifier.fromNamespaceAndPath(Constants.MOD_ID, name), "main");
    }
}
