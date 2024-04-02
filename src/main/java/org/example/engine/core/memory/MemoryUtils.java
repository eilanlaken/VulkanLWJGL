package org.example.engine.core.memory;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.*;

public final class MemoryUtils {

    private MemoryUtils() {}

    public static FloatBuffer store(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static IntBuffer store(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static ShortBuffer store(short[] data) {
        ShortBuffer buffer = MemoryUtil.memAllocShort(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static FloatBuffer createFloatBuffer(final int capacity) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(capacity * Float.BYTES);
        byteBuffer.order(ByteOrder.nativeOrder()); // Set the byte order to native (usually little-endian or big-endian)
        return byteBuffer.asFloatBuffer();
    }

    public static FloatBuffer store(final float[] data, FloatBuffer target) {
        target.put(data);
        return target.flip();
    }

    public static FloatBuffer store(final float[] data, int offset, int count, FloatBuffer target) {
        target.put(data, offset, count);
        return target.flip();
    }

    public static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

}
