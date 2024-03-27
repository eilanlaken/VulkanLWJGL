package org.example.engine.core.collections;

import java.util.LinkedList;

public class CyclicLinkedList<T> extends LinkedList<T> {

    @Override
    public T get(int index) {
        int size = size();
        if (index >= size) return super.get(index % size);
        else if (index < 0) return super.get(index % size + size);
        return super.get(index);
    }

}
