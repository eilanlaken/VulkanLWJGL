package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.physics2d.Physics2DUtils;
import org.example.engine.core.shape.Shape2D;

public class Physics2DBody implements MemoryPool.Reset {

    public    Object      owner;
    protected boolean     created; // TODO: in the insert to world, this will be changed to true.
    public    boolean     sleeping;
    public    Type        type;
    public    Shape2D     shape;
    public    MathVector2 velocity;
    public    float       angularVelocity;

    public CollectionsArray<MathVector2>         forces      = new CollectionsArray<>(false, 2);
    public CollectionsArray<Physics2DConstraint> constraints = new CollectionsArray<>(false, 1);
    public CollectionsArray<Physics2DJoint>      joints      = new CollectionsArray<>(false, 1);

    public float   massInv;
    public float   density;
    public float   friction;
    public float   restitution;
    public boolean ghost;
    public int     bitmask;

    public Physics2DBody() {}

    // TODO: use only this all args constructor from the world.
    // TODO: make protected.
    public Physics2DBody(Object owner,
                         boolean sleeping, Type type, Shape2D shape,
                         MathVector2 velocity, float angularVelocity,
                         float massInv, float density, float friction, float restitution,
                         boolean ghost, int bitmask) {
        this.owner = owner;
        this.created = false;
        this.sleeping = sleeping;
        this.type = type;
        this.shape = shape;
        this.velocity = velocity;
        this.angularVelocity = angularVelocity;
        this.massInv = massInv;
        this.density = density;
        this.friction = friction;
        this.restitution = restitution;
        this.ghost = ghost;
        this.bitmask = bitmask;
    }

    // todo: remove.
    public Physics2DBody(Object owner, boolean sleeping, Type type, Shape2D shape, MathVector2 position, float angle, MathVector2 velocity, float angularVelocity, float density, float friction, float restitution, boolean ghost, int bitmask) {
        this.owner = owner;
        this.created = false;
        this.sleeping = sleeping;
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

    public void setPosition(float x, float y) {
        shape.xy(x, y);
    }

    public void setVelocity(float x, float y) {
        velocity.set(x, y);
    }

    public enum Type {
        STATIC,
        KINEMATIC,
        DYNAMIC
    }

    @Override
    public void reset() {
        this.owner = null;
        this.created = false;
        this.sleeping = false;
        this.type = null;
        this.shape = null;
        this.velocity.set(0, 0);
        this.angularVelocity = 0;
        this.forces.clear();
        this.constraints.clear();
        this.joints.clear();
        this.massInv = 0;
        this.density = 0;
        this.friction = 0;
        this.restitution = 0;
        this.ghost = false;
        this.bitmask = 0;
    }
}
