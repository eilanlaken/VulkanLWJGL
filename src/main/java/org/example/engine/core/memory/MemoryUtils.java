package org.example.engine.core.memory;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public final class MemoryUtils {

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

}
