package org.example.engine.core.collections;

import java.util.Objects;

public class Tuple2<T, U> {

    public T t1;
    public U t2;

    public Tuple2(T t1, U t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple2<?, ?> tuplePair = (Tuple2<?, ?>) o;
        return Objects.equals(t1, tuplePair.t1) &&
               Objects.equals(t2, tuplePair.t2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t1, t2);
    }

    @Override
    public String toString() {
        return "<" + t1 + ", " + t2 + ">";
    }

}