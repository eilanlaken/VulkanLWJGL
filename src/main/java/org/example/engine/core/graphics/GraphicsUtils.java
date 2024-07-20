package org.example.engine.core.graphics;

import org.example.engine.core.application.ApplicationWindow;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.nio.IntBuffer;

public final class GraphicsUtils {

    private static boolean initialized = false;
    private static ApplicationWindow window;

    private static volatile boolean isContinuous = true;
    private static long lastFrameTime = -1;
    private static float deltaTime;
    private static boolean resetDeltaTime = false;
    private static long frameId = 0;
    private static long frameCounterStart = 0;
    private static int frames = 0;
    private static int fps;
    private static int targetFps = 120;
    private static int prevTargetFps = targetFps;
    private static int idleFps = 10;
    private static int maxTextureSize;

    private GraphicsUtils() {}

    public static void init(final ApplicationWindow window) {
        if (initialized) throw new IllegalStateException(GraphicsUtils.class.getSimpleName() + " instance already initialized.");
        GraphicsUtils.window = window;
        maxTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
        initialized = true;
    }

    public static void update() {
        long time = System.nanoTime();
        if (lastFrameTime == -1) lastFrameTime = time;
        if (resetDeltaTime) {
            resetDeltaTime = false;
            deltaTime = 0;
        } else
            deltaTime = (time - lastFrameTime) / 1000000000.0f;
        lastFrameTime = time;

        if (time - frameCounterStart >= 1000000000) {
            fps = frames;
            frames = 0;
            frameCounterStart = time;
        }
        frames++;
        frameId++;
    }

    public static int getFps() {
        return fps;
    }

    public static float getDeltaTime() {
        return deltaTime;
    }

    public static int getIdleFps() {
        return idleFps;
    }

    public static void setIdleFps(int idleFps) {
        GraphicsUtils.idleFps = idleFps;
    }

    public static int getTargetFps() {
        return targetFps;
    }

    public static void setTargetFps(int targetFps) {
        prevTargetFps = GraphicsUtils.targetFps;
        GraphicsUtils.targetFps = targetFps;
    }

    public static int getMaxTextureSize() {
        return maxTextureSize;
    }

    public static int getFrameCount() {
        return frames;
    }

    public static void setContinuousRendering(boolean isContinuous) {
        GraphicsUtils.isContinuous = isContinuous;
    }

    public static boolean isContinuousRendering () {
        return isContinuous;
    }

    public static int getMonitorWidth() {
        return GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()).width();
    }

    public static int getMonitorHeight() {
        return GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()).height();
    }

    public static int getWindowHeight() {
        return window.attributes.height;
    }

    public static int getWindowWidth() {
        return window.attributes.width;
    }

    public static float getMonitorAspectRatio() {
        return getMonitorWidth() / (float) getMonitorHeight();
    }

    public static int getMaxVerticesPerDrawCall() {
        return GL11.glGetInteger(GL20.GL_MAX_ELEMENTS_VERTICES);
    }

    public static int getMaxIndicesPerDrawCall() {
        return GL11.glGetInteger(GL20.GL_MAX_ELEMENTS_INDICES);
    }

    public static float getWindowAspectRatio() {
        return getWindowWidth() / (float) getWindowHeight();
    }

    public static void enableVSync() {
        int refreshRate = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()).refreshRate();
        setTargetFps(refreshRate);
        window.setVSync(true);
    }

    public static int getMaxFragmentShaderTextureUnits() {
        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        GL11.glGetIntegerv(GL20.GL_MAX_TEXTURE_IMAGE_UNITS, intBuffer);
        return intBuffer.get(0);
    }

    public static int getMaxBoundTextureUnits() {
        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        GL11.glGetIntegerv(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, intBuffer);
        return intBuffer.get(0);
    }

    public static void disableVSync() {
        window.setVSync(false);
        setTargetFps(prevTargetFps); // restore target refresh rate before vsync.
    }

    public static boolean isVSyncEnabled() {
        return window.attributes.vSyncEnabled;
    }

}
