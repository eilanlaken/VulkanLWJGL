package org.example.engine.core.physics2d;

import org.example.engine.core.math.MathVector2;

public abstract class Physics2DForceField {

    protected final Physics2DWorld world;

    protected Physics2DForceField(Physics2DWorld world) {
        this.world = world;
    }

    public abstract void calculateForce(Physics2DBody body, MathVector2 out);

}
