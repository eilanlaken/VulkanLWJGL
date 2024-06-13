package org.example.engine.core.physics2d_new;

import org.example.engine.core.math.Vector2;
import org.example.engine.core.physics2d_new.Body;

public abstract class BodyCollider {

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
    private boolean calcRadius      = false;
    private Vector2 localCenter     = null;

    protected Vector2 offset = new Vector2();
    protected float   offsetAngleRad;
    protected Vector2 worldCenter = new Vector2();

    BodyCollider(Data data) {
        this(data.density, data.staticFriction, data.dynamicFriction, data.restitution, data.ghost, data.bitmask, 0, 0, 0);
    }

    BodyCollider(float offsetX, float offsetY, float offsetAngleRad, Data data) {
        this(data.density, data.staticFriction, data.dynamicFriction, data.restitution, data.ghost, data.bitmask, offsetX, offsetY, offsetAngleRad);
    }

    BodyCollider(float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask) {
        this(density, staticFriction, dynamicFriction, restitution, ghost, bitmask, 0,0,0);
    }

    BodyCollider(float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask, float offsetX, float offsetY, float offsetAngleRad) {
        this.density = density;
        this.staticFriction = staticFriction;
        this.dynamicFriction = dynamicFriction;
        this.restitution = restitution;
        this.ghost = ghost;
        this.bitmask = bitmask;
        this.offset.set(offsetX, offsetY);
        this.offsetAngleRad = offsetAngleRad;
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
        if (body == null) return worldCenter.set(offset.x, offset.y);
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
            calcRadius = true;
        }
        return boundingRadius;
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

    public static class Data {

        public float   density         = 1;
        public float   staticFriction  = 1;
        public float   dynamicFriction = 1;
        public float   restitution     = 1;
        public boolean ghost           = false;
        public int     bitmask         = 0; // TODO

    }

}
