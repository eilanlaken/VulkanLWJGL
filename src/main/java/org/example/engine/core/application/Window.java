package org.example.engine.core.application;

import org.example.engine.core.input.Keyboard;
import org.example.engine.core.input.Mouse;
import org.example.engine.core.memory.Resource;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;

/*
TODO: read
https://www.glfw.org/docs/3.3/window_guide.html
requires extra configuration:
for fullscreen
for borderless (look for decoration)
for frame rate
etc.

TODO: read
LwjglGraphics.java

TODO: refactor while considering context etc.
 */
public class Window implements Resource {

    private long handle;
    private String title;
    public int width;
    public int height;
    private boolean vsyncEnabled;
    private boolean allowResize;
    private GLFWErrorCallback errorCallback;
    private WindowScreen screen;
    private int targetFps;
    private int fps;

    // debug
    // TODO: this (kinda) solves the vsync issue. Just set the render skip interval according to the targetFps;
    private boolean toRender = true;
    private int renderSkip = 0;
    private int renderCount = 0;

    // TODO:
    // https://github.com/LWJGL/lwjgl/blob/master/src/java/org/lwjgl/opengl/Sync.java

    public Window(String title, int width, int height, int targetFps, boolean enableVSync, boolean allowResize) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.targetFps = targetFps;
        this.vsyncEnabled = enableVSync;
        this.allowResize = allowResize;
    }

    public void init() {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) throw new RuntimeException("Unable to initialize GLFW.");
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW_RESIZABLE, allowResize ? GLFW.GLFW_TRUE : GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

        handle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (handle == MemoryUtil.NULL) throw new RuntimeException("Unable to create window.");

        // set callback to see if the window has been resized.
        GLFW.glfwSetFramebufferSizeCallback(handle, (window, width, height) -> {
           this.width = width;
           this.height = height;
           screen.resize(width, height);
        });

        GLFW.glfwMakeContextCurrent(handle);
        // TODO: fix
        GLFW.glfwSwapInterval(vsyncEnabled ? 1 : 0);
        GLFW.glfwShowWindow(handle);
        GL.createCapabilities();


        // TODO: refactor out
        // TODO: see what is up
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        GL11.glEnable(GL11.GL_STENCIL_TEST);
//        GL11.glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
    }

    public int getFps() {
        return fps;
    }

    public int getTargetFps() {
        return targetFps;
    }

    public void setTargetFps(int targetFps) {
        this.targetFps = targetFps;
    }

    public long getHandle() {
        return handle;
    }

    // TODO: implement
    public void enableVSync() {

    }

    // TODO: implement
    public void disableVSync() {

    }

    public boolean isVSyncEnabled() {
        return vsyncEnabled;
    }

    // TODO: look at the main loop in the libGDX lwjgl-backend:
    // TODO: LwjglApplication.java: protected void mainLoop ()
    public void loop() {
        float previousTime;
        float currentTime = (float) GLFW.glfwGetTime();
        float lag = 0;

        while (!shouldClose()) {
            float fixedUpdateTimeInterval = 1.0f / targetFps; // in seconds
            previousTime = currentTime;
            currentTime = (float) GLFW.glfwGetTime();
            float elapsedTime = currentTime - previousTime;
            this.fps = (int) (1f / elapsedTime);
            lag += elapsedTime;
            totalTime += elapsedTime;

            Mouse.resetInternalState();
            Keyboard.resetInternalState();
            GLFW.glfwPollEvents();

            // TODO: FIX vsync does not work on laptop + intel HD integrated GPU.
            while (lag >= fixedUpdateTimeInterval) {
                screen.fixedUpdate(fixedUpdateTimeInterval);
                lag -= fixedUpdateTimeInterval;
            }

            // TODO: FIX lags if not rendering even repeating frames.
            screen.frameUpdate(elapsedTime);
            GLFW.glfwSwapBuffers(handle);
            // causes small initial lag?
            /// if (renderSkip != 0) Thread.yield();
            /// renderSkip++;
            /// renderSkip %= 10;
        }
    }

    //https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/chapter02/chapter2.html

    public void setScreen(WindowScreen screen) {
        if (this.screen != null) {
            this.screen.hide();
        }
        this.screen = screen;
        this.screen.show();
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(handle);
    }

    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(handle, title);
        this.title = title;
    }

    public void maximize() {
        // see how it works
        //GLFW.glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
    }

    public void setWindowPosition(int x, int y) {

    }

    @Override
    public void free() {
        GLFW.glfwDestroyWindow(handle);
        GLFW.glfwTerminate();
        errorCallback.free();
    }

}
