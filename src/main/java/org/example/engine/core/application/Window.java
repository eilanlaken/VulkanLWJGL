package org.example.engine.core.application;

import org.example.engine.core.input.Keyboard;
import org.example.engine.core.input.Mouse;
import org.example.engine.core.memory.Resource;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

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
    private boolean enableVSync;
    private boolean allowResize;
    private GLFWErrorCallback errorCallback;
    private WindowScreen screen;
    // TODO: add a bunch of other parameters:
    // is full screen
    // is borderless
    // target fps
    // current frame
    // delta time

    public int targetFrameRate = 1000;

    private boolean resetDeltaTime;
    private float deltaTime;
    private long frameStart;
    private int frames;
    public int fps;
    private long lastTime;

    // TODO:
    // https://github.com/LWJGL/lwjgl/blob/master/src/java/org/lwjgl/opengl/Sync.java

    public Window(String title, int width, int height, int targetFrameRate, boolean enableVSync, boolean allowResize) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.targetFrameRate = targetFrameRate;
        this.enableVSync = enableVSync;
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
        });

        // TODO: see how this works. Refactor.
        // set an input event callback
        GLFW.glfwSetKeyCallback(handle, (window, key, scanCode, action, mods) -> {
            // update the appropriate input device module.
            // this is just an example. TODO: delete this line.
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) closeWindow();
            // test full screen
            if (key == GLFW_KEY_SPACE && action == GLFW_RELEASE) GLFW.glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, 100, 100, 60);
        });

        // get the thread stack and push a new frame
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            // get the window size passed to glfwCreateWindow
            GLFW.glfwGetWindowSize(handle, pWidth, pHeight);
            // get the resolution of the primary monitor
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            // center the window
            GLFW.glfwSetWindowPos(handle, (vidMode.width() - pWidth.get(0)) / 2, (vidMode.height() - pHeight.get(0)) / 2);
        }

        // make the OpenGL context active
        GLFW.glfwMakeContextCurrent(handle);
        // enable / disable v-sync
        GLFW.glfwSwapInterval(enableVSync ? 1 : 0);
        // show the window
        GLFW.glfwShowWindow(handle);
        GL.createCapabilities();


        // TODO: refactor out
        // TODO: see what is up
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        GL11.glEnable(GL11.GL_STENCIL_TEST);
//        GL11.glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
    }


    public long getHandle() {
        return handle;
    }

    public void update() {


        // TODO: reset input flags. See if there is a better solution.

//        long time;
//        if (this.resetDeltaTime) {
//            this.resetDeltaTime = false;
//            time = this.lastTime;
//        } else {
//            time = System.nanoTime();
//        }
//
//        this.deltaTime = (float)(time - this.lastTime) / 1.0E9F;
//        this.lastTime = time;
//        if (time - this.frameStart >= 1000000000L) {
//            this.fps = this.frames;
//            this.frames = 0;
//            this.frameStart = time;
//        }
//
//        this.frames++;
//
//        // limit frame rate.
//        final double targetFPS = 144.0;
//        final double targetFrameTime = 1.0 / targetFPS;
//        double frameStartTime = glfwGetTime();
//        double frameEndTime = GLFW.glfwGetTime();
//        double frameDuration = frameEndTime - frameStartTime;
//        double sleepTime = targetFrameTime - frameDuration;
//        if (sleepTime > 0) {
//            try {
//                Thread.sleep((long) (sleepTime * 1000));
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }

        // TODO: see star contract Sync
        // http://forum.lwjgl.org/index.php?topic=5653.0


        GLFW.glfwSwapBuffers(handle);
        GLFW.glfwPollEvents();
        Mouse.resetInternalState();
        Keyboard.resetInternalState();
        if (this.screen != null) screen.update(deltaTime);
    }

    public void setScreen(WindowScreen screen) {
        if (this.screen != null) {
            this.screen.hide();
        }
        this.screen = screen;
        this.screen.show();
    }
    public void closeWindow() {
        GLFW.glfwSetWindowShouldClose(handle, true);
        if (screen != null) screen.hide();
    }

    public boolean windowShouldClose() {
        return GLFW.glfwWindowShouldClose(handle);
    }

    // TODO: refactor into an input module
    public boolean isKeyPressed(int keycode) {
        return GLFW.glfwGetKey(handle, keycode) == GLFW_PRESS;
    }

    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(handle, title);
        this.title = title;
    }

    // TODO: refactor, improve and test
    public boolean isKeyReleased(int keycode) {
        return GLFW.glfwGetKey(handle, keycode) == GLFW_RELEASE;
    }

    public void maximize() {
        // see how it works
        //GLFW.glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
    }

    @Override
    public void free() {
        GLFW.glfwDestroyWindow(handle);
        GLFW.glfwTerminate();
        errorCallback.free();
    }

}
