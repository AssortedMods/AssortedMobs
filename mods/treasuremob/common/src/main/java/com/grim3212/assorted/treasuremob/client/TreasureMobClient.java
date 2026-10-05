package com.grim3212.assorted.treasuremob.client;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.treasuremob.client.render.entity.TreasureMobRenderer;
import com.grim3212.assorted.treasuremob.client.render.model.TreasureMobModel;
import com.grim3212.assorted.treasuremob.client.render.model.TreasureMobModelLayers;
import com.grim3212.assorted.treasuremob.common.entity.TreasureMobEntities;

/** Client-only startup, called by both loaders' client entry points. */
public class TreasureMobClient {

    public static void init() {
        ClientServices.CLIENT.registerEntityLayer(TreasureMobModelLayers.TREASURE_MOB, TreasureMobModel::createLayer);

        ClientServices.CLIENT.registerEntityRenderer(() -> TreasureMobEntities.TREASURE_MOB.get(), TreasureMobRenderer::new);
    }
}
