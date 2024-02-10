package org.example.engine.core.files;

import org.example.engine.core.graphics.Texture;
import org.example.engine.core.graphics.TextureBinder;
import org.example.engine.core.graphics.TextureSamplingFilter;
import org.example.engine.core.graphics.TextureSamplingWrap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class AssetLoaderTexture {

    public final int maxTextureSize;

    public AssetLoaderTexture() {
        maxTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
    }

    public Texture load(final String path) {
        int width;
        int height;
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);
            System.out.println("path");
            buffer = STBImage.stbi_load(path, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (buffer == null) throw new RuntimeException("Failed to load Texture: " + path);
            width = widthBuffer.get();
            height = heightBuffer.get();
            if (width > maxTextureSize || height > maxTextureSize)
                throw new IllegalStateException("Trying to load texture " + path + " with resolution (" +
                        width + "," + height + ") greater than allowed on your GPU: " + maxTextureSize);
        }
        int glHandle = GL11.glGenTextures();
        Texture texture = new Texture(glHandle,
                width, height,
                TextureSamplingFilter.MIP_MAP_NEAREST_NEAREST, TextureSamplingFilter.MIP_MAP_NEAREST_NEAREST,
                TextureSamplingWrap.CLAMP_TO_EDGE, TextureSamplingWrap.CLAMP_TO_EDGE
        );
        TextureBinder.bindTexture(texture);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        // TODO: here we need to see if we want to: generate mipmaps, use anisotropic filtering, what level of anisotropy etc
        // TODO: For a raw Texture with no TextureMap, use defaults.
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        // TODO: we need to see if the anisotropic filtering extension is available. If yes, create that instead of mipmaps.
        STBImage.stbi_image_free(buffer);
        return texture;
    }

}
