package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Vector2;

public abstract class BodyCollider {

    public final Body body;

    public float   density;
    public float   staticFriction;
    public float   dynamicFriction;
    public float   restitution;
    public boolean ghost;
    public int     bitmask;

    protected float   area        = 0;
    protected boolean calcArea    = false;
    protected boolean calcRadius  = false;
    protected float   boundingRadius = 0; // the bounding radius r
    protected float   boundingRadiusSquared = 0; // r squared
    protected boolean updated     = false;
    protected Vector2 localCenter = null;
    protected Vector2 worldCenter = new Vector2();

    BodyCollider(Body body, float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask) {
        this.body = body;
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
            boundingRadiusSquared = boundingRadius * boundingRadius;
            calcRadius = true;
        }
        return boundingRadius;
    }

    public final float boundingRadiusSquared() {
        if (!calcRadius) {
            boundingRadius = calculateBoundingRadius();
            boundingRadiusSquared = boundingRadius * boundingRadius;
            calcRadius = true;
        }
        return boundingRadiusSquared;
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
