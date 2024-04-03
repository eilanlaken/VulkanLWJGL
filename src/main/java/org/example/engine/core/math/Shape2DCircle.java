package org.example.engine.core.math;

public class Shape2DCircle extends Shape2D {

    public Vector2 localCenter;
    public float originalRadius;
    public Vector2 worldCenter;
    public float radius;

    public Shape2DCircle(float x, float y, float r) {
        if (r < 0) throw new IllegalArgumentException("Radius must be positive. Got: " + r);
        this.localCenter = new Vector2(x, y);
        this.originalRadius = r;
        this.worldCenter = new Vector2(localCenter);
        this.radius = r;
    }

    @Override
    protected void updateWorldCoordinates() {
        if (scaleX != scaleY) throw new IllegalStateException(this.getClass().getSimpleName() + " must have scaleX == scaleY to maintain circle proportions. scaleX: " + scaleX + " and scaleY: " + scaleX + ".");
        worldCenter.set(localCenter);
        if (!MathUtils.isEqual(scaleX, 1.0f)) worldCenter.scl(scaleX, scaleY);
        if (!MathUtils.isZero(angle)) worldCenter.rotateDeg(angle);
        worldCenter.add(x, y);
        radius = scaleX * originalRadius;
    }

    @Override
    protected float calculateOriginalBoundingRadius() {
        return localCenter.len() + originalRadius;
    }

    @Override
    public boolean contains(float x, float y) {
        if (!updated) update();
        return (x - worldCenter.x) * (x - worldCenter.x) + (y - worldCenter.y) * (y - worldCenter.y) <= radius;
    }

    @Override
    public float getArea() {
        return (float) (Math.PI * radius * radius);
    }

    @Override
    public float getPerimeter() {
        return (float) (2.0 * Math.PI * radius);
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() +
                "| worldCenter = " + worldCenter +
                ", radius = " + radius +
                '}';
    }
}