package org.example.engine.core.graphics;

import org.lwjgl.opengl.GL20;

public enum TextureParamFilter {

    NEAREST(GL20.GL_NEAREST),
    LINEAR(GL20.GL_LINEAR),
    MIP_MAP_NEAREST_NEAREST(GL20.GL_NEAREST_MIPMAP_NEAREST),
    MIP_MAP_LINEAR_NEAREST(GL20.GL_LINEAR_MIPMAP_NEAREST),
    MIP_MAP_NEAREST_LINEAR(GL20.GL_NEAREST_MIPMAP_LINEAR),
    MIP_MAP_LINEAR_LINEAR(GL20.GL_LINEAR_MIPMAP_LINEAR)
    ;

    public final int glValue;

    TextureParamFilter(final int glValue) {
        this.glValue = glValue;
    }

}
