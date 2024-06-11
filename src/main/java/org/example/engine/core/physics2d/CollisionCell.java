package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.MemoryPool;

public final class CollisionCell implements MemoryPool.Reset {

    public Array<BodyCollider> colliders = new Array<>(false, 2);
    public boolean             active    = false;

    public CollisionCell() {}

    @Override
    public void reset() {
        colliders.clear();
        active = false;
    }

}
