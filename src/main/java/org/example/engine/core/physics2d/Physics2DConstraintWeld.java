package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathMatrix2;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;

import static org.example.engine.core.math.MathMatrix2.*;

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
    public void initVelocityConstraints(float delta) {
        if (body_a.off || body_b.off)
            return;

        float m_a = body_a.mass;
        float im_a = body_a.massInv;
        float ii_a = body_a.inertiaInv;

        float m_b = body_b.mass;
        float im_b = body_b.massInv;
        float ii_b = body_b.inertiaInv;

        MathVector2 pa = new MathVector2(localPointA).rotateDeg(body_a.shape.angle()).add(body_a.shape.x(), body_a.shape.y());
        MathVector2 ra = pa.sub(body_a.shape.x(), body_a.shape.y());
        MathVector2 pb = new MathVector2(localPointB).rotateDeg(body_b.shape.angle()).add(body_b.shape.x(), body_b.shape.y());
        MathVector2 rb = pb.sub(body_b.shape.x(), body_b.shape.y());

        MathMatrix2 k = new MathMatrix2();
        k.val[M00] = im_a + ra.y * ra.y * ii_a + im_b + rb.y * rb.y * ii_b;
        k.val[M01] = -ra.x * ra.y * ii_a - rb.x * rb.y * ii_b;
        k.val[M10] = k.val[M01];
        k.val[M11] = im_a + ra.x * ra.x * ii_a + im_b + rb.x * rb.x * ii_b;

        effectiveMass = k.inv();
        body_a.applyImpulse(accumulatedImpulse.x * delta, accumulatedImpulse.y * delta, ra.x, ra.y);
        body_b.applyImpulse(-accumulatedImpulse.x * delta, -accumulatedImpulse.y * delta, rb.x, rb.y);
    }

    @Override
    public void solveVelocityConstraints(float delta) {
        MathVector2 ra = new MathVector2(localPointA).rotateDeg(body_a.shape.angle());
        MathVector2 va = new MathVector2(body_a.velocity).add(MathVector2.crs(body_a.omegaDeg * MathUtils.degreesToRadians, ra));
        MathVector2 rb = new MathVector2(localPointB).rotateDeg(body_b.shape.angle());
        MathVector2 vb = new MathVector2(body_b.velocity).add(MathVector2.crs(body_b.omegaDeg * MathUtils.degreesToRadians, rb));

        MathVector2 jvb = new MathVector2(va.x-vb.x, va.y-vb.y);
        jvb.negate();
        MathVector2 J = new MathVector2();
        effectiveMass.mul(jvb, J);
        MathVector2 oldImpulse = new MathVector2(accumulatedImpulse);
        accumulatedImpulse.add(J);

        J.set(accumulatedImpulse).sub(oldImpulse);
        body_a.applyImpulse(accumulatedImpulse.x * delta, accumulatedImpulse.y * delta, ra.x, ra.y);
        body_b.applyImpulse(-accumulatedImpulse.x * delta, -accumulatedImpulse.y * delta, rb.x, rb.y);
    }

    @Override
    public boolean solvePositionConstraints(float delta) {
        return false;
    }

    @Override
    public void getAnchorA(MathVector2 out) {

    }

    @Override
    public void getAnchorB(MathVector2 out) {

    }

    @Override
    public void getReactionForce(float inv_dt, MathVector2 out) {

    }

    @Override
    public float getReactionTorque(float inv_dt) {
        return 0;
    }

}
