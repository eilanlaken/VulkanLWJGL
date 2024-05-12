package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.Shape2D;
import org.jetbrains.annotations.NotNull;

public class Physics2DBody implements MemoryPool.Reset, Comparable<Physics2DBody> {

    public    Object      owner      = null;
    protected boolean     created    = false;
    protected int         index      = -1;
    public    boolean     off        = false;
    public    Shape2D     shape      = null;
    public    MotionType  motionType = null;
    public    MathVector2 velocity   = new MathVector2();
    public    float       omega      = 0;
    public    MathVector2 netForce   = new MathVector2();
    public    float       netTorque  = 0;

    public CollectionsArray<Physics2DBody>       collidesWith = new CollectionsArray<>(false, 2);
    public CollectionsArray<Physics2DConstraint> constraints  = new CollectionsArray<>(false, 1);
    public CollectionsArray<Physics2DJoint>      joints       = new CollectionsArray<>(false, 1);

    // TODO: must set some default values.
    public float   massInv;
    public float   inertiaInv;
    public float   density;
    public float   staticFriction;
    public float   dynamicFriction;
    public float   restitution;
    public boolean ghost;
    public int     bitmask;

    public Physics2DBody() {

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

    public void setOmega(float omega) {
        this.omega = omega;
    }

    public void applyForce(float fx, float fy) {
        netForce.add(fx, fy);
    }

    public void applyTorque(final float tau) {
        netTorque += tau;
    }

    public void setMotionState(float x, float y, float angleDeg, float vx, float vy, float omega) {
        this.shape.setTransform(x, y, angleDeg);
        this.velocity.set(vx, vy);
        this.omega = omega;
    }

    public void turnOn() {
        this.off = false;
    }

    public void turnOff() {
        this.off = true;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public String toString() {
        return "Physics2DBody{" +
                "index=" + index +
                '}';
    }

    @Override
    public void reset() {
        this.owner = null;
        this.created = false;
        this.index = -1;
        this.off = false;
        this.motionType = null;
        this.shape = null;
        this.velocity.set(0, 0);
        this.omega = 0;
        this.netForce.set(0,0);
        this.netTorque = 0;
        this.constraints.clear();
        this.joints.clear();
        this.massInv = 0;
        this.inertiaInv = 0;
        this.density = 0;
        this.staticFriction = 0;
        this.dynamicFriction = 0;
        this.restitution = 0;
        this.ghost = false;
        this.bitmask = 0;
    }

    @Override
    public int compareTo(@NotNull Physics2DBody o) {
        return Integer.compare(index, o.index);
    }

    public enum MotionType {
        STATIC,
        KINEMATIC,
        NEWTONIAN,
        RELATIVISTIC, // for now, just an idea.
    }

}
