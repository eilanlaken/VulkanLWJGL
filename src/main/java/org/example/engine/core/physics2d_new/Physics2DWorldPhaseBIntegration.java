package org.example.engine.core.physics2d_new;

// TODO: continue. consider torque, constraints, joints.
public final class Physics2DWorldPhaseBIntegration implements Physics2DWorldPhase {

    @Override
    public void update(Physics2DWorld world, float delta) {
        float minX =  Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float minY =  Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        float maxR = -Float.MAX_VALUE;

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
            minX = Math.min(minX, min_body_x);
            maxX = Math.max(maxX, max_body_x);
            minY = Math.min(minY, min_body_y);
            maxY = Math.max(maxY, max_body_y);

            float bounding_r = body.shape.getBoundingRadius();
            maxR = Math.max(maxR, bounding_r);
        }

        // calculate the cell size of the grid partition
        world.worldMinX = minX;
        world.worldMaxX = maxX;
        world.worldMinY = minY;
        world.worldMaxY = maxY;

        world.worldWidth  = Math.abs(maxX - minX);
        world.worldHeight = Math.abs(maxY - minY);

        final float maxDiameter = 2 * maxR;
        world.rows = Math.min((int) Math.ceil(world.worldHeight  / maxDiameter), 32);
        world.cols = Math.min((int) Math.ceil(world.worldWidth / maxDiameter), 32);
        world.cellWidth = world.worldWidth / world.cols;
        world.cellHeight = world.worldHeight / world.rows;
    }


}
