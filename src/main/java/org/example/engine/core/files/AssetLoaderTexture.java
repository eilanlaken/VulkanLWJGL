package org.example.engine.core.files;

import org.example.engine.core.graphics.Texture;
import org.example.engine.core.graphics.TextureFilter;
import org.example.engine.core.graphics.TextureWrap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class AssetLoaderTexture {

    public Texture load(final String path) {
        int width;
        int height;
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);
            buffer = STBImage.stbi_load(path, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (buffer == null) throw new RuntimeException("Failed to load Texture: " + path);
            width = widthBuffer.get();
            height = heightBuffer.get();
        }
        int glHandle = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, glHandle);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        return new Texture(glHandle, width, height, TextureFilter.MIP_MAP_LINEAR_LINEAR, TextureWrap.CLAMP_TO_EDGE);
    }

}
