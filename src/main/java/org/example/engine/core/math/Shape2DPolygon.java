package org.example.engine.core.math;

import org.example.engine.core.collections.Array;

// TODO: test
public class Shape2DPolygon implements Shape2D {

    public Array<Vector2> points;

    public Shape2DPolygon(Vector2 ...points) {
        if (points.length < 3) throw new IllegalArgumentException("At least 3 points are needed to construct a polygon. Given: " + points.length);
        this.points = new Array<>();
        for (Vector2 point : points) {
            this.points.add(point);
        }
    }

    @Override
    public boolean contains(float x, float y) {
        boolean inside = false;
        for (int i = 0, j = points.items.length - 1; i < points.size; j = i++) {
            if ((points.items[i].y > y) != (points.items[j].y > y) &&
                    x < (points.items[j].x - points.items[i].x) * (y - points.items[i].y) / (points.items[j].y - points.items[i].y) + points.items[i].x) {
                inside = !inside;
            }
        }
        return inside;
    }

    @Override
    public float getArea() {
        float area = 0;
        for (int i = 0; i < points.size - 1; i++) {
            area += points.items[i].x * points.items[i+1].y - points.items[i].y * points.items[i+1].x;
        }
        return area;
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
