package org.example.engine.core.physics2d_new;

import org.example.engine.core.memory.MemoryPool;

import java.util.Objects;

final class CollisionPair implements MemoryPool.Reset {

    public Body a;
    public Body b;

    public CollisionPair() {}

    public CollisionPair(Body a, Body b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollisionPair that = (CollisionPair) o;
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