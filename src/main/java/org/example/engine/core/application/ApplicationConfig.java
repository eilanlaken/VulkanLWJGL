package org.example.engine.core.application;

public class ApplicationConfig {

    // some defaults.
    public String name = "My Application";
    public String author = "Author";
    public String version = "0.0";
    public String windowTitle = "My Application";
    public boolean allowWindowResize = true;
    public int windowWidth = 1080/2;
    public int windowHeight = 1080/2;
    public int targetFps = 60;
    // TODO: vSync doesn't fucking work (at least on Intel integrated graphics)
    public boolean vSyncEnabled = true;
    public boolean fullScreen = false;
    public boolean borderlessWindow = false;

}
