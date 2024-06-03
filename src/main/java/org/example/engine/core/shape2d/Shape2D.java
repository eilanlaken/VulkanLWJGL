package org.example.engine.core.shape2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathVector2;

public abstract class Shape2D {

    private   float       area                       = 0;
    private   float       boundingRadius             = 0;
    private   float       boundingRadiusSquared      = 0;
    private   boolean     calcLocalGeometryCenter    = false;
    protected MathVector2 geometryCenter             = new MathVector2();

    public final boolean contains(final MathVector2 point) {
        return contains(point.x, point.y);
    }

    public abstract boolean contains(float x, float y);
    public abstract float getArea();
    public abstract float getBoundingRadius();
    public abstract float getBoundingRadiusSquared();

}
