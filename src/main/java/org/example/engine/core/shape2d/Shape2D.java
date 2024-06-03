package org.example.engine.core.shape2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Vector2;

public abstract class Shape2D {

    protected float x      = 0;
    protected float y      = 0;
    protected float angle  = 0;
    protected float scaleX = 1;
    protected float scaleY = 1;

    private   float       area                       = 0;
    private   float       boundingRadius             = 0;
    private   float       boundingRadiusSquared      = 0;
    private   float       unscaledArea               = 0;
    private   float       unscaledBoundingRadius     = 0;
    private   boolean     calcUnscaledArea           = false;
    private   boolean     calcUnscaledBoundingRadius = false;
    private   boolean     calcLocalGeometryCenter    = false;
    protected boolean     updated                    = false;
    private   boolean     areaUpdated                = false;
    private   boolean     boundingRadiusUpdated      = false;
    protected Vector2 localGeometryCenter        = new Vector2();
    protected Vector2 geometryCenter             = new Vector2();

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

    public final Vector2 geometryCenter() {
        if (!calcLocalGeometryCenter) {
            localGeometryCenter.set(calculateLocalGeometryCenter());
            calcLocalGeometryCenter = true;
        }
        return geometryCenter.set(localGeometryCenter).scl(scaleX, scaleY).rotateDeg(angle).add(x,y);
    }

    public final float getArea() {
        if (!areaUpdated) {
            area = getUnscaledArea() * Math.abs(scaleX) * Math.abs(scaleY);
            areaUpdated = true;
        }
        return area;
    }

    public final float getBoundingRadius() {
        updateBoundingRadius();
        return boundingRadius;
    }

    public final float getBoundingRadiusSquared() {
        updateBoundingRadius();
        return boundingRadiusSquared;
    }

    public final float getMinExtentX() {
        updateBoundingRadius();
        return x - boundingRadius;
    }

    public final float getMaxExtentX() {
        updateBoundingRadius();
        return x + boundingRadius;
    }

    public final float getMinExtentY() {
        updateBoundingRadius();
        return y - boundingRadius;
    }

    public final float getMaxExtentY() {
        updateBoundingRadius();
        return y + boundingRadius;
    }

    public final void update() {
        if (updated) return;
        updateWorldCoordinates();
        updated = true;
    }

    public final void updateBoundingRadius() {
        if (boundingRadiusUpdated) return;
        boundingRadius = getUnscaledBoundingRadius() * Math.max(Math.abs(scaleX), Math.abs(scaleY));
        boundingRadiusSquared = boundingRadius * boundingRadius;
        boundingRadiusUpdated = true;
    }

    public final void dx(float dx) {
        this.x += dx;
        updated = false;
    }

    public final void dy(float dy) {
        this.y += dy;
        updated = false;
    }

    public final void  dx_dy(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        updated = false;
    }

    public final void dx_dy_rot(float dx, float dy, float da) {
        this.x += dx;
        this.y += dy;
        angle += da;
        updated = false;
    }

    public final void rot(float da) {
        angle += da;
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

    public final void setTransform(float x, float y, float angleDeg) {
        this.x = x;
        this.y = y;
        this.angle = angleDeg;
        updated = false;
    }

    public final void setTransform(float x, float y, float angleDeg, float scaleX, float scaleY) {
        this.x = x;
        this.y = y;
        this.angle = angleDeg;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        boundingRadiusUpdated = false;
        areaUpdated = false;
        updated = false;
    }

    protected final float getUnscaledArea() {
        if (!calcUnscaledArea) {
            unscaledArea = calculateUnscaledArea();
            calcUnscaledArea = true;
        }
        return unscaledArea;
    }

    protected final float getUnscaledBoundingRadius() {
        if (!calcUnscaledBoundingRadius) {
            unscaledBoundingRadius = calculateUnscaledBoundingRadius();
            calcUnscaledBoundingRadius = true;
        }
        return unscaledBoundingRadius;
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
    public final float scaleX() {
        return scaleX;
    }
    public final float scaleY() {
        return scaleY;
    }

    protected abstract boolean                       containsPoint(float x, float y);
    protected abstract void                          updateWorldCoordinates();
    protected abstract Array<Vector2> getWorldVertices();
    protected abstract float                         calculateUnscaledBoundingRadius();
    protected abstract float                         calculateUnscaledArea();
    protected abstract Vector2 calculateLocalGeometryCenter();

}
