package com.grim3212.assorted.mobs.client.render.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/** The seal, at its own size: a sleek body in three lengths, so it can arch its chest up ashore and scull with its hips afloat. */
public class SealModel extends PinnipedModel {

    public SealModel(ModelPart root) {
        super(root, 1.0F);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -3.5F, -5.5F, 8.0F, 7.0F, 11.0F), PartPose.offset(0.0F, 20.5F, 0.0F));
        PartDefinition chest = body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(38, 0).addBox(-3.5F, -3.0F, -5.0F, 7.0F, 6.0F, 5.0F), PartPose.offset(0.0F, 0.5F, -5.5F));
        PartDefinition head = chest.addOrReplaceChild("head", CubeListBuilder.create().texOffs(38, 11).addBox(-3.0F, -2.5F, -5.0F, 6.0F, 5.0F, 5.0F), PartPose.offset(0.0F, -0.5F, -5.0F));
        head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(22, 24).addBox(-2.0F, -0.5F, -1.0F, 4.0F, 3.0F, 1.0F), PartPose.offset(0.0F, 0.0F, -5.0F));
        PartDefinition hips = body.addOrReplaceChild("hips", CubeListBuilder.create().texOffs(0, 18).addBox(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 5.0F), PartPose.offset(0.0F, 0.5F, 5.5F));
        hips.addOrReplaceChild("left_tail_flipper", CubeListBuilder.create().texOffs(22, 18).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 5.0F), PartPose.offsetAndRotation(1.5F, 1.5F, 5.0F, 0.0F, 0.25F, 0.0F));
        hips.addOrReplaceChild("right_tail_flipper", CubeListBuilder.create().texOffs(22, 18).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 5.0F), PartPose.offsetAndRotation(-1.5F, 1.5F, 5.0F, 0.0F, -0.25F, 0.0F));
        body.addOrReplaceChild("left_flipper", CubeListBuilder.create().texOffs(0, 28).addBox(0.0F, -0.5F, -1.5F, 5.0F, 1.0F, 3.0F), PartPose.offset(4.0F, 2.0F, -3.5F));
        body.addOrReplaceChild("right_flipper", CubeListBuilder.create().texOffs(0, 28).addBox(-5.0F, -0.5F, -1.5F, 5.0F, 1.0F, 3.0F), PartPose.offset(-4.0F, 2.0F, -3.5F));

        return LayerDefinition.create(mesh, 64, 32);
    }
}
