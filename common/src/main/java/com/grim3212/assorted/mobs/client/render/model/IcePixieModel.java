package com.grim3212.assorted.mobs.client.render.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * The ice pixie: a body with its face on the front, stub arms and legs, and a pair of wings behind
 * that turn with it. The wings are a single flat plane, one side of the texture each way round.
 */
public class IcePixieModel extends EntityModel<LivingEntityRenderState> {

    /** How far out from the body's middle the arms hang. */
    private static final float ARM_SPREAD = 5.0F;

    private final ModelPart body;
    private final ModelPart wings;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public IcePixieModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.wings = root.getChild("wings");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 10.0F, -2.0F, 6.0F, 8.0F, 3.0F), PartPose.ZERO);
        root.addOrReplaceChild("wings", CubeListBuilder.create().texOffs(18, 0).addBox(-4.0F, 4.2F, 0.5F, 8.0F, 6.0F, 0.0F), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 11).addBox(0.0F, 10.0F, -2.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(-ARM_SPREAD, 1.0F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 11).mirror().addBox(-2.0F, 10.0F, -2.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(ARM_SPREAD, 1.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 17).mirror().addBox(-1.0F, 6.0F, -2.0F, 3.0F, 6.0F, 3.0F), PartPose.offset(1.0F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 17).addBox(-2.0F, 6.0F, -2.0F, 3.0F, 6.0F, 3.0F), PartPose.offset(-1.0F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        super.setupAnim(state);

        float swing = state.walkAnimationPos;
        float amount = state.walkAnimationSpeed;
        // The whole pixie turns to look, rather than a head on its own.
        float turn = state.yRot * Mth.DEG_TO_RAD;

        this.body.yRot = turn;
        this.wings.yRot = turn;

        this.rightArm.xRot = Mth.cos(swing * 0.6662F + Mth.PI) * 0.3F * amount;
        this.leftArm.xRot = Mth.cos(swing * 0.6662F) * 0.4F * amount;
        this.rightArm.yRot = turn;
        this.leftArm.yRot = turn;
        this.rightArm.x = -Mth.cos(turn) * ARM_SPREAD;
        this.rightArm.z = Mth.sin(turn) * ARM_SPREAD;
        this.leftArm.x = Mth.cos(turn) * ARM_SPREAD;
        this.leftArm.z = -Mth.sin(turn) * ARM_SPREAD;

        this.rightLeg.xRot = Mth.cos(swing * 0.6662F) * 0.4F * amount;
        this.leftLeg.xRot = Mth.cos(swing * 0.6662F + Mth.PI) * 0.4F * amount;
        this.rightLeg.yRot = turn;
        this.leftLeg.yRot = turn;
    }
}
