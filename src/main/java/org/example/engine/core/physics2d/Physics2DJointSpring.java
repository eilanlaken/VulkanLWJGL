package org.example.engine.core.physics2d;

public class Physics2DJointSpring extends Physics2DJoint {

    public float k;
    public float l;

    Physics2DJointSpring(Physics2DBody body_a, Physics2DBody body_b, float k, float l) {
        super(body_a, body_b);
        this.k = k;
        this.l = l;
    }

}
