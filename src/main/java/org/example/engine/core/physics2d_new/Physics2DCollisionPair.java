package org.example.engine.core.physics2d_new;

import org.example.engine.core.memory.MemoryPool;

import java.util.Objects;

public class Physics2DCollisionPair implements MemoryPool.Reset {

    public Physics2DBody a;
    public Physics2DBody b;

    public Physics2DCollisionPair() {}

    public Physics2DCollisionPair(Physics2DBody a, Physics2DBody b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Physics2DCollisionPair that = (Physics2DCollisionPair) o;
        if (Objects.equals(a, that.a) && Objects.equals(b, that.b)) return true;
        if (Objects.equals(b, that.a) && Objects.equals(a, that.b)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(a) + Objects.hashCode(b); // A commutative operation to ensure symmetry
    }

    @Override
    public void reset() {
        a = null;
        b = null;
    }

}
