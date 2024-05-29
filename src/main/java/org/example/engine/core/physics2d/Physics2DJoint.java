package org.example.engine.core.physics2d;

public abstract class Physics2DJoint {

    public Physics2DBody body_a;
    public Physics2DBody body_b;

    Physics2DJoint(Physics2DBody body_a, Physics2DBody body_b) {
        this.body_a = body_a;
        this.body_b = body_b;
    }

}
