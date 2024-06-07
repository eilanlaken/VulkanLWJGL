package org.example.engine.core.physics2d_new;

import org.example.engine.core.math.Vector2;

abstract class BodyCollider {

    // TODO: see if possible and better to replace with index or something.
    public Body body = null;

    public float   density         = 1;
    public float   staticFriction  = 1;
    public float   dynamicFriction = 1;
    public float   restitution     = 1;
    public boolean ghost           = false;
    public int     bitmask         = 0; // TODO

    private float   area            = 0;
    private boolean calcArea        = false;
    private boolean calcRadius      = false;
    private float   boundingRadius  = 0; // the bounding radius r
    private float   boundingRadius2 = 0; // r squared

    protected boolean updated     = false;
    protected Vector2 localCenter = null;
    protected Vector2 worldCenter = new Vector2();

    BodyCollider() {}

    BodyCollider(float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask) {
        this.density = density;
        this.staticFriction = staticFriction;
        this.dynamicFriction = dynamicFriction;
        this.restitution = restitution;
        this.ghost = ghost;
        this.bitmask = bitmask;
    }

    public final boolean contains(final Vector2 point) {
        return contains(point.x, point.y);
    }

    public final boolean contains(float x, float y) {
        if (!updated) update();
        return containsPoint(x, y);
    }

    public final Vector2 worldCenter() {
        if (localCenter == null) {
            localCenter = calculateLocalCenter();
        }
        return worldCenter.set(localCenter).rotateRad(body.angleRad).add(body.x,body.y); // scale -> rotate -> translate ("scale" by 1)
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
            boundingRadius = calculateBoundingRadius();
            boundingRadius2 = boundingRadius * boundingRadius;
            calcRadius = true;
        }
        return boundingRadius;
    }

    public final float boundingRadiusSquared() {
        if (!calcRadius) {
            boundingRadius = calculateBoundingRadius();
            boundingRadius2 = boundingRadius * boundingRadius;
            calcRadius = true;
        }
        return boundingRadius2;
    }

    public final void update() {
        if (updated) return;
        updateWorldCoordinates();
        updated = true;
    }

    public final float getMinExtentX() {
        return body.x - boundingRadius;
    }
    public final float getMaxExtentX() {
        return body.x + boundingRadius;
    }
    public final float getMinExtentY() {
        return body.y - boundingRadius;
    }
    public final float getMaxExtentY() {
        return body.y + boundingRadius;
    }

    protected abstract boolean        containsPoint(float x, float y);
    protected abstract void           updateWorldCoordinates();
    protected abstract float          calculateBoundingRadius();
    protected abstract float          calculateArea();
    protected abstract Vector2        calculateLocalCenter();

}
