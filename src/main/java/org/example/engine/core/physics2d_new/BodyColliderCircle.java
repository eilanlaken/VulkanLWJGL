package org.example.engine.core.physics2d_new;

import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.physics2d_new.BodyCollider;
import org.example.engine.core.physics2d_new.Physics2DException;

public final class BodyColliderCircle extends BodyCollider {

    //public final Vector2 center;
    public final Vector2 worldCenter;
    public float angleRad;
    public final float   r;
    public final float   r2;

    public BodyColliderCircle(Data data, float r, float offsetX, float offsetY, float offsetAngleRad) {
        this(data.density, data.staticFriction, data.dynamicFriction, data.restitution, data.ghost, data.bitmask, r, offsetX, offsetY, offsetAngleRad);
    }

    public BodyColliderCircle(Data data, float r) {
        this(data.density, data.staticFriction, data.dynamicFriction, data.restitution, data.ghost, data.bitmask, r, 0, 0, 0);
    }

    public BodyColliderCircle(float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask, float r) {
        this(density, staticFriction, dynamicFriction, restitution, ghost, bitmask, r, 0, 0, 0);
    }

    public BodyColliderCircle(float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask,
                              float r, float offsetX, float offsetY, float offsetAngleRad) {
        super(density, staticFriction, dynamicFriction, restitution, ghost, bitmask, offsetX, offsetY,0);
        if (r <= 0) throw new Physics2DException("Radius of circle collider must be positive. Got: " + r);
        this.worldCenter = new Vector2(offset());
        this.angleRad = offsetAngleRad;
        this.r  = r;
        this.r2 = r * r;
    }

    @Override
    protected void update() {
        worldCenter.set(offset);
        worldCenter.rotateAroundRad(body.lcmX, body.lcmY, body.aRad);
        worldCenter.add(body.x, body.y);
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        return (x - worldCenter.x) * (x - worldCenter.x) + (y - worldCenter.y) * (y - worldCenter.y) <= r2 + MathUtils.FLOAT_ROUNDING_ERROR;
    }

    @Override
    protected float calculateBoundingRadius() {
        return offset().len() + r;
    }

    @Override
    protected float calculateArea() {
        return MathUtils.PI * r * r;
    }

    @Override
    Vector2 calculateLocalCenter() {
        return offset;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() +
                "| worldCenter = " + worldCenter +
                ", radius = " + r +
                '}';
    }
}