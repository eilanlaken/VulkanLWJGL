package org.example.engine.core.graphics;

import org.lwjgl.opengl.GL20;

public class ShaderProgramBinder {

    private static int boundProgram = -1;

    public static boolean bind(final ShaderProgram shaderProgram) {
        if (boundProgram == shaderProgram.program) return false;
        GL20.glUseProgram(shaderProgram.program);
        boundProgram = shaderProgram.program;
        return true;
    }

    public static void unbind() { GL20.glUseProgram(0); }

}
