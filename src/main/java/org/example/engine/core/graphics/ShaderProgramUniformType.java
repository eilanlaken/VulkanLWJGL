package org.example.engine.core.graphics;

import org.lwjgl.opengl.GL20;

public enum ShaderProgramUniformType {

    SAMPLER_2D(GL20.GL_SAMPLER_2D),

    ;

    public final int glCode;

    ShaderProgramUniformType(final int glCode) {
        this.glCode = glCode;
    }

}
