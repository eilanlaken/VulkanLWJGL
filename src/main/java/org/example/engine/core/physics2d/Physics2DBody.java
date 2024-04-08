package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Shape2D;
import org.example.engine.core.math.Vector2;

public final class Physics2DBody {

    public Object owner;
    public boolean active;

    public Type type;
    public Shape2D shape;
    public Vector2 velocity;
    public float angularVelocity;
    public Array<Vector2> forces;

    public float massInv;
    public float density;
    public float friction;
    public float restitution;
    public boolean ghost;
    public int bitmask;

    // todo: change to protected.
    public Physics2DBody(Object owner, boolean active, Type type, Shape2D shape, Vector2 position, Vector2 velocity, float angularVelocity, float density, float friction, float restitution, boolean ghost, int bitmask) {
        this.owner = owner;
        this.active = active;
        this.type = type;
        this.shape = shape;
        shape.x(position.x);
        shape.y(position.y);
        this.velocity = new Vector2(velocity);
        this.angularVelocity = angularVelocity;
        this.forces = new Array<>(false, 2);
        this.massInv = 1f / Physics2DUtils.calculateMass(shape, density);
        this.density = density;
        this.friction = friction;
        this.restitution = restitution;
        this.ghost = ghost;
        this.bitmask = bitmask;
    }

    public Physics2DBody(Shape2D shape, Vector2 position, Vector2 velocity) {
        this.owner = null;
        this.active = true;
        this.type = Type.DYNAMIC;
        this.shape = shape;
        this.shape.xy(position.x, position.y);
        this.velocity = new Vector2(velocity);
        this.forces = new Array<>(false, 2);
    }

    public enum Type {
        STATIC,
        KINEMATIC,
        DYNAMIC
    }

}
