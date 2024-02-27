package org.example.engine.core.physics2d;

import org.example.engine.core.math.Shape2D;
import org.example.engine.core.math.Vector2;

public class Body2D {

    public Object owner;
    public boolean active;
    public Vector2 position;
    public float angle;
    public Vector2 velocity;
    public float angularVelocity;
    public Shape2D shape;

    protected Body2D() {

    }

}
