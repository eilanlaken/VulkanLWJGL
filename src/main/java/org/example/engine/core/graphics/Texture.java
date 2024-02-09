package org.example.engine.core.graphics;

import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;

// TODO: finish
public class Texture implements Resource {

    public final int glHandle;
    public final int width;
    public final int height;
    private TextureFilter filter;
    private TextureWrap wrap;
    // TODO: see what's up
    protected float anisotropicFilterLevel = 1.0f;
    private static float maxAnisotropicFilterLevel = 0;

    public Texture(int glHandle, final int width, final int height, TextureFilter filter, TextureWrap wrap) {
        this.glHandle = glHandle;
        this.width = width;
        this.height = height;
        this.filter = filter;
        this.wrap = wrap;
    }

    public void setFilter(final TextureFilter filter) {
        // TODO: set in OpenGL global state, only then set =
    }

    public TextureFilter getFilter() {
        return filter;
    }

    public void setWrap(final TextureWrap wrap) {
        // TODO: set in OpenGL global state, only then set =
    }

    public TextureWrap getWrap() {
        return wrap;
    }

    @Override
    public void free() {
        GL11.glDeleteTextures(glHandle);
    }
}
