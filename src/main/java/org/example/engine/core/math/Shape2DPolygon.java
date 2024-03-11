package org.example.engine.core.math;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayShort;

// TODO: test
// https://stackoverflow.com/questions/5247994/simple-2d-polygon-triangulation
// libgdx: EarClippingTriangulator
public class Shape2DPolygon implements Shape2D {

    private boolean isUpdated;
    public Array<Vector2> localPoints;
    public Array<Vector2> worldPoints;
    public ArrayShort indices; // indices created in the triangulation step.

    private float x, y;
    private float angle;
    private float scaleX, scaleY;

    public Shape2DPolygon(Vector2 ... localPoints) {
        if (localPoints.length < 3) throw new IllegalArgumentException("At least 3 points are needed to construct a polygon. Given: " + localPoints.length);
        this.x = 0;
        this.y = 0;
        this.angle = 0;
        this.scaleX = 1;
        this.scaleY = 1;
        this.localPoints = new Array<>(localPoints.length);
        for (Vector2 point : localPoints) {
            this.localPoints.add(point);
        }
        this.worldPoints = new Array<>(this.localPoints.size);
        isUpdated = true;
        triangulate();
    }

    private void triangulate() {

    }

    public void updateWorldPoints() {
        if (isUpdated) return;
        final float cos = MathUtils.cos(angle);
        final float sin = MathUtils.cos(angle);
        for (int i = 0; i < localPoints.size; i++) {
            Vector2 localPoint = localPoints.get(i);
            float worldX = localPoint.x;
            float worldY = localPoint.y;

            if (scaleX != 1) worldX *= scaleX;
            if (scaleY != 1) worldY *= scaleY;

            if (angle != 0) {
                float oldX = worldX;
                worldX = cos * worldX - sin * worldY;
                worldY = sin * oldX + cos * worldY;
            }

            worldPoints.items[i].x = worldX + x;
            worldPoints.items[i].y = worldY + y;
        }
        isUpdated = true;
    }

    @Override
    // TODO: this is wrong - does not take concave polys into account
    public boolean contains(float x, float y) {
        return false;
    }

    @Override
    // TODO: this is wrong - does not take concave polys into account
    public float getArea() {
        return 0;
    }

    @Override
    public float getPerimeter() {
        float perimeter = 0;
        for (int i = 0; i < worldPoints.size - 1; i++) {
            perimeter += Vector2.dist(worldPoints.items[i], worldPoints.items[i+1]);
        }
        return perimeter;
    }

    @Override
    public void translate(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        isUpdated = false;
    }

    @Override
    public void rotate(float degrees) {
        this.angle += degrees;
        isUpdated = false;
    }

    @Override
    public void scale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        isUpdated = false;
    }
}
