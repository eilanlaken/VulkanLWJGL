package org.example.engine.core.physics2d_new;

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
    public final float widthHalf;
    public final float height;
    public final float heightHalf;

    // world corners:
    public final Vector2 c0 = new Vector2();
    public final Vector2 c1 = new Vector2();
    public final Vector2 c2 = new Vector2();
    public final Vector2 c3 = new Vector2();

    public BodyColliderRectangle(float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask,
                                 float width, float height, float offsetX, float offsetY, float offsetAngleRad) {
        super(offsetX, offsetY, offsetAngleRad, density, staticFriction, dynamicFriction, restitution, ghost, bitmask);
        this.width = width;
        this.widthHalf = width * 0.5f;
        this.height = height;
        this.heightHalf = height * 0.5f;
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
        return (float) Math.sqrt(widthHalf * widthHalf + heightHalf * heightHalf);
    }

    @Override
    protected float calculateArea() {
        return width * height;
    }

    @Override
    protected void update() {
        c0.set(-widthHalf, +heightHalf).rotateRad(offsetAngleRad).add(offset);
        c1.set(-widthHalf, -heightHalf).rotateRad(offsetAngleRad).add(offset);
        c2.set(+widthHalf, -heightHalf).rotateRad(offsetAngleRad).add(offset);
        c3.set(+widthHalf, +heightHalf).rotateRad(offsetAngleRad).add(offset);

        c0.rotateAroundRad(body.lcmX, body.lcmY, body.aRad);
        c1.rotateAroundRad(body.lcmX, body.lcmY, body.aRad);
        c2.rotateAroundRad(body.lcmX, body.lcmY, body.aRad);
        c3.rotateAroundRad(body.lcmX, body.lcmY, body.aRad);

        // translate
        c0.add(body.x, body.y);
        c1.add(body.x, body.y);
        c2.add(body.x, body.y);
        c3.add(body.x, body.y);
    }

    @Override
    Vector2 calculateLocalCenter() {
        return offset;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + "| c0: " + c0 + ", c1: " + c1 + ", c2: " + c2 + ", c3: " + c3 + ">";
    }

}