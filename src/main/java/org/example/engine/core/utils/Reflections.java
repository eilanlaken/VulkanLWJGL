package org.example.engine.core.utils;

public final class Reflections {

    public static Object createArray(Class clazz, int size) {
        return java.lang.reflect.Array.newInstance(clazz, size);
    }

}
