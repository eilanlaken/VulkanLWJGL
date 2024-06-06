package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.physics2d.Physics2DBody;
import org.example.engine.core.physics2d.Physics2DException;

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
public abstract class Constraint {

    public final Body body1;
    public final Body body2;

    Constraint(Body body1, Body body2) {
        if (body1 == null)  throw new org.example.engine.core.physics2d.Physics2DException("Constraint must have at least 1 body.");
        if (body1 == body2) throw new org.example.engine.core.physics2d.Physics2DException("body_a cannot be equal to body_b");
        this.body1 = body1;
        this.body2 = body2;
    }

    Constraint(Body body1) {
        if (body1 == null) throw new Physics2DException("Constraint must have at least 1 body.");
        this.body1 = body1;
        this.body2 = null;
    }

    public final void getBodies(Array<Body> out) {
        out.clear();
        out.add(body1);
        if (body2 != null) out.add(body2);
    }

    abstract void    prepare(float delta);
    abstract void    solveVelocity(float delta);
    abstract boolean solvePosition(float delta);

}
