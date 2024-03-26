package org.example.engine.core.math;

// AABB = axis aligned bonding box
public class Shape2DAABB implements Shape2D {

    public Vector2 min;
    public Vector2 max;

    public Shape2DAABB(float x1, float y1, float x2, float y2) {
        this.min = new Vector2(x1, y1);
        this.max = new Vector2(x2, y2);
    }

    @Override
    public boolean contains(float x, float y) {
        return x > min.x && x < max.x && y > min.y && y < max.y;
    }

    @Override
    public float getArea() {
        return Math.abs((max.x - min.x) * (max.y - min.y));
    }

    @Override
    public float getPerimeter() {
        return 2 * (Math.abs(max.y - min.y) + Math.abs(max.x - min.x));
    }

    @Override
    public void translate(float dx, float dy) {
        min.add(dx, dy);
        max.add(dx, dy);
    }

    @Override
    public void rotate(float degrees) {
        throw new UnsupportedOperationException("Cannot rotate an AABB: must remain aligned to axis.");
    }

    @Override
    public void scale(float scaleX, float scaleY) {
        float centerX = (min.x + max.x) * 0.5f;
        float centerY = (min.y + max.y) * 0.5f;
        this.min.sub(centerX, centerY);
        this.max.sub(centerX, centerY);
        this.min.scl(scaleX, scaleY);
        this.max.scl(scaleX, scaleY);
        this.min.add(centerX, centerY);
        this.max.add(centerX, centerY);
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + ": " + "min: " + min + ", max: " + max + ">";
    }

}
