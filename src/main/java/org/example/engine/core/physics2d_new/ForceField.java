package org.example.engine.core.physics2d_new;

import org.example.engine.core.math.Vector2;

public abstract class ForceField {

    protected final World world;

    protected ForceField(World world) {
        this.world = world;
    }

    public abstract void calculateForce(Body body, Vector2 out);

}
