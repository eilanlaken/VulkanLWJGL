package org.example.engine.components;

import org.example.engine.core.graphics.CameraLens_dep;

public class ComponentGraphicsCamera {

    public CameraLens_dep lens;

    // single all args constructor
    protected ComponentGraphicsCamera(CameraLens_dep lens) {
        this.lens = lens;
    }

}
