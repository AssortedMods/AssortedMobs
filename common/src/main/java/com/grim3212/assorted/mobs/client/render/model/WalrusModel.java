package com.grim3212.assorted.mobs.client.render.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/** The walrus: the seal's build at several times the bulk and a good deal slower, with a whiskered muzzle and a pair of tusks. */
public class WalrusModel extends PinnipedModel {

    public WalrusModel(ModelPart root) {
        super(root, 1.6F);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -6.0F, -8.0F, 14.0F, 12.0F, 16.0F), PartPose.offset(0.0F, 18.0F, 0.0F));
        PartDefinition chest = body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(60, 0).addBox(-6.0F, -5.5F, -7.0F, 12.0F, 11.0F, 7.0F), PartPose.offset(0.0F, 0.5F, -8.0F));
        PartDefinition head = chest.addOrReplaceChild("head", CubeListBuilder.create().texOffs(60, 18).addBox(-4.5F, -4.0F, -7.0F, 9.0F, 8.0F, 7.0F), PartPose.offset(0.0F, -1.5F, -7.0F));
        head.addOrReplaceChild("muzzle", CubeListBuilder.create().texOffs(92, 18).addBox(-3.5F, -1.0F, -2.0F, 7.0F, 5.0F, 2.0F), PartPose.offset(0.0F, 0.0F, -7.0F));
        head.addOrReplaceChild("left_tusk", CubeListBuilder.create().texOffs(110, 18).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 6.0F, 1.0F), PartPose.offset(2.5F, 3.0F, -8.0F));
        head.addOrReplaceChild("right_tusk", CubeListBuilder.create().texOffs(110, 18).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 6.0F, 1.0F), PartPose.offset(-2.5F, 3.0F, -8.0F));
        PartDefinition hips = body.addOrReplaceChild("hips", CubeListBuilder.create().texOffs(0, 28).addBox(-5.0F, -4.0F, 0.0F, 10.0F, 8.0F, 7.0F), PartPose.offset(0.0F, 1.0F, 8.0F));
        hips.addOrReplaceChild("left_tail_flipper", CubeListBuilder.create().texOffs(34, 28).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 7.0F), PartPose.offsetAndRotation(2.5F, 3.0F, 7.0F, 0.0F, 0.25F, 0.0F));
        hips.addOrReplaceChild("right_tail_flipper", CubeListBuilder.create().texOffs(34, 28).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 7.0F), PartPose.offsetAndRotation(-2.5F, 3.0F, 7.0F, 0.0F, -0.25F, 0.0F));
        body.addOrReplaceChild("left_flipper", CubeListBuilder.create().texOffs(34, 36).addBox(0.0F, -1.0F, -2.5F, 8.0F, 2.0F, 5.0F), PartPose.offset(7.0F, 3.5F, -5.0F));
        body.addOrReplaceChild("right_flipper", CubeListBuilder.create().texOffs(34, 36).addBox(-8.0F, -1.0F, -2.5F, 8.0F, 2.0F, 5.0F), PartPose.offset(-7.0F, 3.5F, -5.0F));

        return LayerDefinition.create(mesh, 128, 64);
    }
}
