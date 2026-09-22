package com.grim3212.assorted.mobs.client.render.entity;

import com.grim3212.assorted.mobs.Constants;
import com.grim3212.assorted.mobs.common.entity.AmphibiousAnimal;
import com.grim3212.assorted.mobs.common.entity.SeaOtter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * The seal, the walrus and the sea otter: modelled at their own size, with a skin for the young, and
 * told how far into the water they are so the model can ease between its land and water poses.
 */
public class AmphibiousAnimalRenderer<T extends AmphibiousAnimal, M extends EntityModel<AmphibiousRenderState>> extends MobRenderer<T, AmphibiousRenderState, M> {

    /** A little over the half its hitbox shrinks to, or a pup looks lost beside its mother. */
    private static final float BABY_SCALE = 0.6F;

    private final Identifier texture;
    private final Identifier babyTexture;

    public AmphibiousAnimalRenderer(EntityRendererProvider.Context context, M model, String texture, float shadowRadius) {
        super(context, model, shadowRadius);
        this.texture = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/" + texture + ".png");
        this.babyTexture = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/" + texture + "_baby.png");
    }

    @Override
    public AmphibiousRenderState createRenderState() {
        return new AmphibiousRenderState();
    }

    @Override
    public void extractRenderState(T entity, AmphibiousRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.waterAmount = entity.getWaterAmount(partialTicks);
        state.swimPitch = entity.getSwimPitch(partialTicks) * Mth.DEG_TO_RAD;
        state.floatAmount = entity instanceof SeaOtter otter ? otter.getFloatAmount(partialTicks) : 0.0F;
        state.attackAnim = entity.getAttackAnim(partialTicks);
    }

    @Override
    protected void scale(AmphibiousRenderState state, PoseStack poseStack) {
        if (state.isBaby) {
            poseStack.scale(BABY_SCALE, BABY_SCALE, BABY_SCALE);
        }
    }

    @Override
    public Identifier getTextureLocation(AmphibiousRenderState state) {
        return state.isBaby ? this.babyTexture : this.texture;
    }
}
