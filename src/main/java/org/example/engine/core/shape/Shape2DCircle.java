package org.example.engine.core.shape;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;

public class Shape2DCircle extends Shape2D {

    public final MathVector2 localCenter;
    public final float localRadius;
    private MathVector2 worldCenter;
    private float worldRadius;

    public Shape2DCircle(float r, float x, float y) {
        if (r < 0) throw new IllegalArgumentException("Radius must be positive. Got: " + r);
        this.localCenter = new MathVector2(x, y);
        this.localRadius = r;
        this.worldCenter = new MathVector2(localCenter);
        this.worldRadius = r;
    }

    public Shape2DCircle(float r) {
        this(r, 0, 0);
    }

    @Override
    protected void updateWorldCoordinates() {
        if (scaleX != scaleY) throw new IllegalStateException(this.getClass().getSimpleName() + " must have scaleX == scaleY to maintain circle proportions. scaleX: " + scaleX + " and scaleY: " + scaleX + ".");
        worldCenter.set(localCenter);
        if (!MathUtils.floatsEqual(scaleX, 1.0f)) worldCenter.scl(scaleX, scaleY);
        if (!MathUtils.isZero(angle)) worldCenter.rotateDeg(angle);
        worldCenter.add(x, y);
        worldRadius = scaleX * localRadius;
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        return (x - worldCenter.x) * (x - worldCenter.x) + (y - worldCenter.y) * (y - worldCenter.y) <= worldRadius * worldRadius;
    }

    @Override
    protected float calculateUnscaledBoundingRadius() {
        return localCenter.len() + localRadius;
    }

    @Override
    protected float calculateUnscaledArea() {
        return MathUtils.PI * localRadius * localRadius;
    }

    public MathVector2 getWorldCenter() {
        if (!updated) update();
        return worldCenter;
    }

    public float getWorldRadius() {
        if (!updated) update();
        return worldRadius;
    }

    @Override
    protected CollectionsArray<MathVector2> getWorldVertices() {
        throw new UnsupportedOperationException("Cannot get a world vertices list for " + Shape2DCircle.class.getSimpleName() + ": operation not supported. A circle: " + Shape2DCircle.class.getSimpleName() + " is only represented using a center: " + MathVector2.class.getSimpleName() + " and a radius: float.");
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() +
                "| worldCenter = " + worldCenter +
                ", radius = " + worldRadius +
                '}';
    }
}