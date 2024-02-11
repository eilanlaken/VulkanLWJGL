package org.example.engine.core.graphics;

import org.lwjgl.opengl.GL20;

public enum TextureParamWrap {

    MIRRORED_REPEAT(GL20.GL_MIRRORED_REPEAT),
    CLAMP_TO_EDGE(GL20.GL_CLAMP_TO_EDGE),
    REPEAT(GL20.GL_REPEAT)
    ;

    public final int glValue;

    TextureParamWrap(int glValue) {
        this.glValue = glValue;
    }

}
