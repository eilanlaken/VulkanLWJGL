package org.example.engine.core.math;

public abstract class Shape2D {

    protected float x = 0;
    protected float y = 0;
    protected float angle = 0;
    protected float scaleX = 1;
    protected float scaleY = 1;
    protected boolean updated = false;

    private float area;
    private float boundingRadius;
    private float boundingRadiusSquared;
    private boolean areaUpdated = false;
    private boolean boundingRadiusUpdated = false;

    public final boolean contains(final Vector2 point) {
        return contains(point.x, point.y);
    }

    public final float getArea() {
        if (!areaUpdated) {
            area = getUnscaledArea() * Math.abs(scaleX) * Math.abs(scaleY);
            areaUpdated = true;
        }
        return area;
    }

    public final float getBoundingRadius() {
        if (!boundingRadiusUpdated) {
            boundingRadius = getUnscaledBoundingRadius() * Math.max(Math.abs(scaleX), Math.abs(scaleY));
            boundingRadiusSquared = boundingRadius * boundingRadius;
            boundingRadiusUpdated = true;
        }
        return boundingRadius;
    }

    public final float getBoundingRadiusSquared() {
        if (!boundingRadiusUpdated) {
            boundingRadius = getUnscaledBoundingRadius() * Math.max(Math.abs(scaleX), Math.abs(scaleY));
            boundingRadiusSquared = boundingRadius * boundingRadius;
            boundingRadiusUpdated = true;
        }
        return boundingRadiusSquared;
    }

    public final void update() {
        if (updated) return;
        updateWorldCoordinates();
        updated = true;
    }

    public final void x(float x) {
        this.x = x;
        updated = false;
    }
    public final void y(float y) {
        this.y = y;
        updated = false;
    }
    public final void xy(float x, float y) {
        this.x = x;
        this.y = y;
        updated = false;
    }
    public final void angle(float angle) {
        this.angle = angle;
        updated = false;
    }
    public final void scaleX(float scaleX) {
        this.scaleX = scaleX;
        boundingRadiusUpdated = false;
        areaUpdated = false;
        updated = false;
    }
    public final void scaleY(float scaleY) {
        this.scaleY = scaleY;
        boundingRadiusUpdated = false;
        areaUpdated = false;
        updated = false;
    }
    public final void scaleXY(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        boundingRadiusUpdated = false;
        areaUpdated = false;
        updated = false;
    }
    public final void transform(float x, float y, float angle, float scaleX, float scaleY) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        boundingRadiusUpdated = false;
        areaUpdated = false;
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
    protected abstract void updateWorldCoordinates();
    protected abstract float getUnscaledArea();
    protected abstract float getUnscaledBoundingRadius();
}
