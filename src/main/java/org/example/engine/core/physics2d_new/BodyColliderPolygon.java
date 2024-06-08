package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.CollectionsUtils;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryUtils;
import org.jetbrains.annotations.NotNull;

/*
Represents a <i>convex</i> polygon.
Any concave polygons or polygons with holes will be broken down into
a set of convex polygons.
 */
public final class BodyColliderPolygon extends BodyCollider {

    public final int            vertexCount;
    public final float[]        vertices;
    public final int[]          indices;
    public final Array<Vector2> worldVertices;

    BodyColliderPolygon(float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask,
                        float[] vertices) throws RuntimeException {
        super(0, 0, 0, density, staticFriction, dynamicFriction, restitution, ghost, bitmask);
        if (vertices.length < 6) throw new IllegalArgumentException("At least 3 points are needed to construct a polygon; Points array must contain at least 6 values: [x0,y0,x1,y1,x2,y2,...]. Given: " + vertices.length);
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Point array must be of even length in the format [x0,y0, x1,y1, ...].");
        this.vertexCount = vertices.length / 2;
        int windingOrder = MathUtils.polygonWindingOrder(vertices);
        this.vertices = MemoryUtils.copy(vertices);
        if (windingOrder > 0) { // we need to reverse the vertices.
            // reverse the order
            int n = vertices.length;
            for (int i = 0; i < n / 2; i += 2) {
                int j = n - 2 - i;

                // Swap x coordinates
                float tempX = this.vertices[i];
                this.vertices[i] = this.vertices[j];
                this.vertices[j] = tempX;

                // Swap y coordinates
                float tempY = this.vertices[i + 1];
                this.vertices[i + 1] = this.vertices[j + 1];
                this.vertices[j + 1] = tempY;
            }
        }
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
    protected void update() {
        for (int i = 0; i < vertexCount; i++) {
            worldVertices.get(i)
                    .set(vertices[i * 2], vertices[i * 2 + 1])
                    .rotateAroundRad(body.lcmX, body.lcmY, body.aRad)
                    .add(body.x, body.y);
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
        int next = (index + 1) % vertexCount;
        tail.set(worldVertices.getCyclic(index));
        head.set(worldVertices.getCyclic(next));
    }

    public Array<Vector2> worldVertices() {
        return worldVertices;
    }

    @Override
    Vector2 calculateLocalCenter() {
        Vector2 local_center = new Vector2();
        for (int i = 0; i < vertices.length - 1; i += 2) {
            local_center.x += vertices[i];
            local_center.y += vertices[i+1];
        }
        return local_center.scl(1.0f / vertexCount);
    }

}