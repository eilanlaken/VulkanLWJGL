package org.example.engine.core.physics2d_new;

import org.example.engine.core.math.Vector2;
import org.example.engine.core.physics2d.Body;
import org.example.engine.core.physics2d.World;

public abstract class ForceField {

    protected final World world;

    protected ForceField(World world) {
        this.world = world;
    }

    public abstract void calculateForce(Body body, Vector2 out);

}
