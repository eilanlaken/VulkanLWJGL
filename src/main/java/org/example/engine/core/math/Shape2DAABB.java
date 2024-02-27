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
        return (max.x - min.x) * (max.y - min.y);
    }

    @Override
    public float getPerimeter() {
        return 2 * (max.y - min.y + max.x - min.x);
    }

}
