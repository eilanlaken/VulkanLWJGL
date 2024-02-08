package org.example.engine.core.memory;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public final class MemoryUtils {

    public static FloatBuffer store(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

}
