package org.example.engine.core.math;

public abstract class Shape2D {

    protected float x;
    protected float y;
    protected float angle;
    protected float scaleX, scaleY;
    protected boolean updated;

    protected float originalArea = -1.0f;
    protected float area;
    protected float originalBoundingRadius = -1.0f;
    private float boundingRadius;

    public Shape2D() {
        this.x = 0;
        this.y = 0;
        this.angle = 0;
        this.scaleX = 1;
        this.scaleY = 1;
        updated = false;
    }

    public boolean contains(final Vector2 point) {
        return contains(point.x, point.y);
    }



    public final float getArea() {
        if (originalArea < 0.0f) {
            originalArea = calculateOriginalArea();
            area = originalArea * Math.abs(scaleX) * Math.abs(scaleY);
        }
        return area;
    }

    public final float getBoundingRadius() {
        if (originalBoundingRadius < 0.0f) {
            originalBoundingRadius = calculateOriginalBoundingRadius();
            boundingRadius = originalBoundingRadius * Math.max(Math.abs(scaleX), Math.abs(scaleY));
        }
        return boundingRadius;
    }

    public final void update() {
        if (updated) return;
        updateWorldCoordinates();
        updated = true;
    }

    public void applyTransform() {
        updateWorldCoordinates();
        bakeCurrentTransformToLocalCoordinates();
        originalBoundingRadius = -1;//calculateOriginalBoundingRadius();
        originalArea = calculateOriginalArea();
        transform(0,0,0,1,1); // reset transform
    }

    public final void x(float x) {
        this.x = x;
        updated = false;
    }
    public final void y(float y) {
        this.y = y;
        updated = false;
    }
    public final void angle(float angle) {
        this.angle = angle;
        updated = false;
    }
    public final void scaleX(float scaleX) {
        this.scaleX = scaleX;
        updated = false;
    }
    public final void scaleY(float scaleY) {
        this.scaleY = scaleY;
        updated = false;
    }
    public final void transform(float x, float y, float angle, float scaleX, float scaleY) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        updated = false;
    }
    public final float x() {
        return x;
    }
    public final float y() {
        return y;
    }
    public final float angle() {
        return angle;
    }
    public float scaleX() {
        return scaleX;
    }
    public float scaleY() {
        return scaleY;
    }

    public abstract boolean contains(float x, float y);
    protected abstract void bakeCurrentTransformToLocalCoordinates();
    protected abstract void updateWorldCoordinates();
    protected abstract float calculateOriginalArea();
    protected abstract float calculateOriginalBoundingRadius();

}
