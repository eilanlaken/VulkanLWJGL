package org.example.engine.core.physics2d_new;

// TODO: continue. consider torque, constraints, joints.
public final class Physics2DWorldPhaseBIntegration implements Physics2DWorldPhase {

    @Override
    public void update(Physics2DWorld world, float delta) {
        world.worldMinX =  Float.MAX_VALUE;
        world.worldMaxX = -Float.MAX_VALUE;
        world.worldMinY =  Float.MAX_VALUE;
        world.worldMaxY = -Float.MAX_VALUE;
        world.worldMaxR = -Float.MAX_VALUE;

        for (Physics2DBody body : world.allBodies) {
            if (body.off) continue;
            if (body.motionType == Physics2DBody.MotionType.NEWTONIAN) {
                body.velocity.add(body.massInv * delta * body.netForce.x, body.massInv * delta * body.netForce.y);
            }
            if (body.motionType != Physics2DBody.MotionType.STATIC) {
                body.shape.dx_dy_rot(delta * body.velocity.x, delta * body.velocity.y, delta * body.angularVelocityDeg);
            }
            body.shape.update();
            body.netForce.set(0, 0);
            body.collision.clear();

            // prepare heuristics for the broad phase
            world.worldMinX = Math.min(world.worldMinX, body.shape.getMinExtentX());
            world.worldMaxX = Math.max(world.worldMaxX, body.shape.getMaxExtentX());
            world.worldMinY = Math.min(world.worldMinY, body.shape.getMinExtentY());
            world.worldMaxY = Math.max(world.worldMaxY, body.shape.getMaxExtentY());
            world.worldMaxR = Math.max(world.worldMaxR, body.shape.getBoundingRadius());
        }

        final float maxDiameter = 2 * world.worldMaxR;
        world.worldWidth  = Math.abs(world.worldMaxX - world.worldMinX);
        world.worldHeight = Math.abs(world.worldMaxY - world.worldMinY);
        world.rows = Math.min((int) Math.ceil(world.worldHeight  / maxDiameter), 32);
        world.cols = Math.min((int) Math.ceil(world.worldWidth   / maxDiameter), 32);
        world.cellWidth  = world.worldWidth  / world.cols;
        world.cellHeight = world.worldHeight / world.rows;
    }


}
