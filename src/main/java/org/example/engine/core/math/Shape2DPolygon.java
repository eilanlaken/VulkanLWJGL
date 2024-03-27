package org.example.engine.core.math;

// TODO: test
// https://stackoverflow.com/questions/5247994/simple-2d-polygon-triangulation
// libgdx: EarClippingTriangulator
// must always be counter clockwise
public class Shape2DPolygon implements Shape2D {

    public final int vertexCount;
    public final float[] localPoints;
    private float[] worldPoints;
    public final short[] indices; // indices created in the triangulation step.

    private float x, y;
    private float angle;
    private float scaleX, scaleY;

    private boolean updated;

    public Shape2DPolygon(float[] points) {
        if (points.length < 6) throw new IllegalArgumentException("At least 3 points are needed to construct a polygon; Points array must contain at least 6 values: [x0,y0,x1,y1,x2,y2,...]. Given: " + points.length);
        if (points.length % 2 != 0) throw new IllegalArgumentException("Point array must be of even length in the format [x0,y0,x1,y1,...].");
        this.vertexCount = points.length / 2;
        this.x = 0;
        this.y = 0;
        this.angle = 0;
        this.scaleX = 1;
        this.scaleY = 1;
        this.localPoints = points;
        if (Algorithms.isPolygonClockwise(localPoints)) reverseVertices();
        this.indices = Algorithms.triangulatePolygon(localPoints);
        updated = false;
    }

    public void updateWorldPoints() {
        if (updated) return;
        if (worldPoints == null) worldPoints = new float[localPoints.length];
        final float cos = MathUtils.cos(angle);
        final float sin = MathUtils.cos(angle);
        for (int i = 0; i < localPoints.length; i += 2) {
            float localX = localPoints[i];
            float localY = localPoints[i+1];
            if (scaleX != 1) localX *= scaleX;
            if (scaleY != 1) localY *= scaleY;
            if (angle != 0) {
                float oldX = localX;
                localX = cos * localX - sin * localY;
                localY = sin * oldX + cos * localY;
            }
            worldPoints[i] = localX + x;
            worldPoints[i+1] = localY + y;
        }
        updated = true;
    }

    public float[] getWorldPoints() {
        updateWorldPoints();
        return worldPoints;
    }

    @Override
    public boolean contains(float x, float y) {
        updateWorldPoints();
        final float[] vertices = worldPoints;
        final int numFloats = vertices.length;
        int intersects = 0;
        for (int i = 0; i < numFloats; i += 2) {
            float x1 = vertices[i];
            float y1 = vertices[i + 1];
            float x2 = vertices[(i + 2) % numFloats];
            float y2 = vertices[(i + 3) % numFloats];
            if (((y1 <= y && y < y2) || (y2 <= y && y < y1)) && x < ((x2 - x1) / (y2 - y1) * (y - y1) + x1)) intersects++;
        }
        return (intersects & 1) == 1;
    }

    @Override
    public float getArea() {
        updateWorldPoints();
        float area = 0;
        int last = worldPoints.length - 2;
        float x1 = worldPoints[last], y1 = worldPoints[last + 1];
        for (int i = 0; i <= last; i += 2) {
            float x2 = worldPoints[i], y2 = worldPoints[i + 1];
            area += x1 * y2 - x2 * y1;
            x1 = x2;
            y1 = y2;
        }
        return area * 0.5f;
    }

    @Override
    public float getPerimeter() {
        float perimeter = 0;
        for (int i = 0; i < worldPoints.length - 2; i += 2) {
            perimeter += Vector2.dst(worldPoints[i], worldPoints[i+1], worldPoints[i+2], worldPoints[i+3]);
        }
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
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        updated = false;
    }

    private void reverseVertices() {
        int lastX = localPoints.length - 2;
        for (int i = 0, n = localPoints.length / 2; i < n; i += 2) {
            int other = lastX - i;
            float x = localPoints[i];
            float y = localPoints[i + 1];
            localPoints[i] = localPoints[other];
            localPoints[i + 1] = localPoints[other + 1];
            localPoints[other] = x;
            localPoints[other + 1] = y;
        }
    }

}
