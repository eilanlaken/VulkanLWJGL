package org.example.engine.core.collections;

import org.example.engine.core.math.MathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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