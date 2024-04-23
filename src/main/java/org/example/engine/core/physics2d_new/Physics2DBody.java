package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.physics2d.Physics2DUtils;
import org.example.engine.core.shape.Shape2D;

public class Physics2DBody implements MemoryPool.Reset {

    public    Object      owner;
    protected boolean     created;
    public    boolean     off;
    public    MotionType  motionType;
    public    Shape2D     shape;
    public    MathVector2 velocity;
    public    float       angularVelocityDeg;

    public CollectionsArray<MathVector2>         forces      = new CollectionsArray<>(false, 2);
    public CollectionsArray<Physics2DConstraint> constraints = new CollectionsArray<>(false, 1);
    public CollectionsArray<Physics2DJoint>      joints      = new CollectionsArray<>(false, 1);

    public float   massInv;
    public float   density;
    public float   friction;
    public float   restitution;
    public boolean ghost;
    public int     bitmask;

    public Physics2DBody() {
        this.velocity = new MathVector2();
    }

    // TODO: use only this all args constructor from the world.
    // TODO: make protected.
    public Physics2DBody(Object owner,
                         MotionType motionType, Shape2D shape,
                         MathVector2 velocity, float angularVelocityDeg,
                         float massInv, float density, float friction, float restitution,
                         boolean ghost, int bitmask) {
        this.owner = owner;
        this.created = false;
        this.off = false;
        this.motionType = motionType;
        this.shape = shape;
        this.velocity = velocity;
        this.angularVelocityDeg = angularVelocityDeg;
        this.massInv = massInv;
        this.density = density;
        this.friction = friction;
        this.restitution = restitution;
        this.ghost = ghost;
        this.bitmask = bitmask;
    }

    // todo: remove.
    public Physics2DBody(Object owner, boolean off, MotionType motionType, Shape2D shape, MathVector2 position, float angle, MathVector2 velocity, float angularVelocityDeg, float density, float friction, float restitution, boolean ghost, int bitmask) {
        this.owner = owner;
        this.created = false;
        this.off = off;
        this.motionType = motionType;
        this.shape = shape;
        shape.setTransform(position.x, position.y, angle, 1,1);
        this.velocity = new MathVector2(velocity);
        this.angularVelocityDeg = angularVelocityDeg;
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

    public void setAngleDeg(float angleDeg) {
        shape.angle(angleDeg);
    }

    public void setVelocity(float x, float y) {
        velocity.set(x, y);
    }

    public void setAngularVelocityDeg(float angularVelocityDeg) {
        this.angularVelocityDeg = angularVelocityDeg;
    }

    public void setMotionState(float x, float y, float angleDeg, float velX, float velY, float velAngleDeg) {
        this.shape.setTransform(x, y, angleDeg);
        this.velocity.set(velX, velY);
        this.angularVelocityDeg = velAngleDeg;
    }

    public void turnOn() {
        this.off = false;
    }

    public void turnOff() {
        this.off = true;
    }

    public enum MotionType {
        FIXED,
        LOGICAL,
        NEWTONIAN
    }

    @Override
    public void reset() {
        this.owner = null;
        this.created = false;
        this.off = false;
        this.motionType = null;
        this.shape = null;
        this.velocity.set(0, 0);
        this.angularVelocityDeg = 0;
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
