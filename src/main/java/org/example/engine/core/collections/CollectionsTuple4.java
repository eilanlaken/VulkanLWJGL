package org.example.engine.core.collections;

import java.util.Objects;

public class CollectionsTuple4<T, U, V, Y> {

    public final T first;
    public final U second;
    public final V third;
    public final Y fourth;

    public CollectionsTuple4(T first, U second, V third, Y fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionsTuple4<?, ?, ?, ?> tuple4 = (CollectionsTuple4<?, ?, ?, ?>) o;
        return Objects.equals(first, tuple4.first) &&
               Objects.equals(second, tuple4.second) &&
               Objects.equals(third, tuple4.third) &&
               Objects.equals(fourth, tuple4.fourth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third, fourth);
    }

}