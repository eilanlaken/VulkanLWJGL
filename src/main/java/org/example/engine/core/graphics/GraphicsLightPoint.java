package org.example.engine.core.graphics;

import org.example.engine.core.math.MathVector3;

public class GraphicsLightPoint extends GraphicsLight {

    public MathVector3 position;

    public GraphicsLightPoint(GraphicsColor color, float intensity, final MathVector3 position) {
        this(color, intensity, position.x, position.y, position.z);
    }

    public GraphicsLightPoint(GraphicsColor color, float intensity, float x, float y, float z) {
        super(color.r, color.g, color.b, intensity);
        this.position = new MathVector3(x,y,z);
    }

    public GraphicsLightPoint(float r, float g, float b, float intensity, float x, float y, float z) {
        super(r,g,b, intensity);
        this.position = new MathVector3(x,y,z);
    }

}
