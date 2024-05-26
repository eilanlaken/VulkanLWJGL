package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.memory.MemoryPool;

public final class Physics2DRaycasting {

    private final Physics2DWorld world;

    Physics2DRaycasting(Physics2DWorld world) {
        this.world = world;
    }

    void getIntersections(Physics2DWorld.Ray ray, CollectionsArray<Physics2DBody> bodies, CollectionsArray<Physics2DWorld.RayCastResult> intersections) {
        MemoryPool<Physics2DWorld.RayCastResult> rayCastResultsPool = world.getRayCastResultsPool();

    }

}
