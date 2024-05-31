package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;

/*
TODO:

read here
https://github.com/acrlw/Physics2D/blob/master/Physics2D/include/physics2d_weld_joint.h
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
public class Physics2DConstraintWeld {



}
