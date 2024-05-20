package org.example.engine.core.physics2d_new;

import org.example.engine.core.math.MathVector2;
import org.example.engine.core.shape.Shape2D;

public class Physics2DCollisionResolver {

    protected void beginContact(Physics2DBody a, Physics2DBody b) {

    }

    protected void endContact(Physics2DBody a, Physics2DBody b) {

    }

    protected void preSolve(Physics2DWorld.CollisionManifold manifold) {}

    protected void solve(Physics2DBody body_a, Physics2DBody body_b, Physics2DWorld.CollisionManifold manifold) {
        Shape2D shape_a = manifold.shape_a;
        Shape2D shape_b = manifold.shape_b;

        // separate bodies
        float aMassInv = body_a.motionType == Physics2DBody.MotionType.STATIC ? 0 : body_a.massInv;
        float bMassInv = body_b.motionType == Physics2DBody.MotionType.STATIC ? 0 : body_b.massInv;
        final float percent = 0.7f;
        final float threshold = 0.001f;
        final float depth = manifold.depth;
        final MathVector2 normal = manifold.normal;
        final float pushBack = Math.max(depth - threshold, 0.0f);
        MathVector2 correction = new MathVector2(normal).scl(pushBack * percent).scl(1 / (aMassInv + bMassInv));

        MathVector2 aCenter = shape_a.geometryCenter();
        MathVector2 bCenter = shape_b.geometryCenter();
        MathVector2 a_b = new MathVector2(bCenter.x - aCenter.x, bCenter.y - aCenter.y);
        float dot = a_b.dot(manifold.normal);

        if (dot > 0) {
            shape_a.dx_dy(-aMassInv * correction.x, -aMassInv * correction.y);
            shape_b.dx_dy(bMassInv * correction.x, bMassInv * correction.y);
        } else {
            shape_a.dx_dy(aMassInv * correction.x, aMassInv * correction.y);
            shape_b.dx_dy(-bMassInv * correction.x, -bMassInv * correction.y);
        }


        MathVector2 vRel = new MathVector2(body_b.velocity).sub(body_a.velocity);
        float normalVelocity = vRel.dot(manifold.normal);
        if (normalVelocity <= 0) return; // TODO see why it breaks things

        // TODO: collision response - friction, torque
        float e = Math.min(body_a.restitution, body_b.restitution);
        float j = -(1 + e) * normalVelocity / (aMassInv + bMassInv);
        MathVector2 impulse = new MathVector2(normal).scl(j);
        MathVector2 deltaVelA = new MathVector2(impulse).scl(aMassInv);
        MathVector2 deltaVelB = new MathVector2(impulse).scl(bMassInv);
        body_a.velocity.sub(deltaVelA);
        body_b.velocity.add(deltaVelB);
    }

    protected void postSolve(Physics2DWorld.CollisionManifold manifold) {}

}
