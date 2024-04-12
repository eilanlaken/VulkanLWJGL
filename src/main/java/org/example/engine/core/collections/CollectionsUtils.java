package org.example.engine.core.collections;

import org.example.engine.core.math.MathUtils;

public final class CollectionsUtils {

    private CollectionsUtils() {}

    public static Object createArray(Class clazz, int size) {
        return java.lang.reflect.Array.newInstance(clazz, size);
    }

    public static int tableSize(int capacity, float loadFactor) {
        if (capacity < 0) throw new IllegalArgumentException("capacity must be >= 0: " + capacity);
        int tableSize = MathUtils.nextPowerOfTwo(Math.max(2, (int) Math.ceil(capacity / loadFactor)));
        if (tableSize > 1 << 30) throw new IllegalArgumentException("The required capacity is too large: " + capacity);
        return tableSize;
    }

    public static boolean isSorted(int[] array, boolean ascending) {
        if (array == null || array.length <= 1) {
            return true;
        }

        for (int i = 0; i < array.length - 1; i++) {
            boolean increment = array[i + 1] >= array[i];
            if (ascending && !increment) return false;
            if (!ascending && increment) return false;
        }

        return true;
    }

}
