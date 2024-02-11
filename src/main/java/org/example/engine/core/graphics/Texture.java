package org.example.engine.core.graphics;

import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;

// TODO: finish
public class Texture implements Resource {

    protected int glHandle;
    public final int width;
    public final int height;
    public TextureParamFilter magFilter;
    public TextureParamFilter minFilter;
    public TextureParamWrap uWrap;
    public TextureParamWrap vWrap;
    // TODO: see what's up
    protected float anisotropicFilterLevel = 1.0f;
    private static float maxAnisotropicFilterLevel = 0;

    public Texture(int glHandle,
                   final int width, final int height,
                   TextureParamFilter magFilter, TextureParamFilter minFilter,
                   TextureParamWrap uWrap, TextureParamWrap vWrap) {
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
        glHandle = 0;
    }

}
