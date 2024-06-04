package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape2d.Shape2D;

import java.util.Objects;

public class WorldCollision {

    public static final class Manifold implements MemoryPool.Reset {

        public Body    body_a        = null;
        public Body    body_b        = null;
        public Shape2D shape_a       = null;
        public Shape2D shape_b       = null;
        public int     contacts      = 0;
        public float   depth         = 0;
        public Vector2 normal        = new Vector2();
        public Vector2 contactPoint1 = new Vector2();
        public Vector2 contactPoint2 = new Vector2();

        @Override
        public void reset() {
            this.contacts = 0;
        }

    }

    public static final class Cell implements MemoryPool.Reset {

        private final Array<Body> bodies = new Array<>(false, 2);

        private boolean active = false;

        public Cell() {}

        @Override
        public void reset() {
            bodies.clear();
            active = false;
        }

    }

    public static class Pair implements MemoryPool.Reset {

        public Body a;
        public Body b;

        public Pair() {}

        public Pair(Body a, Body b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WorldCollision.Pair that = (WorldCollision.Pair) o;
            if (Objects.equals(a, that.a) && Objects.equals(b, that.b)) return true;
            if (Objects.equals(b, that.a) && Objects.equals(a, that.b)) return true;
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(a) + Objects.hashCode(b); // A commutative operation to ensure symmetry
        }

        @Override
        public void reset() {}

    }

}
