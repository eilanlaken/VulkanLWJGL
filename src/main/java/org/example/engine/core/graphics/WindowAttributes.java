package org.example.engine.core.graphics;

public class WindowAttributes {

    public int posX = -1;
    public int posY = -1;
    public int width = 640;
    public int height = 480;
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
