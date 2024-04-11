package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.input.InputKeyboard;
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

        else if (a.shape instanceof Shape2DRectangle && b.shape instanceof Shape2DCircle) rectangleVsCircle(a, b, manifolds);
    }

    /** AABB vs ____ **/
    private static boolean AABBvsAABB(Shape2DAABB a, Shape2DAABB b, Physics2DWorldCollisionManifold manifold) {
        if (a.worldMax.x < b.worldMin.x || a.worldMin.x > b.worldMax.x) return false;
        if (a.worldMax.y < b.worldMin.y || a.worldMin.y > b.worldMax.y) return false;

        return true;
    }

    private static void AABBvsCircle(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {

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

    // TODO: fix penetration depth and normal direction.
    /** Circle vs ____ **/
    private static void circleVsAABB(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
        Shape2DCircle circle = (Shape2DCircle) a.shape;
        Shape2DAABB aabb = (Shape2DAABB) b.shape;

        MathVector2 circleWorldCenter = circle.getWorldCenter();
        float circleWorldRadius = circle.getWorldRadius();

        float eX = Math.max(0, aabb.worldMin.x - circleWorldCenter.x) + Math.max(0, circleWorldCenter.x - aabb.worldMax.x);
        if (eX > circleWorldRadius) return;

        float eY = Math.max(0, aabb.worldMin.y - circleWorldCenter.y) + Math.max(0, circleWorldCenter.y - aabb.worldMax.y);
        if (eY > circleWorldRadius) return;

        if (eX * eX + eY * eY > circleWorldRadius * circleWorldRadius) return;

        Physics2DWorldCollisionManifold manifold = new Physics2DWorldCollisionManifold();
        manifold.contactsCount = 1;
        manifold.normal = new MathVector2();
        manifold.contactPoint1 = new MathVector2();

        if (aabb.contains(circleWorldCenter)) {
            float dstASquared = MathVector2.dst2(circleWorldCenter.x, circleWorldCenter.y, aabb.worldMin.x, circleWorldCenter.y);
            float dstBSquared = MathVector2.dst2(circleWorldCenter.x, circleWorldCenter.y, circleWorldCenter.x, aabb.worldMax.y);
            float dstCSquared = MathVector2.dst2(circleWorldCenter.x, circleWorldCenter.y, aabb.worldMax.x, circleWorldCenter.y);
            float dstDSquared = MathVector2.dst2(circleWorldCenter.x, circleWorldCenter.y, circleWorldCenter.x, aabb.worldMin.y);
            float minDstSquared = MathUtils.min(dstASquared, dstBSquared, dstCSquared, dstDSquared);
            MathVector2 closest = new MathVector2();
            if (MathUtils.isEqual(minDstSquared, dstASquared)) closest.set(aabb.worldMin.x, circleWorldCenter.y);
            else if (MathUtils.isEqual(minDstSquared, dstBSquared)) closest.set(circleWorldCenter.x, aabb.worldMax.y);
            else if (MathUtils.isEqual(minDstSquared, dstCSquared)) closest.set(aabb.worldMax.x, circleWorldCenter.y);
            else if (MathUtils.isEqual(minDstSquared, dstDSquared)) closest.set(circleWorldCenter.x, aabb.worldMin.y);
            manifold.contactPoint1.set(closest);
            manifold.normal.set(closest).sub(circleWorldCenter).nor();
            manifold.depth = circleWorldRadius + MathVector2.dst(circleWorldCenter, manifold.contactPoint1);
        } else {
            MathVector2 closest = new MathVector2(circleWorldCenter).clamp(aabb.worldMin, aabb.worldMax);
            manifold.contactPoint1.set(closest);
            manifold.depth = circleWorldRadius - MathVector2.dst(circleWorldCenter, manifold.contactPoint1);
            manifold.normal.set(circleWorldCenter).sub(closest).nor();
        }

        manifolds.add(manifold);
    }

    private static void circleVsCircle(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
        Shape2DCircle c1 = (Shape2DCircle) a.shape;
        Shape2DCircle c2 = (Shape2DCircle) b.shape;
        final float dx = c2.x() - c1.x();
        final float dy = c2.y() - c1.y();
        final float radiusSum = c1.getWorldRadius() + c2.getWorldRadius();
        final float distanceSquared = dx * dx + dy * dy;

        if (distanceSquared > radiusSum * radiusSum) return;

        final float distance = (float) Math.sqrt(distanceSquared);

        Physics2DWorldCollisionManifold manifold = new Physics2DWorldCollisionManifold();
        manifold.a = a;
        manifold.b = b;
        manifold.contactsCount = 1;
        manifold.normal = new MathVector2();
        if (distance != 0) {
            manifold.depth = radiusSum - distance;
            manifold.normal.set(dx, dy).scl(-1.0f / distance);
        } else {
            manifold.depth = c1.getWorldRadius();
            manifold.normal.set(1, 0);
        }

        manifold.contactPoint1 = new MathVector2(manifold.normal).scl(-c1.getWorldRadius()).add(c1.getWorldCenter());

        manifolds.add(manifold);
    }

    private static boolean circleVsMorphed(Shape2DCircle circle, Shape2DMorphed morphed, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean circleVsPolygon(Shape2DCircle circle, Shape2DPolygon polygon, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

//    private static void circleVsRectangle(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
//        Shape2DCircle circle = (Shape2DCircle) a.shape;
//        Shape2DRectangle rect = (Shape2DRectangle) b.shape;
//
//        MathVector2 circleWorldCenter = circle.getWorldCenter();
//        float circleWorldRadius = circle.getWorldRadius();
//
//        MathVector2 c1 = rect.c1();
//        MathVector2 c2 = rect.c2();
//        MathVector2 c3 = rect.c3();
//        MathVector2 c4 = rect.c4();
//
//        float dx1 = c2.x - c1.x;
//        float dy1 = c2.y - c1.y;
//        float distance1 = MathVector2.dot(circleWorldCenter.x - c1.x, circleWorldCenter.y - c1.y, dx1, dy1) / MathVector2.len2(dx1, dy1);
//
//        float dx2 = c3.x - c2.x;
//        float dy2 = c3.y - c2.y;
//        float distance2 = MathVector2.dot(circleWorldCenter.x - c2.x, circleWorldCenter.y - c2.y, dx2, dy2) / MathVector2.len2(dx2, dy2);
//
//        float dx3 = c4.x - c3.x;
//        float dy3 = c4.y - c3.y;
//        float distance3 = MathVector2.dot(circleWorldCenter.x - c3.x, circleWorldCenter.y - c3.y, dx3, dy3) / MathVector2.len2(dx3, dy3);
//
//        float dx4 = c1.x - c4.x;
//        float dy4 = c1.y - c4.y;
//        float distance4 = MathVector2.dot(circleWorldCenter.x - c4.x, circleWorldCenter.y - c4.y, dx4, dy4) / MathVector2.len2(dx4, dy4);
//
//        float minDistance = MathUtils.min(distance1, distance2, distance3, distance4);
//
//        MathVector2 projection;
//        if (MathUtils.isEqual(minDistance, distance1)) {
//            projection = new MathVector2(dx4, dy4).scl(distance4).add(c4);
//        } else if (MathUtils.isEqual(minDistance, distance2)) {
//            projection = new MathVector2(dx1, dy1).scl(distance1).add(c1);
//        } else if (MathUtils.isEqual(minDistance, distance3)) {
//            projection = new MathVector2(dx2, dy2).scl(distance2).add(c2);
//        } else {
//            projection = new MathVector2(dx3, dy3).scl(distance3).add(c3);
//        }
//
//        final boolean rectContainsCenter = rect.contains(circleWorldCenter);
//        final boolean circleContainsEdge = circle.contains(projection);
//
//        if (circleContainsEdge) System.out.println("circle contains edge");
//
//        if (!rectContainsCenter) return;
//
//
//        Physics2DWorldCollisionManifold manifold = new Physics2DWorldCollisionManifold();
//        manifold.contactPoint1 = new MathVector2(projection);
//        if (rectContainsCenter) {
//            manifold.normal = new MathVector2(projection).sub(circleWorldCenter).nor();
//            manifold.depth = minDistance + circleWorldRadius;
//        }
//        manifolds.add(manifold);
//    }

    private static void circleVsRectangle(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
        Shape2DCircle circle = (Shape2DCircle) a.shape;
        Shape2DRectangle rect = (Shape2DRectangle) b.shape;

        MathVector2 circleWorldCenter = circle.getWorldCenter();
        float circleWorldRadius = circle.getWorldRadius();

        MathVector2 c1 = rect.c1();
        MathVector2 c2 = rect.c2();
        MathVector2 c3 = rect.c3();
        MathVector2 c4 = rect.c4();

        float dx1 = c2.x - c1.x;
        float dy1 = c2.y - c1.y;
        float scale1 = MathVector2.dot(circleWorldCenter.x - c1.x, circleWorldCenter.y - c1.y, dx1, dy1) / MathVector2.len2(dx1, dy1);
        MathVector2 projection1 = new MathVector2(dx1, dy1).scl(scale1).add(c1);

        float dx2 = c3.x - c2.x;
        float dy2 = c3.y - c2.y;
        float scale2 = MathVector2.dot(circleWorldCenter.x - c2.x, circleWorldCenter.y - c2.y, dx2, dy2) / MathVector2.len2(dx2, dy2);
        MathVector2 projection2 = new MathVector2(dx2, dy2).scl(scale2).add(c2);

        float dx3 = c4.x - c3.x;
        float dy3 = c4.y - c3.y;
        float scale3 = MathVector2.dot(circleWorldCenter.x - c3.x, circleWorldCenter.y - c3.y, dx3, dy3) / MathVector2.len2(dx3, dy3);
        MathVector2 projection3 = new MathVector2(dx3, dy3).scl(scale3).add(c3);

        float dx4 = c1.x - c4.x;
        float dy4 = c1.y - c4.y;
        float scale4 = MathVector2.dot(circleWorldCenter.x - c4.x, circleWorldCenter.y - c4.y, dx4, dy4) / MathVector2.len2(dx4, dy4);
        MathVector2 projection4 = new MathVector2(dx4, dy4).scl(scale4).add(c4);

        if (rect.contains(circleWorldCenter)) System.out.println("rect contains");
        if (circle.contains(c1)) System.out.println("circle contains proj 1");
        if (circle.contains(c2)) System.out.println("circle contains proj 2");
        if (circle.contains(c3)) System.out.println("circle contains proj 3");
        if (circle.contains(c4)) System.out.println("circle contains proj 4");

        Physics2DWorldCollisionManifold manifold = new Physics2DWorldCollisionManifold();
        manifold.contactPoint1 = new MathVector2(projection3);
        manifold.normal = new MathVector2(-10,10);
        manifold.depth = 0;
        manifolds.add(manifold);

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

    private static void rectangleVsCircle(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
        System.out.println("ggg");
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
