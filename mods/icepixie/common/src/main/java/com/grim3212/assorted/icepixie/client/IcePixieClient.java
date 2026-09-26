package com.grim3212.assorted.icepixie.client;

import com.grim3212.assorted.icepixie.client.render.entity.IcePixieRenderer;
import com.grim3212.assorted.icepixie.client.render.model.IcePixieModel;
import com.grim3212.assorted.icepixie.client.render.model.IcePixieModelLayers;
import com.grim3212.assorted.icepixie.common.entity.IcePixieEntities;
import com.grim3212.assorted.lib.platform.ClientServices;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

/** Client-only startup, called by both loaders' client entry points. */
public class IcePixieClient {

    public static void init() {
        ClientServices.CLIENT.registerEntityLayer(IcePixieModelLayers.ICE_PIXIE, IcePixieModel::createLayer);

        ClientServices.CLIENT.registerEntityRenderer(() -> IcePixieEntities.ICE_PIXIE.get(), IcePixieRenderer::new);
        ClientServices.CLIENT.registerEntityRenderer(() -> IcePixieEntities.ICE_CUBE.get(), ThrownItemRenderer::new);
    }
}
