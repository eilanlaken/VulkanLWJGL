package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.Shape2D;
import org.example.engine.core.shape.Shape2DCircle;
import org.example.engine.core.shape.Shape2DRectangle;
import org.jetbrains.annotations.NotNull;

public final class Physics2DRayCasting {

    // TODO: see if useful when dealing with polygons.
    private static final int COLLINEAR         = 0;
    private static final int CLOCKWISE         = 1;
    private static final int COUNTER_CLOCKWISE = 2;

    private final Physics2DWorld world;
    private final MemoryPool<Physics2DWorld.Intersection> IntersectionsPool;

    Physics2DRayCasting(Physics2DWorld world) {
        this.world = world;
        this.IntersectionsPool = world.getIntersectionsPool();
    }

    void calculateIntersections(Physics2DWorld.Ray ray, Array<Physics2DBody> bodies, @NotNull Array<Physics2DWorld.Intersection> intersections) {
        for (Physics2DBody body : bodies) {
            Shape2D shape = body.shape;
            if (shape instanceof Shape2DCircle) {
                rayVsCircle(body, ray, (Shape2DCircle) shape, intersections);
            }
            if (shape instanceof Shape2DRectangle) {
                rayVsRectangle(body, ray, (Shape2DRectangle) shape, intersections);
            }
        }
    }

    private void rayVsCircle(Physics2DBody body, Physics2DWorld.Ray ray, Shape2DCircle circle, @NotNull Array<Physics2DWorld.Intersection> intersections) {
        Vector2 m = new Vector2(ray.originX, ray.originY).sub(circle.x(), circle.y());
        float b = 2 * m.dot(ray.dirX, ray.dirY);
        float c = m.len2() - circle.getWorldRadius() * circle.getWorldRadius();
        float det = b * b - 4 * c;
        if (det < 0) return;

        if (MathUtils.isZero(det)) {
            float t = -b / 2.0f;
            if (t < 0 || t > ray.dst) return;
            Physics2DWorld.Intersection result = IntersectionsPool.allocate();
            result.body = body;
            result.point.set(ray.originX, ray.originY).add(t * ray.dirX, t * ray.dirY);
            result.direction.set(result.point).sub(circle.x(), circle.y());
            intersections.add(result);
            return;
        }

        float t1 = (-b + (float) Math.sqrt(det)) / 2.0f;
        if (t1 > 0 && t1 < ray.dst) {
            Physics2DWorld.Intersection result1 = IntersectionsPool.allocate();
            result1.body = body;
            result1.point.set(ray.originX, ray.originY).add(t1 * ray.dirX, t1 * ray.dirY);
            result1.direction.set(result1.point).sub(circle.x(), circle.y());
            intersections.add(result1);
        }

        float t2 = (-b - (float) Math.sqrt(det)) / 2.0f;
        if (t2 > 0 && t2 < ray.dst) {
            Physics2DWorld.Intersection result2 = IntersectionsPool.allocate();
            result2.body = body;
            result2.point.set(ray.originX, ray.originY).add(t2 * ray.dirX, t2 * ray.dirY);
            result2.direction.set(result2.point).sub(circle.x(), circle.y());
            intersections.add(result2);
        }
    }

    private void rayVsRectangle(Physics2DBody body, Physics2DWorld.Ray ray, Shape2DRectangle rectangle, @NotNull Array<Physics2DWorld.Intersection> intersections) {
        // broad phase
        float boundingRadius = rectangle.getBoundingRadius();
        Vector2 m = new Vector2(ray.originX, ray.originY).sub(rectangle.x(), rectangle.y());
        float b = 2 * m.dot(ray.dirX, ray.dirY);
        float c = m.len2() - boundingRadius * boundingRadius;
        float det = b * b - 4 * c;
        if (det < 0) return;

        // narrow phase
        float dst = ray.dst == Float.POSITIVE_INFINITY ? 1.0f : ray.dst;
        float x3 = ray.originX;
        float y3 = ray.originY;
        float x4 = ray.originX + dst * ray.dirX;
        float y4 = ray.originY + dst * ray.dirY;

        Vector2 c0 = rectangle.c0();
        Vector2 c1 = rectangle.c1();
        Vector2 c2 = rectangle.c2();
        Vector2 c3 = rectangle.c3();

        Array<Vector2> edges = new Array<>(true, 4);
        edges.add(c0, c1, c2, c3);

        for (int i = 0; i < 4; i++) {
            Vector2 p = edges.getCyclic(i);
            float x1 = p.x;
            float y1 = p.y;

            Vector2 q = edges.getCyclic(i+1);
            float x2 = q.x;
            float y2 = q.y;

            float den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
            if (MathUtils.isZero(den)) continue;

            float t =  ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / den;
            float u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / den;

            if (ray.dst == Float.POSITIVE_INFINITY) {
                if (t > 0 && t < 1 && u > 0) {
                    Physics2DWorld.Intersection result = IntersectionsPool.allocate();
                    result.body = body;
                    result.point.set(x1 + t * (x2 - x1), y1 + t * (y2 - y1));
                    result.direction.set(x4 - x3, y4 - y3);
                    intersections.add(result);
                }
            } else if (t > 0 && t < 1 && u > 0 && u < 1) {
                Physics2DWorld.Intersection result = IntersectionsPool.allocate();
                result.body = body;
                result.point.set(x1 + t * (x2 - x1), y1 + t * (y2 - y1));
                result.direction.set(x4 - x3, y4 - y3);
                intersections.add(result);
            }
        }
    }

    private boolean onSegment(float px, float py, float qx, float qy, float rx, float ry) {
        return qx <= Math.max(px, rx) && qx >= Math.min(px, rx) &&
                qy <= Math.max(py, ry) && qy >= Math.min(py, ry);
    }

    private int orientation(float x1, float y1, float x2, float y2, float x3, float y3) {
        float val = (y2 - y1) * (x3 - x2) - (x2 - x1) * (y3 - y2);
        if (val == 0) return COLLINEAR;
        return (val > 0) ? CLOCKWISE : COUNTER_CLOCKWISE;
    }

}
