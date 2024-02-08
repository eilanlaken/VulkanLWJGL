package org.example.engine.core.graphics;

import org.lwjgl.opengl.GL11;

public class ModelInstance {

    public final Model model;

    // transform, etc.
    public int renderPrimitive = GL11.GL_TRIANGLES;

    public ModelInstance(final Model model) {
        this.model = model;
    }

}
