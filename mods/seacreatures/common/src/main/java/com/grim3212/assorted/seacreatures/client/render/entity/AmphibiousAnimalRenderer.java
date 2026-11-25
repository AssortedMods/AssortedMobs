package com.grim3212.assorted.seacreatures.client.render.entity;

import com.grim3212.assorted.seacreatures.Constants;
import com.grim3212.assorted.seacreatures.common.entity.AmphibiousAnimal;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * The seal, the walrus and the sea otter: modelled at their own size, with a skin for the young. The model reads how
 * far into the water each is itself, so it can ease between its land and water poses.
 */
public class AmphibiousAnimalRenderer<T extends AmphibiousAnimal, M extends EntityModel<T>> extends MobRenderer<T, M> {

    private final ResourceLocation texture;
    private final ResourceLocation babyTexture;

    public AmphibiousAnimalRenderer(EntityRendererProvider.Context context, M model, String texture, float shadowRadius) {
        super(context, model, shadowRadius);
        this.texture = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/" + texture + ".png");
        this.babyTexture = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/" + texture + "_baby.png");
    }

    /** The same half LivingEntity#getAgeScale shrinks a pup's hitbox to, so what you see is what it collides with. 1 for an adult. */
    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTicks) {
        float ageScale = entity.getAgeScale();
        poseStack.scale(ageScale, ageScale, ageScale);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity.isBaby() ? this.babyTexture : this.texture;
    }
}
