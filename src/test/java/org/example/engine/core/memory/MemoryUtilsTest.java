package org.example.engine.core.memory;

import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lwjgl.system.MemoryStack;

import java.lang.instrument.Instrumentation;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class MemoryUtilsTest {

    @Test
    void store() {
    }

    @Test
    void testStore() {
    }

    @Test
    void testStore1() {
    }

    @Test
    void createFloatBuffer() {
    }

    @Test
    void testStore2() {
    }

    @Test
    void testStore3() {
    }

    @Test
    void resizeBuffer() {
    }

    @Test
    void testStackPush() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer b = stack.malloc(Float.BYTES * 2 + Character.BYTES);
            b.putFloat(2.2f);
            b.putChar('a');
            b.putFloat(4.5f);
            b.flip();
            float x = b.getFloat();
            char a = b.getChar();
            float y = b.getFloat();
            Assertions.assertEquals(2.2f, x, MathUtils.FLOAT_ROUNDING_ERROR);
            Assertions.assertEquals('a', a);
            Assertions.assertEquals(4.5f, y, MathUtils.FLOAT_ROUNDING_ERROR);
        }
    }

    @Test
    void testStackPushPop() {
        MemoryStack stack = MemoryStack.stackPush();
        {
            ByteBuffer b = stack.malloc(Float.BYTES * 2);
            b.putFloat(2.2f);
            b.putFloat(4.5f);
            b.flip();
            float x = b.getFloat();
            float y = b.getFloat();
            Assertions.assertEquals(2.2f, x, MathUtils.FLOAT_ROUNDING_ERROR);
            Assertions.assertEquals(4.5f, y, MathUtils.FLOAT_ROUNDING_ERROR);
        }
        stack.close();
    }

}