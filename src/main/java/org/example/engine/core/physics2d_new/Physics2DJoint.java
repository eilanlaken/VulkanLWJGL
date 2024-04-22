package org.example.engine.core.physics2d_new;

import org.example.engine.core.physics2d.Physics2DBody;

public abstract class Physics2DJoint {

    public final Physics2DBody a;
    public final Physics2DBody b;

    public Physics2DJoint(final Physics2DBody a, final Physics2DBody b) {
        this.a = a;
        this.b = b;
    }

}
