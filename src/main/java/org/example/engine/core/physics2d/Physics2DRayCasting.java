package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.Shape2D;
import org.example.engine.core.shape.Shape2DCircle;
import org.example.engine.core.shape.Shape2DRectangle;
import org.jetbrains.annotations.NotNull;

public final class Physics2DRayCasting {

    private final Physics2DWorld world;
    private final MemoryPool<Physics2DWorld.Intersection> IntersectionsPool;

    Physics2DRayCasting(Physics2DWorld world) {
        this.world = world;
        this.IntersectionsPool = world.getIntersectionsPool();
    }

    void calculateIntersections(Physics2DWorld.Ray ray, CollectionsArray<Physics2DBody> bodies, @NotNull CollectionsArray<Physics2DWorld.Intersection> intersections) {
        for (Physics2DBody body : bodies) {
            Shape2D shape = body.shape;
            if (shape instanceof Shape2DCircle) {
                rayVsCircle(body, ray, (Shape2DCircle) shape, intersections);
            }

        }
    }

    private void rayVsCircle(Physics2DBody body, Physics2DWorld.Ray ray, Shape2DCircle circle, @NotNull CollectionsArray<Physics2DWorld.Intersection> intersections) {
        MathVector2 m = new MathVector2(ray.originX, ray.originY).sub(circle.x(), circle.y());
        float b = 2 * m.dot(ray.dirX, ray.dirY);
        float c = m.len2() - circle.getWorldRadius() * circle.getWorldRadius();
        float det = b * b - 4 * c;
        if (det < 0) return;

        if (MathUtils.isZero(det)) {
            Physics2DWorld.Intersection result = IntersectionsPool.allocate();
            result.body = body;
            float t = -b / 2;
            result.point.set(ray.originX, ray.originY).add(t * ray.dirX, t * ray.dirY);
            result.direction.set(result.point).sub(circle.x(), circle.y());
            intersections.add(result);
            return;
        }

        float t1 = (-b + (float) Math.sqrt(det)) / 2.0f;
        if (t1 > 0) {
            Physics2DWorld.Intersection result1 = IntersectionsPool.allocate();
            result1.body = body;
            result1.point.set(ray.originX, ray.originY).add(t1 * ray.dirX, t1 * ray.dirY);
            result1.direction.set(result1.point).sub(circle.x(), circle.y());
            intersections.add(result1);
        }

        float t2 = (-b - (float) Math.sqrt(det)) / 2.0f;
        if (t2 > 0) {
            Physics2DWorld.Intersection result2 = IntersectionsPool.allocate();
            result2.body = body;
            result2.point.set(ray.originX, ray.originY).add(t2 * ray.dirX, t2 * ray.dirY);
            result2.direction.set(result2.point).sub(circle.x(), circle.y());
            intersections.add(result2);
        }
    }

    private void rayVsRectangle(Physics2DBody body, Physics2DWorld.Ray ray, Shape2DRectangle rectangle, @NotNull CollectionsArray<Physics2DWorld.Intersection> intersections) {
        float s1x1 = ray.originX;
        float s1y1 = ray.originY;
        float s1x2 = ray.originX + ray.dst * ray.dirX;
        float s1y2 = ray.originY + ray.dst * ray.dirY;

        MathVector2 c0 = rectangle.c0();
        MathVector2 c1 = rectangle.c1();
        MathVector2 c2 = rectangle.c2();
        MathVector2 c3 = rectangle.c3();
    }

}
