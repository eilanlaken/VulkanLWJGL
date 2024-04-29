package org.example.engine.core.graphics;

public class GraphicsLight {

    public GraphicsColor color;
    public float intensity;

    public GraphicsLight(float r, float g, float b, float intensity) {
        this.color = new GraphicsColor(r, g, b,1.0f);
        this.intensity = intensity;
    }

}
