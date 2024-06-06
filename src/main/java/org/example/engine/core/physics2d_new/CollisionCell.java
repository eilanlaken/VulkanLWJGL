package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.MemoryPool;

public final class CollisionCell implements MemoryPool.Reset {

    private final Array<Body> bodies = new Array<>(false, 2);

    private boolean active = false;

    public CollisionCell() {}

    @Override
    public void reset() {
        bodies.clear();
        active = false;
    }

}
