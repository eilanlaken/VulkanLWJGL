package org.example.engine.core.graphics;

import org.example.engine.core.application.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;

public final class Graphics {

    private static boolean initialized = false;
    private static Window window;

    public static void init(final Window window) {
        if (initialized) throw new IllegalStateException(Graphics.class.getSimpleName() + " instance already initialized.");
        Graphics.window = window;
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

    public static void enableVSync() {
        GLFW.glfwSwapInterval(1);
    }

    public static void disableVSync() {
        GLFW.glfwSwapInterval(0);
    }

    public static void setAntiAliasing(int value) {
        if (value != 0 && value != 2 && value != 4 && value != 8 && value != 16)
            throw new IllegalArgumentException("Multisampling (anti-aliasing) can only be set to: 0, 2, 4, 8 or 16. Got: " + value);
        GLFW.glfwWindowHint(GLFW_SAMPLES, value); //  enable multi sampling
    }

}
