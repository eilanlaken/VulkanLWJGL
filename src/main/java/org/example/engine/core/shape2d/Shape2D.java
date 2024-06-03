package org.example.engine.core.shape2d;

import org.example.engine.core.math.Vector2;

public abstract class Shape2D {

    private   float       area                       = 0;
    private   float       boundingRadius             = 0;
    private   float       boundingRadiusSquared      = 0;
    private   boolean     calcLocalGeometryCenter    = false;
    protected Vector2 geometryCenter             = new Vector2();

    public final boolean contains(final Vector2 point) {
        return contains(point.x, point.y);
    }

    public abstract boolean contains(float x, float y);
    public abstract float getArea();
    public abstract float getBoundingRadius();
    public abstract float getBoundingRadiusSquared();

}
