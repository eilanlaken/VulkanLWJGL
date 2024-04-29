package org.example.engine.core.graphics;

import org.example.engine.core.memory.MemoryUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public final class GraphicsTextureBuilder {

    public static final int maxTextureSize = GraphicsUtils.getMaxTextureSize();

    private GraphicsTextureBuilder() {}

    public static GraphicsTexture buildFromFilePath(final String path) {
        Data data = getTextureDataFromFilePath(path);
        return createTexture(data, null, null, null, null);
    }

    public static GraphicsTexture buildFromClassPath(final String name) {
        ByteBuffer imageBuffer;

        // Load the image resource into a ByteBuffer
        try (InputStream is = GraphicsTextureBuilder.class.getClassLoader().getResourceAsStream(name);
             ReadableByteChannel rbc = Channels.newChannel(is)) {
            imageBuffer = BufferUtils.createByteBuffer(1024);

            while (true) {
                int bytes = rbc.read(imageBuffer);
                if (bytes == -1) {
                    break;
                }
                if (imageBuffer.remaining() == 0) {
                    imageBuffer = MemoryUtils.resizeBuffer(imageBuffer, imageBuffer.capacity() * 2);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        imageBuffer.flip(); // Flip the buffer for reading
        int[] width = new int[1];
        int[] height = new int[1];
        int[] comp = new int[1];

        ByteBuffer buffer = STBImage.stbi_load_from_memory(imageBuffer, width, height, comp, 4);
        if (buffer == null) {
            throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
        }

        Data data = new Data(width[0], height[0], buffer);
        return createTexture(data, null, null, null, null);
    }

    public static Data getTextureDataFromFilePath(final String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);
            final ByteBuffer buffer = STBImage.stbi_load(path, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (buffer == null) throw new RuntimeException("Failed to load a texture file. Check that the path is correct: " + path
                    + System.lineSeparator() + "STBImage error: "
                    + STBImage.stbi_failure_reason());
            final int width = widthBuffer.get();
            final int height = heightBuffer.get();
            if (width > maxTextureSize || height > maxTextureSize)
                throw new IllegalStateException("Trying to load texture " + path + " with resolution (" + width + "," + height + ") greater than allowed on your GPU: " + maxTextureSize);
            return new Data(width, height, buffer);
        }
    }

    public static GraphicsTexture createTexture(Data data, GraphicsTexture.Filter magFilter, GraphicsTexture.Filter minFilter, GraphicsTexture.Wrap uWrap, GraphicsTexture.Wrap vWrap) {
        if (magFilter == null) magFilter = GraphicsTexture.Filter.MIP_MAP_NEAREST_NEAREST;
        if (minFilter == null) minFilter = GraphicsTexture.Filter.MIP_MAP_NEAREST_NEAREST;
        if (uWrap == null) uWrap = GraphicsTexture.Wrap.CLAMP_TO_EDGE;
        if (vWrap == null) vWrap = GraphicsTexture.Wrap.CLAMP_TO_EDGE;

        int glHandle = GL11.glGenTextures();
        GraphicsTexture texture = new GraphicsTexture(glHandle,
                data.width, data.height,
                magFilter, minFilter,
                uWrap, vWrap
        );
        GraphicsTextureBinder.bind(texture);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        // TODO: here we need to see if we want to: generate mipmaps, use anisotropic filtering, what level of anisotropy etc
        // TODO: For a raw Texture with no TextureMap, use defaults.
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, data.width, data.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        // TODO: we need to see if the anisotropic filtering extension is available. If yes, create that instead of mipmaps.
        STBImage.stbi_image_free(data.buffer);
        return texture;
    }

    protected static class Data {

        public final int width;
        public final int height;
        public final ByteBuffer buffer;

        public Data(int width, int height, ByteBuffer buffer) {
            this.width = width;
            this.height = height;
            this.buffer = buffer;
        }
    }

}
