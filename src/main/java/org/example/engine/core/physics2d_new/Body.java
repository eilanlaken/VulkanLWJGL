package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.physics2d_new.BodyCollider;
import org.jetbrains.annotations.NotNull;

public final class Body implements MemoryPool.Reset, Comparable<Body> {

    public final Array<BodyCollider> colliders = new Array<>();

    public    Object     owner       = null;
    protected boolean    initialized = false; // if the body is currently in the world
    protected int        index       = -1;
    public    boolean    off         = false; // bodies can be turned on / off
    public BodyMotionType motionType  = null;

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

    public float M;
    public float invM;
    public float I;
    public float invI;

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

//        this.touching.clear();
//        this.justCollided.clear();
//        this.justSeparated.clear();
//        this.constraints.clear();
    }

    @Override
    public int compareTo(@NotNull Body o) {
        return Integer.compare(index, o.index);
    }

}
