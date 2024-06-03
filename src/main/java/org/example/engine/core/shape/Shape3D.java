package org.example.engine.core.shape;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector3;

public abstract class Shape3D {

    protected float x      = 0;
    protected float y      = 0;
    protected float z      = 0;
    protected float angleX = 0;
    protected float angleY = 0;
    protected float angleZ = 0;
    protected float scaleX = 1;
    protected float scaleY = 1;
    protected float scaleZ = 1;

    private float     volume                     = 0;
    private float     boundingRadius             = 0;
    private float     boundingRadiusSquared      = 0;
    private float     unscaledVolume             = 0;
    private float     unscaledBoundingRadius     = 0;
    private boolean   calcUnscaledVolume         = false;
    private boolean   calcUnscaledBoundingRadius = false;
    protected boolean updated                    = false;
    private boolean   areaUpdated                = false;
    private boolean   boundingRadiusUpdated      = false;

    public final boolean contains(final Vector3 point) {
        return contains(point.x, point.y, point.z);
    }

    public final boolean contains(float x, float y, float z) {
        if (!updated) update();
        return containsPoint(x, y, z);
    }

    public final Array<Vector3> worldVertices() {
        if (!updated) update();
        return getWorldVertices();
    }

    public final float getVolume() {
        if (!areaUpdated) {
            volume = getUnscaledVolume() * Math.abs(scaleX) * Math.abs(scaleY) * Math.abs(scaleZ);
            areaUpdated = true;
        }
        return volume;
    }

    public final float getBoundingRadius() {
        if (!boundingRadiusUpdated) {
            boundingRadius = getUnscaledBoundingRadius() * MathUtils.max(Math.abs(scaleX), Math.abs(scaleY), Math.abs(scaleZ));
            boundingRadiusSquared = boundingRadius * boundingRadius;
            boundingRadiusUpdated = true;
        }
        return boundingRadius;
    }

    public final float getBoundingRadiusSquared() {
        if (!boundingRadiusUpdated) {
            boundingRadius = getUnscaledBoundingRadius() * MathUtils.max(Math.abs(scaleX), Math.abs(scaleY), Math.abs(scaleZ));
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

    public final void dz(float dz) {
        this.z += dz;
        updated = false;
    }

    public final void dx_dy_dz(float dx, float dy, float dz) {
        this.x += dx;
        this.y += dy;
        this.z += dz;
        updated = false;
    }

    public final void rotX(float da_x) {
        angleX = MathUtils.normalizeAngleDeg(angleX + da_x);
        updated = false;
    }

    public final void rotY(float da_y) {
        angleY = MathUtils.normalizeAngleDeg(angleY + da_y);
        updated = false;
    }

    public final void rotZ(float da_z) {
        angleZ = MathUtils.normalizeAngleDeg(angleZ + da_z);
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

    public final void z(float z) {
        this.z = z;
        updated = false;
    }

    public final void xyz(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        updated = false;
    }

    public final void angleX(float angleX) {
        this.angleX = MathUtils.normalizeAngleDeg(angleX);
        updated = false;
    }

    public final void angleY(float angleY) {
        this.angleY = MathUtils.normalizeAngleDeg(angleY);
        updated = false;
    }

    public final void angleZ(float angleZ) {
        this.angleZ = MathUtils.normalizeAngleDeg(angleZ);
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

    public final void scaleZ(float scaleZ) {
        this.scaleZ = scaleZ;
        boundingRadiusUpdated = false;
        areaUpdated = false;
        updated = false;
    }

    public final void scaleXYZ(float scaleX, float scaleY, float scaleZ) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        boundingRadiusUpdated = false;
        areaUpdated = false;
        updated = false;
    }

    public final void setTransform(float x, float y, float z, float angleX, float angleY, float angleZ, float scaleX, float scaleY, float scaleZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.angleX = MathUtils.normalizeAngleDeg(angleX);
        this.angleY = MathUtils.normalizeAngleDeg(angleY);
        this.angleZ = MathUtils.normalizeAngleDeg(angleZ);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        boundingRadiusUpdated = false;
        areaUpdated = false;
        updated = false;
    }

    protected final float getUnscaledVolume() {
        if (!calcUnscaledVolume) {
            unscaledVolume = calculateUnscaledVolume();
            calcUnscaledVolume = true;
        }
        return unscaledVolume;
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
    public final float z() { return z; }
    public final float angleX() {
        return angleX;
    }
    public final float angleY() { return angleY; }
    public final float angleZ() { return angleZ; }
    public final float scaleX() {
        return scaleX;
    }
    public final float scaleY() {
        return scaleY;
    }
    public final float scaleZ() { return scaleZ; }

    protected abstract boolean                       containsPoint(float x, float y, float z);
    protected abstract void                          updateWorldCoordinates();
    protected abstract Array<Vector3> getWorldVertices();
    protected abstract float                         calculateUnscaledBoundingRadius();
    protected abstract float                         calculateUnscaledVolume();

}
