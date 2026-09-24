package com.grim3212.assorted.mobs.client.render.model;

import com.grim3212.assorted.mobs.client.render.entity.ParabuzzyRenderer;
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
 * A shell on four legs with a pair of wings that beat without stopping. Sitting drops the shell to
 * the ground on splayed legs and lets the wings hang still, so a sit order reads from across a field.
 * The spikes are always there; the plain colours' textures leave them clear. The textures are twice
 * the resolution these UVs are laid out for.
 */
public class ParabuzzyModel extends EntityModel<ParabuzzyRenderer.State> {

    /** Enough to put the belly on the ground, the legs no longer holding it up. */
    private static final float SIT_DROP = 4.0F;
    /** As far as the wings hang before their tips reach the ground. */
    private static final float SIT_WING_DROOP = 0.85F;

    private final ModelPart head;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLeftLeg;
    private final ModelPart frontRightLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart backRightLeg;

    public ParabuzzyModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.rightWing = root.getChild("right_wing");
        this.leftWing = root.getChild("left_wing");
        this.frontLeftLeg = root.getChild("front_left_leg");
        this.frontRightLeg = root.getChild("front_right_leg");
        this.backLeftLeg = root.getChild("back_left_leg");
        this.backRightLeg = root.getChild("back_right_leg");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        CubeDeformation spike = new CubeDeformation(-0.2F);
        CubeListBuilder leg = CubeListBuilder.create().texOffs(52, 25).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(-0.2F));

        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, 14.49F, 0.0F));
        root.addOrReplaceChild("belly", CubeListBuilder.create().texOffs(22, 0).addBox(-2.0F, 0.0F, -1.5F, 4.0F, 1.0F, 3.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, 18.0F, 0.5F));
        root.addOrReplaceChild("spike", CubeListBuilder.create().texOffs(37, 11).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 7.0F, 1.0F, spike), PartPose.offset(0.24F, 5.1F, -0.15F));
        root.addOrReplaceChild("crossed_spike", CubeListBuilder.create().texOffs(37, 11).addBox(-3.5F, 0.0F, 0.0F, 7.0F, 7.0F, 1.0F, spike), PartPose.offsetAndRotation(-0.24F, 5.1F, -0.2F, 0.0F, Mth.HALF_PI, 0.0F));
        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 10).addBox(-2.5F, -1.5F, -1.5F, 5.0F, 3.0F, 3.0F), PartPose.offset(0.0F, 19.0F, -2.5F));
        root.addOrReplaceChild("shell_rim", CubeListBuilder.create().texOffs(28, 0).addBox(-4.5F, -1.0F, -4.0F, 9.0F, 2.0F, 8.0F), PartPose.offset(0.0F, 18.5F, 0.5F));
        root.addOrReplaceChild("shell_front", CubeListBuilder.create().texOffs(0, 27).addBox(-4.5F, -1.0F, -3.0F, 9.0F, 2.0F, 3.0F), PartPose.offset(0.0F, 16.5F, -1.6F));
        root.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(14, 16).addBox(0.0F, 0.0F, -3.5F, 7.0F, 1.0F, 7.0F), PartPose.offset(4.0F, 14.0F, 0.0F));
        root.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 16).addBox(-7.0F, 0.0F, -3.5F, 7.0F, 1.0F, 7.0F), PartPose.offset(-4.0F, 14.0F, 0.0F));
        root.addOrReplaceChild("front_left_leg", leg, PartPose.offset(2.4F, 18.0F, -1.5F));
        root.addOrReplaceChild("front_right_leg", leg, PartPose.offset(-2.4F, 18.0F, -1.5F));
        root.addOrReplaceChild("back_left_leg", leg, PartPose.offset(2.4F, 18.0F, 2.2F));
        root.addOrReplaceChild("back_right_leg", leg, PartPose.offset(-2.4F, 18.0F, 2.2F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(ParabuzzyRenderer.State state) {
        super.setupAnim(state);

        this.head.xRot = state.xRot / 100.0F;
        this.head.yRot = state.yRot / 650.0F;

        if (state.isSitting) {
            this.root().y = SIT_DROP;
            this.leftWing.zRot = SIT_WING_DROOP;
            this.rightWing.zRot = -SIT_WING_DROOP;
            this.frontLeftLeg.xRot = -0.5F;
            this.frontRightLeg.xRot = -0.5F;
            this.backLeftLeg.xRot = 0.5F;
            this.backRightLeg.xRot = 0.5F;
            this.frontLeftLeg.zRot = -1.3F;
            this.frontRightLeg.zRot = 1.3F;
            this.backLeftLeg.zRot = -1.3F;
            this.backRightLeg.zRot = 1.3F;
            return;
        }

        this.leftWing.zRot = Mth.cos(state.ageInTicks + Mth.PI);
        this.rightWing.zRot = Mth.cos(state.ageInTicks);

        float step = Mth.cos(state.walkAnimationPos * 0.6662F) * 0.4F * state.walkAnimationSpeed;
        this.frontLeftLeg.xRot = step;
        this.frontRightLeg.xRot = step;
        this.backLeftLeg.xRot = step;
        this.backRightLeg.xRot = step;
        this.frontLeftLeg.zRot = 0.2F;
        this.frontRightLeg.zRot = -0.2F;
        this.backLeftLeg.zRot = 0.2F;
        this.backRightLeg.zRot = -0.2F;
    }
}
