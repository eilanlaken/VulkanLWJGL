package org.example.engine.ecs;

import org.example.engine.core.graphics.Camera;

public class ComponentGraphicsCamera {

    public Camera camera;

    // single all args constructor
    protected ComponentGraphicsCamera(Camera camera) {
        this.camera = camera;
    }

}
