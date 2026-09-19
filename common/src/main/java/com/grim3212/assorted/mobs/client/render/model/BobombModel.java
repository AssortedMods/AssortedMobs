package com.grim3212.assorted.mobs.client.render.model;

import com.grim3212.assorted.mobs.client.render.entity.BobombRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** A round bomb on two feet, a fuse on top and a wind-up key turning at its back. */
public class BobombModel extends EntityModel<BobombRenderer.State> {

    /** Radians the key turns each tick. */
    private static final float KEY_SPEED = 0.25F;

    private final ModelPart keyShaft;
    private final ModelPart keyGrip;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightFoot;
    private final ModelPart leftFoot;

    public BobombModel(ModelPart root) {
        super(root);
        this.keyShaft = root.getChild("key_shaft");
        this.keyGrip = root.getChild("key_grip");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.rightFoot = root.getChild("right_foot");
        this.leftFoot = root.getChild("left_foot");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        CubeDeformation thin = new CubeDeformation(-0.3F);
        CubeDeformation slim = new CubeDeformation(-0.2F);

        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(1, 14).addBox(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F), PartPose.offset(0.0F, 15.0F, 0.0F));
        root.addOrReplaceChild("top", CubeListBuilder.create().texOffs(24, 0).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 2.0F, 5.0F), PartPose.offset(0.0F, 9.0F, 0.0F));
        root.addOrReplaceChild("fuse", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -5.0F, 0.0F, 7.0F, 7.0F, 1.0F, new CubeDeformation(-0.5F)), PartPose.offset(-0.63F, 7.5F, -0.5F));
        root.addOrReplaceChild("key_shaft", CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, thin), PartPose.offset(0.0F, 15.0F, 4.2F));
        root.addOrReplaceChild("key_grip", CubeListBuilder.create().texOffs(48, 16).addBox(-0.3F, -5.0F, 0.0F, 2.0F, 10.0F, 6.0F, thin), PartPose.offset(0.0F, 15.0F, 5.2F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 8).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, thin), PartPose.offset(-2.3F, 17.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 8).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, thin), PartPose.offset(2.3F, 17.0F, 0.0F));
        root.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(44, 0).addBox(-2.0F, 4.0F, -4.0F, 4.0F, 2.0F, 6.0F, slim), PartPose.offset(-2.3F, 17.5F, 0.0F));
        root.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(44, 0).addBox(-2.0F, 4.0F, -4.0F, 4.0F, 2.0F, 6.0F, slim), PartPose.offset(2.3F, 17.5F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(BobombRenderer.State state) {
        super.setupAnim(state);

        float swing = state.walkAnimationPos;
        float amount = state.walkAnimationSpeed;
        this.leftLeg.xRot = Mth.cos(swing * 1.9662F + Mth.PI) * amount;
        this.rightLeg.xRot = Mth.cos(swing * 1.9662F) * amount;
        this.leftFoot.xRot = this.leftLeg.xRot;
        this.rightFoot.xRot = this.rightLeg.xRot;

        this.keyShaft.zRot = state.ageInTicks * KEY_SPEED;
        this.keyGrip.zRot = this.keyShaft.zRot;
    }
}
