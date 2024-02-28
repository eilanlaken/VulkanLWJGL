package org.example.engine.core.graphics;

public class EnvironmentLightAmbient {

    public Color color;
    public float intensity;

    public EnvironmentLightAmbient(float r, float g, float b, float intensity) {
        this.color = new Color(r, g, b,1.0f);
        this.intensity = intensity;
    }

}
