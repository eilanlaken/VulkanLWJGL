package org.example.engine.core.graphics;

import org.example.engine.core.math.Vector3;

public class EnvironmentLightPoint extends EnvironmentLight {

    public Vector3 position;

    public EnvironmentLightPoint(Color color, float intensity, final Vector3 position) {
        this(color, intensity, position.x, position.y, position.z);
    }

    public EnvironmentLightPoint(Color color, float intensity, float x, float y, float z) {
        super(color.r, color.g, color.b, intensity);
        this.position = new Vector3(x,y,z);
    }

    public EnvironmentLightPoint(float r, float g, float b, float intensity, float x, float y, float z) {
        super(r,g,b, intensity);
        this.position = new Vector3(x,y,z);
    }

}
