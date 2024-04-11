package org.example.engine.core.graphics;

import org.example.engine.core.math.MathVector3;

public class LightSpot extends Light {

    public MathVector3 position;
    public MathVector3 direction;
    public float cutoffAngle;
    public float exponent;

    public LightSpot(Color color, float intensity, final MathVector3 position, final MathVector3 direction, float cutoffAngle, float exponent) {
        super(color.r, color.g, color.b, intensity);
        this.position = new MathVector3(position);
        this.direction = new MathVector3(direction);
        this.cutoffAngle = cutoffAngle;
        this.exponent = exponent;
    }

}
