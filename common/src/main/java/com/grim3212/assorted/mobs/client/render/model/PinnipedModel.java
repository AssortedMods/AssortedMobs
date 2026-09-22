package com.grim3212.assorted.mobs.client.render.model;

import com.grim3212.assorted.mobs.client.render.entity.AmphibiousRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/**
 * How a seal or a walrus carries itself; they differ in bulk and in tusks, not in this. Hauled out
 * it props its chest up on its fore flippers and heaves itself along. In the water it lies out
 * straight, fore flippers swept back, and sculls with its hind end from side to side. The two are
 * blended by how far into the water it is.
 */
public abstract class PinnipedModel extends EntityModel<AmphibiousRenderState> {

    private final ModelPart body;
    private final ModelPart chest;
    private final ModelPart head;
    private final ModelPart hips;
    private final ModelPart leftTailFlipper;
    private final ModelPart rightTailFlipper;
    private final ModelPart leftFlipper;
    private final ModelPart rightFlipper;
    private final float heft;

    /** @param heft how much slower than a seal everything about it moves: 1 for the seal */
    protected PinnipedModel(ModelPart root, float heft) {
        super(root);
        this.body = root.getChild("body");
        this.chest = this.body.getChild("chest");
        this.head = this.chest.getChild("head");
        this.hips = this.body.getChild("hips");
        this.leftTailFlipper = this.hips.getChild("left_tail_flipper");
        this.rightTailFlipper = this.hips.getChild("right_tail_flipper");
        this.leftFlipper = this.body.getChild("left_flipper");
        this.rightFlipper = this.body.getChild("right_flipper");
        this.heft = heft;
    }

    @Override
    public void setupAnim(AmphibiousRenderState state) {
        super.setupAnim(state);

        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        this.haulOut(state, 1.0F - state.waterAmount, speed);
        this.swim(state, state.waterAmount, speed);

        // Tusks first. Nothing but a walrus ever swings.
        float lunge = Mth.sin(state.attackAnim * Mth.PI);
        this.chest.xRot += lunge * 0.35F;
        this.head.xRot += lunge * 0.5F;
    }

    private void haulOut(AmphibiousRenderState state, float land, float speed) {
        float age = state.ageInTicks;
        float gait = state.walkAnimationPos * 0.866F / this.heft;
        float heave = Math.abs(Mth.sin(gait)) * speed;
        float headYaw = state.yRot * Mth.DEG_TO_RAD;

        // Chest up, head levelled off again on top of it, breathing.
        this.chest.xRot = land * (-0.38F + Mth.sin(age * 0.06F / this.heft) * 0.025F - heave * 0.12F);
        this.head.xRot = land * (0.26F + state.xRot * Mth.DEG_TO_RAD * 0.6F);
        this.head.yRot = land * headYaw * 0.7F;
        this.chest.yRot = land * headYaw * 0.3F;

        // Each stride a heave off the ground, the hind end swinging the other way after it.
        this.body.y -= land * heave * 1.2F;
        this.body.yRot = land * Mth.sin(gait) * 0.07F * speed;
        this.hips.yRot = land * -Mth.sin(gait) * 0.25F * speed;
        this.hips.xRot = land * (0.06F + heave * 0.1F);

        float reach = Mth.cos(gait) * 0.55F * speed;
        this.leftFlipper.zRot = land * 0.32F;
        this.rightFlipper.zRot = land * -0.32F;
        this.leftFlipper.yRot = land * (-0.15F + reach);
        this.rightFlipper.yRot = land * (0.15F + reach);

        // Now and then its hind flippers fan out and flick,
        float flick = Mth.sin(age * 0.021F) > 0.85F ? Mth.sin(age * 0.6F) * 0.25F : 0.0F;
        this.leftTailFlipper.yRot += land * (0.1F + flick);
        this.rightTailFlipper.yRot -= land * (0.1F + flick);
        // and less often, lying still, it waves a fore flipper at nothing.
        if (Mth.sin(age * 0.013F + 1.0F) > 0.93F) {
            this.leftFlipper.zRot -= land * (1.0F + Mth.sin(age * 0.5F) * 0.3F) * (1.0F - speed);
        }
    }

    private void swim(AmphibiousRenderState state, float water, float speed) {
        float stroke = state.ageInTicks * 0.3F / this.heft;
        // Never quite still: treading water is the same stroke, only lazier.
        float power = 0.3F + 0.7F * speed;
        float scull = Mth.sin(stroke);

        this.body.xRot += water * state.swimPitch;
        this.body.yRot -= water * scull * 0.06F * power;
        this.chest.yRot += water * scull * 0.05F * power;
        // Held a little against the dive, so it looks where it is going.
        this.head.xRot += water * (-0.08F - state.swimPitch * 0.3F);
        this.head.yRot += water * scull * 0.04F * power;

        // The wave runs backwards: hips, then the flippers behind them.
        this.hips.yRot += water * Mth.sin(stroke - 0.9F) * 0.32F * power;
        float fan = Mth.sin(stroke - 1.8F) * 0.5F * power;
        this.leftTailFlipper.yRot += water * fan;
        this.rightTailFlipper.yRot += water * fan;

        float steer = Mth.sin(stroke * 0.5F) * 0.12F;
        this.leftFlipper.yRot -= water * 1.05F;
        this.rightFlipper.yRot += water * 1.05F;
        this.leftFlipper.zRot += water * (0.25F + steer);
        this.rightFlipper.zRot += water * (-0.25F + steer);
    }
}
