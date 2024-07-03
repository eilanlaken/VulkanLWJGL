package org.example.engine.core.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class CollectionsUtilsTest {

    @Test
    void shuffle() {

    }

    @Test
    void isSorted() {
        int[] a1 = new int[] {};
        Assertions.assertTrue(CollectionsUtils.isSorted(a1, true));
        Assertions.assertTrue(CollectionsUtils.isSorted(a1, false));

        int[] a2 = new int[] {1,2,3};
        Assertions.assertTrue(CollectionsUtils.isSorted(a2, true));
        Assertions.assertFalse(CollectionsUtils.isSorted(a2, false));

        int[] a3 = new int[] {0,-1,2};
        Assertions.assertFalse(CollectionsUtils.isSorted(a3, true));
        Assertions.assertFalse(CollectionsUtils.isSorted(a3, false));

        int[] a4 = new int[] {0,0,1};
        Assertions.assertTrue(CollectionsUtils.isSorted(a4, true));
        Assertions.assertFalse(CollectionsUtils.isSorted(a4, false));

        int[] a5 = new int[] {1,1,2,3,3,4,5};
        Assertions.assertTrue(CollectionsUtils.isSorted(a5, true));
        Assertions.assertFalse(CollectionsUtils.isSorted(a5, false));

        int[] a6 = new int[] {5,4,3,2,1};
        Assertions.assertTrue(CollectionsUtils.isSorted(a6, false));
        Assertions.assertFalse(CollectionsUtils.isSorted(a6, true));
    }

}