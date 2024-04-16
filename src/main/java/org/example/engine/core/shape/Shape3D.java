package org.example.engine.core.shape;

import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.math.MathVector3;

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

    private float boundingRadius;
    private float boundingRadiusSquared;
    protected boolean updated             = false;
    private boolean boundingRadiusUpdated = false;

    public final boolean contains(final MathVector3 point) {
        return contains(point.x, point.y, point.z);
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

    protected void forceUpdateBoundingRadius() {
        boundingRadius = getUnscaledBoundingRadius() * MathUtils.max(Math.abs(scaleX), Math.abs(scaleY), Math.abs(scaleZ));
        boundingRadiusSquared = boundingRadius * boundingRadius;
        boundingRadiusUpdated = true;
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
        angleX += da_x;
        angleX %= 360.0f;
        if (angleX < 0) angleX += 360.0f;
        updated = false;
    }


    public final void rotY(float da_y) {
        angleY += da_y;
        angleY %= 360.0f;
        if (angleY < 0) angleY += 360.0f;
        updated = false;
    }


    public final void rotZ(float da_z) {
        angleZ += da_z;
        angleZ %= 360.0f;
        if (angleZ < 0) angleZ += 360.0f;
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
        updated = false;
    }

    public final void scaleY(float scaleY) {
        this.scaleY = scaleY;
        boundingRadiusUpdated = false;
        updated = false;
    }

    public final void scaleZ(float scaleZ) {
        this.scaleZ = scaleZ;
        boundingRadiusUpdated = false;
        updated = false;
    }

    public final void scaleXYZ(float scaleX, float scaleY, float scaleZ) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        boundingRadiusUpdated = false;
        updated = false;
    }

    public final void transform(float x, float y, float z,
                                float angleX, float angleY, float angleZ,
                                float scaleX, float scaleY, float scaleZ) {
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
        updated = false;
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
    public final float angleY() {
        return angleY;
    }
    public final float angleZ() {
        return angleZ;
    }
    public float scaleX() { return scaleX; }
    public float scaleY() { return scaleY; }
    public float scaleZ() {
        return scaleZ;
    }

    public abstract boolean contains(float x, float y, float z);
    protected abstract void updateWorldCoordinates();
    protected abstract float getUnscaledBoundingRadius();

    protected static boolean isTransformIdentity(final Shape3D shape) {
        return  MathUtils.isZero(shape.x)
                && MathUtils.isZero(shape.y)
                && MathUtils.isZero(shape.z)

                && MathUtils.isZero(shape.angleX % 360)
                && MathUtils.isZero(shape.angleY % 360)
                && MathUtils.isZero(shape.angleZ % 360)

                && MathUtils.isEqual(shape.scaleX, 1.0f)
                && MathUtils.isEqual(shape.scaleY, 1.0f)
                && MathUtils.isEqual(shape.scaleZ, 1.0f);
    }

}
