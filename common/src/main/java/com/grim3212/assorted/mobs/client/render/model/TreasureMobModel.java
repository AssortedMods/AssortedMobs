package com.grim3212.assorted.mobs.client.render.model;

import com.grim3212.assorted.mobs.client.render.entity.TreasureMobRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** A chest whose lid hangs open like a mouth, and shuts while it sits. */
public class TreasureMobModel extends EntityModel<TreasureMobRenderer.State> {

    private static final float LID_Z = -7.5F;
    private static final float OPEN = 0.5934119F;
    /** A shut lid sits a little further back, flush with the base. */
    private static final float SHUT_SHIFT = 1.6F;

    private final ModelPart lid;

    public TreasureMobModel(ModelPart root) {
        super(root);
        this.lid = root.getChild("chest").getChild("lid");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition chest = mesh.getRoot().addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.76F, 0.0F, 0.0F, Mth.PI, 0.0F));

        chest.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 17).addBox(0.0F, 0.0F, 0.0F, 11.0F, 4.0F, 11.0F), PartPose.offset(-6.0F, 13.0F, LID_Z));
        chest.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 11.0F, 6.0F, 11.0F), PartPose.offset(-6.0F, 16.0F, -6.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(TreasureMobRenderer.State state) {
        super.setupAnim(state);

        if (state.isSitting) {
            this.lid.xRot = 0.0F;
            this.lid.z = LID_Z + SHUT_SHIFT;
        } else {
            this.lid.xRot = OPEN;
            this.lid.z = LID_Z;
        }
    }
}
