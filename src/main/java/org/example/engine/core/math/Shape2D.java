package org.example.engine.core.math;

public abstract class Shape2D {

    protected float x;
    protected float y;
    protected float angle;
    protected float scaleX, scaleY;
    protected boolean updated;
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

    public final void setTranslation(float x, float y) {
        this.x = x;
        this.y = y;
        updated = false;
    }

    public final void setRotation(float degrees) {
        this.angle = degrees;
        updated = false;
    }

    public final void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        updated = false;
    }

    public final void setTransform(float x, float y, float angle, float scaleX, float scaleY) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        updated = false;
    }

    protected abstract float calculateOriginalBoundingRadius();

    public final float getBoundingRadius() {
        if (originalBoundingRadius < 0.0f) {
            originalBoundingRadius = calculateOriginalBoundingRadius();
            boundingRadius = originalBoundingRadius * Math.max(Math.abs(scaleX), Math.abs(scaleY));
        }
        return boundingRadius;
    }

    public final void update(float x, float y, float angle, float scaleX, float scaleY) {
        setTransform(x, y, angle, scaleX, scaleY);
        update();
    }

    public final void update() {
        if (updated) return;
        updateWorldCoordinates();
        updated = true;
    }

    public final float getX() {
        return x;
    }
    public final float getY() {
        return y;
    }
    public final float getAngle() {
        return angle;
    }
    public float getScaleX() {
        return scaleX;
    }
    public float getScaleY() {
        return scaleY;
    }
    public abstract boolean contains(float x, float y);
    public abstract float getArea();
    public abstract float getPerimeter();
    protected abstract void updateWorldCoordinates();

}
