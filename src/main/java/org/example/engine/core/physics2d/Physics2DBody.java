package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.Shape2D;
import org.jetbrains.annotations.NotNull;

// TODO:
/*
Look up: TODO
https://box2d.org/documentation/b2__body_8h_source.html
 void ApplyForce(const b2Vec2& force, const b2Vec2& point, bool wake);
 void ApplyForceToCenter(const b2Vec2& force, bool wake);
 void ApplyTorque(float torque, bool wake);
 void ApplyLinearImpulse(const b2Vec2& impulse, const b2Vec2& point, bool wake);
 void ApplyLinearImpulseToCenter(const b2Vec2& impulse, bool wake);
 void ApplyAngularImpulse(float impulse, bool wake);

     // TODO https://github.com/jbox2d/jbox2d/blob/master/jbox2d-library/src/main/java/org/jbox2d/dynamics/Body.java#L483


 */
public class Physics2DBody implements MemoryPool.Reset, Comparable<Physics2DBody> {

    public    Object      owner      = null;
    protected boolean     created    = false;
    protected int         index      = -1;
    public    boolean     off        = false; // TODO: consider off bodies in update()
    public    Shape2D     shape      = null;
    public    MotionType  motionType = null;
    public    MathVector2 velocity   = new MathVector2();
    public    MathVector2 com        = new MathVector2();
    public    float       omegaDeg   = 0;
    // TODO: see what's up
    public    MathVector2 netForce   = new MathVector2();
    public    float       netTorque  = 0;

    public CollectionsArray<Physics2DBody>  touching      = new CollectionsArray<>(false, 2);
    public CollectionsArray<Physics2DBody>  justCollided  = new CollectionsArray<>(false, 2);
    public CollectionsArray<Physics2DBody>  justSeparated = new CollectionsArray<>(false, 2);
    public CollectionsArray<Physics2DJoint> joints        = new CollectionsArray<>(false, 2);

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

    // TODO: FIXME
    public MathVector2 getCenterOfMass(MathVector2 out) {
        return out.set(shape.x(), shape.y());
        //return shape.geometryCenter();
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

    public void setOmegaDeg(float omegaDeg) {
        this.omegaDeg = omegaDeg;
    }

    @Deprecated public void applyForce(float fx, float fy) {
        netForce.add(fx, fy);
    }

    @Deprecated public void applyTorque(final float tau) {
        netTorque += tau;
    }

    // TODO https://github.com/ByteArena/box2d/blob/master/DynamicsB2Body.go#L406
    // TODO https://github.com/jbox2d/jbox2d/blob/master/jbox2d-library/src/main/java/org/jbox2d/dynamics/Body.java#L483
    public void applyForce(MathVector2 force, MathVector2 point, boolean wake) {

    }

    // TODO https://github.com/ByteArena/box2d/blob/master/DynamicsB2Body.go#L406
    // TODO https://github.com/jbox2d/jbox2d/blob/master/jbox2d-library/src/main/java/org/jbox2d/dynamics/Body.java#L483
    public void applyForceToCenter(MathVector2 force, boolean wake) {

    }

    // TODO https://github.com/ByteArena/box2d/blob/master/DynamicsB2Body.go#L406
    // TODO https://github.com/jbox2d/jbox2d/blob/master/jbox2d-library/src/main/java/org/jbox2d/dynamics/Body.java#L483
    public void applyTorque(float torque, boolean wake) {

    }

    // TODO https://github.com/ByteArena/box2d/blob/master/DynamicsB2Body.go#L406
    // TODO https://github.com/jbox2d/jbox2d/blob/master/jbox2d-library/src/main/java/org/jbox2d/dynamics/Body.java#L483
    public void applyLinearImpulse(MathVector2 impulse, MathVector2 point, boolean wake) {

    }

    // TODO https://github.com/ByteArena/box2d/blob/master/DynamicsB2Body.go#L406
    // TODO https://github.com/jbox2d/jbox2d/blob/master/jbox2d-library/src/main/java/org/jbox2d/dynamics/Body.java#L483
    public void applyLinearImpulseToCenter(MathVector2 impulse, boolean wake) {

    }

    // TODO https://github.com/ByteArena/box2d/blob/master/DynamicsB2Body.go#L406
    // TODO https://github.com/jbox2d/jbox2d/blob/master/jbox2d-library/src/main/java/org/jbox2d/dynamics/Body.java#L483
    public void applyAngularImpulse(float impulse, boolean wake) {

    }

    public void setMotionState(float x, float y, float angleDeg, float vx, float vy, float omega) {
        this.shape.setTransform(x, y, angleDeg);
        this.velocity.set(vx, vy);
        this.omegaDeg = omega;
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
        this.omegaDeg = 0;
        this.netForce.set(0,0);
        this.com.set(0,0);
        this.netTorque = 0;
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
