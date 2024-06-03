package org.example.engine.core.shape2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.shape.ShapeException;

public class Shape2DCircle extends Shape2D {

    public  final Vector2 localCenter;
    public  final float   localRadius;
    private final Vector2 worldCenter;
    private float         worldRadius;

    public Shape2DCircle(float r, float x, float y) {
        if (r < 0) throw new ShapeException("Radius must be positive. Got: " + r);
        this.localCenter = new Vector2(x, y);
        this.localRadius = r;
        this.worldCenter = new Vector2(localCenter);
        this.worldRadius = r;
    }

    public Shape2DCircle(float r) {
        this(r, 0, 0);
    }

    @Override
    protected void updateWorldCoordinates() {
        if (!MathUtils.floatsEqual(scaleX, scaleY)) throw new ShapeException(this.getClass().getSimpleName() + " must have scaleX == scaleY to maintain circle proportions. scaleX: " + scaleX + " and scaleY: " + scaleX + ".");
        worldCenter.set(localCenter);
        if (!MathUtils.floatsEqual(scaleX, 1.0f)) worldCenter.scl(scaleX, scaleY);
        if (!MathUtils.isZero(angle)) worldCenter.rotateDeg(angle);
        worldCenter.add(x, y);
        worldRadius = Math.abs(scaleX) * localRadius;
    }

    @Override
    protected Vector2 calculateLocalGeometryCenter() {
        return localCenter;
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        return (x - worldCenter.x) * (x - worldCenter.x) + (y - worldCenter.y) * (y - worldCenter.y) <= worldRadius * worldRadius + MathUtils.FLOAT_ROUNDING_ERROR;
    }

    @Override
    protected float calculateUnscaledBoundingRadius() {
        return localCenter.len() + localRadius;
    }

    @Override
    protected float calculateUnscaledArea() {
        return MathUtils.PI * localRadius * localRadius;
    }

    public Vector2 getLocalCenter() {
        return localCenter;
    }

    public Vector2 getWorldCenter() {
        if (!updated) update();
        return worldCenter;
    }

    public float getLocalRadius() {
        return localRadius;
    }

    public float getWorldRadius() {
        if (!updated) update();
        return worldRadius;
    }

    @Override
    protected Array<Vector2> getWorldVertices() {
        throw new ShapeException("Cannot get a world vertices list for " + Shape2DCircle.class.getSimpleName() + ": operation not supported. A circle: " + Shape2DCircle.class.getSimpleName() + " is only represented using a center: " + Vector2.class.getSimpleName() + " and a radius: float.");
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() +
                "| worldCenter = " + worldCenter +
                ", radius = " + worldRadius +
                '}';
    }
}