package org.example.engine.core.math;

public class Shape2DPolygon extends Shape2D {

    public final int vertexCount;
    public final float[] localPoints;
    public final int[] indices;
    private float[] worldPoints;
    private float area;

    private final Vector2 tmp = new Vector2();

    public Shape2DPolygon(float[] vertices) {
        this(vertices, null);
    }

    public Shape2DPolygon(float[] vertices, int[] holes) {
        if (vertices.length < 6) throw new IllegalArgumentException("At least 3 points are needed to construct a polygon; Points array must contain at least 6 values: [x0,y0,x1,y1,x2,y2,...]. Given: " + vertices.length);
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Point array must be of even length in the format [x0,y0, x1,y1, ...].");

        this.vertexCount = vertices.length / 2;
        this.localPoints = new float[vertices.length];
        System.arraycopy(vertices, 0, localPoints, 0, vertices.length);
        this.area = Algorithms.calculatePolygonSignedArea(vertices);
        this.worldPoints = new float[vertices.length];
        System.out.println("holes: " + holes);
        this.indices = Algorithms.triangulatePolygon(localPoints, holes, 2);
        updated = false;
    }

    @Override
    public void update() {
        for (int i = 0; i < localPoints.length - 1; i += 2) {
            tmp.set(localPoints[i] * scaleX, localPoints[i + 1] * scaleY).rotateDeg(angle).add(x, y);
            worldPoints[i] = tmp.x;
            worldPoints[i + 1] = tmp.y;
        }
        updated = true;
    }

    public float[] getWorldPoints() {
        if (!updated) update();
        return worldPoints;
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
        return Math.abs(area * scaleX * scaleY);
    }

    @Override
    public float getPerimeter() {
        if (!updated) update();
        float perimeter = 0;
        for (int i = 0; i < worldPoints.length - 2; i += 2) perimeter += Vector2.dst(worldPoints[i], worldPoints[i+1], worldPoints[i+2], worldPoints[i+3]);
        perimeter += Vector2.dst(worldPoints[worldPoints.length - 2], worldPoints[worldPoints.length - 1], worldPoints[0], worldPoints[1]);
        return perimeter;
    }

}
