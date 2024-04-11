package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.*;
import org.example.engine.core.shape.*;

public final class Physics2DWorldCollisionDetection {

    private Physics2DWorldCollisionDetection() {}

    public static boolean broadPhaseCollision(final Shape2D a, final Shape2D b) {
        final float dx = b.x() - a.x();
        final float dy = b.y() - a.y();
        final float sum = a.getBoundingRadius() + b.getBoundingRadius();
        return dx * dx + dy * dy < sum * sum;
    }

    public static void narrowPhaseCollision(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
        // circle vs ____ //
        if (a.shape instanceof Shape2DCircle && b.shape instanceof Shape2DCircle) circleVsCircle(a, b, manifolds);
        else if (a.shape instanceof Shape2DCircle && b.shape instanceof Shape2DAABB) circleVsAABB(a, b, manifolds);
        else if (a.shape instanceof Shape2DCircle && b.shape instanceof Shape2DRectangle) circleVsRectangle(a, b, manifolds);
    }

    /** AABB vs ____ **/
    private static boolean AABBvsAABB(Shape2DAABB a, Shape2DAABB b, Physics2DWorldCollisionManifold manifold) {
        if (a.worldMax.x < b.worldMin.x || a.worldMin.x > b.worldMax.x) return false;
        if (a.worldMax.y < b.worldMin.y || a.worldMin.y > b.worldMax.y) return false;

        return true;
    }

    private static void AABBvsCircle(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {

        System.out.println("check aabb circle");

        Shape2DAABB aabb = (Shape2DAABB) a.shape;
        Shape2DCircle circle = (Shape2DCircle) b.shape;
        MathVector2 centerPositiveQuadrant = new MathVector2(circle.worldCenter);
        centerPositiveQuadrant.x = Math.abs(centerPositiveQuadrant.x);
        centerPositiveQuadrant.y = Math.abs(centerPositiveQuadrant.y);

        MathVector2 cm_box = new MathVector2(aabb.worldMin).add(aabb.worldMax).scl(0.5f);
        MathVector2 cornerTopRight = new MathVector2(aabb.worldMax).sub(cm_box);

        MathVector2 c = new MathVector2(centerPositiveQuadrant).sub(cornerTopRight);
        c.x = Math.max(c.x, 0);
        c.y = Math.max(c.y, 0);

        if (c.len2() >= circle.worldRadius * circle.worldRadius) return;

        System.out.println("intersection");

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

    // TODO: continue
    /** Circle vs ____ **/
    private static void circleVsAABB(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
        Shape2DCircle circle = (Shape2DCircle) a.shape;
        Shape2DAABB aabb = (Shape2DAABB) b.shape;

        float eX = Math.max(0, aabb.worldMin.x - circle.worldCenter.x) + Math.max(0, circle.worldCenter.x - aabb.worldMax.x);
        if (eX > circle.worldRadius) return;

        float eY = Math.max(0, aabb.worldMin.y - circle.worldCenter.y) + Math.max(0, circle.worldCenter.y - aabb.worldMax.y);
        if (eY > circle.worldRadius) return;

        if (eX * eX + eY * eY > circle.worldRadius * circle.worldRadius) return;

        Physics2DWorldCollisionManifold manifold = new Physics2DWorldCollisionManifold();
        manifold.contactsCount = 1;
        manifold.normal = new MathVector2();
        manifold.contactPoint1 = new MathVector2();

        if (aabb.contains(circle.worldCenter)) {
            float dstASquared = MathVector2.dst2(circle.worldCenter.x, circle.worldCenter.y, aabb.worldMin.x, circle.worldCenter.y);
            float dstBSquared = MathVector2.dst2(circle.worldCenter.x, circle.worldCenter.y, circle.worldCenter.x, aabb.worldMax.y);
            float dstCSquared = MathVector2.dst2(circle.worldCenter.x, circle.worldCenter.y, aabb.worldMax.x, circle.worldCenter.y);
            float dstDSquared = MathVector2.dst2(circle.worldCenter.x, circle.worldCenter.y, circle.worldCenter.x, aabb.worldMin.y);
            float minDstSquared = MathUtils.min(dstASquared, dstBSquared, dstCSquared, dstDSquared);
            MathVector2 closest = new MathVector2();
            if (MathUtils.isEqual(minDstSquared, dstASquared)) closest.set(aabb.worldMin.x, circle.worldCenter.y);
            else if (MathUtils.isEqual(minDstSquared, dstBSquared)) closest.set(circle.worldCenter.x, aabb.worldMax.y);
            else if (MathUtils.isEqual(minDstSquared, dstCSquared)) closest.set(aabb.worldMax.x, circle.worldCenter.y);
            else if (MathUtils.isEqual(minDstSquared, dstDSquared)) closest.set(circle.worldCenter.x, aabb.worldMin.y);
            manifold.contactPoint1.set(closest);
            manifold.normal.set(circle.worldCenter).sub(closest).nor();
            manifold.depth = MathVector2.dst(circle.worldCenter, manifold.contactPoint1);
        } else {
            MathVector2 closest = new MathVector2(circle.worldCenter).clamp(aabb.worldMin, aabb.worldMax);
            manifold.contactPoint1.set(closest);
            manifold.depth = circle.worldRadius - MathVector2.dst(circle.worldCenter, manifold.contactPoint1);
            manifold.normal.set(circle.worldCenter).sub(closest).nor();
        }

        manifolds.add(manifold);
    }

    // TODO: modify to use manifold etc.
    private static void circleVsCircle(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
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
        manifold.normal = new MathVector2();
        if (distance != 0) {
            manifold.depth = radiusSum - distance;
            manifold.normal.set(dx, dy).scl(1.0f / distance);
        } else {
            manifold.depth = c1.worldRadius;
            manifold.normal.set(1, 0);
        }

        manifold.contactPoint1 = new MathVector2(manifold.normal).scl(c1.worldRadius).add(c1.worldCenter);


        manifolds.add(manifold);
    }

    private static boolean circleVsMorphed(Shape2DCircle circle, Shape2DMorphed morphed, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean circleVsPolygon(Shape2DCircle circle, Shape2DPolygon polygon, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static void circleVsRectangle(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
        Shape2DCircle circle = (Shape2DCircle) a.shape;
        Shape2DRectangle rect = (Shape2DRectangle) b.shape;

        MathVector2 circleWorldCenter = circle.worldCenter;
        float circleWorldRadius = circle.worldRadius;

        MathVector2 c1 = rect.c1();
        MathVector2 c2 = rect.c2();
        MathVector2 c3 = rect.c3();

        MathVector2 axis1 = new MathVector2(c2).sub(c1).rotate90(-1);

        float minExtentRect = MathVector2.dot(c1, axis1);
        float maxExtentRect = MathVector2.dot(c3, axis1);
        float minExtentCircle = MathVector2.dot(circleWorldCenter, axis1) - circleWorldRadius;
        float maxExtentCircle = MathVector2.dot(circleWorldCenter, axis1) + circleWorldRadius;

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
