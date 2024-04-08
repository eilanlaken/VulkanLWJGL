package org.example.engine.core.collections;

import java.util.Objects;

public class TuplePair<T, U> {

    public T first;
    public U second;

    public TuplePair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TuplePair<?, ?> tuplePair = (TuplePair<?, ?>) o;
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