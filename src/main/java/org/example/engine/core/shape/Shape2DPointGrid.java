package org.example.engine.core.shape;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.jetbrains.annotations.NotNull;

// will be used in rigged 2d meshes.
// TODO: implement
public class Shape2DPointGrid extends Shape2D {

    public final int vertexCount;
    public final float[] vertices;
    public final int[] indices;
    private final CollectionsArray<MathVector2> worldVertices;

    protected Shape2DPointGrid(float[] vertices) {
        if (vertices.length < 6) throw new IllegalArgumentException("At least 3 points are needed to construct a polygon; Points array must contain at least 6 values: [x0,y0,x1,y1,x2,y2,...]. Given: " + vertices.length);
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Point array must be of even length in the format [x0,y0, x1,y1, ...].");
        this.vertexCount = vertices.length / 2;
        this.vertices = vertices;
        this.worldVertices = new CollectionsArray<>(true, vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            this.worldVertices.add(new MathVector2());
        }
        this.indices = null; // calculate triangles.
        // TODO: fix the area calculations.
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

    // TODO: write unit tests for that. By the way, in its previous form, it was at the very least
    // TODO: incorrect for polygons with holes.
    @Override
    protected boolean containsPoint(float x, float y) {
        // TODO: look at polygon for reference.
        return false;
    }

    // TODO: implement
    @Override
    protected float calculateUnscaledBoundingRadius() {
        return 0;
    }

    // TODO: implement
    @Override
    protected float calculateUnscaledArea() {
        return 0;
    }

    public void getWorldEdge(int index, @NotNull MathVector2 tail, @NotNull MathVector2 head) {
        if (!updated) update();
        int next = (index + 1) % vertexCount;
        tail.set(worldVertices.getCyclic(index));
        head.set(worldVertices.getCyclic(next));
    }

    @Override
    protected MathVector2 calculateLocalGeometryCenter() {
        return null;
    }
}