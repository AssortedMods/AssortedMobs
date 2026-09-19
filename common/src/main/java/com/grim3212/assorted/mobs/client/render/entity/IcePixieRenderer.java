package com.grim3212.assorted.mobs.client.render.entity;

import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.client.render.model.IcePixieModel;
import com.grim3212.assorted.mobs.client.render.model.MobsModelLayers;
import com.grim3212.assorted.mobs.common.entity.IcePixie;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class IcePixieRenderer extends MobRenderer<IcePixie, LivingEntityRenderState, IcePixieModel> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/ice_pixie.png");
    private static final float SCALE = 0.7F;

    public IcePixieRenderer(EntityRendererProvider.Context context) {
        super(context, new IcePixieModel(context.bakeLayer(MobsModelLayers.ICE_PIXIE)), 0.2F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack poseStack) {
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
