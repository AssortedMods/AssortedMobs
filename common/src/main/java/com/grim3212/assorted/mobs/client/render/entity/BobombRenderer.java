package com.grim3212.assorted.mobs.client.render.entity;

import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.client.render.model.BobombModel;
import com.grim3212.assorted.mobs.client.render.model.MobsModelLayers;
import com.grim3212.assorted.mobs.common.entity.Bobomb;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class BobombRenderer extends MobRenderer<Bobomb, BobombRenderer.State, BobombModel> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/bobomb.png");
    private static final Identifier LIT_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/bobomb_lit.png");
    private static final float SCALE = 0.65F;

    public BobombRenderer(EntityRendererProvider.Context context) {
        super(context, new BobombModel(context.bakeLayer(MobsModelLayers.BOBOMB)), 0.3F);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(Bobomb entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isLit = entity.isLit();
        PerchRendering.extract(entity, state, partialTicks);
    }

    @Override
    public boolean shouldRender(Bobomb entity, Frustum culler, double camX, double camY, double camZ) {
        return !PerchRendering.hiddenFromCarrier(entity) && super.shouldRender(entity, culler, camX, camY, camZ);
    }

    @Override
    protected void scale(State state, PoseStack poseStack) {
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public Identifier getTextureLocation(State state) {
        return state.isLit ? LIT_TEXTURE : TEXTURE;
    }

    public static class State extends LivingEntityRenderState {
        public boolean isLit;
    }
}
