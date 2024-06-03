package org.example.engine.core.physics2d;

import org.example.engine.core.math.Matrix3x3;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.math.Vector3;
import org.example.engine.core.shape.ShapeUtils;

import static org.example.engine.core.math.Matrix3x3.*;

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
    // https://github.com/dyn4j/dyn4j/blob/master/src/main/java/org/dyn4j/dynamics/joint/WeldJoint.java
public class Physics2DConstraintWeld extends Physics2DConstraint {

    protected Vector2 referenceVector;
    protected float referenceAngleRad; // in rad
    private Vector2 r1;
    private Vector2 r2;
    private Matrix3x3 K;
    private Vector3 impulse;

    public Physics2DConstraintWeld(final Physics2DBody body1, final Physics2DBody body2) {
        super(body1, body2);
        if (body2 == null) throw new Physics2DException("Weld joint must connect 2 non-null bodies. Got : " + body1 + ", " + null);

        // set the anchor points
        this.referenceVector = new Vector2(body2.x() - body1.x(), body2.y() - body1.y());
        // set the reference angle
        this.referenceAngleRad = ShapeUtils.getRelativeRotationDeg(body1.shape, body2.shape);

        // initialize
        this.K = new Matrix3x3();
        this.r1 = new Vector2();
        this.r2 = new Vector2();
        this.impulse = new Vector3();
    }

    @Override
    public void prepare(float delta) {
        float invM1 = body1.massInv;
        float invM2 = body2.massInv;
        float invI1 = body1.inertiaInv;
        float invI2 = body2.inertiaInv;

        //this.r1.set(localAnchor1).sub(this.body1.getCenterOfMass()).rotateDeg(body1.angle());
        //this.r2.set(localAnchor2).sub(this.body2.getCenterOfMass()).rotateDeg(body2.angle());


        // compute the K inverse matrix
        this.K.val[M00] = invM1 + invM2 + this.r1.y * this.r1.y * invI1 + this.r2.y * this.r2.y * invI2;
        this.K.val[M01] = -this.r1.y * this.r1.x * invI1 - this.r2.y * this.r2.x * invI2;
        this.K.val[M02] = -this.r1.y * invI1 - this.r2.y * invI2;
        this.K.val[M10] = this.K.val[M01];
        this.K.val[M11] = invM1 + invM2 + this.r1.x * this.r1.x * invI1 + this.r2.x * this.r2.x * invI2;
        this.K.val[M12] = this.r1.x * invI1 + this.r2.x * invI2;
        this.K.val[M20] = this.K.val[M02];
        this.K.val[M21] = this.K.val[M12];
        this.K.val[M22] = invI1 + invI2;

        this.impulse.set(0,0,0);
    }

    @Override
    public void solveVelocity(float delta) {
        float invM1 = body1.massInv;
        float invM2 = body2.massInv;
        float invI1 = body1.inertiaInv;
        float invI2 = body2.inertiaInv;

        Vector2 r1XOmega = new Vector2();
        Vector2.crs(this.r1, this.body1.omegaDeg * MathUtils.degreesToRadians, r1XOmega);
        Vector2 v1 = new Vector2(this.body1.velocity).add(r1XOmega);

        Vector2 r2XOmega = new Vector2();
        Vector2.crs(this.r2, this.body2.omegaDeg * MathUtils.degreesToRadians, r2XOmega);
        Vector2 v2 = new Vector2(this.body2.velocity).add(r2XOmega);

        Vector2 relv = v1.sub(v2);

        float relativeOmegaRad = (this.body1.omegaDeg - this.body2.omegaDeg) * MathUtils.degreesToRadians;
        Vector3 C = new Vector3(relv.x, relv.y, relativeOmegaRad);

        Vector3 stepImpulse = new Vector3();
        if (this.K.val[M22] > 0.0f) {
            // TODO: write unit tests for solve33
            MathUtils.solve33(K, C.negate(), stepImpulse);
        } else {
            // TODO: write unit tests for solve22
            Vector2 solution = new Vector2();
            MathUtils.solve22(K, relv, solution);
            solution.negate();
            stepImpulse.set(solution.x, solution.y, 0);
        }
        this.impulse.add(stepImpulse);

        // apply the impulse
        Vector2 imp = new Vector2(stepImpulse.x, stepImpulse.y);
        this.body1.velocity.add(imp.x * invM1, imp.y * invM1);
        //this.body1.omegaDeg += invI1 * (this.r1.crs(imp) + stepImpulse.z) * MathUtils.radiansToDegrees;
        this.body2.velocity.add(imp.x * invM2, imp.y * invM2);
        //this.body2.omegaDeg += invI2 * (this.r2.crs(imp) + stepImpulse.z) * MathUtils.radiansToDegrees;
    }

    @Override
    public boolean solvePosition(float delta) {
        return true;
    }

}
