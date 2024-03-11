package org.example.engine.core.math;

// TODO: finish implementing
public class Shape2DCircle implements Shape2D {

    public Vector2 center;
    public float radius;

    public Shape2DCircle(float x, float y, float r) {
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

    }

    @Override
    public void rotate(float degrees) {

    }

    @Override
    public void scale(float scaleX, float scaleY) {

    }
}
