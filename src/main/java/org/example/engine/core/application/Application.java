package org.example.engine.core.application;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.input.Mouse;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Application {

    private static boolean initialized = false;
    private static boolean debugMode;
    private static Window window;
    private static List<Runnable> tasks = new ArrayList<>();
    private static boolean running = false;

    public static void createSingleWindowApplication(final WindowAttributes attributes) {
        // init GLFW
        GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) throw new RuntimeException("Unable to initialize GLFW.");
//        GLFW.glfwDefaultWindowHints();
//        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, config.transparentFrameBuffer ? GLFW.GLFW_TRUE : GLFW_FALSE);
//        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
//        GLFW.glfwWindowHint(GLFW_RESIZABLE, config.resizable ? GLFW.GLFW_TRUE : GLFW_FALSE);
//        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
//        GLFW.glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
//        GLFW.glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

        window = new Window(attributes);
        GraphicsUtils.init(window);
        Mouse.init(window);
        Keyboard.init(window);

        initialized = true;
        // init OpenGL Context
        GL.createCapabilities();
    }

    public static void launch(WindowScreen screen) {
        if (!initialized) throw new IllegalStateException("Must call createSingleWindowApplication before launch().");
        window.setScreen(screen);
        running = true;
        loop();
        clean();
    }

    public static void loop() {
        while (running && !window.shouldClose()) {
            GLFW.glfwMakeContextCurrent(window.getHandle());
            boolean windowRendered = window.refresh();
            int targetFrameRate = GraphicsUtils.getTargetFps();

            Mouse.resetInternalState();
            Keyboard.resetInternalState();
            GLFW.glfwPollEvents();

            boolean requestRendering;
            for (Runnable task : tasks) {
                task.run();
            }
            synchronized (tasks) {
                requestRendering = tasks.size() > 0;
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
                ApplicationSync.sync(targetFrameRate); // sleep as needed to meet the target framerate
            }
        }
    }

    private static void clean() {

    }

    public static void exit() {
        running = false;
    }

    public static synchronized void addTask(Runnable task) {
        tasks.add(task);
    }

}
