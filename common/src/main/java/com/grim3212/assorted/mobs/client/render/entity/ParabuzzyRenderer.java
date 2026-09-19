package com.grim3212.assorted.mobs.client.render.entity;

import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.client.render.model.MobsModelLayers;
import com.grim3212.assorted.mobs.client.render.model.ParabuzzyModel;
import com.grim3212.assorted.mobs.common.entity.Parabuzzy;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

import java.util.EnumMap;
import java.util.Map;

public class ParabuzzyRenderer extends MobRenderer<Parabuzzy, ParabuzzyRenderer.State, ParabuzzyModel> {

    private static final Map<Parabuzzy.Variant, Identifier> TEXTURES = textures("");
    private static final Map<Parabuzzy.Variant, Identifier> ANGRY_TEXTURES = textures("_angry");

    public ParabuzzyRenderer(EntityRendererProvider.Context context) {
        super(context, new ParabuzzyModel(context.bakeLayer(MobsModelLayers.PARABUZZY)), 0.4F);
    }

    private static Map<Parabuzzy.Variant, Identifier> textures(String suffix) {
        Map<Parabuzzy.Variant, Identifier> textures = new EnumMap<>(Parabuzzy.Variant.class);
        for (Parabuzzy.Variant variant : Parabuzzy.Variant.values()) {
            textures.put(variant, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/parabuzzy/" + variant.getSerializedName() + suffix + ".png"));
        }
        return textures;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(Parabuzzy entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.variant = entity.getVariant();
        state.isAngry = entity.isAngry();
        state.isSitting = entity.isInSittingPose();
        PerchRendering.extract(entity, state, partialTicks);
    }

    @Override
    public boolean shouldRender(Parabuzzy entity, Frustum culler, double camX, double camY, double camZ) {
        return !PerchRendering.hiddenFromCarrier(entity) && super.shouldRender(entity, culler, camX, camY, camZ);
    }

    @Override
    public Identifier getTextureLocation(State state) {
        return (state.isAngry ? ANGRY_TEXTURES : TEXTURES).get(state.variant);
    }

    public static class State extends LivingEntityRenderState {
        public Parabuzzy.Variant variant = Parabuzzy.Variant.BLUE;
        public boolean isAngry;
        public boolean isSitting;
    }
}
