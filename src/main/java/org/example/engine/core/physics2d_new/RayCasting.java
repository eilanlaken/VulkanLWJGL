package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.physics2d.Physics2DWorld;

public final class RayCasting {

    public interface RayHitCallback {

        void intersected(final Array<Physics2DWorld.Intersection> results);

    }

}
