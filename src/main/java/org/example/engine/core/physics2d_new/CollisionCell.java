package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.MemoryPool;

final class CollisionCell implements MemoryPool.Reset {

    Array<BodyCollider> colliders = new Array<>(false, 2);
    boolean             active    = false;

    public CollisionCell() {}

    @Override
    public void reset() {
        colliders.clear();
        active = false;
    }

}
