package org.example.engine.core.graphics;

import org.example.engine.core.memory.MemoryResource;
import org.example.engine.core.memory.MemoryResourceHolder;

import java.util.HashMap;
import java.util.Map;

public abstract class WindowScreen implements MemoryResourceHolder {

    protected Window window;

    protected abstract void show();

    protected abstract void refresh();

    protected abstract void hide();

    protected abstract void resize(int width, int height);

    public Map<String, Class<? extends MemoryResource>> getRequiredAssets() {
        return new HashMap<>();
    }

}
