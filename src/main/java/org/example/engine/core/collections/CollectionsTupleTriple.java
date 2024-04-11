package org.example.engine.core.collections;

import java.util.Objects;

public class CollectionsTupleTriple<T, U, V> {

    public final T first;
    public final U second;
    public final V third;

    public CollectionsTupleTriple(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionsTupleTriple<?, ?, ?> tupleTriple = (CollectionsTupleTriple<?, ?, ?>) o;
        return Objects.equals(first, tupleTriple.first) &&
               Objects.equals(second, tupleTriple.second) &&
               Objects.equals(third, tupleTriple.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

}