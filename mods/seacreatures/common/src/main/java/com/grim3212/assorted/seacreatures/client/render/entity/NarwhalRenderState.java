package com.grim3212.assorted.seacreatures.client.render.entity;

import com.grim3212.assorted.seacreatures.common.entity.Narwhal;
import net.minecraft.util.Mth;

public class NarwhalRenderState extends SeaCreatureRenderState {
    /** The pitch it swims at, in radians; nose down is positive. */
    public float swimPitch;
    /** How far it leans into a turn, in radians. */
    public float bank;
    /** 0 swimming to 1 stood on its tail with its tusk in the air. */
    public float tuskAmount;

    /** What the narwhal itself says, eased to the frame. */
    public void extract(Narwhal entity, float partialTicks) {
        this.swimPitch = entity.getSwimPitch(partialTicks) * Mth.DEG_TO_RAD;
        this.bank = entity.getBank(partialTicks);
        this.tuskAmount = entity.getTuskAmount(partialTicks);
    }
}
