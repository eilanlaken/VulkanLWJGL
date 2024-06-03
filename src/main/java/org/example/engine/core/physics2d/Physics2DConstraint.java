package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;

// TODO:
/*
read
https://github.com/acrlw/Physics2D/blob/master/Physics2D/include/physics2d_joint.h
https://github.com/acrlw/Physics2D/blob/master/Physics2D/include/physics2d_weld_joint.h

https://ubm-twvideo01.s3.amazonaws.com/o1/vault/gdc09/slides/04-GDC09_Catto_Erin_Solver.pdf

https://box2d.org/files/ErinCatto_UnderstandingConstraints_GDC2014.pdf

https://dyn4j.org/tags#constrained-dynamics
https://dyn4j.org/2010/07/equality-constraints/
 */
public abstract class Physics2DConstraint {

    public static final int   SPRING_MODE_FREQUENCY               = 1;
    public static final int   SPRING_MODE_STIFFNESS               = 2;
    public static final float DEFAULT_MAXIMUM_WARM_START_DISTANCE = 1.0e-2f;
    public static final float DEFAULT_BAUMGARTE                   = 0.2f;

    public final Physics2DBody body1;
    public final Physics2DBody body2;

    Physics2DConstraint(Physics2DBody body1, Physics2DBody body2) {
        if (body1 == null) throw new Physics2DException("Constraint must have at least 1 body.");
        if (body1 == body2) throw new Physics2DException("body_a cannot be equal to body_b");
        this.body1 = body1;
        this.body2 = body2;
    }

    Physics2DConstraint(Physics2DBody body1) {
        if (body1 == null) throw new Physics2DException("Constraint must have at least 1 body.");
        this.body1 = body1;
        this.body2 = null;
    }


    public final void getBodies(Array<Physics2DBody> out) {
        out.clear();
        out.add(body1);
        if (body2 != null) out.add(body2);
    }

    abstract void prepare(float delta);
    abstract void solveVelocity(float delta);
    abstract boolean solvePosition(float delta);

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

}
