package org.example.engine.core.assets;

import org.example.engine.core.memory.Resource;

public class DebugDependency implements Resource {

    public String content;

    public DebugDependency(String content) {
        this.content = content;
    }

    @Override
    public void free() {

    }
}
