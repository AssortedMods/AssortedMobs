package com.grim3212.assorted.mobs.client.render.entity;

import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.client.render.model.MobsModelLayers;
import com.grim3212.assorted.mobs.client.render.model.TreasureMobModel;
import com.grim3212.assorted.mobs.common.entity.TreasureMob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class TreasureMobRenderer extends MobRenderer<TreasureMob, TreasureMobRenderer.State, TreasureMobModel> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/treasure_mob.png");

    public TreasureMobRenderer(EntityRendererProvider.Context context) {
        super(context, new TreasureMobModel(context.bakeLayer(MobsModelLayers.TREASURE_MOB)), 0.5F);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(TreasureMob entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isSitting = entity.isInSittingPose();
    }

    @Override
    public Identifier getTextureLocation(State state) {
        return TEXTURE;
    }

    public static class State extends LivingEntityRenderState {
        public boolean isSitting;
    }
}
