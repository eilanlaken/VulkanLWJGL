package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;

// TODO:
/*
read
https://github.com/acrlw/Physics2D/blob/master/Physics2D/include/physics2d_joint.h
https://github.com/acrlw/Physics2D/blob/master/Physics2D/include/physics2d_weld_joint.h

https://box2d.org/files/ErinCatto_UnderstandingConstraints_GDC2014.pdf

https://dyn4j.org/tags#constrained-dynamics
https://dyn4j.org/2010/07/equality-constraints/
 */
public abstract class Physics2DConstraintPin {

    public static final int   SPRING_MODE_FREQUENCY               = 1;
    public static final int   SPRING_MODE_STIFFNESS               = 2;
    public static final float DEFAULT_MAXIMUM_WARM_START_DISTANCE = 1.0e-2f;
    public static final float DEFAULT_BAUMGARTE                   = 0.2f;

    public final Physics2DBody body_a;
    public final Physics2DBody body_b;

    Physics2DConstraintPin(Physics2DBody body_a, Physics2DBody body_b) {
        if (body_a == null) throw new Physics2DException("Constraint must have at least 1 body.");
        if (body_a == body_b) throw new Physics2DException("body_a cannot be equal to body_b");
        this.body_a = body_a;
        this.body_b = body_b;
    }

    Physics2DConstraintPin(Physics2DBody body_a) {
        if (body_a == null) throw new Physics2DException("Constraint must have at least 1 body.");
        this.body_a = body_a;
        this.body_b = null;
    }


    public final void getBodies(CollectionsArray<Physics2DBody> out) {
        out.clear();
        out.add(body_a);
        if (body_b != null) out.add(body_b);
    }

    public abstract void initializeConstraints(TimeStep step);
    public abstract void solveVelocityConstraints(TimeStep step);
    public abstract boolean solvePositionConstraints(TimeStep step);
    public abstract MathVector2 getReactionForce(float invdt);
    public abstract float getReactionTorque(float invdt);

    protected static float calculateReducedMass(final Physics2DBody body_a, final Physics2DBody body_b) {
        float m1 = body_a.mass;
        float m2 = body_b.mass;

        if (m1 > 0.0 && m2 > 0.0) {
            return m1 * m2 / (m1 + m2);
        } else if (m1 > 0.0) {
            return m1;
        } else {
            return m2;
        }
    }

    public static class TimeStep {
        /** The last elapsed time */
        protected float dt0;

        /** The last inverse elapsed time */
        protected float invdt0;

        /** The elapsed time */
        protected float dt;

        /** The inverse elapsed time */
        protected float invdt;

        /** The elapsed time ratio from the last to the current */
        protected float dtRatio;

        /**
         * Default constructor.
         * @param dt the initial delta time in seconds; must be greater than zero
         * @throws IllegalArgumentException if dt is less than or equal to zero
         */
        public TimeStep(float dt) {
            if (dt <= 0.0f) throw new Physics2DException("dt must be > 0.0; Got: " + dt);

            this.dt = dt;
            this.invdt = 1.0f / dt;
            this.dt0 = this.dt;
            this.invdt0 = this.invdt;
            this.dtRatio = 1.0f;
        }

        public void update(float dt) {
            if (dt <= 0.0f) throw new Physics2DException("dt must be > 0.0; Got: " + dt);

            this.dt0 = this.dt;
            this.invdt0 = this.invdt;
            this.dt = dt;
            this.invdt = 1.0f / dt;
            this.dtRatio = this.invdt0 * dt;
        }



    }

}
