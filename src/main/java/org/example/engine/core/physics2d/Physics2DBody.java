package org.example.engine.core.physics2d;

import org.example.engine.core.math.Shape2D;
import org.example.engine.core.math.Vector2;

public class Physics2DBody {

    public Object owner;
    public boolean active;

    public Vector2 position;
    public float angle;
    public Vector2 velocity;
    public float angularVelocity;

    public Shape2D shape;
    public float density;
    public float friction;
    public float restitution;
    public boolean sensor;
    public int bitmask;

    protected Physics2DBody() {

    }

    public enum Type {
        STATIC,
        KINEMATIC,
        DYNAMIC
    }

}
