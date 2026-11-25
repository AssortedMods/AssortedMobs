package com.grim3212.assorted.seacreatures.client.render.entity;

import com.grim3212.assorted.seacreatures.common.entity.AmphibiousAnimal;
import com.grim3212.assorted.seacreatures.common.entity.SeaOtter;
import net.minecraft.util.Mth;

public class AmphibiousRenderState extends SeaCreatureRenderState {
    /** 0 on land to 1 afloat. */
    public float waterAmount;
    /** The pitch it swims at, in radians; nose down is positive. */
    public float swimPitch;
    /** 0 the right way up to 1 on its back. Only the sea otter's is ever more than 0. */
    public float floatAmount;
    /** 0 to 1 through a swing of its tusks. Only the walrus swings. */
    public float attackAnim;

    /** What the animal itself says, eased to the frame. */
    public void extract(AmphibiousAnimal entity, float partialTicks) {
        this.waterAmount = entity.getWaterAmount(partialTicks);
        this.swimPitch = entity.getSwimPitch(partialTicks) * Mth.DEG_TO_RAD;
        this.floatAmount = entity instanceof SeaOtter otter ? otter.getFloatAmount(partialTicks) : 0.0F;
        this.attackAnim = entity.getAttackAnim(partialTicks);
    }
}
