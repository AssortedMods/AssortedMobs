package com.grim3212.assorted.icepixie.client.render.entity;

import com.grim3212.assorted.icepixie.Constants;
import com.grim3212.assorted.icepixie.client.render.model.IcePixieModel;
import com.grim3212.assorted.icepixie.client.render.model.IcePixieModelLayers;
import com.grim3212.assorted.icepixie.common.entity.IcePixie;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class IcePixieRenderer extends MobRenderer<IcePixie, IcePixieModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/ice_pixie.png");
    private static final float SCALE = 0.7F;

    public IcePixieRenderer(EntityRendererProvider.Context context) {
        super(context, new IcePixieModel(context.bakeLayer(IcePixieModelLayers.ICE_PIXIE)), 0.2F);
    }

    @Override
    protected void scale(IcePixie entity, PoseStack poseStack, float partialTicks) {
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public ResourceLocation getTextureLocation(IcePixie entity) {
        return TEXTURE;
    }
}
