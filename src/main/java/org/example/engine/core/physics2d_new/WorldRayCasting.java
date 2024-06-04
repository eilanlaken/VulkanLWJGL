package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.physics2d.Physics2DWorld;

public class WorldRayCasting {

    public interface RayHitCallback {

        void intersected(final Array<Physics2DWorld.Intersection> results);

    }

    public static class Ray implements MemoryPool.Reset {

        public float originX;
        public float originY;
        public float dirX;
        public float dirY;
        public float dst;
        public int   bitmask;

        public Ray() {}

        @Override
        public void reset() {
            dst = Float.POSITIVE_INFINITY;
            bitmask = 0;
        }

    }

    public static class Intersection implements MemoryPool.Reset {

        public Body body      = null;
        public Vector2 point     = new Vector2();
        public Vector2 direction = new Vector2();
        public float         dst2      = 0;

        public Intersection() {}

        @Override
        public void reset() {
            body = null;
        }

    }

}
