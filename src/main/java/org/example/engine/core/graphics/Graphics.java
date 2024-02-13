package org.example.engine.core.graphics;

import org.example.engine.core.application.Application;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;

public final class Graphics {

    public static Window window;

    public static int getWindowHeight() {
        return window.width;
    }

    public static int getWindowWidth() {
        return window.height;
    }

    public static int getMaxTextureSize() {
        return GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
    }

}
