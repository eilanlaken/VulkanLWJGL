package org.example.engine.core.graphics;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;

public final class GraphicsUtils {

    static int[] width = new int[1];
    static int[] height = new int[1];

    public static int getScreenWidth() {
        GLFW.glfwGetWindowSize(0, width, height);
        return width[0];
    }

    public static int getScreenHeight() {
        GLFW.glfwGetWindowSize(0, width, height);
        return width[0];
    }

    public static int getMaxTextureSize() {
        return GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
    }

}
