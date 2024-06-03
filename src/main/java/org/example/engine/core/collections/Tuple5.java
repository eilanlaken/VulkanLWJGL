package org.example.engine.core.collections;

import java.util.Objects;

public class Tuple5<T, U, V, Y, Z> {

    public final T t1;
    public final U t2;
    public final V t3;
    public final Y t4;
    public final Z t5;

    public Tuple5(T t1, U t2, V t3, Y t4, Z t5) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple5<?, ?, ?, ?, ?> tuple5 = (Tuple5<?, ?, ?, ?, ?>) o;
        return Objects.equals(t1, tuple5.t1) &&
               Objects.equals(t2, tuple5.t2) &&
               Objects.equals(t3, tuple5.t3) &&
               Objects.equals(t4, tuple5.t4) &&
               Objects.equals(t5, tuple5.t5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t1, t2, t3, t4, t5);
    }

}