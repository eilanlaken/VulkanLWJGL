package org.example.engine.core.graphics;

import org.example.engine.core.memory.Resource;

public abstract class WindowScreen implements Resource {

    protected Window window;

    protected abstract void show();

    protected abstract void refresh();

    protected abstract void hide();

    protected abstract void resize(int width, int height);

}
