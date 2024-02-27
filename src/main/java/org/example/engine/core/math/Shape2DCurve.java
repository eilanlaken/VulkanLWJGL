package org.example.engine.core.math;

import org.example.engine.core.collections.Array;

// TODO: test
public class Shape2DCurve implements Shape2D {

    public Array<Vector2> points;

    public Shape2DCurve(Vector2 ...points) {
        if (points.length < 2) throw new IllegalArgumentException("At least 3 points are needed to construct a curve. Given: " + points.length);
        this.points = new Array<>();
        for (Vector2 point : points) {
            this.points.add(point);
        }
    }

    @Override
    public boolean contains(float x, float y) {
        boolean contained = false;
        for (int i = 0; i < points.size - 1; i++) {
            final Vector2 a = points.items[i];
            final Vector2 b = points.items[i+1];
            contained |= ((b.x - a.x) * (y - a.y) == (x - a.x) * (b.y - a.y) && Math.abs(Float.compare(a.x, x) + Float.compare(b.x, x)) <= 1 && Math.abs(Float.compare(a.y, y) + Float.compare(b.y, y)) <= 1);
        }

        return contained;
    }

    @Override
    public float getArea() {
        return 0;
    }

    @Override
    public float getPerimeter() {
        float perimeter = 0;
        for (int i = 0; i < points.size - 1; i++) {
            perimeter += Vector2.dist(points.items[i], points.items[i+1]);
        }
        return perimeter;
    }
}
