package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.Resource;

public class Renderer2D implements Resource {

    public Renderer2D() {

    }

    @Override
    public void free() {
        // free shader
        // free dynamic mesh
    }
}
