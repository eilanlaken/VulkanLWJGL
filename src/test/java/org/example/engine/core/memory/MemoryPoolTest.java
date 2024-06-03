package org.example.engine.core.memory;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MemoryPoolTest {

    @Test
    void createPools() {
        Assertions.assertThrows(RuntimeException.class, () -> new MemoryPool<>(ClassC.class,  20));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MemoryPool<>(ClassAbstract.class, 20));
        Assertions.assertThrows(IllegalArgumentException.class,        () -> new MemoryPool<>(ClassB.class, -1));
        Assertions.assertThrows(IllegalArgumentException.class,        () -> new MemoryPool<>(ClassB.class,  0));
        Assertions.assertThrows(IllegalArgumentException.class,    () -> new MemoryPool<>(InterfaceA.class, 20));
        Assertions.assertDoesNotThrow(() -> new MemoryPool<>(ClassB.class, 20));
    }

    @Test
    void allocate() {
        MemoryPool<ClassA> aMemoryPool = new MemoryPool<>(ClassA.class, 10);
        ClassA a = aMemoryPool.allocate();
        Assertions.assertEquals(0, a.x);
        // test grabbing objects beyond initial capacity
        for (int i = 0; i < 30; i++) {
            ClassA aa = aMemoryPool.allocate();
            Assertions.assertEquals(0, aa.x);
        }
        MemoryPool<ClassB> bMemoryPool = new MemoryPool<>(ClassB.class, 10);
        ClassB b = bMemoryPool.allocate();
        Assertions.assertEquals(0, b.x);
        Assertions.assertNull(b.obj);
    }

    @Test
    void free() {
        MemoryPool<ClassA> aMemoryPool = new MemoryPool<>(ClassA.class, 1);
        ClassA a = aMemoryPool.allocate();
        a.x = 10;
        aMemoryPool.free(a);
        ClassA aa = aMemoryPool.allocate();
        Assertions.assertEquals(0, aa.x);
        aa = null;
        aMemoryPool.free(aa);
        ClassA aaa = aMemoryPool.allocate();
        Assertions.assertEquals(0, aaa.x);
        Assertions.assertDoesNotThrow(() -> aMemoryPool.free(null));
    }

    @Test
    void freeAll() {
        MemoryPool<ClassA> aMemoryPool = new MemoryPool<>(ClassA.class, 20);
        Array<ClassA> array = new Array<>(true, 5);
        for (int i = 0; i < 5; i++) {
            ClassA a = aMemoryPool.allocate();
            a.x = MathUtils.random(100);
            array.add(a);
        }
        aMemoryPool.freeAll(array);
        for (ClassA a : array) {
            Assertions.assertEquals(0, a.x);
        }
        array.clear();
        array.addAll(null, null, null);
        Assertions.assertDoesNotThrow(() -> aMemoryPool.freeAll(array));
    }

    public static class ClassA implements MemoryPool.Reset {

        public int x;

        @Override
        public void reset() {
            this.x = 0;
        }

    }

    public static class ClassB implements MemoryPool.Reset {

        public int x;
        public Object obj;

        @Override
        public void reset() {
            this.x = 0;
            this.obj = null;
        }
    }

    public static class ClassC implements MemoryPool.Reset {

        public int x, y;

        public ClassC(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void reset() {
            this.x = 0;
            this.y = 0;
        }
    }

    public interface InterfaceA extends MemoryPool.Reset {
        void function();
        @Override
        default void reset() {};
    }

    public abstract static class ClassAbstract implements MemoryPool.Reset {

    }

}