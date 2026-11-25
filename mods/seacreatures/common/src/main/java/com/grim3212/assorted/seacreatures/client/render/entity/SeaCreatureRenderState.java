package com.grim3212.assorted.seacreatures.client.render.entity;

/**
 * What a sea creature's model draws from, gathered once a frame. 1.21.1 hands a model the entity and these few numbers
 * rather than a render state, so each model keeps one of these and fills it as it is set up.
 */
public class SeaCreatureRenderState {
    /** Ticks it has lived, with the frame's partial tick. */
    public float ageInTicks;
    /** How far along its walk cycle it is. */
    public float walkAnimationPos;
    /** How fast it is walking, 0 to 1. */
    public float walkAnimationSpeed;
    /** Its head's turn from its body, in degrees. */
    public float yRot;
    /** Its head's pitch, in degrees. */
    public float xRot;

    /** Takes what {@code EntityModel#setupAnim} is handed. */
    public void setUp(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.walkAnimationPos = limbSwing;
        this.walkAnimationSpeed = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.yRot = netHeadYaw;
        this.xRot = headPitch;
    }
}
