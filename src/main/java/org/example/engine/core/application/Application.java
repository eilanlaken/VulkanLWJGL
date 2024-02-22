package org.example.engine.core.application;

import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.input.Mouse;

public abstract class Application {

    private static boolean initialized = false;
    private static Window_glfw window;

    public static void createSingleWindowApplication(final ApplicationConfig config) {
        window = new Window_glfw(config.windowTitle, config.windowWidth, config.windowHeight, config.targetFps, config.vSyncEnabled, config.allowWindowResize);
        window.init();
        GraphicsUtils.init(window);
        Mouse.init(window);
        Keyboard.init(window);

        initialized = true;
    }

    public static void launch(WindowScreen screen) {
        if (!initialized) throw new IllegalStateException("Must call createSingleWindowApplication before launch().");
        window.setScreen(screen);
        window.loop();
        window.free();
    }
    /// TODO: https://stackoverflow.com/questions/14968857/controlling-fps-limit-in-opengl-application
    // TODO: implement later later later
    public static void createMultiWindowApplication(final ApplicationConfig[] configs, WindowScreen ...screens) {}

    public static void setScreen(WindowScreen screen) {
        window.setScreen(screen);
    }


}
