package org.example.engine.core.memory;

import org.example.engine.core.collections.CollectionsArray;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MemoryPool<T> {

    private final CollectionsArray<T> freeObjects;
    private final Constructor<T> constructor;
    private final int initialCapacity;

    // TODO: create a dev and release
    public MemoryPool(Class<T> type) throws RuntimeException {
        this(type, 1000);
    }

    public MemoryPool(Class<T> type, int initialCapacity) {
        if (initialCapacity <= 0) throw new IllegalArgumentException("Memory pool initial capacity must be greater than 0. Got: " + initialCapacity);
        this.initialCapacity = initialCapacity;
        this.freeObjects = new CollectionsArray<>(initialCapacity);
        try {
            this.constructor = type.getConstructor();
            for (int i = 0; i < freeObjects.size; i++) {
                freeObjects.add(constructor.newInstance());
            }
        } catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException("Classes managed by a " + MemoryPool.class.getSimpleName() + " MUST declare a no-args constructor.");
        }
    }

    public synchronized T grabOne() throws RuntimeException {
        try {
            if (this.freeObjects.size == 0) {
                for (int i = 0; i < initialCapacity; i++) {
                    freeObjects.add(constructor.newInstance());
                }
            }
            return this.freeObjects.pop();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to create new instance: " + this.constructor.getDeclaringClass().getName(), e);
        }
    }

    public synchronized void letGo(T obj) {
        if (obj == null) throw new IllegalArgumentException("object cannot be null.");
        this.freeObjects.add(obj);
    }

}
