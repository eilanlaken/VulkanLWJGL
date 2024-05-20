package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.physics2d_new.Physics2DBody;
import org.example.engine.core.physics2d_new.Physics2DWorld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CollisionManifoldTest {

    private MemoryPool<Physics2DWorld.CollisionManifold> manifoldMemoryPool = new MemoryPool<>(Physics2DWorld.CollisionManifold.class, 10);
    private MemoryPool<Physics2DBody>                    bodiesMemoryPool   = new MemoryPool<>(Physics2DBody.class, 10);

    @Test
    void sort() {
        CollectionsArray<Physics2DWorld.CollisionManifold> manifolds = new CollectionsArray<>();
        for (int i = 0; i < 10; i++) {
            Physics2DWorld.CollisionManifold manifold = manifoldMemoryPool.allocate();
            manifold.body_a = bodiesMemoryPool.allocate();
            manifold.body_b = bodiesMemoryPool.allocate();
            manifold.depth = i + MathUtils.random(1, 10);
            manifolds.add(manifold);
        }
        manifolds.sort();
        for (int i = 1; i < manifolds.size; i++) {
            Physics2DWorld.CollisionManifold manifold1 = manifolds.get(i-1);
            Physics2DWorld.CollisionManifold manifold2 = manifolds.get(i);
            Assertions.assertTrue(manifold1.compareTo(manifold2) <= 0);
        }
        System.out.println(manifolds);
    }

    @Test
    void reset() {

    }

}
