package org.example.engine.core.physics2d;

public abstract class Physics2DJoint {

    public final Physics2DBody a;
    public final Physics2DBody b;

    public Physics2DJoint(final Physics2DBody a, final Physics2DBody b) {
        this.a = a;
        this.b = b;
    }

}
