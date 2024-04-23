package org.example.engine.core.physics2d_new;

public final class Physics2DWorldPhaseAPreparation implements Physics2DWorldPhase {

    // TODO: test.
    @Override
    public void update(Physics2DWorld world, float delta) {
        world.collisionCandidates.clear();
        world.manifoldMemoryPool.freeAll(world.collisionManifolds);
        world.collisionManifolds.clear();
        for (Physics2DBody body : world.bodiesToRemove) {
            world.allBodies.removeValue(body, true);
            world.bodyMemoryPool.free(body);
        }
        for (Physics2DBody body : world.bodiesToAdd) {
            world.allBodies.add(body);
            body.created = true;
        }
        world.bodiesToRemove.clear();
        world.bodiesToAdd.clear();
    }


}
