package com.grim3212.assorted.eightbit.client.render.entity;

import com.grim3212.assorted.eightbit.Constants;
import com.grim3212.assorted.eightbit.client.render.model.EightBitModelLayers;
import com.grim3212.assorted.eightbit.client.render.model.ParabuzzyModel;
import com.grim3212.assorted.eightbit.common.entity.Parabuzzy;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.EnumMap;
import java.util.Map;

public class ParabuzzyRenderer extends MobRenderer<Parabuzzy, ParabuzzyModel> {

    private static final Map<Parabuzzy.Variant, ResourceLocation> TEXTURES = textures("");
    private static final Map<Parabuzzy.Variant, ResourceLocation> ANGRY_TEXTURES = textures("_angry");

    public ParabuzzyRenderer(EntityRendererProvider.Context context) {
        super(context, new ParabuzzyModel(context.bakeLayer(EightBitModelLayers.PARABUZZY)), 0.4F);
    }

    private static Map<Parabuzzy.Variant, ResourceLocation> textures(String suffix) {
        Map<Parabuzzy.Variant, ResourceLocation> textures = new EnumMap<>(Parabuzzy.Variant.class);
        for (Parabuzzy.Variant variant : Parabuzzy.Variant.values()) {
            textures.put(variant, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/parabuzzy/" + variant.getSerializedName() + suffix + ".png"));
        }
        return textures;
    }

    @Override
    public void render(Parabuzzy entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        PerchRendering.withCarrierBody(entity, () -> super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight));
    }

    @Override
    public Vec3 getRenderOffset(Parabuzzy entity, float partialTicks) {
        return PerchRendering.offset(entity, partialTicks, super.getRenderOffset(entity, partialTicks));
    }

    @Override
    public boolean shouldRender(Parabuzzy entity, Frustum culler, double camX, double camY, double camZ) {
        return !PerchRendering.hiddenFromCarrier(entity) && super.shouldRender(entity, culler, camX, camY, camZ);
    }

    @Override
    public ResourceLocation getTextureLocation(Parabuzzy entity) {
        return (entity.isAngry() ? ANGRY_TEXTURES : TEXTURES).get(entity.getVariant());
    }
}
