package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.shape.ShapeException;

public final class BodyColliderCircle extends BodyCollider {

    public final Vector2 localCenter;
    public final Vector2 worldCenter;
    public final float   r;
    public final float   r2;

    public BodyColliderCircle(Body body, float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask,
                              float r, float x, float y) {
        super(body, density, staticFriction, dynamicFriction, restitution, ghost, bitmask);
        if (r <= 0) throw new Physics2DException("Radius of circle collider must be positive. Got: " + r);
        this.localCenter = new Vector2(x, y);
        this.worldCenter = new Vector2(localCenter);
        this.r  = r;
        this.r2 = r * r;
    }

    @Override
    protected void updateWorldCoordinates() {
        worldCenter.set(localCenter);
        if (!MathUtils.isZero(body.angleRad)) worldCenter.rotateRad(body.angleRad);
        worldCenter.add(body.x, body.y);
    }

    @Override
    protected Vector2 calculateLocalCenter() {
        return localCenter;
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        return (x - worldCenter.x) * (x - worldCenter.x) + (y - worldCenter.y) * (y - worldCenter.y) <= r2 + MathUtils.FLOAT_ROUNDING_ERROR;
    }

    @Override
    protected float calculateBoundingRadius() {
        return localCenter.len() + r;
    }

    @Override
    protected float calculateArea() {
        return MathUtils.PI * r2;
    }

    public Vector2 getLocalCenter() {
        return localCenter;
    }

    public Vector2 getWorldCenter() {
        if (!updated) update();
        return worldCenter;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() +
                "| worldCenter = " + worldCenter +
                ", radius = " + r +
                '}';
    }
}