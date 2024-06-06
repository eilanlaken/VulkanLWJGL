package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;

/**
Represent a rectangular body collider:

       c0 ---------------c3
       |                 |
       |                 |
       |                 |
       c1 --------------c2

 **/
public final class BodyColliderRectangle extends BodyCollider {

    public final float width;
    public final float height;

    // original corners
    private final Vector2 c0Local;
    private final Vector2 c1Local;
    private final Vector2 c2Local;
    private final Vector2 c3Local;

    // world corners:
    private final Vector2 c0 = new Vector2();
    private final Vector2 c1 = new Vector2();
    private final Vector2 c2 = new Vector2();
    private final Vector2 c3 = new Vector2();

    public BodyColliderRectangle(Body body, float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask,
                                 float centerX, float centerY, float width, float height, float rotateRad) {
        super(body, density, staticFriction, dynamicFriction, restitution, ghost, bitmask);
        this.width = width;
        this.height = height;
        final float widthHalf = width * 0.5f;
        final float heightHalf = height * 0.5f;
        this.c0Local = new Vector2(-widthHalf, +heightHalf).rotateRad(rotateRad).add(centerX, centerY);
        this.c1Local = new Vector2(-widthHalf, -heightHalf).rotateRad(rotateRad).add(centerX, centerY);
        this.c2Local = new Vector2(+widthHalf, -heightHalf).rotateRad(rotateRad).add(centerX, centerY);
        this.c3Local = new Vector2(+widthHalf, +heightHalf).rotateRad(rotateRad).add(centerX, centerY);
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        Vector2 tmp1 = new Vector2();
        Vector2 tmp2 = new Vector2();

        tmp1.set(c3).sub(c0);
        tmp2.set(x,y).sub(c0);
        float projection1 = tmp1.dot(tmp2);
        if (projection1 < 0 || projection1 > tmp1.dot(tmp1)) return false;

        tmp1.set(c1).sub(c0);
        tmp2.set(x,y).sub(c0);
        float projection2 = tmp1.dot(tmp2);
        if (projection2 < 0 || projection2 > tmp1.dot(tmp1)) return false;

        return true;
    }

    @Override
    protected float calculateBoundingRadius() {
        return (float) Math.sqrt(MathUtils.max(c0Local.len2(), c1Local.len2(), c2Local.len2(), c3Local.len2()));
    }

    @Override
    protected float calculateArea() {
        return width * height;
    }

    @Override
    protected Vector2 calculateLocalCenter() {
        Vector2 center = new Vector2();
        center.add(c0Local);
        center.add(c1Local);
        center.add(c2Local);
        center.add(c3Local);
        center.scl(0.25f);
        return center;
    }

    @Override
    protected void updateWorldCoordinates() {
        c0.set(c0Local);
        c1.set(c1Local);
        c2.set(c2Local);
        c3.set(c3Local);

        // rotate
        if (!MathUtils.isZero(body.angleRad)) {
            c0.rotateRad(body.angleRad);
            c1.rotateRad(body.angleRad);
            c2.rotateRad(body.angleRad);
            c3.rotateRad(body.angleRad);
        }

        // translate
        c0.add(body.x, body.y);
        c1.add(body.x, body.y);
        c2.add(body.x, body.y);
        c3.add(body.x, body.y);
    }

    public Vector2 c0() {
        if (!updated) update();
        return c0;
    }

    public Vector2 c1() {
        if (!updated) update();
        return c1;
    }

    public Vector2 c2() {
        if (!updated) update();
        return c2;
    }

    public Vector2 c3() {
        if (!updated) update();
        return c3;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + "| c0: " + c0 + ", c1: " + c1 + ", c2: " + c2 + ", c3: " + c3 + ">";
    }

}