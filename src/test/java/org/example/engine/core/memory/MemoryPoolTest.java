package org.example.engine.core.memory;

import org.example.engine.core.math.MathVector2;
import org.example.engine.core.shape.Shape2D;
import org.example.engine.core.shape.Shape2DCircle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MemoryPoolTest {

    @Test
    void createPools() {
        Assertions.assertThrows(RuntimeException.class, () -> new MemoryPool<>(PooledClassC.class,  20));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MemoryPool<>(SomeAbstractClass.class, 20));
        Assertions.assertThrows(IllegalArgumentException.class,      () -> new MemoryPool<>(PooledClassB.class, -1));
        Assertions.assertThrows(IllegalArgumentException.class,      () -> new MemoryPool<>(PooledClassB.class,  0));
        Assertions.assertThrows(IllegalArgumentException.class,     () -> new MemoryPool<>(SomeInterface.class, 20));
        Assertions.assertDoesNotThrow(() -> new MemoryPool<>(PooledClassB.class, 20));
    }

    @Test
    void grabOne() {
        MemoryPool<PooledClassA> aMemoryPool = new MemoryPool<>(PooledClassA.class, 10);
        PooledClassA a = aMemoryPool.grabOne();
        Assertions.assertEquals(0, a.x);
        // test grabbing objects beyond initial capacity
        for (int i = 0; i < 30; i++) {
            PooledClassA aa = aMemoryPool.grabOne();
            Assertions.assertEquals(0, aa.x);
        }
        MemoryPool<PooledClassB> bMemoryPool = new MemoryPool<>(PooledClassB.class, 10);
        PooledClassB b = bMemoryPool.grabOne();
        Assertions.assertEquals(0, b.x);
        Assertions.assertNull(b.obj);
    }

    @Test
    void letGo() {
        MemoryPool<PooledClassA> aMemoryPool = new MemoryPool<>(PooledClassA.class, 1);
        PooledClassA a = aMemoryPool.grabOne();
        a.x = 10;
        aMemoryPool.letGo(a);
        PooledClassA aa = aMemoryPool.grabOne();
        Assertions.assertEquals(0, aa.x);
        aa = null;
        aMemoryPool.letGo(aa);
        PooledClassA aaa = aMemoryPool.grabOne();
        Assertions.assertEquals(0, aaa.x);
    }

    public static class PooledClassA implements MemoryPool.Reset {

        public int x;

        @Override
        public void reset() {
            this.x = 0;
        }

    }

    public static class PooledClassB implements MemoryPool.Reset {

        public int x;
        public Object obj;

        @Override
        public void reset() {
            this.x = 0;
            this.obj = null;
        }
    }

    public static class PooledClassC implements MemoryPool.Reset {

        public int x, y;

        public PooledClassC(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void reset() {
            this.x = 0;
            this.y = 0;
        }
    }

    public interface SomeInterface extends MemoryPool.Reset {
        void function();
        @Override
        default void reset() {};
    }

    public abstract class SomeAbstractClass implements MemoryPool.Reset {

    }

}