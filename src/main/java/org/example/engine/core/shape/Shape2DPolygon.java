package org.example.engine.core.shape;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.collections.CollectionsTuplePair;
import org.example.engine.core.collections.CollectionsUtils;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Shape2DPolygon extends Shape2D {

    public final int vertexCount;
    public final float[] vertices;
    public final int[] indices;
    public final boolean isConvex;
    private float[] worldVertices;

    public final int[] holes;
    public final boolean hasHoles;
    private final CollectionsArray<CollectionsTuplePair<Integer, Integer>> loops;

    private final float unscaledArea;
    private final float unscaledBoundingRadius;

    private final MathVector2 tmp = new MathVector2();

    protected Shape2DPolygon(int[] indices, float[] vertices) {
        if (vertices.length < 6) throw new IllegalArgumentException("At least 3 points are needed to construct a polygon; Points array must contain at least 6 values: [x0,y0,x1,y1,x2,y2,...]. Given: " + vertices.length);
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Point array must be of even length in the format [x0,y0, x1,y1, ...].");
        this.vertexCount = vertices.length / 2;
        this.vertices = vertices;
        this.worldVertices = new float[vertices.length];
        this.indices = indices;
        this.isConvex = ShapeUtils.isPolygonConvex(vertices);
        this.unscaledArea = Math.abs(ShapeUtils.incorrect_calculatePolygonSignedArea(this.vertices));
        this.unscaledBoundingRadius = ShapeUtils.calculatePolygonBoundingRadius(this.vertices);
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
        if (vertices.length < 6) throw new IllegalArgumentException("At least 3 points are needed to construct a polygon; Points array must contain at least 6 values: [x0,y0,x1,y1,x2,y2,...]. Given: " + vertices.length);
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Point array must be of even length in the format [x0,y0, x1,y1, ...].");
        if (holes != null && !CollectionsUtils.isSorted(holes, true)) Arrays.sort(holes);
        this.vertexCount = vertices.length / 2;
        this.vertices = vertices;
        this.worldVertices = new float[vertices.length];
        this.indices = ShapeUtils.triangulatePolygon(this.vertices, holes, 2);
        this.holes = holes;
        this.loops = ShapeUtils.getLoops(holes, vertexCount);
        this.hasHoles = holes != null && holes.length != 0;
        this.isConvex = !hasHoles && ShapeUtils.isPolygonConvex(vertices);
        // TODO: fix this one and write unit tests.
        this.unscaledArea = Math.abs(ShapeUtils.incorrect_calculatePolygonSignedArea(this.vertices));
        this.unscaledBoundingRadius = ShapeUtils.calculatePolygonBoundingRadius(this.vertices);
    }

    @Override
    protected float getUnscaledBoundingRadius() {
        return unscaledBoundingRadius;
    }

    @Override
    protected void updateWorldCoordinates() {
        if (MathUtils.isZero(angle)) {
            for (int i = 0; i < vertices.length - 1; i += 2) {
                worldVertices[i] = vertices[i] * scaleX + x;
                worldVertices[i + 1] = vertices[i + 1] * scaleY + y;
            }
        } else {
            for (int i = 0; i < vertices.length - 1; i += 2) {
                tmp.set(vertices[i] * scaleX, vertices[i + 1] * scaleY).rotateDeg(angle).add(x, y);
                worldVertices[i] = tmp.x;
                worldVertices[i + 1] = tmp.y;
            }
        }
    }

    public float[] getWorldVertices() {
        if (!updated) update();
        return worldVertices;
    }

    @Override
    public boolean contains(float x, float y) {
        if (!updated) update();
        boolean inside = false;
        for (int i = 0, j = worldVertices.length - 2; i < worldVertices.length; i += 2) {
            float x1 = worldVertices[i];
            float y1 = worldVertices[i+1];
            float x2 = worldVertices[j];
            float y2 = worldVertices[j+1];
            if ( ((y1 > y) != (y2 > y)) )
                if (x < (x2 - x1) * (y - y1) / (y2 - y1) + x1) inside = !inside;
            j = i;
        }
        return inside;
    }

    @Override
    protected float getUnscaledArea() {
        return unscaledArea;
    }

    public void getWorldEdge(int index, @NotNull MathVector2 tail, @NotNull MathVector2 head) {
        if (!hasHoles) {
            getWorldVertex(index, tail);
            getWorldVertex(index + 1, head);
            return;
        }

        getWorldVertex(index, tail);
        int next = index + 1;
        for (CollectionsTuplePair<Integer, Integer> loop : loops) {
            if (index == loop.second) next = loop.first;
        }
        getWorldVertex(next, head);
    }

    public MathVector2 getWorldVertex(int index, MathVector2 out) {
        if (!updated) update();
        if (out == null) out = new MathVector2();
        int n2 = worldVertices.length / 2;
        if (index >= n2) return out.set(worldVertices[(index % n2) * 2], worldVertices[(index % n2) * 2 + 1]);
        else if (index < 0) return out.set(worldVertices[(index % n2 + n2) * 2], worldVertices[(index % n2 + n2) * 2 + 1]);
        else return out.set(worldVertices[index * 2], worldVertices[index * 2 + 1]);
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