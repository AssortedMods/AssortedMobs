package com.grim3212.assorted.treasuremob.client.render.entity;

import com.grim3212.assorted.treasuremob.Constants;
import com.grim3212.assorted.treasuremob.client.render.model.TreasureMobModelLayers;
import com.grim3212.assorted.treasuremob.client.render.model.TreasureMobModel;
import com.grim3212.assorted.treasuremob.common.entity.TreasureMob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TreasureMobRenderer extends MobRenderer<TreasureMob, TreasureMobModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/treasure_mob.png");

    public TreasureMobRenderer(EntityRendererProvider.Context context) {
        super(context, new TreasureMobModel(context.bakeLayer(TreasureMobModelLayers.TREASURE_MOB)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TreasureMob entity) {
        return TEXTURE;
    }
}
