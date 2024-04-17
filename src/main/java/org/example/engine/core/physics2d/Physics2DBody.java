package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.shape.Shape2D;
import org.example.engine.core.math.MathVector2;

public final class Physics2DBody {

    public Object owner;
    public boolean active;

    public Type type;
    public Shape2D shape;
    public MathVector2 velocity;
    public float angularVelocity;
    public CollectionsArray<MathVector2> forces;

    public float massInv;
    public float density;
    public float friction;
    public float restitution;
    public boolean ghost;
    public int bitmask;

    // todo: change to protected.
    public Physics2DBody(Object owner, boolean active, Type type, Shape2D shape, MathVector2 position, float angle, MathVector2 velocity, float angularVelocity, float density, float friction, float restitution, boolean ghost, int bitmask) {
        this.owner = owner;
        this.active = active;
        this.type = type;
        this.shape = shape;
        shape.setTransform(position.x, position.y, angle, 1,1);
        this.velocity = new MathVector2(velocity);
        this.angularVelocity = angularVelocity;
        this.forces = new CollectionsArray<>(false, 2);
        this.massInv = 1f / Physics2DUtils.calculateMass(shape, density);
        this.density = density;
        this.friction = friction;
        this.restitution = restitution;
        this.ghost = ghost;
        this.bitmask = bitmask;
    }

    public Physics2DBody(Shape2D shape, MathVector2 position, float angle, MathVector2 velocity) {
        this.owner = null;
        this.active = true;
        this.type = Type.DYNAMIC;
        this.shape = shape;
        this.shape.setTransform(position.x, position.y, angle, 1, 1);
        this.velocity = new MathVector2(velocity);
        this.forces = new CollectionsArray<>(false, 2);
    }

    public void setPosition(float x, float y) {
        shape.xy(x, y);
    }

    public void setVelocity(float x, float y) {

    }

    public void dx_dy_rot(float dx, float dy, float da) {
        shape.dx_dy_rot(dx, dy, da);
    }

    public enum Type {
        STATIC,
        KINEMATIC,
        DYNAMIC
    }

}
