package org.example.engine.core.physics2d;

import org.example.engine.core.math.MathVector2;

public abstract class Physics2DJoint {

    public Physics2DBody body_a;
    public Physics2DBody body_b;

    Physics2DJoint(Physics2DBody body_a, Physics2DBody body_b) {
        this.body_a = body_a;
        this.body_b = body_b;
    }

    public abstract void  getReactionForce (float inv_dt, MathVector2 out);
    public abstract float getReactionTorque(float inv_dt);

    public abstract void    initVelocityConstraints (SolverData data);
    public abstract void    solveVelocityConstraints(SolverData data);
    public abstract boolean solvePositionConstraints(SolverData data);

    public class SolverData {
        public TimeStep step;
        public MathVector2[] positions;
        public float[] angles;
        public MathVector2[] velocities;
        public float[] omegas;
    }

    public class TimeStep {

        public float dt;

        public float inv_dt; //inverse time step (0 if dt == 0).

        /** dt * inv_dt0 */
        public float dtRatio;

        public int velocityIterations;

        public int positionIterations;

        public boolean warmStarting;
    }

}
