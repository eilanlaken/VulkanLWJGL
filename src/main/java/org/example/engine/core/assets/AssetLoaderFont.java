package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.Font;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class AssetLoaderFont implements AssetLoader<Font> {

    private ByteBuffer ttf;
    private STBTTFontinfo info;

    private int ascent;
    private int descent;
    private int lineGap;

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return null;
    }

    @Override
    public void asyncLoad(String path) {
        try {
            ttf = AssetUtils.readFileToByteBuffer(path, 512 * 1024);
        } catch (Exception e) {
            throw new AssetsException("Error reading Font data to ByteBuffer: " + e.getMessage());
        }

        info = STBTTFontinfo.create();
        if (!STBTruetype.stbtt_InitFont(info, ttf)) {
            throw new IllegalStateException("Failed to initialize font information.");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pAscent  = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            STBTruetype.stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);

            ascent = pAscent.get(0);
            descent = pDescent.get(0);
            lineGap = pLineGap.get(0);
        }
    }

    @Override
    public Font create() {
        return null;
    }

}
