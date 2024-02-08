package org.example.engine.core.graphics;

import org.example.engine.core.memory.Resource;

// TODO: finish
public class Texture implements Resource {

    public final int glHandle;
    protected TextureFilter filter;
    protected TextureWrap wrap;

    // TODO: see what's up
    protected float anisotropicFilterLevel = 1.0f;
    private static float maxAnisotropicFilterLevel = 0;

    public Texture(int glHandle) {
        this.glHandle = glHandle;
    }

    @Override
    public void free() {

    }
}
