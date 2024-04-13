package org.example.engine.core.collections;

import java.util.Objects;

public class CollectionsTuple4<T, U, V, Y> {

    public final T t1;
    public final U t2;
    public final V t3;
    public final Y t4;

    public CollectionsTuple4(T t1, U t2, V t3, Y t4) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionsTuple4<?, ?, ?, ?> tuple4 = (CollectionsTuple4<?, ?, ?, ?>) o;
        return Objects.equals(t1, tuple4.t1) &&
               Objects.equals(t2, tuple4.t2) &&
               Objects.equals(t3, tuple4.t3) &&
               Objects.equals(t4, tuple4.t4);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t1, t2, t3, t4);
    }

}