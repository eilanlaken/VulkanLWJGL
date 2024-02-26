package org.example.engine.core.graphics;

import org.example.engine.core.application_old.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;

public final class GraphicsUtils_old {

    private static boolean initialized = false;
    private static Window window;

    public static void init(final Window window) {
        if (initialized) throw new IllegalStateException(GraphicsUtils_old.class.getSimpleName() + " instance already initialized.");
        GraphicsUtils_old.window = window;
        initialized = true;
    }

    public static int getMonitorWidth() {
        return GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()).width();
    }

    public static int getMonitorHeight() {
        return GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()).height();
    }

    public static void setWindowPosition(int x, int y) {
        GLFW.glfwSetWindowPos(window.getHandle(), x, y);
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

    public static int getTargetFps() {
        return window.getTargetFps();
    }

    public static void setTargetFrameRate(int targetFps) {
        window.setTargetFps(targetFps);
    }

    public static int getFps() {
        return window.getFps();
    }

    public static void enableVSync() {
        window.enableVSync();
    }

    public static void disableVSync() {
        window.disableVSync();
    }

    public static void setAntiAliasing(int value) {
        if (value != 0 && value != 2 && value != 4 && value != 8 && value != 16)
            throw new IllegalArgumentException("Multisampling (anti-aliasing) can only be set to: 0, 2, 4, 8 or 16. Got: " + value);
        GLFW.glfwWindowHint(GLFW_SAMPLES, value); //  enable multi sampling
    }

}
