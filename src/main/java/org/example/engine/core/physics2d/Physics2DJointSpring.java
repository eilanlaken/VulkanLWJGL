package org.example.engine.core.physics2d;

import org.example.engine.core.math.MathVector2;

public class Physics2DJointSpring extends Physics2DJoint {

    public float k;
    public float l;

    Physics2DJointSpring(Physics2DBody body_a, Physics2DBody body_b, float k, float l) {
        super(body_a, body_b);
        this.k = k;
        this.l = l;
    }

    @Override
    public void getReactionForce(float inv_dt, MathVector2 out) {

    }

    @Override
    public float getReactionTorque(float inv_dt) {
        return 0;
    }

    @Override
    public void initVelocityConstraints(SolverData data) {

    }

    @Override
    public void solveVelocityConstraints(SolverData data) {

    }

    @Override
    public boolean solvePositionConstraints(SolverData data) {
        return false;
    }
}
