package org.example.engine.core.memory;

import org.example.engine.core.collections.Array;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Pool<T extends Pooled> {

    private final Array<T> freeObjects;
    private final Constructor<T> constructor;

    public Pool(Class<T> type) throws RuntimeException {
        this(type, 400);
    }

    public Pool(Class<T> type, int initialCapacity) {
        this.freeObjects = new Array<>(initialCapacity);
        try {
            this.constructor = type.getConstructor();
            for (int i = 0; i < freeObjects.size; i++) {
                freeObjects.add((T) constructor.newInstance());
            }
        } catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException("Classes managed by a " + Pool.class.getSimpleName() + " MUST declare a no-args constructor.");
        }
    }

    public T grabOne() throws RuntimeException {
        try {
            return this.freeObjects.size == 0 ? this.constructor.newInstance() : this.freeObjects.pop();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to create new instance: " + this.constructor.getDeclaringClass().getName(), e);
        }
    }

    public void letGo(T obj) {
        if (obj == null) throw new IllegalArgumentException("object cannot be null.");
        this.freeObjects.add(obj);
        obj.reset();
    }

}
