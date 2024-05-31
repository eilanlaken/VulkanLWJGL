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
public abstract class Physics2DConstraint {

    public static final int   SPRING_MODE_FREQUENCY               = 1;
    public static final int   SPRING_MODE_STIFFNESS               = 2;
    public static final float DEFAULT_MAXIMUM_WARM_START_DISTANCE = 1.0e-2f;
    public static final float DEFAULT_BAUMGARTE                   = 0.2f;

    public final Physics2DBody body_a;
    public final Physics2DBody body_b;

    Physics2DConstraint(Physics2DBody body_a, Physics2DBody body_b) {
        if (body_a == null) throw new Physics2DException("Constraint must have at least 1 body.");
        if (body_a == body_b) throw new Physics2DException("body_a cannot be equal to body_b");
        this.body_a = body_a;
        this.body_b = body_b;
    }

    Physics2DConstraint(Physics2DBody body_a) {
        if (body_a == null) throw new Physics2DException("Constraint must have at least 1 body.");
        this.body_a = body_a;
        this.body_b = null;
    }


    public final void getBodies(CollectionsArray<Physics2DBody> out) {
        out.clear();
        out.add(body_a);
        if (body_b != null) out.add(body_b);
    }

    public abstract void initVelocityConstraints(SolverData data);
    public abstract void solveVelocityConstraints(SolverData data);
    public abstract boolean solvePositionConstraints(SolverData data);

    public abstract void getAnchorA(MathVector2 out);
    public abstract void getAnchorB(MathVector2 out);
    public abstract void getReactionForce(float inv_dt, MathVector2 out);
    public abstract float getReactionTorque(float inv_dt);

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

    public class SolverData {
        public TimeStep step;
        public Position[] positions;
        public Velocity[] velocities;
    }

    public class TimeStep {

        /** time step */
        public float dt;

        /** inverse time step (0 if dt == 0). */
        public float inv_dt;

        /** dt * inv_dt0 */
        public float dtRatio;

        public int velocityIterations;

        public int positionIterations;

        public boolean warmStarting;
    }

    @Deprecated public static class Velocity {
        public final MathVector2 v = new MathVector2();
        public float w;
    }

    @Deprecated public static class Position {
        public final MathVector2 c = new MathVector2();
        public float a;
    }

}
