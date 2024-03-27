package org.example.engine.core.math;

public class Shape2DPolygon implements Shape2D {

    public final int vertexCount;
    public final float[] localPoints;
    private float[] worldPoints;
    public final int[] indices;

    private float x, y;
    private float angle;
    private float scaleX, scaleY;
    private float area;

    private boolean updated;
    private final Vector2 tmp = new Vector2();

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
        this.worldPoints = new float[points.length];
        System.arraycopy(points, 0, worldPoints, 0, points.length);
        this.area = Algorithms.calculatePolygonSignedArea(this.worldPoints);
        this.indices = Algorithms.triangulatePolygon(localPoints);
        updated = false;
    }

    public void update() {
        for (int i = 0; i < localPoints.length - 1; i += 2) {
            tmp.set(localPoints[i] * scaleX, localPoints[i + 1] * scaleY).rotateDeg(angle).add(x, y);
            worldPoints[i] = tmp.x;
            worldPoints[i + 1] = tmp.y;
        }
        updated = true;
    }

    public Vector2 getVertex(int index, Vector2 output) {
        if (index >= vertexCount) output.set(worldPoints[(index * 2) % worldPoints.length], worldPoints[(index * 2) % worldPoints.length + 1]);
        else if (index < 0) output.set(worldPoints[(index * 2) % worldPoints.length + worldPoints.length], worldPoints[(index * 2) % worldPoints.length + worldPoints.length + 1]);
        else output.set(worldPoints[index * 2], worldPoints[index * 2 + 1]);
        return output;
    }

    @Override
    public boolean contains(float x, float y) {
        if (!updated) update();
        // preliminary test
        float minX = worldPoints[0];
        float maxX = worldPoints[0];
        float minY = worldPoints[1];
        float maxY = worldPoints[1];
        for (int i = 2; i < worldPoints.length - 1; i += 2) {
            minX = Math.min(minX, worldPoints[i]);
            maxX = Math.max(maxX, worldPoints[i]);
            minY = Math.min(minY, worldPoints[i+1]);
            maxY = Math.max(maxY, worldPoints[i+1]);
        }
        if (x < minX || x > maxX || y < minY || y > maxY) return false;
        // if broad test passed:
        boolean inside = false;
        for (int i = 0, j = worldPoints.length - 2; i < worldPoints.length; i += 2) {
            float x1 = worldPoints[i];
            float y1 = worldPoints[i+1];
            float x2 = worldPoints[j];
            float y2 = worldPoints[j+1];
            if ( ((y1 > y) != (y2 > y)) )
                if (x < (x2 - x1) * (y - y1) / (y2 - y1) + x1) inside = !inside;
            j = i;
        }
        return inside;
    }

    @Override
    public float getArea() {
        return Math.abs(area);
    }

    @Override
    public float getPerimeter() {
        if (!updated) update();
        float perimeter = 0;
        for (int i = 0; i < worldPoints.length - 2; i += 2) perimeter += Vector2.dst(worldPoints[i], worldPoints[i+1], worldPoints[i+2], worldPoints[i+3]);
        perimeter += Vector2.dst(worldPoints[worldPoints.length - 2], worldPoints[worldPoints.length - 1], worldPoints[0], worldPoints[1]);
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
        this.area *= scaleX;
        this.area *= scaleY;
        updated = false;
    }

    public static void getVertex(float[] points, int index, Vector2 output) {
        final int vertexCount = points.length / 2;
        if (index >= vertexCount) output.set(points[(index * 2) % points.length], points[(index * 2) % points.length + 1]);
        else if (index < 0) output.set(points[(index * 2) % points.length + points.length], points[(index * 2) % points.length + points.length + 1]);
        else output.set(points[index * 2], points[index * 2 + 1]);
    }

}
