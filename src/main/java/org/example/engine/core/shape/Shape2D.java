package org.example.engine.core.shape;

import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;

public abstract class Shape2D {

    protected float x      = 0;
    protected float y      = 0;
    protected float angle  = 0;
    protected float scaleX = 1;
    protected float scaleY = 1;

    private float area                    = 0;
    private float boundingRadius          = 0;
    private float boundingRadiusSquared   = 0;
    protected boolean updated             = false;
    private boolean areaUpdated           = false;
    private boolean boundingRadiusUpdated = false;

    public final boolean contains(final MathVector2 point) {
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

    public final void dx(float dx) {
        this.x += dx;
        updated = false;
    }

    public final void dy(float dy) {
        this.y += dy;
        updated = false;
    }

    public final void dx_dy_rot(float dx, float dy, float da) {
        this.x += dx;
        this.y += dy;
        angle += da;
        angle = MathUtils.normalizeAngleDeg(angle);
        updated = false;
    }

    public final void rot(float da) {
        angle = MathUtils.normalizeAngleDeg(angle + da);
        updated = false;
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
        this.angle = MathUtils.normalizeAngleDeg(angle);
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

    public final void setTransform(float x, float y, float angle, float scaleX, float scaleY) {
        this.x = x;
        this.y = y;
        this.angle = MathUtils.normalizeAngleDeg(angle);
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

    protected static boolean isTransformIdentity(final Shape2D shape) {
        return MathUtils.isZero(shape.x) && MathUtils.isZero(shape.y)
                && MathUtils.isZero(shape.angle % 360)
                && MathUtils.isEqual(shape.scaleX, 1.0f) && MathUtils.isEqual(shape.scaleY, 1.0f);
    }

}
