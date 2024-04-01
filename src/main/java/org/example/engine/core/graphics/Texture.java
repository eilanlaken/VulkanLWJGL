package org.example.engine.core.graphics;

import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

// TODO: finish
public class Texture implements Resource {

    protected int handle;
    private int slot;
    public final int width;
    public final int height;
    public final float invWidth;
    public final float invHeight;
    public final Filter magFilter;
    public final Filter minFilter;
    public final Wrap uWrap;
    public final Wrap vWrap;
    // TODO: see what's up
    protected float anisotropicFilterLevel = 1.0f;
    private static float maxAnisotropicFilterLevel = 0;

    public Texture(int handle,
                   final int width, final int height,
                   Filter magFilter, Filter minFilter,
                   Wrap uWrap, Wrap vWrap) {
        this.handle = handle;
        this.slot = -1;
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
        TextureBinder.unbind(this);
        GL11.glDeleteTextures(handle);
        handle = 0;
    }

    protected final void setSlot(final int slot) {
        this.slot = slot;
    }

    protected final int getSlot() {
        return slot;
    }

    public enum Filter {

        NEAREST(GL20.GL_NEAREST),
        LINEAR(GL20.GL_LINEAR),
        MIP_MAP_NEAREST_NEAREST(GL20.GL_NEAREST_MIPMAP_NEAREST),
        MIP_MAP_LINEAR_NEAREST(GL20.GL_LINEAR_MIPMAP_NEAREST),
        MIP_MAP_NEAREST_LINEAR(GL20.GL_NEAREST_MIPMAP_LINEAR),
        MIP_MAP_LINEAR_LINEAR(GL20.GL_LINEAR_MIPMAP_LINEAR)
        ;

        public final int glValue;

        Filter(final int glValue) {
            this.glValue = glValue;
        }

    }

    public enum Wrap {

        MIRRORED_REPEAT(GL20.GL_MIRRORED_REPEAT),
        CLAMP_TO_EDGE(GL20.GL_CLAMP_TO_EDGE),
        REPEAT(GL20.GL_REPEAT)
        ;

        public final int glValue;

        Wrap(int glValue) {
            this.glValue = glValue;
        }

    }
}
