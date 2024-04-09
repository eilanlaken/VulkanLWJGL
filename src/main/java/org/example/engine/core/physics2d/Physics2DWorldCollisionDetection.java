package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.*;

public final class Physics2DWorldCollisionDetection {

    private Physics2DWorldCollisionDetection() {}

    public static boolean broadPhaseCollision(final Shape2D a, final Shape2D b) {
        final float dx = b.x() - a.x();
        final float dy = b.y() - a.y();
        final float sum = a.getBoundingRadius() + b.getBoundingRadius();
        return dx * dx + dy * dy < sum * sum;
    }

    public static void narrowPhaseCollision(Physics2DBody a, Physics2DBody b, Array<Physics2DWorldCollisionManifold> manifolds) {
        if (a.shape instanceof Shape2DCircle && b.shape instanceof Shape2DCircle) circleVsCircle(a, b, manifolds);
    }

    /** AABB vs ____ **/
    private static boolean AABBvsAABB(Shape2DAABB a, Shape2DAABB b, Physics2DWorldCollisionManifold manifold) {
        if (a.worldMax.x < b.worldMin.x || a.worldMin.x > b.worldMax.x) return false;
        if (a.worldMax.y < b.worldMin.y || a.worldMin.y > b.worldMax.y) return false;

        return true;
    }

    private static boolean AABBvsCircle(Shape2DAABB aabb, Shape2DCircle circle, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean AABBvsMorphed(Shape2DAABB aabb, Shape2DMorphed morphed, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean AABBvsPolygon(Shape2DAABB aabb, Shape2DPolygon polygon, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean AABBvsRectangle(Shape2DAABB aabb, Shape2DRectangle rectangle, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    /** Circle vs ____ **/
    private static boolean circleVsAABB(Shape2DCircle circle, Shape2DAABB aabb, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    // TODO: modify to use manifold etc.
    private static void circleVsCircle(Physics2DBody a, Physics2DBody b, Array<Physics2DWorldCollisionManifold> manifolds) {
        Shape2DCircle c1 = (Shape2DCircle) a.shape;
        Shape2DCircle c2 = (Shape2DCircle) b.shape;
        final float dx = c2.x() - c1.x();
        final float dy = c2.y() - c1.y();
        // todo: what if world radius is not updated?
        final float radiusSum = c1.worldRadius + c2.worldRadius;
        final float distanceSquared = dx * dx + dy * dy;

        if (distanceSquared > radiusSum * radiusSum) return;

        final float distance = (float) Math.sqrt(distanceSquared);

        // todo: grab from pool
        Physics2DWorldCollisionManifold manifold = new Physics2DWorldCollisionManifold();
        manifold.a = a;
        manifold.b = b;
        manifold.contactsCount = 1;
        manifold.normal = new Vector2();
        if (distance != 0) {
            manifold.depth = radiusSum - distance;
            manifold.normal.set(dx, dy).scl(1.0f / distance);
        } else {
            manifold.depth = c1.worldRadius;
            manifold.normal.set(1, 0);
        }

        manifold.contactPoint1 = new Vector2(manifold.normal).scl(c1.worldRadius).add(c1.worldCenter);


        manifolds.add(manifold);
    }

    private static boolean circleVsMorphed(Shape2DCircle circle, Shape2DMorphed morphed, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean circleVsPolygon(Shape2DCircle circle, Shape2DPolygon polygon, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean circleVsRectangle(Shape2DCircle circle, Shape2DRectangle rectangle, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    /** Morphed vs ___ **/
    private static boolean morphedVsAABB(Shape2DMorphed morphed, Shape2DAABB aabb, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean morphedVsCircle(Shape2DMorphed morphed, Shape2DCircle circle, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean morphedVsMorphed(Shape2DMorphed morphed1, Shape2DMorphed morphed2, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean morphedVsPolygon(Shape2DMorphed morphed, Shape2DPolygon polygon, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean morphedVsRectangle(Shape2DMorphed morphed, Shape2DRectangle rectangle, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    /** Polygon vs ____ **/
    private static boolean polygonVsAABB(Shape2DPolygon polygon, Shape2DAABB aabb, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean polygonVsCircle(Shape2DPolygon polygon, Shape2DCircle circle, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean polygonVsMorphed(Shape2DPolygon polygon, Shape2DMorphed morphed, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean polygonVsPolygon(Shape2DPolygon p1, Shape2DPolygon p2, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean polygonVsRectangle(Shape2DPolygon polygon, Shape2DRectangle rectangle, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    /** Rectangle vs ____ **/
    private static boolean rectangleVsAABB(Shape2DRectangle rectangle, Shape2DAABB aabb, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean rectangleVsCircle(Shape2DRectangle rectangle, Shape2DCircle circle, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean rectangleVsMorphed(Shape2DRectangle rectangle, Shape2DMorphed morphed, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean rectangleVsPolygon(Shape2DRectangle rectangle, Shape2DPolygon polygon, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean rectangleVsRectangle(Shape2DRectangle r1, Shape2DRectangle r2, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

}
