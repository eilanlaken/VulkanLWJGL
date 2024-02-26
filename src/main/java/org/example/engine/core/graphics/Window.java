package org.example.engine.core.graphics;

import org.example.engine.core.application.Application;
import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.Resource;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Window implements Resource {

    // compute and auxiliary buffers
    private final IntBuffer tmpBuffer = BufferUtils.createIntBuffer(1);
    private final IntBuffer tmpBuffer2 = BufferUtils.createIntBuffer(1);
    private final long primaryMonitor = GLFW.glfwGetPrimaryMonitor();
    private final GLFWVidMode videoMode = GLFW.glfwGetVideoMode(primaryMonitor);

    // window attributes
    private long handle;
    private WindowAttributes attributes;

    private boolean focused = false;
    private Array<String> filesDropped = new Array<>();
    private volatile int backBufferWidth;
    private volatile int backBufferHeight;
    private volatile int logicalWidth;
    private volatile int logicalHeight;

    // state management
    List<Runnable> tasks = new ArrayList<>();
    private boolean requestRendering = false;
    private WindowScreen screen;

    GLFWFramebufferSizeCallback resizeCallback = new GLFWFramebufferSizeCallback() {
        private volatile boolean requested;

        @Override
        public void invoke(long windowHandle, final int width, final int height) {
            if (Configuration.GLFW_CHECK_THREAD0.get(true)) {
                renderWindow(width, height);
            } else {
                if (requested) return;
                requested = true;
                Application.addTask(new Runnable() {
                    @Override
                    public void run () {
                        requested = false;
                        Window.this.attributes.width = width;
                        Window.this.attributes.height = height;
                        renderWindow(width, height);
                    }
                });
            }
        }
    };

    private final GLFWWindowFocusCallback defaultFocusChangeCallback = new GLFWWindowFocusCallback() {
        @Override
        public synchronized void invoke(long handle, final boolean focused) {
            tasks.add(new Runnable() {
                @Override
                public void run() {
                    Window.this.focused = focused;
                }
            });
        }
    };

    private final GLFWWindowIconifyCallback defaultMinimizedCallback = new GLFWWindowIconifyCallback() {
        @Override
        public synchronized void invoke(long handle, final boolean minimized) {
            tasks.add(new Runnable() {
                @Override
                public void run () {
                    Window.this.attributes.minimized = minimized;
                }
            });
        }
    };

    private final GLFWWindowMaximizeCallback defaultMaximizedCallback = new GLFWWindowMaximizeCallback() {
        @Override
        public synchronized void invoke(long windowHandle, final boolean maximized) {
            tasks.add(new Runnable() {
                @Override
                public void run () {
                    Window.this.attributes.maximized = maximized;
                }
            });
        }
    };

    private final GLFWWindowCloseCallback defaultCloseCallback = new GLFWWindowCloseCallback() {
        @Override
        public synchronized void invoke(final long handle) {
            tasks.add(new Runnable() {
                @Override
                public void run () {
                    GLFW.glfwSetWindowShouldClose(handle, false);
                }
            });
        }
    };

    private final GLFWDropCallback filesDroppedCallback = new GLFWDropCallback() {
        @Override
        public synchronized void invoke(final long windowHandle, final int count, final long names) {
            tasks.add(new Runnable() {
                @Override
                public void run () {
                    filesDropped.clear();
                    for (int i = 0; i < count; i++) {
                        filesDropped.add(GLFWDropCallback.getName(names, i));
                    }
                }
            });
        }
    };

    // TODO: set icon
    public Window(WindowAttributes attributes) {
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, attributes.resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, attributes.maximized ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, attributes.autoMinimized ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, attributes.transparentFrameBuffer ? GLFW.GLFW_TRUE : GLFW_FALSE);

        this.attributes = attributes;
        if (attributes.title == null) attributes.title = "";
        if (attributes.fullScreen) {
            GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, videoMode.refreshRate());
            handle = GLFW.glfwCreateWindow(attributes.width, attributes.height, attributes.title,
                    videoMode.refreshRate(), MemoryUtil.NULL);
        } else {
            GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, attributes.decorated ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            handle = GLFW.glfwCreateWindow(attributes.width, attributes.height, attributes.title, MemoryUtil.NULL, MemoryUtil.NULL);
        }
        if (handle == MemoryUtil.NULL) throw new RuntimeException("Unable to create window.");
        setSizeLimits(attributes.minWidth, attributes.minHeight, attributes.maxWidth, attributes.maxHeight);
        // we need to set window position
        if (!attributes.fullScreen) {
            if (attributes.posX == -1 && attributes.posY == -1) setPosition(GraphicsUtils.getMonitorWidth() / 2 - attributes.width / 2,GraphicsUtils.getMonitorHeight() / 2 - attributes.height / 2);
            else setPosition(attributes.posX, attributes.posY);
            if (attributes.maximized) maximize();
        }

        // TODO
        if (attributes.iconPath != null) {

        }

        // register callbacks
        GLFW.glfwSetFramebufferSizeCallback(handle, resizeCallback);
        GLFW.glfwSetWindowFocusCallback(handle, defaultFocusChangeCallback);
        GLFW.glfwSetWindowIconifyCallback(handle, defaultMinimizedCallback);
        GLFW.glfwSetWindowMaximizeCallback(handle, defaultMaximizedCallback);
        GLFW.glfwSetWindowCloseCallback(handle, defaultCloseCallback);
        GLFW.glfwSetDropCallback(handle, filesDroppedCallback);
        GLFW.glfwMakeContextCurrent(handle);
        GLFW.glfwSwapInterval(attributes.vSyncEnabled ? 1 : 0);
        GLFW.glfwShowWindow(handle);
    }

    private void renderWindow(final int width, final int height) {
        updateFramebufferInfo();
        GLFW.glfwMakeContextCurrent(handle);
        GL20.glViewport(0, 0, backBufferWidth, backBufferHeight);
        screen.resize(width, height);
        GraphicsUtils.update();
        screen.refresh();
        GLFW.glfwSwapBuffers(handle);
    }

    private void updateFramebufferInfo() {
        GLFW.glfwGetFramebufferSize(handle, tmpBuffer, tmpBuffer2);
        this.backBufferWidth = tmpBuffer.get(0);
        this.backBufferHeight = tmpBuffer2.get(0);
        GLFW.glfwGetWindowSize(handle, tmpBuffer, tmpBuffer2);
        this.logicalWidth = tmpBuffer.get(0);
        this.logicalHeight = tmpBuffer2.get(0);
    }

    // TODO
    public boolean refresh() {
        for (Runnable task : tasks) {
            task.run();
        }
        boolean shouldRefresh = tasks.size() > 0 || GraphicsUtils.isContinuousRendering();
        synchronized (this) {
            tasks.clear();
            shouldRefresh |= requestRendering && !attributes.minimized;
            requestRendering = false;
        }

        // input handling here?

        if (shouldRefresh) {
            GraphicsUtils.update();
            screen.refresh();
            GLFW.glfwSwapBuffers(handle);
        }

        return shouldRefresh;
    }

    public void requestRendering() {
        synchronized (this) {
            this.requestRendering = true;
        }
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(handle);
    }

    public void setDecorated(boolean decorated) {
        this.attributes.decorated = decorated;
        GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_DECORATED, decorated ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public void setTitle(final String title) {
        this.attributes.title = title;
        GLFW.glfwSetWindowTitle(handle, title);
    }

    public void setPosition(int x, int y) {
        GLFW.glfwSetWindowPos(handle, x, y);
        attributes.posX = x;
        attributes.posY = y;
    }

    public int getPositionX() {
        GLFW.glfwGetWindowPos(handle, tmpBuffer, tmpBuffer2);
        return tmpBuffer.get(0);
    }

    public int getPositionY() {
        GLFW.glfwGetWindowPos(handle, tmpBuffer, tmpBuffer2);
        return tmpBuffer2.get(0);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            GLFW.glfwShowWindow(handle);
        } else {
            GLFW.glfwHideWindow(handle);
        }
    }

    public void closeWindow() {
        GLFW.glfwSetWindowShouldClose(handle, true);
    }

    public void minimize() {
        GLFW.glfwIconifyWindow(handle);
        attributes.minimized = true;
    }

    public boolean isMinimized() {
        return attributes.minimized;
    }

    public void maximize() {
        GLFW.glfwMaximizeWindow(handle);
        attributes.maximized = true;
    }

    public void flash() {
        GLFW.glfwRequestWindowAttention(handle);
    }

    public void restoreWindow() {
        GLFW.glfwRestoreWindow(handle);
    }

    public void focusWindow() {
        GLFW.glfwFocusWindow(handle);
    }

    public boolean isFocused() {
        return focused;
    }

    public void setScreen(WindowScreen screen) {
        if (this.screen != null) {
            this.screen.hide();
            this.screen.window = null;
        }
        this.screen = screen;
        this.screen.show();
        this.screen.window = this;
    }

    // TODO: implement.
    public void setIcon() {

    }

    public long getHandle() {
        return handle;
    }

    public void setSizeLimits(int minWidth, int minHeight, int maxWidth, int maxHeight) {
        GLFW.glfwSetWindowSizeLimits(handle, minWidth > -1 ? minWidth : GLFW.GLFW_DONT_CARE,
                minHeight > -1 ? minHeight : GLFW.GLFW_DONT_CARE, maxWidth > -1 ? maxWidth : GLFW.GLFW_DONT_CARE,
                maxHeight > -1 ? maxHeight : GLFW.GLFW_DONT_CARE);
    }

    @Override
    public void free() {
        GLFW.glfwSetWindowFocusCallback(handle, null);
        GLFW.glfwSetWindowIconifyCallback(handle, null);
        GLFW.glfwSetWindowCloseCallback(handle, null);
        GLFW.glfwSetDropCallback(handle, null);
        GLFW.glfwDestroyWindow(handle);

        defaultFocusChangeCallback.free();
        defaultMinimizedCallback.free();
        defaultMaximizedCallback.free();
        defaultCloseCallback.free();
        filesDroppedCallback.free();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Window other = (Window)obj;
        return handle == other.handle;
    }

}
