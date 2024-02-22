package org.example.engine.core.graphics;

public class EnvironmentLightSpot extends EnvironmentLight {

    public float cutoffAngle;
    public float exponent;

    public EnvironmentLightSpot(Color color, float intensity, float cutoffAngle, float exponent) {
        super(color, intensity);
        this.cutoffAngle = cutoffAngle;
        this.exponent = exponent;
    }

}
