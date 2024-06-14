package org.example.engine.core.physics2d;

import org.example.engine.core.memory.MemoryPool;

import java.util.Objects;

public final class CollisionPair implements MemoryPool.Reset {

    private BodyCollider a;
    private BodyCollider b;

    public CollisionPair() {}

    void set(BodyCollider a, BodyCollider b) {
        if (a.body.index < b.body.index) {
            this.a = a;
            this.b = b;
        } else {
            this.a = b;
            this.b = a;
        }
    }

    BodyCollider getA() {
        return a;
    }

    BodyCollider getB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollisionPair that = (CollisionPair) o;
        return Objects.equals(a, that.a) && Objects.equals(b, that.b);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(a) + Objects.hashCode(b); // A commutative operation to ensure symmetry
    }

    @Override
    public void reset() {}

}
