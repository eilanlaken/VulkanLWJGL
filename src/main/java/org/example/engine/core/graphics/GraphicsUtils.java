package org.example.engine.core.graphics;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;

public final class GraphicsUtils {

    public static int getScreenWidth() {
        return 0;
    }

    public static int getScreenHeight() {
        return 0;
    }

    public static int getMaxTextureSize() {
        return GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
    }

}
