package org.example.engine.core.graphics;

import org.example.engine.core.application.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public final class Graphics {

    private static boolean initialized = false;
    private static Window window;

    public static void init(final Window window) {
        if (initialized) throw new IllegalStateException(Graphics.class.getSimpleName() + " instance already initialized.");
        Graphics.window = window;
        initialized = true;
    }

    public static int getWindowHeight() {
        return window.width;
    }

    public static int getWindowWidth() {
        return window.height;
    }

    public static int getMaxTextureSize() {
        return GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
    }

    public static int getFps() {
        return window.fps;
    }

    public static int getTargetFps() {
        return window.targetFrameRate;
    }

    public static void setTargetFrameRate(int targetFrameRate) {
        window.targetFrameRate = targetFrameRate;
    }

    public static void enableVSync() {
        GLFW.glfwSwapInterval(1);
    }

    public static void disableVSync() {
        GLFW.glfwSwapInterval(0);
    }

}
