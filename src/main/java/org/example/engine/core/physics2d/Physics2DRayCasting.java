package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.Shape2D;
import org.example.engine.core.shape.Shape2DCircle;
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
                rayVsCircle2(body, ray, (Shape2DCircle) shape, intersections);
            }
        }
    }

    private void rayVsCircle2(Physics2DBody body, Physics2DWorld.Ray ray, Shape2DCircle circle, @NotNull CollectionsArray<Physics2DWorld.Intersection> intersections) {
        MathVector2 m = new MathVector2(ray.originX, ray.originY).sub(circle.x(), circle.y());

        float b = 2 * m.dot(ray.dirX, ray.dirY);
        float c = m.len2() - circle.getWorldRadius() * circle.getWorldRadius();

        float det = b * b - 4 * c;

        System.out.println(m);
        if (det < 0) return;

        if (MathUtils.isZero(det)) {
            Physics2DWorld.Intersection result = IntersectionsPool.allocate();
            result.body = body;
            float t = -b / 2;
            result.point.set(ray.originX, ray.originY).add(t * ray.dirX, t * ray.dirY);
            result.direction.set(result.point).sub(circle.x(), circle.y());
            intersections.add(result);
        } else {
            Physics2DWorld.Intersection result1 = IntersectionsPool.allocate();
            result1.body = body;
            float t1 = (-b + (float) Math.sqrt(det)) / 2.0f;
            result1.point.set(ray.originX, ray.originY).add(t1 * ray.dirX, t1 * ray.dirY);
            result1.direction.set(result1.point).sub(circle.x(), circle.y());
            intersections.add(result1);

            Physics2DWorld.Intersection result2 = IntersectionsPool.allocate();
            result2.body = body;
            float t2 = (-b - (float) Math.sqrt(det)) / 2.0f;
            result2.point.set(ray.originX, ray.originY).add(t2 * ray.dirX, t2 * ray.dirY);
            result2.direction.set(result2.point).sub(circle.x(), circle.y());
            intersections.add(result2);
        }
    }

    private void rayVsCircle(Physics2DBody body, Physics2DWorld.Ray ray, Shape2DCircle circle, @NotNull CollectionsArray<Physics2DWorld.Intersection> intersections) {
        MathVector2 u  = new MathVector2(circle.x(), circle.y()).sub(ray.originX, ray.originY);
        MathVector2 u1 = new MathVector2(ray.dirX, ray.dirY).scl(MathVector2.dot(u.x, u.y, ray.dirX, ray.dirY));
        MathVector2 u2 = new MathVector2(u).sub(u1);


        float d2 = u2.len2();
        float r2 = circle.getWorldRadius() * circle.getWorldRadius();

        System.out.println(d2);


        if (d2 > r2) return;

        float m = (float) Math.sqrt(r2 - d2);

        if (MathUtils.isZero(m)) {
            Physics2DWorld.Intersection result = IntersectionsPool.allocate();
            result.body = body;
            result.point.set(ray.originX, ray.originY).add(u1);
            result.direction.set(result.point).sub(circle.x(), circle.y());
            intersections.add(result);
        } else {
            Physics2DWorld.Intersection result1 = IntersectionsPool.allocate();
            result1.body = body;
            result1.point.set(ray.originX, ray.originY).add(u1).add(m * ray.dirX, m * ray.dirY);
            result1.direction.set(result1.point).sub(circle.x(), circle.y());
            intersections.add(result1);

            Physics2DWorld.Intersection result2 = IntersectionsPool.allocate();
            result2.body = body;
            result2.point.set(ray.originX, ray.originY).add(u1).sub(m * ray.dirX, m * ray.dirY);
            result2.direction.set(result2.point).sub(circle.x(), circle.y());
            intersections.add(result2);
        }
    }

}
