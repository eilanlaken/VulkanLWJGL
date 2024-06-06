package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.MemoryPool;
import org.jetbrains.annotations.NotNull;

public class Body implements MemoryPool.Reset, Comparable<Body> {

    public Array<z_BodyCollider_old> colliders = new Array<>();

    public    Object     owner      = null;
    protected boolean    inserted   = false; // if the body is currently in the world
    protected int        index      = -1;
    public    boolean    off        = false; // bodies can be turned on / off
    public    MotionType motionType = null;

    // transform
    protected float x;
    protected float y;
    protected float angleRad;

    // velocity
    protected float vx;
    protected float vy;
    protected float angularVelocityRad;

    // acceleration
    public float netForceX;
    public float netForceY;
    public float netTorque;

    public Array<Body> touching      = new Array<>(false, 2);
    public Array<Body> justCollided  = new Array<>(false, 2);
    public Array<Body> justSeparated = new Array<>(false, 2);

    public Array<Constraint> constraints = new Array<>(false, 2);

    public float mass;
    public float massInv;
    public float inertia;
    public float inertiaInv;

    public Body() {}

    public void setMotionState(float x, float y, float angleRad, float vx, float vy, float angularVelocityRad) {
        this.x = x;
        this.y = y;
        this.angleRad = angleRad;
        this.vx = vx;
        this.vy = vy;
        this.angularVelocityRad = angularVelocityRad;
    }

    @Override
    public void reset() {
        this.owner = null;
        this.inserted = false;
        this.index = -1;
        this.off = false;
        this.motionType = null;

        this.x = 0;
        this.y = 0;
        this.angleRad = 0;

        this.vx = 0;
        this.vy = 0;
        this.angularVelocityRad = 0;

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
