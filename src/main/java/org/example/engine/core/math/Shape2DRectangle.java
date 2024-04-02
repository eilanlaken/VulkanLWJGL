package org.example.engine.core.math;

public class Shape2DRectangle extends Shape2D {

    public Vector2 localCenter;
    public final float originalWidth;
    private final float originalWidthHalf;
    public final float originalHeight;
    private final float originalHeightHalf;

    // world corners:
    /**
     *  c1 ---------------c4
     *  |                 |
     *  |                 |
     *  |                 |
     *  c2 --------------c3
     */
    private Vector2 c1;
    private Vector2 c2;
    private Vector2 c3;
    private Vector2 c4;

    private final Vector2 tmp1 = new Vector2();
    private final Vector2 tmp2 = new Vector2();

    public Shape2DRectangle(float centerX, float centerY, float width, float height) {
        this.localCenter = new Vector2(centerX, centerY);
        this.originalWidth = width;
        this.originalHeight = height;
        this.originalWidthHalf = width * 0.5f;
        this.originalHeightHalf = height * 0.5f;
        this.c1 = new Vector2(centerX - originalWidthHalf, centerY + originalHeightHalf);
        this.c2 = new Vector2(centerX - originalWidthHalf, centerY - originalHeightHalf);
        this.c3 = new Vector2(centerX + originalWidthHalf, centerY - originalHeightHalf);
        this.c4 = new Vector2(centerX + originalWidthHalf, centerY + originalHeightHalf);
        this.originalBoundingRadius = (float) Math.sqrt(originalWidthHalf * originalWidthHalf + originalHeightHalf * originalHeightHalf);
    }

    public Shape2DRectangle(float width, float height) {
        this(0,0, width, height);
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
    public float getArea() {
        return originalWidth * scaleX * originalHeight * scaleY;
    }

    @Override
    public float getPerimeter() {
        return 2 * (originalWidth * scaleX + originalHeight * scaleY);
    }

    @Override
    public void update() {
        if (updated) return;
        // scale
        c1.set(-originalWidthHalf, originalHeightHalf).scl(scaleX, scaleY).add(localCenter);
        c2.set(-originalWidthHalf, -originalHeightHalf).scl(scaleX, scaleY).add(localCenter);
        c3.set(originalWidthHalf, -originalHeightHalf).scl(scaleX, scaleY).add(localCenter);
        c4.set(originalWidthHalf, originalHeightHalf).scl(scaleX, scaleY).add(localCenter);
        // rotate
        if (angle != 0.0f) {
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
        this.updated = true;
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
