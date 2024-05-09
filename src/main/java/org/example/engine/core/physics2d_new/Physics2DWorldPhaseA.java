package org.example.engine.core.physics2d_new;

public final class Physics2DWorldPhaseA {

    private final Physics2DWorld world;

    protected Physics2DWorldPhaseA(final Physics2DWorld world) {
        this.world = world;
    }

    public synchronized void update() {
        world.collisionCandidates.clear();
        world.activeCells.clear();
        world.manifoldMemoryPool.freeAll(world.collisionManifolds);
        world.collisionManifolds.clear();
        for (Physics2DBody body : world.bodiesToRemove) {
            world.allBodies.removeValue(body, true);
            world.bodyMemoryPool.free(body);
        }

        for (Physics2DBody body : world.bodiesToAdd) {
            world.allBodies.add(body);
            body.created = true;
            body.index = world.bodiesCreated;
            world.bodiesCreated++;
        }
        world.bodiesToRemove.clear();
        world.bodiesToAdd.clear();
    }


}
