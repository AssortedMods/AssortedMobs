package com.grim3212.assorted.mobs.client.render.entity;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class NarwhalRenderState extends LivingEntityRenderState {
    /** The pitch it swims at, in radians; nose down is positive. */
    public float swimPitch;
    /** How far it leans into a turn, in radians. */
    public float bank;
    /** 0 swimming to 1 stood on its tail with its tusk in the air. */
    public float tuskAmount;
}
