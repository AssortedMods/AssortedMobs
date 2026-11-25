package com.grim3212.assorted.seacreatures.client.render.entity;

import com.grim3212.assorted.seacreatures.Constants;
import com.grim3212.assorted.seacreatures.client.render.model.NarwhalModel;
import com.grim3212.assorted.seacreatures.client.render.model.SeaCreaturesModelLayers;
import com.grim3212.assorted.seacreatures.common.entity.Narwhal;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class NarwhalRenderer extends MobRenderer<Narwhal, NarwhalModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/narwhal.png");

    public NarwhalRenderer(EntityRendererProvider.Context context) {
        super(context, new NarwhalModel(context.bakeLayer(SeaCreaturesModelLayers.NARWHAL)), 0.8F);
    }

    @Override
    public ResourceLocation getTextureLocation(Narwhal entity) {
        return TEXTURE;
    }
}
