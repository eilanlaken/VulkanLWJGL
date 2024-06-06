package org.example.engine.core.physics2d_new;

import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;

public final class RayCastingIntersection implements MemoryPool.Reset {

    public Body body      = null;
    public Vector2 point     = new Vector2();
    public Vector2 direction = new Vector2();
    public float         dst2      = 0;

    public RayCastingIntersection() {}

    @Override
    public void reset() {
        body = null;
    }

}
