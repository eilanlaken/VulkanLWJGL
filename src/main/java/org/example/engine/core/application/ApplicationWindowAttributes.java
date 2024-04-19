package org.example.engine.core.application;

// TODO: revise.
public class ApplicationWindowAttributes {

    public int posX = -1;
    public int posY = -1;
    public int width = 640*2;
    public int height = 480*2;
    public int minWidth = -1;
    public int minHeight = -1;
    public int maxWidth = -1;
    public int maxHeight = -1;
    public boolean autoMinimized = true;
    public boolean resizable = true;
    public boolean decorated = true;
    public boolean minimized = false;
    public boolean maximized = false;
    public String iconPath;
    public boolean visible = true;
    public boolean fullScreen;
    public String title;
    public boolean initialVisible = true;
    public boolean vSyncEnabled = false;
    public boolean transparentFrameBuffer = false;

}
