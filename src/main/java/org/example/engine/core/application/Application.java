package org.example.engine.core.application;

import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.async.AsyncUtils;
import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.input.Mouse;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

public class Application {

    private static boolean initialized = false;
    private static boolean debugMode;
    private static ApplicationWindow window;
    private static Array<Runnable> tasks = new Array<>();
    private static boolean running = false;
    private static GLFWErrorCallback errorCallback;

    public static void createSingleWindowApplication(final ApplicationWindowAttributes attributes) {
        // init GLFW
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) throw new RuntimeException("Unable to initialize GLFW.");
        window = new ApplicationWindow(attributes);
        GL.createCapabilities();
        AsyncUtils.init();
        GraphicsUtils.init(window);
        AssetUtils.init(window);
        Mouse.init(window);
        Keyboard.init(window);
        // init OpenGL Context
        initialized = true;
    }

    public static void launch(ApplicationScreen screen) {
        if (!initialized) throw new IllegalStateException("Must call createSingleWindowApplication before launch().");
        window.setScreen(screen);
        running = true;
        loop();
        clean();
    }

    public static void switchScreen(ApplicationScreen screen) {
        window.setScreen(screen);
    }

    public static void loop() {
        while (running && !window.shouldClose()) {
            GLFW.glfwMakeContextCurrent(window.getHandle());
            boolean windowRendered = window.refresh();
            int targetFrameRate = GraphicsUtils.getTargetFps();

            // asset loading
            AssetStore.update();
            Mouse.resetInternalState();
            Keyboard.resetInternalState();
            GLFW.glfwPollEvents();

            boolean requestRendering;
            for (Runnable task : tasks) {
                task.run();
            }
            synchronized (tasks) {
                requestRendering = tasks.size > 0;
                tasks.clear();
            }

            if (requestRendering && !GraphicsUtils.isContinuousRendering()) window.requestRendering();
            if (!windowRendered) {
                // Sleep a few milliseconds in case no rendering was requested
                // with continuous rendering disabled.
                try {
                    Thread.sleep(1000 / GraphicsUtils.getIdleFps());
                } catch (InterruptedException ignored) {
                    // ignore
                }
            } else if (targetFrameRate > 0) {
                AsyncUtils.sync(targetFrameRate); // sleep as needed to meet the target framerate
            }
        }
    }

    private static void clean() {
        window.delete();
        GLFW.glfwTerminate();
        errorCallback.free();
    }

    public static void exit() {
        running = false;
    }

    public static synchronized void addTask(Runnable task) {
        tasks.add(task);
    }

}
