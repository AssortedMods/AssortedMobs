package com.grim3212.assorted.mobs.client;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.mobs.client.render.entity.AmphibiousAnimalRenderer;
import com.grim3212.assorted.mobs.client.render.entity.BobombRenderer;
import com.grim3212.assorted.mobs.client.render.entity.IcePixieRenderer;
import com.grim3212.assorted.mobs.client.render.entity.NarwhalRenderer;
import com.grim3212.assorted.mobs.client.render.entity.ParabuzzyRenderer;
import com.grim3212.assorted.mobs.client.render.entity.TreasureMobRenderer;
import com.grim3212.assorted.mobs.client.render.model.BobombModel;
import com.grim3212.assorted.mobs.client.render.model.IcePixieModel;
import com.grim3212.assorted.mobs.client.render.model.MobsModelLayers;
import com.grim3212.assorted.mobs.client.render.model.NarwhalModel;
import com.grim3212.assorted.mobs.client.render.model.ParabuzzyModel;
import com.grim3212.assorted.mobs.client.render.model.SeaOtterModel;
import com.grim3212.assorted.mobs.client.render.model.SealModel;
import com.grim3212.assorted.mobs.client.render.model.TreasureMobModel;
import com.grim3212.assorted.mobs.client.render.model.WalrusModel;
import com.grim3212.assorted.mobs.common.entity.MobsEntities;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

/** Client-only startup, called by both loaders' client entry points. */
public class MobsClient {

    public static void init() {
        ClientServices.CLIENT.registerEntityLayer(MobsModelLayers.ICE_PIXIE, IcePixieModel::createLayer);
        ClientServices.CLIENT.registerEntityLayer(MobsModelLayers.TREASURE_MOB, TreasureMobModel::createLayer);
        ClientServices.CLIENT.registerEntityLayer(MobsModelLayers.BOBOMB, BobombModel::createLayer);
        ClientServices.CLIENT.registerEntityLayer(MobsModelLayers.PARABUZZY, ParabuzzyModel::createLayer);
        ClientServices.CLIENT.registerEntityLayer(MobsModelLayers.SEAL, SealModel::createLayer);
        ClientServices.CLIENT.registerEntityLayer(MobsModelLayers.WALRUS, WalrusModel::createLayer);
        ClientServices.CLIENT.registerEntityLayer(MobsModelLayers.NARWHAL, NarwhalModel::createLayer);
        ClientServices.CLIENT.registerEntityLayer(MobsModelLayers.SEA_OTTER, SeaOtterModel::createLayer);

        ClientServices.CLIENT.registerEntityRenderer(() -> MobsEntities.ICE_PIXIE.get(), IcePixieRenderer::new);
        ClientServices.CLIENT.registerEntityRenderer(() -> MobsEntities.ICE_CUBE.get(), ThrownItemRenderer::new);
        ClientServices.CLIENT.registerEntityRenderer(() -> MobsEntities.TREASURE_MOB.get(), TreasureMobRenderer::new);
        ClientServices.CLIENT.registerEntityRenderer(() -> MobsEntities.BOBOMB.get(), BobombRenderer::new);
        ClientServices.CLIENT.registerEntityRenderer(() -> MobsEntities.PARABUZZY.get(), ParabuzzyRenderer::new);



        // The sea creatures, each modelled at its own size. In 1.2.5 they were small models blown up: the narwhal to eighteen blocks.
        ClientServices.CLIENT.registerEntityRenderer(() -> MobsEntities.SEAL.get(), context -> new AmphibiousAnimalRenderer<>(context, new SealModel(context.bakeLayer(MobsModelLayers.SEAL)), "seal", 0.45F));
        ClientServices.CLIENT.registerEntityRenderer(() -> MobsEntities.WALRUS.get(), context -> new AmphibiousAnimalRenderer<>(context, new WalrusModel(context.bakeLayer(MobsModelLayers.WALRUS)), "walrus", 0.8F));
        ClientServices.CLIENT.registerEntityRenderer(() -> MobsEntities.SEA_OTTER.get(), context -> new AmphibiousAnimalRenderer<>(context, new SeaOtterModel(context.bakeLayer(MobsModelLayers.SEA_OTTER)), "sea_otter", 0.35F));
        ClientServices.CLIENT.registerEntityRenderer(() -> MobsEntities.NARWHAL.get(), NarwhalRenderer::new);
    }
}
