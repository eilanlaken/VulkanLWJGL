package org.example.engine.ecs;

import org.example.engine.core.graphics.GraphicsCamera;

public class ComponentGraphicsCamera {

    public GraphicsCamera camera;

    // single all args constructor
    protected ComponentGraphicsCamera(GraphicsCamera camera) {
        this.camera = camera;
    }

}
