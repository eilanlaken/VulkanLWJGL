package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.shape.ShapeException;

public class ShapeCircle extends Shape {

    public  final Vector2 localCenter;
    public  final float   r;
    public  final float   r2;
    private final Vector2 worldCenter;

    public ShapeCircle(float r, float x, float y) {
        if (r < 0) throw new IllegalArgumentException("Radius must be positive. Got: " + r);
        this.localCenter = new Vector2(x, y);
        this.r = r;
        this.r2 = r * r;
        this.worldCenter = new Vector2(localCenter);
    }

    public ShapeCircle(float r) {
        this(r, 0, 0);
    }

    @Override
    protected void updateWorldCoordinates() {
        worldCenter.set(localCenter);
        if (!MathUtils.isZero(angleRad)) worldCenter.rotateRad(angleRad);
        worldCenter.add(x, y);
    }

    @Override
    protected Vector2 calculateLocalCenter() {
        return localCenter;
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        return (x - worldCenter.x) * (x - worldCenter.x) + (y - worldCenter.y) * (y - worldCenter.y) <= r2 + MathUtils.FLOAT_ROUNDING_ERROR;
    }

    @Override
    protected float calculateBoundingRadius() {
        return localCenter.len() + r;
    }

    @Override
    protected float calculateArea() {
        return MathUtils.PI * r * r;
    }

    public Vector2 getLocalCenter() {
        return localCenter;
    }

    public Vector2 getWorldCenter() {
        if (!updated) update();
        return worldCenter;
    }

    public float getR() {
        return r;
    }

    @Override
    protected Array<Vector2> getWorldVertices() {
        throw new ShapeException("Cannot get a world vertices list for " + ShapeCircle.class.getSimpleName() + ": operation not supported. A circle: " + ShapeCircle.class.getSimpleName() + " is only represented using a center: " + Vector2.class.getSimpleName() + " and a radius: float.");
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() +
                "| worldCenter = " + worldCenter +
                ", radius = " + r +
                '}';
    }
}