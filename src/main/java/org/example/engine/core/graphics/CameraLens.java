package org.example.engine.core.graphics;

import org.example.engine.core.math.Vector3;

public class CameraLens {

    private CameraLensProjectionType projectionType;
    public Vector3 position = new Vector3(0,0,0);
    public Vector3 forward = new Vector3(0,0,-1);
    public Vector3 up = new Vector3(0,1,0);

}
