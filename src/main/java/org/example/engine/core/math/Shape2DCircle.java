package org.example.engine.core.math;

public class Shape2DCircle implements Shape2D {

    public Vector2 center;
    public float radius;

    public Shape2DCircle(float x, float y, float r) {
        if (r < 0) throw new IllegalArgumentException("Radius must be positive. Got: " + r);
        this.center = new Vector2(x, y);
        this.radius = r;
    }

    @Override
    public boolean contains(float x, float y) {
        return (x - center.x) * (x - center.x) + (y - center.y) * (y - center.y) <= radius;
    }

    @Override
    public float getArea() {
        return (float) (Math.PI * radius * radius);
    }

    @Override
    public float getPerimeter() {
        return (float) (2.0 * Math.PI * radius);
    }

    @Override
    public void translate(float dx, float dy) {
        center.add(dx, dy);
    }

    @Override
    public void rotate(float degrees) {
        // ignore.
    }

    @Override
    public void scale(float scaleX, float scaleY) {
        if (scaleX != scaleY) throw new IllegalArgumentException("Must have scaleX == scaleY to maintain circle proportions. Got: scaleX: " + scaleX + " and scaleY: " + scaleX + ".");
        radius *= scaleX;
    }
}
