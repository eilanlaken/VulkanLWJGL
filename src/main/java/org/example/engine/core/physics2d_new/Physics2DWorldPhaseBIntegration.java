package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.physics2d_new.Physics2DBody;

public final class Physics2DWorldPhaseBIntegration implements Physics2DWorldPhase {

    @Override
    public void update(Physics2DWorld world, float delta) {
        for (Physics2DBody body : world.allBodies) {
            if (body.off) continue;
            if (body.motionType == Physics2DBody.MotionType.FIXED) continue;
            if (body.motionType == Physics2DBody.MotionType.NEWTONIAN) body.velocity.add(body.massInv * delta * body.netForce.x, body.massInv * delta * body.netForce.y);
            body.shape.dx_dy_rot(delta * body.velocity.x, delta * body.velocity.y, delta * body.angularVelocityDeg);
            body.shape.update();
        }
    }


}
