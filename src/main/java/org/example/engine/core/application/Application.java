package org.example.engine.core.application;

import org.example.engine.core.graphics.Window;
import org.example.engine.core.graphics.WindowScreen;

public abstract class Application {

    private static boolean initialized = false;
    private static Window window;


    protected Application(String name, String author, String version) {

    }

    public static void createSingleWindowApplication(final ApplicationConfig config) {
        window = new Window(config.windowTitle, config.windowWidth, config.windowHeight, config.targetFps, config.vSyncEnabled, config.allowWindowResize);
        window.init();
        initialized = true;
    }

    // TODO: implement later later later
    public static void createMultiWindowApplication(final ApplicationConfig[] configs, WindowScreen ...screens) {

    }

    public static void setScreen(WindowScreen screen) {

    }

    public static void launch(WindowScreen screen) {
        if (!initialized) throw new IllegalStateException("Must call createSingleWindowApplication before launch().");
        window.setScreen(screen);
        while (!window.windowShouldClose()) {
            window.update();
        }
        window.free();
    }

}
