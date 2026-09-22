package com.grim3212.assorted.mobs.client.render.model;

import com.grim3212.assorted.mobs.client.render.entity.AmphibiousRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * The sea otter, three ways: loping along the shore, rippling through the water with its legs swept
 * back, and rolled over on its back at the surface with its paws on its chest. Each is blended in
 * by how far into it the otter is, so it rolls over and rights itself rather than snapping between them.
 */
public class SeaOtterModel extends EntityModel<AmphibiousRenderState> {

    private final ModelPart body;
    private final ModelPart hips;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart tailTip;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;

    public SeaOtterModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.hips = this.body.getChild("hips");
        this.head = this.body.getChild("head");
        this.tail = this.hips.getChild("tail");
        this.tailTip = this.tail.getChild("tail_tip");
        this.leftFrontLeg = this.body.getChild("left_front_leg");
        this.rightFrontLeg = this.body.getChild("right_front_leg");
        this.leftHindLeg = this.hips.getChild("left_hind_leg");
        this.rightHindLeg = this.hips.getChild("right_hind_leg");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Pivoted at its middle, so it rolls over and pitches into a dive in place.
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.5F, -7.0F, 6.0F, 5.0F, 8.0F), PartPose.offset(0.0F, 19.5F, 0.0F));
        PartDefinition hips = body.addOrReplaceChild("hips", CubeListBuilder.create().texOffs(28, 0).addBox(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 7.0F, new CubeDeformation(-0.02F)), PartPose.ZERO);
        PartDefinition tail = hips.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(30, 12).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 6.0F), PartPose.offset(0.0F, 0.5F, 7.0F));
        tail.addOrReplaceChild("tail_tip", CubeListBuilder.create().texOffs(48, 12).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 5.0F), PartPose.offset(0.0F, 0.0F, 6.0F));
        hips.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-1.0F, 0.0F, -1.5F, 2.0F, 2.0F, 3.0F), PartPose.offset(2.0F, 2.5F, 5.0F));
        hips.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-1.0F, 0.0F, -1.5F, 2.0F, 2.0F, 3.0F), PartPose.offset(-2.0F, 2.5F, 5.0F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 13).addBox(-2.5F, -2.5F, -4.0F, 5.0F, 4.0F, 4.0F), PartPose.offset(0.0F, -1.0F, -7.0F));
        head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(18, 13).addBox(-1.5F, -0.5F, -1.0F, 3.0F, 2.0F, 1.0F), PartPose.offset(0.0F, 0.0F, -4.0F));
        head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(26, 13).addBox(0.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F), PartPose.offset(2.5F, -2.0F, -1.5F));
        head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(26, 13).addBox(-1.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F), PartPose.offset(-2.5F, -2.0F, -1.5F));
        body.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(18, 17).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F), PartPose.offset(2.0F, 2.5F, -5.0F));
        body.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(18, 17).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F), PartPose.offset(-2.0F, 2.5F, -5.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(AmphibiousRenderState state) {
        super.setupAnim(state);

        float age = state.ageInTicks;
        float floating = state.floatAmount;
        // Settled on its back it is in the water whatever else is true.
        float water = Math.max(state.waterAmount, floating);
        this.walk(state, 1.0F - water);
        this.dive(state, water * (1.0F - floating));
        this.floatOnBack(age, floating);
    }

    private void walk(AmphibiousRenderState state, float land) {
        float pos = state.walkAnimationPos;
        float speed = state.walkAnimationSpeed;
        float swing = Mth.cos(pos * 0.9327F) * 1.1F * speed * land;
        this.leftFrontLeg.xRot = swing;
        this.rightHindLeg.xRot = swing;
        this.rightFrontLeg.xRot = -swing;
        this.leftHindLeg.xRot = -swing;
        // A lope: up off the ground once a stride.
        this.body.y -= land * Math.abs(Mth.sin(pos * 0.9327F)) * 0.6F * speed;

        // Dragged low behind it, curling level again at the tip.
        this.tail.xRot = land * (-0.38F + Mth.cos(pos * 0.6662F) * 0.08F * speed);
        this.tailTip.xRot = land * 0.3F;
        this.tail.yRot = land * Mth.sin(state.ageInTicks * 0.08F) * 0.12F;
        this.head.yRot = state.yRot * Mth.DEG_TO_RAD * land;
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD * land;
    }

    /** A wave run down it from nose to tail, harder the faster it goes, with the legs swept back out of the way. */
    private void dive(AmphibiousRenderState state, float dive) {
        float stroke = state.ageInTicks * 0.35F;
        float power = 0.35F + 0.65F * Math.min(state.walkAnimationSpeed, 1.0F);
        this.body.xRot += dive * (state.swimPitch + Mth.sin(stroke) * 0.06F * power);
        // Held a little against the dive, so it looks where it is going and not at its feet.
        this.head.xRot += dive * (-state.swimPitch * 0.35F - Mth.sin(stroke - 0.4F) * 0.1F * power);
        this.hips.xRot += dive * Mth.sin(stroke - 0.8F) * 0.22F * power;
        this.tail.xRot += dive * Mth.sin(stroke - 1.2F) * 0.35F * power;
        this.tailTip.xRot += dive * Mth.sin(stroke - 2.2F) * 0.45F * power;

        this.leftFrontLeg.xRot += dive * 1.35F;
        this.rightFrontLeg.xRot += dive * 1.35F;
        this.leftFrontLeg.zRot -= dive * 0.15F;
        this.rightFrontLeg.zRot += dive * 0.15F;
        this.leftHindLeg.xRot += dive * (1.2F + Mth.sin(stroke + 0.5F) * 0.35F * power);
        this.rightHindLeg.xRot += dive * (1.2F + Mth.sin(stroke - 0.5F) * 0.35F * power);
    }

    /** Rolled right over, bobbing, head up out of the water, hind feet paddling, and its paws at work on a shell. */
    private void floatOnBack(float age, float floating) {
        this.body.zRot = floating * (Mth.PI + Mth.sin(age * 0.07F) * 0.06F);
        this.body.y += floating * (0.6F + Mth.sin(age * 0.09F) * 0.35F);
        this.head.xRot += floating * (0.75F + Mth.sin(age * 0.05F) * 0.08F);
        // Upside down its neck is at the bottom of it: lifted, or it floats with its face under.
        this.head.y += floating * 2.0F;
        this.hips.xRot += floating * 0.08F;

        // Taps in bursts, with a rest between.
        float tap = Mth.sin(age * 0.045F) > 0.3F ? Math.max(0.0F, Mth.sin(age * 0.9F)) : 0.0F;
        this.leftFrontLeg.zRot += floating * (1.25F - tap * 0.5F);
        this.rightFrontLeg.zRot -= floating * (1.25F - tap * 0.5F);
        this.leftFrontLeg.xRot -= floating * 0.2F;
        this.rightFrontLeg.xRot -= floating * 0.2F;

        float paddle = Mth.sin(age * 0.18F) * 0.3F;
        this.leftHindLeg.xRot += floating * (0.55F + paddle);
        this.rightHindLeg.xRot += floating * (0.55F - paddle);
        this.tail.xRot += floating * (0.15F + Mth.sin(age * 0.09F) * 0.06F);
        this.tailTip.xRot += floating * 0.1F;
        this.tail.yRot += floating * Mth.sin(age * 0.11F) * 0.2F;
    }
}
