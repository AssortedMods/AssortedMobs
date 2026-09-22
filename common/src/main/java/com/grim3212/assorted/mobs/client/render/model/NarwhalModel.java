package com.grim3212.assorted.mobs.client.render.model;

import com.grim3212.assorted.mobs.client.render.entity.NarwhalRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * The narwhal at its own size: a whale's body in three lengths behind a long spiralled tusk. A wave
 * runs down it from head to flukes, up and down as a whale's does, harder the faster it goes. It
 * pitches with its dives, leans into its turns, and stands on its tail to raise its tusk.
 */
public class NarwhalModel extends EntityModel<NarwhalRenderState> {

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart tailEnd;
    private final ModelPart flukes;
    private final ModelPart leftFlipper;
    private final ModelPart rightFlipper;

    public NarwhalModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.tail = this.body.getChild("tail");
        this.tailEnd = this.tail.getChild("tail_end");
        this.flukes = this.tailEnd.getChild("flukes");
        this.leftFlipper = this.body.getChild("left_flipper");
        this.rightFlipper = this.body.getChild("right_flipper");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Pivoted at its middle, so it pitches into a dive and leans into a turn in place.
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -5.5F, -12.0F, 12.0F, 11.0F, 20.0F).texOffs(64, 0).addBox(-1.0F, -6.5F, -6.0F, 2.0F, 1.0F, 12.0F), PartPose.offset(0.0F, 17.0F, 0.0F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 31).addBox(-5.0F, -4.5F, -8.0F, 10.0F, 9.0F, 8.0F).texOffs(92, 0).addBox(-4.0F, -6.0F, -7.0F, 8.0F, 2.0F, 5.0F), PartPose.offset(0.0F, 0.5F, -12.0F));
        head.addOrReplaceChild("tusk", CubeListBuilder.create().texOffs(36, 31).addBox(-0.5F, -0.5F, -26.0F, 1.0F, 1.0F, 26.0F), PartPose.offset(0.0F, 2.0F, -8.0F));
        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 48).addBox(-4.5F, -4.0F, 0.0F, 9.0F, 8.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 8.0F));
        PartDefinition tailEnd = tail.addOrReplaceChild("tail_end", CubeListBuilder.create().texOffs(90, 31).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 9.0F), PartPose.offset(0.0F, 0.5F, 8.0F));
        tailEnd.addOrReplaceChild("flukes", CubeListBuilder.create().texOffs(64, 13).addBox(-7.0F, -0.5F, 0.0F, 14.0F, 1.0F, 6.0F), PartPose.offset(0.0F, 0.0F, 8.0F));
        body.addOrReplaceChild("left_flipper", CubeListBuilder.create().texOffs(92, 8).addBox(0.0F, -0.5F, -2.0F, 6.0F, 1.0F, 4.0F), PartPose.offset(6.0F, 3.5F, -8.0F));
        body.addOrReplaceChild("right_flipper", CubeListBuilder.create().texOffs(92, 8).addBox(-6.0F, -0.5F, -2.0F, 6.0F, 1.0F, 4.0F), PartPose.offset(-6.0F, 3.5F, -8.0F));

        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(NarwhalRenderState state) {
        super.setupAnim(state);

        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        float stroke = state.ageInTicks * 0.22F;
        // Never quite still: hanging in the water is the same stroke, only lazier.
        float power = 0.3F + 0.7F * speed;
        float tusk = state.tuskAmount;

        this.body.xRot = state.swimPitch + Mth.sin(stroke) * 0.04F * power - tusk * 1.05F;
        this.body.zRot = state.bank;
        // Held a little against the dive, so it looks where it is going.
        this.head.xRot = -state.swimPitch * 0.2F - Mth.sin(stroke - 0.3F) * 0.05F * power - tusk * 0.15F;
        // Stood up, its tail curls back under it to tread water.
        this.tail.xRot = Mth.sin(stroke - 0.9F) * 0.16F * power + tusk * 0.25F;
        this.tailEnd.xRot = Mth.sin(stroke - 1.8F) * 0.26F * power + tusk * 0.2F;
        this.flukes.xRot = Mth.sin(stroke - 2.7F) * 0.42F * power;

        // Flippers trimmed back at speed, feathering, and dipped on the inside of a turn.
        float sway = Mth.sin(stroke * 0.5F) * 0.1F;
        this.leftFlipper.zRot = 0.45F + sway - state.bank * 0.5F;
        this.rightFlipper.zRot = -0.45F + sway - state.bank * 0.5F;
        this.leftFlipper.yRot = -0.5F - 0.3F * speed;
        this.rightFlipper.yRot = 0.5F + 0.3F * speed;
    }
}
