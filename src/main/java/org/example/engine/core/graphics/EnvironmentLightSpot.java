package org.example.engine.core.graphics;

import org.example.engine.core.math.Vector3;

public class EnvironmentLightSpot extends EnvironmentLight {

    public Vector3 position;
    public Vector3 direction;
    public float cutoffAngle;
    public float exponent;

    public EnvironmentLightSpot(Color color, float intensity, final Vector3 position, final Vector3 direction, float cutoffAngle, float exponent) {
        super(color.r, color.g, color.b, intensity);
        this.position = new Vector3(position);
        this.direction = new Vector3(direction);
        this.cutoffAngle = cutoffAngle;
        this.exponent = exponent;
    }

}
