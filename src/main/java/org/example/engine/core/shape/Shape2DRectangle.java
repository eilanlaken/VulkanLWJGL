package org.example.engine.core.shape;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;

public class Shape2DRectangle extends Shape2D {

    public final float unscaledWidth;
    public final float unscaledHeight;

    // original corners
    private final MathVector2 c0Local;
    private final MathVector2 c1Local;
    private final MathVector2 c2Local;
    private final MathVector2 c3Local;

    // world corners:
    /**
     *  c0 ---------------c3
     *  |                 |
     *  |                 |
     *  |                 |
     *  c1 --------------c2
     */
    private final MathVector2 c0 = new MathVector2();
    private final MathVector2 c1 = new MathVector2();
    private final MathVector2 c2 = new MathVector2();
    private final MathVector2 c3 = new MathVector2();
    private final CollectionsArray<MathVector2> worldVertices;

    public Shape2DRectangle(float centerX, float centerY, float width, float height, float rotate) {
        this.unscaledWidth = width;
        this.unscaledHeight = height;
        final float widthHalf = width * 0.5f;
        final float heightHalf = height * 0.5f;
        this.c0Local = new MathVector2(-widthHalf, +heightHalf).rotateDeg(rotate).add(centerX, centerY);
        this.c1Local = new MathVector2(-widthHalf, -heightHalf).rotateDeg(rotate).add(centerX, centerY);
        this.c2Local = new MathVector2(+widthHalf, -heightHalf).rotateDeg(rotate).add(centerX, centerY);
        this.c3Local = new MathVector2(+widthHalf, +heightHalf).rotateDeg(rotate).add(centerX, centerY);
        this.worldVertices = new CollectionsArray<>(true, 4);
        this.worldVertices.addAll(c0, c1, c2, c3);
    }

    public Shape2DRectangle(float width, float height) {
        this.unscaledWidth = width;
        this.unscaledHeight = height;
        final float widthHalf = width * 0.5f;
        final float heightHalf = height * 0.5f;
        this.c0Local = new MathVector2(-widthHalf, +heightHalf);
        this.c1Local = new MathVector2(-widthHalf, -heightHalf);
        this.c2Local = new MathVector2(+widthHalf, -heightHalf);
        this.c3Local = new MathVector2(+widthHalf, +heightHalf);
        this.worldVertices = new CollectionsArray<>(true, 4);
        this.worldVertices.addAll(c0, c1, c2, c3);
    }

    public Shape2DRectangle(float width, float height, float angle) {
        this(0,0, width, height, angle);
    }

    @Override
    protected MathVector2 calculateLocalGeometryCenter() {
        MathVector2 center = new MathVector2();
        center.add(c0Local);
        center.add(c1Local);
        center.add(c2Local);
        center.add(c3Local);
        center.scl(0.25f);
        return center;
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        MathVector2 tmp1 = new MathVector2();
        MathVector2 tmp2 = new MathVector2();

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

    // TODO: test
    public float getWidth() {
        return unscaledWidth * Math.abs(scaleX);
    }

    // TODO: test
    public float getHeight() {
        return unscaledHeight * Math.abs(scaleY);
    }

    @Override
    protected float calculateUnscaledBoundingRadius() {
        return (float) Math.sqrt(MathUtils.max(c0Local.len2(), c1Local.len2(), c2Local.len2(), c3Local.len2()));
    }

    @Override
    protected float calculateUnscaledArea() {
        return unscaledWidth * unscaledHeight;
    }

    @Override
    protected void updateWorldCoordinates() {
        c0.set(c0Local);
        c1.set(c1Local);
        c2.set(c2Local);
        c3.set(c3Local);
        // scale
        if (!MathUtils.floatsEqual(scaleX,1.0f) || !MathUtils.floatsEqual(scaleY,1.0f)) {
            c0.scl(scaleX, scaleY);
            c1.scl(scaleX, scaleY);
            c2.scl(scaleX, scaleY);
            c3.scl(scaleX, scaleY);
        }
        // rotate
        if (!MathUtils.isZero(angle)) {
            c0.rotateDeg(angle);
            c1.rotateDeg(angle);
            c2.rotateDeg(angle);
            c3.rotateDeg(angle);
        }
        // translate
        c0.add(x, y);
        c1.add(x, y);
        c2.add(x, y);
        c3.add(x, y);
    }

    public MathVector2 c0() {
        if (!updated) update();
        return c0;
    }

    public MathVector2 c1() {
        if (!updated) update();
        return c1;
    }

    public MathVector2 c2() {
        if (!updated) update();
        return c2;
    }

    public MathVector2 c3() {
        if (!updated) update();
        return c3;
    }

    public boolean isAxisAligned() {
        if (!updated) update();
        if (MathUtils.floatsEqual(c0.x, c1.x) && MathUtils.floatsEqual(c1.y, c2.y)) return true;
        if (MathUtils.floatsEqual(c0.y, c1.y) && MathUtils.floatsEqual(c1.x, c2.x)) return true;
        return false;
    }

    @Override
    protected CollectionsArray<MathVector2> getWorldVertices() {
        return worldVertices;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + "| c0: " + c0 + ", c1: " + c1 + ", c2: " + c2 + ", c3: " + c3 + ">";
    }

}