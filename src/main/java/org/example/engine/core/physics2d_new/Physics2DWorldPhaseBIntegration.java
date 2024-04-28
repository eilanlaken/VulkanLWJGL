package org.example.engine.core.physics2d_new;

// TODO: continue. consider torque, constraints, joints.
public final class Physics2DWorldPhaseBIntegration implements Physics2DWorldPhase {

    @Override
    public void update(Physics2DWorld world, float delta) {
        world.worldMinX =  Float.MAX_VALUE;
        world.worldMaxX = -Float.MAX_VALUE;
        world.worldMinY =  Float.MAX_VALUE;
        world.worldMaxY = -Float.MAX_VALUE;
        float maxR      = -Float.MAX_VALUE;

        for (Physics2DBody body : world.allBodies) {
            if (body.off) continue;
            if (body.motionType == Physics2DBody.MotionType.NEWTONIAN) {
                body.velocity.add(body.massInv * delta * body.netForce.x, body.massInv * delta * body.netForce.y);
            }
            if (body.motionType != Physics2DBody.MotionType.FIXED) body.shape.dx_dy_rot(delta * body.velocity.x, delta * body.velocity.y, delta * body.angularVelocityDeg);
            body.shape.update();
            body.netForce.set(0, 0);

            // prepare further heuristics for the broad phase
            float min_body_x = body.shape.getMinExtentX();
            float max_body_x = body.shape.getMaxExtentX();
            float min_body_y = body.shape.getMinExtentY();
            float max_body_y = body.shape.getMaxExtentY();
            world.worldMinX = Math.min(world.worldMinX, min_body_x);
            world.worldMaxX = Math.max(world.worldMaxX, max_body_x);
            world.worldMinY = Math.min(world.worldMinY, min_body_y);
            world.worldMaxY = Math.max(world.worldMaxY, max_body_y);

            float bounding_r = body.shape.getBoundingRadius();
            maxR = Math.max(maxR, bounding_r);
        }

        world.worldWidth  = Math.abs(world.worldMaxX - world.worldMinX);
        world.worldHeight = Math.abs(world.worldMaxY - world.worldMinY);

        final float maxDiameter = 2 * maxR;
        world.rows = Math.min((int) Math.ceil(world.worldHeight  / maxDiameter), 32);
        world.cols = Math.min((int) Math.ceil(world.worldWidth / maxDiameter), 32);
        world.cellWidth = world.worldWidth / world.cols;
        world.cellHeight = world.worldHeight / world.rows;
    }


}
