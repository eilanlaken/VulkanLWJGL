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
    private float   boundingRadius  = 0;
    private float   boundingRadius2 = 0; // r squared
    private boolean calcRadius      = false;
    private Vector2 localCenter     = null;

    protected Vector2 offset = new Vector2();
    protected float   offsetAngleRad;
    protected Vector2 worldCenter = new Vector2();

    BodyCollider() {}

    BodyCollider(float offsetX, float offsetY, float offsetAngleRad, float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask) {
        this.offset.set(offsetX, offsetY);
        this.offsetAngleRad = offsetAngleRad;
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
        return containsPoint(x, y);
    }

    public final Vector2 offset() {
        return offset;
    }

    public final Vector2 localCenter() {
        if (localCenter == null) {
            localCenter = calculateLocalCenter();
        }
        return localCenter;
    }

    public final Vector2 worldCenter() {
        if (body == null) return offset;
        else return worldCenter.set(offset.x + body.x, offset.y + body.y).rotateAroundRad(body.cmX, body.cmY, body.aRad); // "scale" (by 1) -> rotate -> translate
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

    abstract boolean containsPoint(float x, float y);
    abstract void    update();
    abstract float   calculateBoundingRadius();
    abstract float   calculateArea();
    abstract Vector2 calculateLocalCenter();

}
