package org.example.engine.core.collections;

import java.util.Objects;

public class TupleTriple<T, U, V> {

    public T first;
    public U second;
    public V third;

    public TupleTriple(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TupleTriple<?, ?, ?> tupleTriple = (TupleTriple<?, ?, ?>) o;
        return Objects.equals(first, tupleTriple.first) &&
               Objects.equals(second, tupleTriple.second) &&
               Objects.equals(third, tupleTriple.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

}