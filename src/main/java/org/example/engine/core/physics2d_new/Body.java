package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;
import org.jetbrains.annotations.NotNull;

public class Body implements MemoryPool.Reset, Comparable<Body> {

    public Array<BodyCollider> colliders = new Array<>();

    public    Object     owner       = null;
    protected boolean    initialized = false; // if the body is currently in the world
    protected int        index       = -1;
    public    boolean    off         = false; // bodies can be turned on / off
    public    MotionType motionType  = null;

    // transform
    protected float x; // x of the center of mass
    protected float y; // y in the center of mass
    protected float angleRad; // the angle around the center of mass

    // velocity
    protected float vx; // the x speed of the center of mass
    protected float vy; // the y speed of the center of mass
    protected float angularVelocityRad; // the change in angleRad

    // acceleration
    public float netForceX;
    public float netForceY;
    public float netTorque; // the torque about the center of mass

    public Array<Body> touching      = new Array<>(false, 2);
    public Array<Body> justCollided  = new Array<>(false, 2);
    public Array<Body> justSeparated = new Array<>(false, 2);

    public Array<Constraint> constraints = new Array<>(false, 2);

    public float mass;
    public float massInv;
    public float inertia;
    public float inertiaInv;

    public Body() {}

    /**
     * This method is called whenever a {@link Body} is inserted into the world.
     * It does 4 very important things:
     * - calculate the total mass (and its inverse)
     * - calculate the local center of mass then shifts the colliders frame of reference to the center of mass
     * - sets the position of the body to the center of mass
     * - calculates the moment of inertia relative to the center of mass (and its inverse)
     */
    void init() {
        float originX = x;
        float originY = y;
        float initialAngleRad = angleRad;
        float totalMass = 0;
        Vector2 local_center_of_mass = new Vector2();
        for (BodyCollider collider : colliders) {
            float shapeMass = collider.area() * collider.density;
            totalMass += shapeMass;
            final Vector2 shapeCenter = collider.offset();
            local_center_of_mass.x += shapeCenter.x * shapeMass;
            local_center_of_mass.y += shapeCenter.y * shapeMass;
        }
        local_center_of_mass.scl(1.0f / totalMass);
        for (BodyCollider collider : colliders) {
            collider.shiftLocalCenter(local_center_of_mass);
        }
        this.mass = totalMass;
        this.massInv = 1.0f / totalMass;
        local_center_of_mass.rotateRad(initialAngleRad);
        // set the center of mass to be the origin.
        this.x = originX + local_center_of_mass.x;
        this.y = originY + local_center_of_mass.y;

        this.initialized = true;
    }

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
        this.initialized = false;
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
