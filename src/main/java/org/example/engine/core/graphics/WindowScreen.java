package org.example.engine.core.graphics;

import org.example.engine.core.memory.Resource;

import java.util.HashMap;
import java.util.Map;

public abstract class WindowScreen implements Resource {

    protected Window window;

    protected abstract void show();

    protected abstract void refresh();

    protected abstract void hide();

    protected abstract void resize(int width, int height);

    public Map<String, Class<? extends Resource>> getRequiredAssets() {
        return new HashMap<>();
    }

}
