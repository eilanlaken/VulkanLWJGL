package org.example.engine.core.shape;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.collections.CollectionsTuple2;
import org.example.engine.core.collections.CollectionsUtils;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Shape2DPolygon extends Shape2D {

    public  final int     vertexCount;
    public  final float[] vertices;
    public  final int[]   indices;
    public  final boolean isConvex;
    public  final int[]   holes;
    public  final boolean hasHoles;

    private final CollectionsArray<CollectionsTuple2<Integer, Integer>> loops;
    private final CollectionsArray<MathVector2>                         worldVertices;

    Shape2DPolygon(int[] indices, float[] vertices) {
        if (vertices.length < 6) throw new IllegalArgumentException("At least 3 points are needed to construct a polygon; Points array must contain at least 6 values: [x0,y0,x1,y1,x2,y2,...]. Given: " + vertices.length);
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Point array must be of even length in the format [x0,y0, x1,y1, ...].");
        this.vertexCount = vertices.length / 2;
        this.vertices = vertices;
        this.worldVertices = new CollectionsArray<>(true, vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            this.worldVertices.add(new MathVector2());
        }
        this.indices = indices;
        this.isConvex = ShapeUtils.isPolygonConvex(vertices);
        this.holes = null;
        this.loops = ShapeUtils.getLoops(null, vertexCount);
        this.hasHoles = false;
    }

    public Shape2DPolygon(float[] vertices) {
        this(vertices, null);
    }

    /**
     * @param vertices is a flat array of vertex coordinates like [x0,y0, x1,y1, x2,y2, ...].
     * @param holes is an array of hole indices if any (e.g. [5, 8] for a 12-vertex input would mean one hole with vertices 5-7 and another with 8-11).
     */
    public Shape2DPolygon(float[] vertices, int[] holes) {
        if (vertices.length < 6)      throw new ShapeException("At least 3 points are needed to construct a polygon; Points array must contain at least 6 values: [x0,y0,x1,y1,x2,y2,...]. Given: " + vertices.length);
        if (vertices.length % 2 != 0) throw new ShapeException("Point array must be of even length in the format [x0,y0, x1,y1, ...].");

        if (holes != null && !CollectionsUtils.isSorted(holes, true)) Arrays.sort(holes);
        this.vertexCount = vertices.length / 2;
        this.vertices = vertices;
        this.worldVertices = new CollectionsArray<>(true, vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            this.worldVertices.add(new MathVector2());
        }
        try {
            this.indices = ShapeUtils.triangulatePolygon(this.vertices, holes, 2);
        } catch (Exception e) {
            throw new ShapeException("Failed to triangulate polygon: polygon either contains collinear or intersecting edges");
        }
        this.holes = holes;
        this.loops = ShapeUtils.getLoops(holes, vertexCount);
        this.hasHoles = holes != null && holes.length != 0;
        this.isConvex = !hasHoles && ShapeUtils.isPolygonConvex(vertices);
    }

    @Override
    protected MathVector2 calculateLocalGeometryCenter() {
        MathVector2 center = new MathVector2();
        if (!hasHoles) {
            for (int i = 0; i < vertices.length - 1; i += 2) {
                center.add(vertices[i], vertices[i + 1]);
            }
            center.scl(1.0f / vertexCount);
        } else {
            for (int i = 0; i < indices.length - 2; i += 3) {
                int   v1 = indices[i];
                float x1 = vertices[v1];
                float y1 = vertices[v1 + 1];

                int   v2 = indices[i + 1];
                float x2 = vertices[v2];
                float y2 = vertices[v2 + 1];

                int   v3 = indices[i + 2];
                float x3 = vertices[v3];
                float y3 = vertices[v3 + 1];

                MathVector2 triangleCenter = new MathVector2((x1 + x2 + x3) / 3, (y1 + y2 + y3) / 3);
                center.add(triangleCenter);
            }
            center.scl(1f / (indices.length - 2));
        }
        return center;
    }

    @Override
    protected float calculateUnscaledBoundingRadius() {
        float max = 0;
        for (int i = 0; i < vertices.length - 1; i += 2) {
            float l2 = vertices[i] * vertices[i] + vertices[i+1] * vertices[i+1];
            if (l2 > max) max = l2;
        }
        return (float) Math.sqrt(max);
    }

    @Override
    protected float calculateUnscaledArea() {
        float sum = 0;
        for (int i = 0; i < indices.length - 2; i += 3) {
            int   v1 = indices[i];
            float x1 = vertices[v1];
            float y1 = vertices[v1 + 1];

            int   v2 = indices[i + 1];
            float x2 = vertices[v2];
            float y2 = vertices[v2 + 1];

            int   v3 = indices[i + 2];
            float x3 = vertices[v3];
            float y3 = vertices[v3 + 1];

            sum += MathUtils.areaTriangle(x1, y1, x2, y2, x3, y3);
        }
        return sum;
    }

    @Override
    protected void updateWorldCoordinates() {
        if (MathUtils.isZero(angle)) {
            for (int i = 0; i < vertexCount; i++) {
                worldVertices.get(i).set(vertices[i * 2] * scaleX + x, vertices[i * 2 + 1] * scaleY + y);
            }
        } else {
            for (int i = 0; i < vertexCount; i++) {
                worldVertices.get(i).set(vertices[i * 2] * scaleX, vertices[i * 2 + 1] * scaleY).rotateDeg(angle).add(x, y);
            }
        }
    }

    @Override
    protected CollectionsArray<MathVector2> getWorldVertices() {
        return worldVertices;
    }

    // TODO: write unit tests
    @Override
    protected boolean containsPoint(float x, float y) {
        boolean inside = false;
        MathVector2 tail = new MathVector2();
        MathVector2 head = new MathVector2();
        for (int i = 0; i < worldVertices.size; i++) {
            getWorldEdge(i, tail, head);
            float x1 = tail.x;
            float y1 = tail.y;
            float x2 = head.x;
            float y2 = head.y;
            if ( ((y1 > y) != (y2 > y)) )
                if (x < (x2 - x1) * (y - y1) / (y2 - y1) + x1) inside = !inside;
        }
        return inside;
    }

    public void getWorldEdge(int index, @NotNull MathVector2 tail, @NotNull MathVector2 head) {
        if (!updated) update();
        int next = (index + 1) % vertexCount;
        if (hasHoles) {
            for (CollectionsTuple2<Integer, Integer> loop : loops) {
                if (index == loop.t2) next = loop.t1;
            }
        }
        tail.set(worldVertices.getCyclic(index));
        head.set(worldVertices.getCyclic(next));
    }

}