package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.MemoryPool;

public class Body implements MemoryPool.Reset {

    public Array<BodyCollider> colliders;

    public    Object     data       = null;
    protected boolean    inserted   = false; // if the body is currently in the world
    protected int        index      = -1;
    public    boolean    off        = false; // bodies can be turned on / off
    public    MotionType motionType = null;

    // transform
    protected float x;
    protected float y;
    protected float angleDeg;

    // velocity
    protected float vx;
    protected float vy;
    protected float angularVelocityDeg;

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

    @Override
    public void reset() {
        this.data = null;
        this.inserted = false;
        this.index = -1;
        this.off = false;
        this.motionType = null;

        this.x = 0;
        this.y = 0;
        this.angleDeg = 0;

        this.vx = 0;
        this.vy = 0;
        this.angularVelocityDeg = 0;

        this.netForceX = 0;
        this.netForceY = 0;
        this.netTorque = 0;

        this.touching.clear();
        this.justCollided.clear();
        this.justSeparated.clear();
        this.constraints.clear();
    }

    public enum MotionType {
        STATIC,
        KINEMATIC,
        NEWTONIAN,
    }

}
