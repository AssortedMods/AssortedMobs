package com.grim3212.assorted.eightbit.client.render.entity;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.client.render.model.BobombModel;
import com.grim3212.assorted.eightbit.client.render.model.EightBitModelLayers;
import com.grim3212.assorted.eightbit.common.entity.Bobomb;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class BobombRenderer extends MobRenderer<Bobomb, BobombModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/bobomb.png");
    private static final ResourceLocation LIT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/bobomb_lit.png");
    private static final float SCALE = 0.65F;

    public BobombRenderer(EntityRendererProvider.Context context) {
        super(context, new BobombModel(context.bakeLayer(EightBitModelLayers.BOBOMB)), 0.3F);
    }

    @Override
    public void render(Bobomb entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        PerchRendering.withCarrierBody(entity, () -> super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight));
    }

    @Override
    public Vec3 getRenderOffset(Bobomb entity, float partialTicks) {
        return PerchRendering.offset(entity, partialTicks, super.getRenderOffset(entity, partialTicks));
    }

    @Override
    public boolean shouldRender(Bobomb entity, Frustum culler, double camX, double camY, double camZ) {
        return !PerchRendering.hiddenFromCarrier(entity) && super.shouldRender(entity, culler, camX, camY, camZ);
    }

    @Override
    protected void scale(Bobomb entity, PoseStack poseStack, float partialTicks) {
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public ResourceLocation getTextureLocation(Bobomb entity) {
        return entity.isLit() ? LIT_TEXTURE : TEXTURE;
    }
}
