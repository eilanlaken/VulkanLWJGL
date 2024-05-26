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
                rayVsCircle(body, ray, (Shape2DCircle) shape, intersections);
            }
        }
    }

    private void rayVsCircle(Physics2DBody body, Physics2DWorld.Ray ray, Shape2DCircle circle, @NotNull CollectionsArray<Physics2DWorld.Intersection> intersections) {
        MathVector2 u  = new MathVector2(circle.x(), circle.y()).sub(ray.originX, ray.originY);
        MathVector2 u1 = new MathVector2(ray.dirX, ray.dirY).scl(MathVector2.dot(u.x, u.y, ray.dirX, ray.dirY));
        MathVector2 u2 = new MathVector2(u).sub(u1);

        System.out.println(new MathVector2(ray.dirX, ray.dirY));

        float d2 = u2.len2();
        float r2 = circle.getWorldRadius() * circle.getWorldRadius();

        System.out.println(d2);
        System.out.println(r2);
        if (d2 > r2) return;

        float m = (float) Math.sqrt(r2 - d2);
        System.out.println("m: " + m);

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
