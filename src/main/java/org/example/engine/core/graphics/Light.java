package org.example.engine.core.graphics;

public class Light {

    public Color color;
    public float intensity;

    public Light(float r, float g, float b, float intensity) {
        this.color = new Color(r, g, b,1.0f);
        this.intensity = intensity;
    }

}
