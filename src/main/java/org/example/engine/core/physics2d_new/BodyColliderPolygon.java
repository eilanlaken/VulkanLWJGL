package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.jetbrains.annotations.NotNull;

/*
Represents a <i>convex</i> polygon.
Any concave polygons or polygons with holes will be broken down into
a set of convex polygons.
 */
public final class BodyColliderPolygon extends BodyCollider {

    final int     vertexCount;
    final float[] vertices;
    final int[]   indices;

    private final Array<Vector2> worldVertices;

    BodyColliderPolygon(Body body, float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask,
                        float[] vertices) throws RuntimeException {
        super(body, density, staticFriction, dynamicFriction, restitution, ghost, bitmask);
        if (vertices.length < 6) throw new IllegalArgumentException("At least 3 points are needed to construct a polygon; Points array must contain at least 6 values: [x0,y0,x1,y1,x2,y2,...]. Given: " + vertices.length);
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Point array must be of even length in the format [x0,y0, x1,y1, ...].");
        this.vertexCount = vertices.length / 2;
        this.vertices = vertices;
        this.worldVertices = new Array<>(true, vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            this.worldVertices.add(new Vector2());
        }
        this.indices = MathUtils.triangulate2DPolygon(this.vertices);
    }

    @Override
    protected float calculateBoundingRadius() {
        float max = 0;
        for (int i = 0; i < vertices.length - 1; i += 2) {
            float l2 = vertices[i] * vertices[i] + vertices[i+1] * vertices[i+1];
            if (l2 > max) max = l2;
        }
        return (float) Math.sqrt(max);
    }

    @Override
    protected float calculateArea() {
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
    protected Vector2 calculateLocalCenter() {
        Vector2 center = new Vector2();
        for (int i = 0; i < vertices.length - 1; i += 2) {
            center.add(vertices[i], vertices[i + 1]);
        }
        center.scl(1.0f / vertexCount);
        return center;
    }

    @Override
    protected void updateWorldCoordinates() {
        if (MathUtils.isZero(body.angleRad)) {
            for (int i = 0; i < vertexCount; i++) {
                worldVertices.get(i).set(vertices[i * 2] + body.x, vertices[i * 2 + 1] + body.y);
            }
        } else {
            for (int i = 0; i < vertexCount; i++) {
                worldVertices.get(i).set(vertices[i * 2], vertices[i * 2 + 1]).rotateRad(body.angleRad).add(body.x, body.y);
            }
        }
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        boolean inside = false;
        Vector2 tail = new Vector2();
        Vector2 head = new Vector2();
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

    public void getWorldEdge(int index, @NotNull Vector2 tail, @NotNull Vector2 head) {
        if (!updated) update();
        int next = (index + 1) % vertexCount;
        tail.set(worldVertices.getCyclic(index));
        head.set(worldVertices.getCyclic(next));
    }

    public Array<Vector2> worldVertices() {
        if (!updated) update();
        return worldVertices;
    }
}