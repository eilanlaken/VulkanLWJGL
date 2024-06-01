package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.math.MathMatrix2;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;

import static org.example.engine.core.math.MathMatrix2.*;

/*
TODO:

read here
https://github.com/acrlw/Physics2D/blob/master/Physics2D/include/physics2d_weld_joint.h
https://ubm-twvideo01.s3.amazonaws.com/o1/vault/gdc09/slides/04-GDC09_Catto_Erin_Solver.pdf
 */

/**
 * Derivation: https://dyn4j.org/2010/12/weld-constraint/
 *
 * Point-to-point constraint
 * C = p2 - p1
 * Cdot = v2 - v1 = v2 + cross(w2, r2) - v1 - cross(w1, r1)
 * J = [-I -r1_skew I r2_skew ]
 * Identity used:
 * w k % (rx i + ry j) = w * (-ry i + rx j)
 *
 * Angle constraint
 * C = angle2 - angle1 - referenceAngle
 * Cdot = w2 - w1
 * J = [0 0 -1 0 0 1]
 * K = invI1 + invI2
 */

// https://github.com/jbox2d/jbox2d/blob/master/jbox2d-library/src/main/java/org/jbox2d/dynamics/joints/WeldJoint.java
public class Physics2DConstraintWeld extends Physics2DConstraint {

    MathVector2 localPointA = new MathVector2(1,0);
    MathVector2 localPointB = new MathVector2(-1,0);
    MathMatrix2 effectiveMass;
    MathVector2 accumulatedImpulse = new MathVector2();

    public Physics2DConstraintWeld(final Physics2DBody body_a, final Physics2DBody body_b) {
        super(body_a, body_b);
        if (body_b == null) throw new Physics2DException("Weld joint must connect 2 non-null bodies. Got : " + body_a + ", " + null);
    }

    @Override
    public void init(float delta) {

    }

    @Override
    public void updateVelocity(float delta) {

    }

    @Override
    public boolean updatePosition(float delta) {
        return false;
    }


}
