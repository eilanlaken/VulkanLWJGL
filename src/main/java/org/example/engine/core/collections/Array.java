package org.example.engine.core.collections;

import java.util.Iterator;

public class Array<T> implements Iterable<T> {

    public T[] items;
    public int size;
    public boolean ordered;

    public Array() {
        this(16, true);
    }

    public Array (int capacity) {
        this(capacity, true);
    }

    public Array(int capacity, boolean ordered) {
        this.ordered = ordered;
        this.items = (T[]) new Object[capacity];
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }
}
