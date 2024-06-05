package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Vector2;

public abstract class Shape {

    protected float x        = 0;
    protected float y        = 0;
    protected float angleRad = 0;

    protected float   area        = 0;
    protected boolean calcArea    = false;
    protected boolean calcRadius  = false;
    protected float   r           = 0; // the bounding radius r
    protected float   r2          = 0; // r squared
    protected boolean updated     = false;
    protected Vector2 localCenter = null;
    protected Vector2 worldCenter = new Vector2();

    public final boolean contains(final Vector2 point) {
        return contains(point.x, point.y);
    }

    public final boolean contains(float x, float y) {
        if (!updated) update();
        return containsPoint(x, y);
    }

    public final Array<Vector2> worldVertices() {
        if (!updated) update();
        return getWorldVertices();
    }

    public final Vector2 worldCenter() {
        if (localCenter == null) {
            localCenter = calculateLocalCenter();
        }
        return worldCenter.set(localCenter).rotateDeg(angleRad).add(x,y); // scale -> rotate -> translate ("scale" by 1)
    }

    public final float area() {
        if (!calcArea) {
            area = calculateArea();
            calcArea = true;
        }
        return area;
    }

    public final float boundingRadius() {
        if (!calcRadius) {
            r = calculateBoundingRadius();
            r2 = r * r;
            calcRadius = true;
        }
        return r;
    }

    public final float boundingRadiusSquared() {
        if (!calcRadius) {
            r = calculateBoundingRadius();
            r2 = r * r;
            calcRadius = true;
        }
        return r2;
    }

    public final float getMinExtentX() {
        return x - r;
    }

    public final float getMaxExtentX() {
        return x + r;
    }

    public final float getMinExtentY() {
        return y - r;
    }

    public final float getMaxExtentY() {
        return y + r;
    }

    public final void update() {
        if (updated) return;
        updateWorldCoordinates();
        updated = true;
    }

    public final void dx(float dx) {
        x += dx;
        updated = false;
    }

    public final void dy(float dy) {
        y += dy;
        updated = false;
    }

    public final void  dx_dy(float dx, float dy) {
        x += dx;
        y += dy;
        updated = false;
    }

    public final void dx_dy_rot(float dx, float dy, float da) {
        x += dx;
        y += dy;
        angleRad += da;
        updated = false;
    }

    public final void rot(float da) {
        angleRad += da;
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
        this.angleRad = angle;
        updated = false;
    }

    public final void setTransform(float x, float y, float angleDeg) {
        this.x = x;
        this.y = y;
        this.angleRad = angleDeg;
        updated = false;
    }

    public final float x() {
        return x;
    }
    public final float y() {
        return y;
    }
    public final float angle() {
        return angleRad;
    }

    protected abstract boolean                       containsPoint(float x, float y);
    protected abstract void                          updateWorldCoordinates();
    protected abstract Array<Vector2> getWorldVertices();
    protected abstract float calculateBoundingRadius();
    protected abstract float calculateArea();
    protected abstract Vector2 calculateLocalCenter();

}
