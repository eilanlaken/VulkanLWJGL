package org.example.engine.core.math;

public class Shape2DRectangle implements Shape2D {

    public Vector2 center;
    public float width;
    public float height;
    public float angle;
    private boolean updated;

    // corners:
    /**
     *  c1 --------- c4
     *  |             |
     *  |             |
     *  |             |
     *  c2 --------- c3
     */
    private Vector2 c1;
    private Vector2 c2;
    private Vector2 c3;
    private Vector2 c4;

    private Vector2 tmp1 = new Vector2();
    private Vector2 tmp2 = new Vector2();

    public Shape2DRectangle(float centerX, float centerY, float width, float height, float angle) {
        this.center = new Vector2(centerX, centerY);
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.c1 = new Vector2();
        this.c2 = new Vector2();
        this.c3 = new Vector2();
        this.c4 = new Vector2();
        this.updated = false;
    }

    public Shape2DRectangle(float width, float height) {
        this.center = new Vector2();
        this.width = width;
        this.height = height;
        this.angle = 0;
        this.updated = false;
    }

    @Override
    public boolean contains(float x, float y) {
        float widthHalf = width * 0.5f;
        float heightHalf = height * 0.5f;
        if (angle == 0) {
            if (x > center.x + widthHalf || x < center.x - widthHalf) return false;
            if (y > center.y + heightHalf || y < center.y - heightHalf) return false;
            return true;
        }
        if (!updated) updateCorners();
        // https://math.stackexchange.com/questions/190111/how-to-check-if-a-point-is-inside-a-rectangle
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
        return width * height;
    }

    @Override
    public float getPerimeter() {
        return 2 * (width + height);
    }

    @Override
    public void translate(float dx, float dy) {
        center.add(dx, dy);
        this.updated = false;
    }

    @Override
    public void rotate(float degrees) {
        angle += degrees;
        angle %= 360;
        this.updated = false;
    }

    @Override
    public void scale(float scaleX, float scaleY) {
        this.width *= scaleX;
        this.height *= scaleY;
        this.updated = false;
    }

    public void updateCorners() {
        float sin = MathUtils.sinDeg(angle);
        float cos = MathUtils.cosDeg(angle);

        float widthHalf = width * 0.5f;
        float heightHalf = height * 0.5f;

        float x1 = -widthHalf * cos - heightHalf * sin;
        float y1 = -widthHalf * sin + heightHalf * cos;

        float x2 = -widthHalf * cos + heightHalf * sin;
        float y2 = -widthHalf * sin - heightHalf * cos;

        float x3 = widthHalf * cos + heightHalf * sin;
        float y3 = widthHalf * sin - heightHalf * cos;

        float x4 = widthHalf * cos - heightHalf * sin;
        float y4 = widthHalf * sin + heightHalf * cos;

        this.c1.set(center.x, center.y).add(x1, y1);
        this.c2.set(center.x, center.y).add(x2, y2);
        this.c3.set(center.x, center.y).add(x3, y3);
        this.c4.set(center.x, center.y).add(x4, y4);
        this.updated = true;
    }

    public Vector2 getTopLeftCorner() {
        if (!updated) updateCorners();
        return c1;
    }

    public Vector2 getBottomLeftCorner() {
        if (!updated) updateCorners();
        return c2;
    }

    public Vector2 getBottomRightCorner() {
        if (!updated) updateCorners();
        return c3;
    }

    public Vector2 getTopRightCorner() {
        if (!updated) updateCorners();
        return c4;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + "| c1: " + c1 + ", c2: " + c2 + ", c3: " + c3 + ", c4: " + c4 + ">";
    }

}
