package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;
import org.jetbrains.annotations.NotNull;

public final class Body implements MemoryPool.Reset, Comparable<Body> {

    public final Array<BodyCollider> colliders = new Array<>();

    public    Object     owner       = null;
    protected boolean    initialized = false; // if the body is currently in the world
    protected int        index       = -1;
    public    boolean    off         = false; // bodies can be turned on / off
    public    MotionType motionType  = null;

    // transform
    protected float x      = 0; // x of the origin
    protected float y      = 0; // y of the origin
    protected float lcmX   = 0;
    protected float lcmY   = 0;
    protected float cmX    = 0;
    protected float cmY    = 0;
    protected float aRad   = 0; // the angle around the center of mass
    // velocity
    protected float vx     = 0;
    protected float vy     = 0;
    protected float wRad   = 0; // the change in aRad
    // acceleration
    public float netForceX = 0;
    public float netForceY = 0;
    public float netTorque = 0; // the torque about the center of mass

    public Array<Body> touching      = new Array<>(false, 2);
    public Array<Body> justCollided  = new Array<>(false, 2);
    public Array<Body> justSeparated = new Array<>(false, 2);

    public Array<Constraint> constraints = new Array<>(false, 2);

    public float M;
    public float invM;
    public float I;
    public float invI;

    public Body() {}

    /**
     * This method is called whenever a {@link Body} is inserted into the world.
     * It does 3 very important things:
     * - calculate the total mass (and its inverse)
     * - calculate the local center of mass
     * - calculates the moment of inertia relative to the center of mass (and its inverse)
     */
    final void init() {
        float totalMass = 0;

        for (BodyCollider collider : colliders) {
            float shapeMass = collider.area() * collider.density;
            totalMass += shapeMass;
            final Vector2 shapeCenter = collider.localCenter();
            this.lcmX += shapeCenter.x * shapeMass;
            this.lcmY += shapeCenter.y * shapeMass;
        }
        this.lcmX /= totalMass;
        this.lcmY /= totalMass;
        this.M = totalMass;
        this.invM = 1.0f / totalMass;

        // calculate moment of inertia
        float totalInertia = 0;
        for (BodyCollider collider : colliders) {
            float shapeMass = collider.area() * collider.density;
            float d2 = Vector2.dst2(collider.localCenter().x, collider.localCenter().y, lcmX, lcmY);
            float I = Physics2DUtils.calculateMomentOfInertia(collider) + shapeMass * d2;
            totalInertia += I;
        }

        this.I = totalInertia;
        this.invI = 1.0f / totalInertia;

        syncTransform();
        this.initialized = true;
    }

    final void syncTransform() {
        cmX = x + lcmX;
        cmY = y + lcmY;
        for (BodyCollider collider : colliders) {
            collider.update();
        }
    }

    public final void setTransform(float x, float y, float angleRad) {
        this.x = x;
        this.y = y;
        this.aRad = angleRad;
        syncTransform();
    }

    public final void setVelocity(float vx, float vy, float wRad) {
        this.vx = vx;
        this.vy = vy;
        this.wRad = wRad;
    }

    @Override
    public void reset() {
        this.colliders.clear();

        this.owner = null;
        this.initialized = false;
        this.index = -1;
        this.off = false;
        this.motionType = null;

        this.x = 0;
        this.y = 0;
        this.lcmX = 0;
        this.lcmY = 0;
        this.aRad = 0;

        this.vx = 0;
        this.vy = 0;
        this.wRad = 0;

        this.netForceX = 0;
        this.netForceY = 0;
        this.netTorque = 0;

        this.touching.clear();
        this.justCollided.clear();
        this.justSeparated.clear();
        this.constraints.clear();
    }

    @Override
    public int compareTo(@NotNull Body o) {
        return Integer.compare(index, o.index);
    }

    public enum MotionType {
        STATIC,
        KINEMATIC,
        NEWTONIAN,
    }

}
