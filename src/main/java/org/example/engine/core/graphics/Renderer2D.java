package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.Resource;

public class Renderer2D implements Resource {

    public Renderer2D() {

    }

    public void begin(CameraLens lens) {

    }

    // push texture region
    // push light
    // push shape
    public void push() {

    }

    public void end() {

    }

    private void draw() {
        // contains the logic that send everything to the GPU for rendering
    }

    @Override
    public void free() {
        // free shader
        // free dynamic mesh
    }
}
