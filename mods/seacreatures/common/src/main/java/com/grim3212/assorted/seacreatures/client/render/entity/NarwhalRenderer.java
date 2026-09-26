package com.grim3212.assorted.seacreatures.client.render.entity;

import com.grim3212.assorted.seacreatures.Constants;
import com.grim3212.assorted.seacreatures.client.render.model.SeaCreaturesModelLayers;
import com.grim3212.assorted.seacreatures.client.render.model.NarwhalModel;
import com.grim3212.assorted.seacreatures.common.entity.Narwhal;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class NarwhalRenderer extends MobRenderer<Narwhal, NarwhalRenderState, NarwhalModel> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/narwhal.png");

    public NarwhalRenderer(EntityRendererProvider.Context context) {
        super(context, new NarwhalModel(context.bakeLayer(SeaCreaturesModelLayers.NARWHAL)), 0.8F);
    }

    @Override
    public NarwhalRenderState createRenderState() {
        return new NarwhalRenderState();
    }

    @Override
    public void extractRenderState(Narwhal entity, NarwhalRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.swimPitch = entity.getSwimPitch(partialTicks) * Mth.DEG_TO_RAD;
        state.bank = entity.getBank(partialTicks);
        state.tuskAmount = entity.getTuskAmount(partialTicks);
    }

    @Override
    public Identifier getTextureLocation(NarwhalRenderState state) {
        return TEXTURE;
    }
}
