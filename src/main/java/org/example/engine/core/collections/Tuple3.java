package org.example.engine.core.collections;

import java.util.Objects;

public class Tuple3<T, U, V> {

    public final T t1;
    public final U t2;
    public final V t3;

    public Tuple3(T t1, U t2, V t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple3<?, ?, ?> tupleTriple = (Tuple3<?, ?, ?>) o;
        return Objects.equals(t1, tupleTriple.t1) &&
               Objects.equals(t2, tupleTriple.t2) &&
               Objects.equals(t3, tupleTriple.t3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t1, t2, t3);
    }

}