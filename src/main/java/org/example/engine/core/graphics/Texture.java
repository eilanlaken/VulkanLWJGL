package org.example.engine.core.graphics;

import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

// TODO: finish
public class Texture implements Resource {

    public final int glHandle;
    public final int width;
    public final int height;
    public TextureSamplingFilter magFilter;
    public TextureSamplingFilter minFilter;
    public TextureSamplingWrap uWrap;
    public TextureSamplingWrap vWrap;
    // TODO: see what's up
    protected float anisotropicFilterLevel = 1.0f;
    private static float maxAnisotropicFilterLevel = 0;

    public Texture(int glHandle,
                   final int width, final int height,
                   TextureSamplingFilter magFilter, TextureSamplingFilter minFilter,
                   TextureSamplingWrap uWrap, TextureSamplingWrap vWrap) {
        this.glHandle = glHandle;
        this.width = width;
        this.height = height;
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        this.uWrap = uWrap;
        this.vWrap = vWrap;

    }

    @Override
    public void free() {
        GL11.glDeleteTextures(glHandle);
    }
}
