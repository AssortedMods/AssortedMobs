package com.grim3212.assorted.eightbit.client;

import com.grim3212.assorted.eightbit.client.render.entity.BobombRenderer;
import com.grim3212.assorted.eightbit.client.render.entity.ParabuzzyRenderer;
import com.grim3212.assorted.eightbit.client.render.model.BobombModel;
import com.grim3212.assorted.eightbit.client.render.model.EightBitModelLayers;
import com.grim3212.assorted.eightbit.client.render.model.ParabuzzyModel;
import com.grim3212.assorted.eightbit.common.entity.EightBitEntities;
import com.grim3212.assorted.lib.platform.ClientServices;

/** Client-only startup, called by both loaders' client entry points. */
public class EightBitClient {

    public static void init() {
        ClientServices.CLIENT.registerEntityLayer(EightBitModelLayers.BOBOMB, BobombModel::createLayer);
        ClientServices.CLIENT.registerEntityLayer(EightBitModelLayers.PARABUZZY, ParabuzzyModel::createLayer);

        ClientServices.CLIENT.registerEntityRenderer(() -> EightBitEntities.BOBOMB.get(), BobombRenderer::new);
        ClientServices.CLIENT.registerEntityRenderer(() -> EightBitEntities.PARABUZZY.get(), ParabuzzyRenderer::new);
    }
}
