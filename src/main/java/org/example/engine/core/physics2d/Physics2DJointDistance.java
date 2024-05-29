package org.example.engine.core.physics2d;

public class Physics2DJointDistance extends Physics2DJoint {

    public float d;

    Physics2DJointDistance(Physics2DBody body_a, Physics2DBody body_b, float d) {
        super(body_a, body_b);
        this.d = d;
    }

}
