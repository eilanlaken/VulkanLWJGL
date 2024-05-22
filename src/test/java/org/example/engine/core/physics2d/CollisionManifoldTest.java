package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.memory.MemoryPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CollisionManifoldTest {

    private MemoryPool<Physics2DWorld.CollisionManifold> manifoldMemoryPool = new MemoryPool<>(Physics2DWorld.CollisionManifold.class, 10);
    private MemoryPool<Physics2DBody>                    bodiesMemoryPool   = new MemoryPool<>(Physics2DBody.class, 10);

    @Test
    void reset() {

    }

}
