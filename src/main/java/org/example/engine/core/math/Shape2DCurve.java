package org.example.engine.core.math;

import org.example.engine.core.collections.Array;

public class Shape2DCurve implements Shape2D {

    private Array<Vector2> points;
    public Array<Vector2> worldPoints;

    private float x, y;
    private float angle;
    private float scaleX, scaleY;

    private final Vector2 tmp1 = new Vector2();
    private final Vector2 tmp2 = new Vector2();

    private boolean updated;

    public Shape2DCurve(Vector2 ...points) {
        if (points.length < 2) throw new IllegalArgumentException("At least 2 points are needed to construct a curve. Given: " + points.length);
        this.points = new Array<>();
        this.worldPoints = new Array<>();
        for (Vector2 point : points) {
            this.points.add(new Vector2(point));
            this.worldPoints.add(new Vector2(point));
        }
        this.x = 0;
        this.y = 0;
        this.angle = 0;
        this.scaleX = 1;
        this.scaleY = 1;
        this.updated = true;
    }

    public void update() {
        for (int i = 0; i < worldPoints.size; i++) {
            // reset
            worldPoints.items[i].set(points.items[i]);
            // apply scale
            worldPoints.items[i].scl(scaleX, scaleY);
            // rotate
            worldPoints.items[i].rotateDeg(angle);
            // translate
            worldPoints.items[i].add(x, y);
        }
        updated = true;
    }

    @Override
    public boolean contains(float x, float y) {
        if (!updated) update();
        for (int i = 0; i < worldPoints.size - 1; i++) {
            if (x > Math.max(worldPoints.get(i).x, worldPoints.get(i+1).x) || x < Math.min(worldPoints.get(i).x, worldPoints.get(i+1).x)) continue;
            if (y > Math.max(worldPoints.get(i).y, worldPoints.get(i+1).y) || y < Math.min(worldPoints.get(i).y, worldPoints.get(i+1).y)) continue;
            tmp1.set(worldPoints.get(i+1)).sub(worldPoints.get(i));
            if (tmp2.set(x, y).isOnLine(tmp1)) return true;
        }
        return false;
    }

    @Override
    public float getArea() {
        return 0;
    }

    @Override
    public float getPerimeter() {
        if (!updated) update();
        float perimeter = 0;
        for (int i = 0; i < worldPoints.size - 1; i++) perimeter += Vector2.dst(worldPoints.items[i], worldPoints.items[i+1]);
        return perimeter;
    }

    @Override
    public void translate(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        updated = false;
    }

    @Override
    public void rotate(float degrees) {
        this.angle += degrees;
        updated = false;
    }

    @Override
    public void scale(float scaleX, float scaleY) {
        this.scaleX *= scaleX;
        this.scaleY *= scaleY;
        updated = false;
    }

}
