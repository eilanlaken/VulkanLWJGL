package org.example.engine.core.math;

public class Shape2DCircle extends Shape2D {

    public final Vector2 localCenter;
    public final float localRadius;

    // TODO: change to private with getters and setters.
    public Vector2 worldCenter;
    public float worldRadius;

    private final float unscaledArea;
    private final float unscaledBoundingRadius;

    public Shape2DCircle(float r, float x, float y) {
        if (r < 0) throw new IllegalArgumentException("Radius must be positive. Got: " + r);
        this.localCenter = new Vector2(x, y);
        this.localRadius = r;
        this.worldCenter = new Vector2(localCenter);
        this.worldRadius = r;

        this.unscaledArea = MathUtils.PI * localRadius * localRadius;
        this.unscaledBoundingRadius = localCenter.len() + r;
    }

    public Shape2DCircle(float r) {
        this(r, 0, 0);
    }

    @Override
    protected void updateWorldCoordinates() {
        if (scaleX != scaleY) throw new IllegalStateException(this.getClass().getSimpleName() + " must have scaleX == scaleY to maintain circle proportions. scaleX: " + scaleX + " and scaleY: " + scaleX + ".");
        worldCenter.set(localCenter);
        if (!MathUtils.isEqual(scaleX, 1.0f)) worldCenter.scl(scaleX, scaleY);
        if (!MathUtils.isZero(angle)) worldCenter.rotateDeg(angle);
        worldCenter.add(x, y);
        worldRadius = scaleX * localRadius;
    }

    @Override
    public boolean contains(float x, float y) {
        if (!updated) update();
        return (x - worldCenter.x) * (x - worldCenter.x) + (y - worldCenter.y) * (y - worldCenter.y) <= worldRadius;
    }

    @Override
    protected float getUnscaledBoundingRadius() {
        return unscaledBoundingRadius;
    }

    @Override
    protected float getUnscaledArea() {
        return unscaledArea;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() +
                "| worldCenter = " + worldCenter +
                ", radius = " + worldRadius +
                '}';
    }
}