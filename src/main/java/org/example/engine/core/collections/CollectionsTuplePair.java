package org.example.engine.core.collections;

import java.util.Objects;

public class CollectionsTuplePair<T, U> {

    public T first;
    public U second;

    public CollectionsTuplePair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionsTuplePair<?, ?> tuplePair = (CollectionsTuplePair<?, ?>) o;
        return Objects.equals(first, tuplePair.first) &&
               Objects.equals(second, tuplePair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "<" + first + ", " + second + ">";
    }

}