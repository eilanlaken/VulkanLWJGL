package org.example.engine.components;

import org.example.engine.core.graphics.CameraLens;

public class ComponentGraphicsCamera {

    public CameraLens lens;

    // single all args constructor
    protected ComponentGraphicsCamera(CameraLens lens) {
        this.lens = lens;
    }

}