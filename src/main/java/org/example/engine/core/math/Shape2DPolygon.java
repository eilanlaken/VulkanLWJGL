package org.example.engine.core.math;

public class Shape2DPolygon extends Shape2D {

    public final int vertexCount;
    public final float[] localPoints;
    public final int[] indices;
    private final float[] worldPoints;
    public final boolean isConvex;
    private float area = Float.NaN;

    private final Vector2 tmp = new Vector2();

    protected Shape2DPolygon(int[] indices, float[] vertices) {
        if (vertices.length < 6) throw new IllegalArgumentException("At least 3 points are needed to construct a polygon; Points array must contain at least 6 values: [x0,y0,x1,y1,x2,y2,...]. Given: " + vertices.length);
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Point array must be of even length in the format [x0,y0, x1,y1, ...].");

        this.vertexCount = vertices.length / 2;
        this.localPoints = vertices;
        this.worldPoints = new float[vertices.length];
        this.indices = indices;
        this.isConvex = Algorithms.isPolygonConvex(vertices);
        float max = 0;
        for (int i = 0; i < vertices.length - 1; i += 2) {
            float l2 = vertices[i] * vertices[i] + vertices[i+1] * vertices[i+1];
            if (l2 > max) max = l2;
        }
        super.originalBoundingRadius = (float) Math.sqrt(max);
        updated = false;
    }

    public Shape2DPolygon(float[] vertices) {
        this(vertices, null);
    }

    public Shape2DPolygon(float[] vertices, int[] holes) {
        if (vertices.length < 6) throw new IllegalArgumentException("At least 3 points are needed to construct a polygon; Points array must contain at least 6 values: [x0,y0,x1,y1,x2,y2,...]. Given: " + vertices.length);
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Point array must be of even length in the format [x0,y0, x1,y1, ...].");

        this.vertexCount = vertices.length / 2;
        this.localPoints = vertices;
        this.worldPoints = new float[vertices.length];
        this.indices = Algorithms.triangulatePolygon(localPoints, holes, 2);
        this.isConvex = (holes == null || holes.length == 0) && Algorithms.isPolygonConvex(vertices);
        float max = 0;
        for (int i = 0; i < vertices.length - 1; i += 2) {
            float l2 = vertices[i] * vertices[i] + vertices[i+1] * vertices[i+1];
            if (l2 > max) max = l2;
        }
        super.originalBoundingRadius = (float) Math.sqrt(max);
        updated = false;
    }

    @Override
    public void update() {
        if (updated) return;
        for (int i = 0; i < localPoints.length - 1; i += 2) {
            tmp.set(localPoints[i] * scaleX, localPoints[i + 1] * scaleY);
            if (angle != 0.0f) tmp.rotateDeg(angle);
            tmp.add(x, y);
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
        if (Float.isNaN(area)) this.area = Algorithms.calculatePolygonSignedArea(localPoints);
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

    public static float getVertexX(int index, float[] vertices) {
        int n2 = vertices.length / 2;
        if (index >= n2) return vertices[(index % n2) * 2];
        else if (index < 0) return vertices[(index % n2 + n2) * 2];
        return vertices[index * 2];
    }

    public static float getVertexY(int index, float[] vertices) {
        int n2 = vertices.length / 2;
        if (index >= n2) return vertices[(index % n2) * 2 + 1];
        else if (index < 0) return vertices[(index % n2 + n2) * 2 + 1];
        return vertices[index * 2 + 1];
    }

}
