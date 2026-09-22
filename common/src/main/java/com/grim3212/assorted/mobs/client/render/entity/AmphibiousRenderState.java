package com.grim3212.assorted.mobs.client.render.entity;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class AmphibiousRenderState extends LivingEntityRenderState {
    /** 0 on land to 1 afloat. */
    public float waterAmount;
    /** The pitch it swims at, in radians; nose down is positive. */
    public float swimPitch;
    /** 0 the right way up to 1 on its back. Only the sea otter's is ever more than 0. */
    public float floatAmount;
    /** 0 to 1 through a swing of its tusks. Only the walrus swings. */
    public float attackAnim;
}
