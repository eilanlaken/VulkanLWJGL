package org.example.engine.core.collections;

import org.example.engine.core.math.MathUtils;

public final class CollectionsUtils {

    public static Object createArray(Class clazz, int size) {
        return java.lang.reflect.Array.newInstance(clazz, size);
    }

    public static int tableSize(int capacity, float loadFactor) {
        if (capacity < 0) throw new IllegalArgumentException("capacity must be >= 0: " + capacity);
        int tableSize = MathUtils.nextPowerOfTwo(Math.max(2, (int) Math.ceil(capacity / loadFactor)));
        if (tableSize > 1 << 30) throw new IllegalArgumentException("The required capacity is too large: " + capacity);
        return tableSize;
    }


}
