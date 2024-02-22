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
    private int targetFps;

    // TODO:
    // https://github.com/LWJGL/lwjgl/blob/master/src/java/org/lwjgl/opengl/Sync.java

    public Window(String title, int width, int height, int targetFps, boolean enableVSync, boolean allowResize) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.targetFps = targetFps;
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
           screen.resize(width, height);
        });

        GLFW.glfwMakeContextCurrent(handle);
        // TODO: fix
        int v = enableVSync ? 1 : 0;
        System.out.println("enable? " + enableVSync);
        System.out.println("v: " + v);
        //GLFW.glfwSwapInterval(v);
        GLFW.glfwShowWindow(handle);
        GL.createCapabilities();


        // TODO: refactor out
        // TODO: see what is up
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        GL11.glEnable(GL11.GL_STENCIL_TEST);
//        GL11.glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
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

    // TODO: look at the main loop in the libGDX lwjgl-backend:
    // LwjglApplication.java:
    /**
     *
     * protected void mainLoop () {
     * 		SnapshotArray<LifecycleListener> lifecycleListeners = this.lifecycleListeners;
     *
     * 		try {
     * 			graphics.setupDisplay();
     *                } catch (LWJGLException e) {
     * 			throw new GdxRuntimeException(e);
     *        }
     *
     * 		listener.create();
     * 		graphics.resize = true;
     *
     * 		int lastWidth = graphics.getWidth();
     * 		int lastHeight = graphics.getHeight();
     *
     * 		graphics.lastTime = System.nanoTime();
     * 		boolean wasPaused = false;
     * 		while (running) {
     * 			Display.processMessages();
     * 			if (Display.isCloseRequested()) exit();
     *
     * 			boolean isMinimized = graphics.config.pauseWhenMinimized && !Display.isVisible();
     * 			boolean isBackground = !Display.isActive();
     * 			boolean paused = isMinimized || (isBackground && graphics.config.pauseWhenBackground);
     * 			if (!wasPaused && paused) { // just been minimized
     * 				wasPaused = true;
     * 				synchronized (lifecycleListeners) {
     * 					LifecycleListener[] listeners = lifecycleListeners.begin();
     * 					for (int i = 0, n = lifecycleListeners.size; i < n; ++i)
     * 						listeners[i].pause();
     * 					lifecycleListeners.end();
     *                }
     * 				listener.pause();
     *            }
     * 			if (wasPaused && !paused) { // just been restore from being minimized
     * 				wasPaused = false;
     * 				synchronized (lifecycleListeners) {
     * 					LifecycleListener[] listeners = lifecycleListeners.begin();
     * 					for (int i = 0, n = lifecycleListeners.size; i < n; ++i)
     * 						listeners[i].resume();
     * 					lifecycleListeners.end();
     *                }
     * 				listener.resume();
     *            }
     *
     * 			boolean shouldRender = false;
     *
     * 			if (graphics.canvas != null) {
     * 				int width = graphics.canvas.getWidth();
     * 				int height = graphics.canvas.getHeight();
     * 				if (lastWidth != width || lastHeight != height) {
     * 					lastWidth = width;
     * 					lastHeight = height;
     * 					Gdx.gl.glViewport(0, 0, lastWidth, lastHeight);
     * 					listener.resize(lastWidth, lastHeight);
     * 					shouldRender = true;
     *                }
     *            } else {
     * 				graphics.config.x = Display.getX();
     * 				graphics.config.y = Display.getY();
     * 				if (graphics.resize || Display.wasResized()
     * 					|| (int)(Display.getWidth() * Display.getPixelScaleFactor()) != graphics.config.width
     * 					|| (int)(Display.getHeight() * Display.getPixelScaleFactor()) != graphics.config.height) {
     * 					graphics.resize = false;
     * 					graphics.config.width = (int)(Display.getWidth() * Display.getPixelScaleFactor());
     * 					graphics.config.height = (int)(Display.getHeight() * Display.getPixelScaleFactor());
     * 					Gdx.gl.glViewport(0, 0, graphics.config.width, graphics.config.height);
     * 					if (listener != null) listener.resize(graphics.config.width, graphics.config.height);
     * 					shouldRender = true;
     *                }
     *            }
     *
     * 			if (executeRunnables()) shouldRender = true;
     *
     * 			// If one of the runnables set running to false, for example after an exit().
     * 			if (!running) break;
     *
     * 			input.update();
     * 			if (graphics.shouldRender()) shouldRender = true;
     * 			input.processEvents();
     * 			if (audio != null) audio.update();
     *
     * 			if (isMinimized)
     * 				shouldRender = false;
     * 			else if (isBackground && graphics.config.backgroundFPS == -1) //
     * 				shouldRender = false;
     *
     * 			int frameRate = isBackground ? graphics.config.backgroundFPS : graphics.config.foregroundFPS;
     * 			if (shouldRender) {
     * 				graphics.updateTime();
     * 				graphics.frameId++;
     * 				listener.render();
     * 				Display.update(false);
     *            } else {
     * 				// Sleeps to avoid wasting CPU in an empty loop.
     * 				if (frameRate == -1) frameRate = 10;
     * 				if (frameRate == 0) frameRate = graphics.config.backgroundFPS;
     * 				if (frameRate == 0) frameRate = 30;
     *            }
     * 			if (frameRate > 0) Display.sync(frameRate);
     *        }
     *
     * 		synchronized (lifecycleListeners) {
     * 			LifecycleListener[] listeners = lifecycleListeners.begin();
     * 			for (int i = 0, n = lifecycleListeners.size; i < n; ++i) {
     * 				listeners[i].pause();
     * 				listeners[i].dispose();
     *            }
     * 			lifecycleListeners.end();
     *        }
     * 		listener.pause();
     * 		listener.dispose();
     * 		Display.destroy();
     * 		if (audio != null) audio.dispose();
     * 		if (graphics.config.forceExit) System.exit(-1);* 	}
     */

    public void loop() {
        float previousTime = (float) GLFW.glfwGetTime();
        float lag = 0;

        while (!shouldClose()) {
            float fixedUpdateTimeInterval = 1.0f / targetFps; // in seconds
            float currentTime = (float) GLFW.glfwGetTime();
            float elapsedTime = currentTime - previousTime;
            lag += elapsedTime;
            previousTime = currentTime;

            Mouse.resetInternalState();
            Keyboard.resetInternalState();
            GLFW.glfwPollEvents();

            // TODO: vsync does not work on laptop + intel HD integrated GPU.
            while (lag >= fixedUpdateTimeInterval) {
                screen.fixedUpdate(fixedUpdateTimeInterval);
                lag -= fixedUpdateTimeInterval;
            }

            screen.frameUpdate(elapsedTime);
            GLFW.glfwSwapBuffers(handle);
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
