package org.example.engine.core.math;

public class Shape2DRectangle extends Shape2D {

    // original corners
    private final Vector2 c1;
    private final Vector2 c2;
    private final Vector2 c3;
    private final Vector2 c4;
    private float area;

    // world corners:
    /**
     *  c1 ---------------c4
     *  |                 |
     *  |                 |
     *  |                 |
     *  c2 --------------c3
     */
    private final Vector2 c1World = new Vector2();
    private final Vector2 c2World = new Vector2();
    private final Vector2 c3World = new Vector2();
    private final Vector2 c4World = new Vector2();

    private final Vector2 tmp1 = new Vector2();
    private final Vector2 tmp2 = new Vector2();

    public Shape2DRectangle(float centerX, float centerY, float width, float height, float rotate) {
        final float widthHalf = width * 0.5f;
        final float heightHalf = height * 0.5f;
        this.c1 = new Vector2(-widthHalf, +heightHalf).rotateDeg(rotate).add(centerX, centerY);
        this.c2 = new Vector2(-widthHalf, -heightHalf).rotateDeg(rotate).add(centerX, centerY);
        this.c3 = new Vector2(+widthHalf, -heightHalf).rotateDeg(rotate).add(centerX, centerY);
        this.c4 = new Vector2(+widthHalf, +heightHalf).rotateDeg(rotate).add(centerX, centerY);
        this.area = width * height;
    }

    public Shape2DRectangle(float width, float height) {
        final float widthHalf = width * 0.5f;
        final float heightHalf = height * 0.5f;
        this.c1 = new Vector2(-widthHalf, +heightHalf);
        this.c2 = new Vector2(-widthHalf, -heightHalf);
        this.c3 = new Vector2(+widthHalf, -heightHalf);
        this.c4 = new Vector2(+widthHalf, +heightHalf);
        this.area = width * height;
    }

    @Override
    protected float calculateOriginalBoundingRadius() {
        return (float) Math.sqrt(MathUtils.max(c1.len2(), c2.len2(), c3.len2(), c4.len2()));
    }

    @Override
    public boolean contains(float x, float y) {
        if (!updated) update();

        tmp1.set(c4World).sub(c1World);
        tmp2.set(x,y).sub(c1World);
        float projection1 = tmp1.dot(tmp2);
        if (projection1 < 0 || projection1 > tmp1.dot(tmp1)) return false;

        tmp1.set(c2World).sub(c1World);
        tmp2.set(x,y).sub(c1World);
        float projection2 = tmp1.dot(tmp2);
        if (projection2 < 0 || projection2 > tmp1.dot(tmp1)) return false;

        return true;
    }

    @Override
    protected float calculateOriginalArea() {
        return area;
    }

    @Override
    protected void updateWorldCoordinates() {
        c1World.set(c1);
        c2World.set(c2);
        c3World.set(c3);
        c4World.set(c4);
        // scale
        if (!MathUtils.isEqual(scaleX,1.0f) || !MathUtils.isEqual(scaleY,1.0f)) {
            c1World.scl(scaleX, scaleY);
            c2World.scl(scaleX, scaleY);
            c3World.scl(scaleX, scaleY);
            c4World.scl(scaleX, scaleY);
        }
        // rotate
        if (!MathUtils.isZero(angle)) {
            c1World.rotateDeg(angle);
            c2World.rotateDeg(angle);
            c3World.rotateDeg(angle);
            c4World.rotateDeg(angle);
        }
        // translate
        c1World.add(x, y);
        c2World.add(x, y);
        c3World.add(x, y);
        c4World.add(x, y);
    }

    @Override
    protected void bakeCurrentTransformToLocalCoordinates() {

    }

    public Vector2 c1() {
        if (!updated) update();
        return c1World;
    }

    public Vector2 c2() {
        if (!updated) update();
        return c2World;
    }

    public Vector2 c3() {
        if (!updated) update();
        return c3World;
    }

    public Vector2 c4() {
        if (!updated) update();
        return c4World;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + "| c1: " + c1World + ", c2: " + c2World + ", c3: " + c3World + ", c4: " + c4World + ">";
    }

}