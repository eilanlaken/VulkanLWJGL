package org.example.engine.core.math;

public class Shape2DRectangle extends Shape2D {

    private final float unscaledArea;
    private final float unscaledBoundingRadius;

    // original corners
    private final Vector2 c1Local;
    private final Vector2 c2Local;
    private final Vector2 c3Local;
    private final Vector2 c4Local;

    // world corners:
    /**
     *  c1 ---------------c4
     *  |                 |
     *  |                 |
     *  |                 |
     *  c2 --------------c3
     */
    private final Vector2 c1 = new Vector2();
    private final Vector2 c2 = new Vector2();
    private final Vector2 c3 = new Vector2();
    private final Vector2 c4 = new Vector2();

    private final Vector2 tmp1 = new Vector2();
    private final Vector2 tmp2 = new Vector2();

    public Shape2DRectangle(float centerX, float centerY, float width, float height, float rotate) {
        final float widthHalf = width * 0.5f;
        final float heightHalf = height * 0.5f;
        this.c1Local = new Vector2(-widthHalf, +heightHalf).rotateDeg(rotate).add(centerX, centerY);
        this.c2Local = new Vector2(-widthHalf, -heightHalf).rotateDeg(rotate).add(centerX, centerY);
        this.c3Local = new Vector2(+widthHalf, -heightHalf).rotateDeg(rotate).add(centerX, centerY);
        this.c4Local = new Vector2(+widthHalf, +heightHalf).rotateDeg(rotate).add(centerX, centerY);
        this.unscaledArea = width * height;
        this.unscaledBoundingRadius = (float) Math.sqrt(MathUtils.max(c1Local.len2(), c2Local.len2(), c3Local.len2(), c4Local.len2()));
    }

    public Shape2DRectangle(float width, float height) {
        final float widthHalf = width * 0.5f;
        final float heightHalf = height * 0.5f;
        this.c1Local = new Vector2(-widthHalf, +heightHalf);
        this.c2Local = new Vector2(-widthHalf, -heightHalf);
        this.c3Local = new Vector2(+widthHalf, -heightHalf);
        this.c4Local = new Vector2(+widthHalf, +heightHalf);
        this.unscaledArea = width * height;
        this.unscaledBoundingRadius = (float) Math.sqrt(MathUtils.max(c1Local.len2(), c2Local.len2(), c3Local.len2(), c4Local.len2()));
    }

    @Override
    public boolean contains(float x, float y) {
        if (!updated) update();

        tmp1.set(c4).sub(c1);
        tmp2.set(x,y).sub(c1);
        float projection1 = tmp1.dot(tmp2);
        if (projection1 < 0 || projection1 > tmp1.dot(tmp1)) return false;

        tmp1.set(c2).sub(c1);
        tmp2.set(x,y).sub(c1);
        float projection2 = tmp1.dot(tmp2);
        if (projection2 < 0 || projection2 > tmp1.dot(tmp1)) return false;

        return true;
    }

    @Override
    protected float getUnscaledBoundingRadius() {
        return unscaledBoundingRadius;
    }

    @Override
    protected float getUnscaledArea() {
        return unscaledArea;
    }

    @Override
    protected void updateWorldCoordinates() {
        c1.set(c1Local);
        c2.set(c2Local);
        c3.set(c3Local);
        c4.set(c4Local);
        // scale
        if (!MathUtils.isEqual(scaleX,1.0f) || !MathUtils.isEqual(scaleY,1.0f)) {
            c1.scl(scaleX, scaleY);
            c2.scl(scaleX, scaleY);
            c3.scl(scaleX, scaleY);
            c4.scl(scaleX, scaleY);
        }
        // rotate
        if (!MathUtils.isZero(angle)) {
            c1.rotateDeg(angle);
            c2.rotateDeg(angle);
            c3.rotateDeg(angle);
            c4.rotateDeg(angle);
        }
        // translate
        c1.add(x, y);
        c2.add(x, y);
        c3.add(x, y);
        c4.add(x, y);
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

    public Vector2 c4() {
        if (!updated) update();
        return c4;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + "| c1: " + c1 + ", c2: " + c2 + ", c3: " + c3 + ", c4: " + c4 + ">";
    }

}