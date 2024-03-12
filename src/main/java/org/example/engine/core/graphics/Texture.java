package org.example.engine.core.graphics;

import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;

// TODO: finish
public class Texture implements Resource {

    protected int handle;
    public final int width;
    public final int height;
    public final float invWidth;
    public final float invHeight;
    public TextureParamFilter magFilter;
    public TextureParamFilter minFilter;
    public TextureParamWrap uWrap;
    public TextureParamWrap vWrap;
    // TODO: see what's up
    protected float anisotropicFilterLevel = 1.0f;
    private static float maxAnisotropicFilterLevel = 0;

    public Texture(int handle,
                   final int width, final int height,
                   TextureParamFilter magFilter, TextureParamFilter minFilter,
                   TextureParamWrap uWrap, TextureParamWrap vWrap) {
        this.handle = handle;
        this.width = width;
        this.height = height;
        this.invWidth = 1.0f / width;
        this.invHeight = 1.0f / height;
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        this.uWrap = uWrap;
        this.vWrap = vWrap;
    }

    @Override
    public void free() {
        GL11.glDeleteTextures(handle);
        handle = 0;
    }

}
