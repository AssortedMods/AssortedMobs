package com.grim3212.assorted.seacreatures.client;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.seacreatures.client.render.entity.AmphibiousAnimalRenderer;
import com.grim3212.assorted.seacreatures.client.render.entity.NarwhalRenderer;
import com.grim3212.assorted.seacreatures.client.render.model.NarwhalModel;
import com.grim3212.assorted.seacreatures.client.render.model.SeaCreaturesModelLayers;
import com.grim3212.assorted.seacreatures.client.render.model.SeaOtterModel;
import com.grim3212.assorted.seacreatures.client.render.model.SealModel;
import com.grim3212.assorted.seacreatures.client.render.model.WalrusModel;
import com.grim3212.assorted.seacreatures.common.entity.SeaCreaturesEntities;

/** Client-only startup, called by both loaders' client entry points. */
public class SeaCreaturesClient {

    public static void init() {
        ClientServices.CLIENT.registerEntityLayer(SeaCreaturesModelLayers.SEAL, SealModel::createLayer);
        ClientServices.CLIENT.registerEntityLayer(SeaCreaturesModelLayers.WALRUS, WalrusModel::createLayer);
        ClientServices.CLIENT.registerEntityLayer(SeaCreaturesModelLayers.NARWHAL, NarwhalModel::createLayer);
        ClientServices.CLIENT.registerEntityLayer(SeaCreaturesModelLayers.SEA_OTTER, SeaOtterModel::createLayer);

        // Each modelled at its own size. In 1.2.5 they were small models blown up: the narwhal to eighteen blocks.
        ClientServices.CLIENT.registerEntityRenderer(() -> SeaCreaturesEntities.SEAL.get(), context -> new AmphibiousAnimalRenderer<>(context, new SealModel(context.bakeLayer(SeaCreaturesModelLayers.SEAL)), "seal", 0.45F));
        ClientServices.CLIENT.registerEntityRenderer(() -> SeaCreaturesEntities.WALRUS.get(), context -> new AmphibiousAnimalRenderer<>(context, new WalrusModel(context.bakeLayer(SeaCreaturesModelLayers.WALRUS)), "walrus", 0.8F));
        ClientServices.CLIENT.registerEntityRenderer(() -> SeaCreaturesEntities.SEA_OTTER.get(), context -> new AmphibiousAnimalRenderer<>(context, new SeaOtterModel(context.bakeLayer(SeaCreaturesModelLayers.SEA_OTTER)), "sea_otter", 0.35F));
        ClientServices.CLIENT.registerEntityRenderer(() -> SeaCreaturesEntities.NARWHAL.get(), NarwhalRenderer::new);
    }
}
