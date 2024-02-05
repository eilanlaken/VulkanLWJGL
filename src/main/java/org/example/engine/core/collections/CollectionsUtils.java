package org.example.engine.core.collections;

public final class CollectionsUtils {

    public static Object createArray(Class clazz, int size) {
        return java.lang.reflect.Array.newInstance(clazz, size);
    }

}
