package org.example.engine.core.collections;

import org.example.engine.core.math.MathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CollectionsArrayTest {

    @Test
    void add() {
    }

    @Test
    void testAdd() {
    }

    @Test
    void testAdd1() {
    }

    @Test
    void testAdd2() {
    }

    @Test
    void addAll() {
    }

    @Test
    void testAddAll() {
    }

    @Test
    void testAddAll1() {
    }

    @Test
    void testAddAll2() {
    }

    @Test
    void get() {
    }

    @Test
    void testGet() {
    }

    @Test
    void getCircular() {
    }

    @Test
    void set() {
        CollectionsArray<Integer> arr1 = new CollectionsArray<>(5);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> arr1.set(0, 1,  false));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> arr1.set(2, 1,  false));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> arr1.set(-1, 1, false));

        CollectionsArray<Integer> arr2 = new CollectionsArray<>(1);
        arr2.add(1);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> arr2.set(-1, 1,  false));
        Assertions.assertDoesNotThrow(() -> arr2.set(0, 2, false));
        Assertions.assertEquals(2, arr2.get(0));
        Assertions.assertEquals(1, arr2.size);

        CollectionsArray<Integer> arr3 = new CollectionsArray<>(1);
        arr3.add(0,1,2,3);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> arr3.set(-1, 7,  true));
        Assertions.assertDoesNotThrow(() -> arr3.set(2, 7, true));
        Assertions.assertEquals(7, arr3.get(2));
        Assertions.assertEquals(4, arr3.size);

        Assertions.assertDoesNotThrow(() -> arr3.set(10, 7, true));
        Assertions.assertEquals(7, arr3.get(10));
        Assertions.assertEquals(11, arr3.size);
        Assertions.assertNull(arr3.get(4));
        Assertions.assertNull(arr3.get(5));
        Assertions.assertNull(arr3.get(6));
        Assertions.assertNull(arr3.get(7));
        Assertions.assertNull(arr3.get(8));
        Assertions.assertNull(arr3.get(9));

        Assertions.assertDoesNotThrow(() -> arr3.set(7, 7, true));
        Assertions.assertEquals(7, arr3.get(7));
        Assertions.assertEquals(11, arr3.size);

        arr3.clear();
        Assertions.assertDoesNotThrow(() -> arr3.set(7, 7, true));
    }

    @Test
    void insert() {
    }

    @Test
    void swap() {
    }

    @Test
    void contains() {
    }

    @Test
    void containsAll() {
    }

    @Test
    void containsAny() {
    }

    @Test
    void containsDuplicates() {
        Object a1 = new Object();
        Object a2 = new Object();
        Object a3 = new Object();
        Object a4 = new Object();
        Object a5 = new Object();

        CollectionsArray<Object> arr1 = new CollectionsArray<>();
        arr1.add(a1);
        arr1.add(a1);
        Assertions.assertTrue(arr1.containsDuplicates(true));

        CollectionsArray<Object> arr2 = new CollectionsArray<>();
        arr2.add(a1);
        arr2.add(a2);
        Assertions.assertFalse(arr2.containsDuplicates(true));

        CollectionsArray<Object> arr3 = new CollectionsArray<>();
        arr3.add(a1);
        arr3.add(a2);
        arr3.add(a3);
        arr3.add(a4);
        arr3.add(a5);
        Assertions.assertFalse(arr3.containsDuplicates(true));

        CollectionsArray<Object> arr4 = new CollectionsArray<>();
        arr4.add(null);
        arr4.add(a2);
        Assertions.assertFalse(arr4.containsDuplicates(true));

        CollectionsArray<Object> arr5 = new CollectionsArray<>();
        arr5.add(null);
        arr5.add(null);
        Assertions.assertTrue(arr5.containsDuplicates(true));

        CollectionsArray<Object> arr6 = new CollectionsArray<>();
        Assertions.assertFalse(arr6.containsDuplicates(true));
    }

    @Test
    void indexOf() {
    }

    @Test
    void removeValue() {
    }

    @Test
    void removeIndex() {
    }

    @Test
    void removeAll() {
    }

    @Test
    void pop() {
    }

    @Test
    void peek() {
    }

    @Test
    void first() {
    }

    @Test
    void notEmpty() {
    }

    @Test
    void isEmpty() {
    }

    @Test
    void clear() {
    }

    @Test
    void pack() {
    }

    @Test
    void ensureCapacity() {
    }

    @Test
    void setSize() {
    }

    @Test
    void resize() {
    }

    @Test
    void sort() {
        CollectionsArray<Float> array = new CollectionsArray<>();
        array.add(5f);
        array.add(-1f);
        array.add(2f);
        array.add(0f);
        array.add(0f);
        array.sort();
        Assertions.assertEquals(-1f, array.get(0), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0f, array.get(1), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0f, array.get(2), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2f, array.get(3), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(5f, array.get(4), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void testSort() {
    }

    @Test
    void reverse() {
    }

    @Test
    void shuffle() {
    }

    @Test
    void removeDuplicates() {
    }

    @Test
    void truncate() {
    }

    @Test
    void random() {
    }

    @Test
    void toArray() {
    }

    @Test
    void testToArray() {
    }

    @Test
    void testEquals() {
    }
}