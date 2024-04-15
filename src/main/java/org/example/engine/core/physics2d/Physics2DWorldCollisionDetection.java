package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.*;
import org.example.engine.core.shape.*;

// TODO: SOLVED: contact points explained:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
// https://oercommons.s3.amazonaws.com/media/courseware/relatedresource/file/imth-6-1-9-6-1-coordinate_plane_plotter/index.html
public final class Physics2DWorldCollisionDetection {

    private Physics2DWorldCollisionDetection() {}

    public static boolean broadPhaseCollision(final Shape2D a, final Shape2D b) {
        final float dx = b.x() - a.x();
        final float dy = b.y() - a.y();
        final float sum = a.getBoundingRadius() + b.getBoundingRadius();
        return dx * dx + dy * dy < sum * sum;
    }

    public static void narrowPhaseCollision(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
        // TODO: "sort" if order by expected frequency
        // circle vs **** //
        if (a.shape instanceof Shape2DCircle) {
            if      (b.shape instanceof Shape2DCircle)    circleVsCircle(a,    b, manifolds);
            else if (b.shape instanceof Shape2DAABB)      circleVsAABB(a,      b, manifolds);
            else if (b.shape instanceof Shape2DRectangle) circleVsRectangle(a, b, manifolds);
            else if (b.shape instanceof Shape2DPolygon)   circleVsPolygon(a,   b, manifolds);
            return;
        }

        // AABB vs **** //
        if (a.shape instanceof Shape2DAABB) {
            if      (b.shape instanceof Shape2DCircle)    AABBvsCircle(a,      b, manifolds);
            else if (b.shape instanceof Shape2DAABB)      AABBvsAABB(a,        b, manifolds);
            else if (b.shape instanceof Shape2DRectangle) AABBvsRectangle(a, b, manifolds);
            else if (b.shape instanceof Shape2DPolygon)   circleVsPolygon(a,   b, manifolds);
            return;
        }
    }

    /** AABB vs ____ **/
    private static void AABBvsAABB(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
        Shape2DAABB aabb1 = (Shape2DAABB) a.shape;
        Shape2DAABB aabb2 = (Shape2DAABB) b.shape;

        MathVector2 a_min = aabb1.getWorldMin();
        MathVector2 a_max = aabb1.getWorldMax();
        MathVector2 b_min = aabb2.getWorldMin();
        MathVector2 b_max = aabb2.getWorldMax();

        if (a_min.x > b_max.x || a_max.x < b_min.x || a_min.y > b_max.y || a_max.y < b_min.y) return; // no collision

        float x_overlap = MathUtils.intervalsOverlap(a_min.x, a_max.x, b_min.x, b_max.x);
        float y_overlap = MathUtils.intervalsOverlap(a_min.y, a_max.y, b_min.y, b_max.y);

        Physics2DWorldCollisionManifold manifold = new Physics2DWorldCollisionManifold();
        manifold.depth = Math.min(x_overlap, y_overlap);
        manifold.contactsCount = 2;
        if (x_overlap < y_overlap) {
            manifold.normal = new MathVector2(1,0);
            float left = Math.max(a_min.x, b_min.x);
            float right = Math.min(a_max.x, b_max.x);
            float top = Math.min(a_max.y, b_max.y);
            manifold.contactPoint1 = new MathVector2(left, top);
            manifold.contactPoint2 = new MathVector2(right, top);
        } else {
            manifold.normal = new MathVector2(0,1);
            float left = Math.max(a_min.x, b_min.x);
            float top = Math.min(a_max.y, b_max.y);
            float bottom = Math.max(a_min.y, b_min.y);
            manifold.contactPoint1 = new MathVector2(left, top);
            manifold.contactPoint2 = new MathVector2(left, bottom);
        }
        manifolds.add(manifold);
    }

    private static void AABBvsCircle(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
        circleVsAABB(b, a, manifolds);
    }

    private static boolean AABBvsMorphed(Shape2DAABB aabb, Shape2DComposite morphed, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean AABBvsPolygon(Shape2DAABB aabb, Shape2DPolygon polygon, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static void AABBvsRectangle(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
        Shape2DAABB aabb = (Shape2DAABB) a.shape;
        Shape2DRectangle rect = (Shape2DRectangle) b.shape;

        // aabb corners
        MathVector2 aabb_min = aabb.getWorldMin();
        MathVector2 aabb_max = aabb.getWorldMax();

        // rect corners
        MathVector2 c1 = rect.c1();
        MathVector2 c2 = rect.c2();
        MathVector2 c3 = rect.c3();
        MathVector2 c4 = rect.c4();

        // SAT - X axis
        float aabb_min_x = aabb_min.x;
        float aabb_max_x = aabb_max.x;
        float rect_min_x = MathUtils.min(c1.x, c2.x, c3.x, c4.x);
        float rect_max_x = MathUtils.max(c1.x, c2.x, c3.x, c4.x);
        float x_overlap = MathUtils.intervalsOverlap(aabb_min_x, aabb_max_x, rect_min_x, rect_max_x);
        if (MathUtils.isZero(x_overlap)) return; // no collision

        // SAT - Y axis
        float aabb_min_y = aabb_min.y;
        float aabb_max_y = aabb_max.y;
        float rect_min_y = MathUtils.min(c1.y, c2.y, c3.y, c4.y);
        float rect_max_y = MathUtils.max(c1.y, c2.y, c3.y, c4.y);
        float y_overlap = MathUtils.intervalsOverlap(aabb_min_y, aabb_max_y, rect_min_y, rect_max_y);
        if (MathUtils.isZero(y_overlap)) return; // no collision

        // TODO: fix
        // aabb projections onto axis1:
        MathVector2 axis1 = new MathVector2(c3).sub(c2).nor();
        float aabb_c1_axis1 = MathVector2.dot(aabb_min.x - c2.x, aabb_max.y - c2.y, axis1.x, axis1.y);
        float aabb_c2_axis1 = MathVector2.dot(aabb_min.x - c2.x, aabb_min.y - c2.y, axis1.x, axis1.y);
        float aabb_c3_axis1 = MathVector2.dot(aabb_max.x - c2.x, aabb_max.y - c2.y, axis1.x, axis1.y);
        float aabb_c4_axis1 = MathVector2.dot(aabb_max.x - c2.x, aabb_max.y - c2.y, axis1.x, axis1.y);
        float aabb_min_axis1 = MathUtils.min(aabb_c1_axis1, aabb_c2_axis1, aabb_c3_axis1, aabb_c4_axis1);
        float aabb_max_axis1 = MathUtils.max(aabb_c1_axis1, aabb_c2_axis1, aabb_c3_axis1, aabb_c4_axis1);
        float axis1_overlap = MathUtils.intervalsOverlap(aabb_min_axis1, aabb_max_axis1, 0, rect.unscaledWidth * rect.scaleX());
        if (MathUtils.isZero(axis1_overlap)) return; // no collision

        // TODO: fix
        MathVector2 axis2 = new MathVector2(c4).sub(c3).nor();
        float aabb_c1_axis2 = MathVector2.dot(aabb_min.x - c3.x, aabb_max.y - c3.y, axis2.x, axis2.y);
        float aabb_c2_axis2 = MathVector2.dot(aabb_min.x - c3.x, aabb_min.y - c3.y, axis2.x, axis2.y);
        float aabb_c3_axis2 = MathVector2.dot(aabb_max.x - c3.x, aabb_max.y - c3.y, axis2.x, axis2.y);
        float aabb_c4_axis2 = MathVector2.dot(aabb_max.x - c3.x, aabb_max.y - c3.y, axis2.x, axis2.y);
        float aabb_min_axis2 = MathUtils.min(aabb_c1_axis2, aabb_c2_axis2, aabb_c3_axis2, aabb_c4_axis2);
        float aabb_max_axis2 = MathUtils.max(aabb_c1_axis2, aabb_c2_axis2, aabb_c3_axis2, aabb_c4_axis2);
        float axis2_overlap = MathUtils.intervalsOverlap(aabb_min_axis2, aabb_max_axis2, 0, rect.unscaledHeight * rect.scaleY());
        if (MathUtils.isZero(axis2_overlap)) return; // no collision

        System.out.println("vs");
    }

    // TODO: fix penetration depth and normal direction.
    /** Circle vs ____ **/
    private static void circleVsAABB(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
        Shape2DCircle circle = (Shape2DCircle) a.shape;
        Shape2DAABB aabb = (Shape2DAABB) b.shape;

        MathVector2 circleWorldCenter = circle.getWorldCenter();
        float circleWorldRadius = circle.getWorldRadius();

        MathVector2 worldMin = aabb.getWorldMin();
        MathVector2 worldMax = aabb.getWorldMax();

        float eX = Math.max(0, worldMin.x - circleWorldCenter.x) + Math.max(0, circleWorldCenter.x - worldMax.x);
        if (eX > circleWorldRadius) return;

        float eY = Math.max(0, worldMin.y - circleWorldCenter.y) + Math.max(0, circleWorldCenter.y - worldMax.y);
        if (eY > circleWorldRadius) return;

        if (eX * eX + eY * eY > circleWorldRadius * circleWorldRadius) return;

        Physics2DWorldCollisionManifold manifold = new Physics2DWorldCollisionManifold();
        manifold.contactsCount = 1;
        manifold.normal = new MathVector2();
        manifold.contactPoint1 = new MathVector2();

        if (aabb.contains(circleWorldCenter)) {
            float dstASquared = MathVector2.dst2(circleWorldCenter.x, circleWorldCenter.y, worldMin.x, circleWorldCenter.y);
            float dstBSquared = MathVector2.dst2(circleWorldCenter.x, circleWorldCenter.y, circleWorldCenter.x, worldMax.y);
            float dstCSquared = MathVector2.dst2(circleWorldCenter.x, circleWorldCenter.y, worldMax.x, circleWorldCenter.y);
            float dstDSquared = MathVector2.dst2(circleWorldCenter.x, circleWorldCenter.y, circleWorldCenter.x, worldMin.y);
            float minDstSquared = MathUtils.min(dstASquared, dstBSquared, dstCSquared, dstDSquared);
            MathVector2 closest = new MathVector2();
            if (MathUtils.isEqual(minDstSquared, dstASquared)) closest.set(worldMin.x, circleWorldCenter.y);
            else if (MathUtils.isEqual(minDstSquared, dstBSquared)) closest.set(circleWorldCenter.x, worldMax.y);
            else if (MathUtils.isEqual(minDstSquared, dstCSquared)) closest.set(worldMax.x, circleWorldCenter.y);
            else if (MathUtils.isEqual(minDstSquared, dstDSquared)) closest.set(circleWorldCenter.x, worldMin.y);
            manifold.contactPoint1.set(closest);
            manifold.normal.set(closest).sub(circleWorldCenter).nor();
            manifold.depth = circleWorldRadius + MathVector2.dst(circleWorldCenter, manifold.contactPoint1);
        } else {
            MathVector2 closest = new MathVector2(circleWorldCenter).clamp(worldMin, worldMax);
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

    private static boolean circleVsMorphed(Shape2DCircle circle, Shape2DComposite morphed, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static void circleVsPolygon(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
        Shape2DCircle circle = (Shape2DCircle) a.shape;
        Shape2DPolygon polygon = (Shape2DPolygon) b.shape;

        MathVector2 circleWorldCenter = circle.getWorldCenter();
        CollectionsArray<MathVector2> projections = new CollectionsArray<>(false, polygon.vertexCount);
        int closestProjectionIndex = 0;
        float minDistanceSquared = Float.MAX_VALUE;
        for (int i = 0; i < polygon.vertexCount; i++) {
            MathVector2 tail = new MathVector2();
            MathVector2 head = new MathVector2();
            polygon.getWorldEdge(i, tail, head);
            float dx = head.x - tail.x;
            float dy = head.y - tail.y;

            if (MathUtils.isZero(dx) && MathUtils.isZero(dy)) return;
            float scale1 = MathVector2.dot(circleWorldCenter.x - tail.x, circleWorldCenter.y - tail.y, dx, dy) / MathVector2.len2(dx, dy);
            MathVector2 projection = new MathVector2(dx, dy).scl(scale1).add(tail);
            projection.clamp(tail, head);
            projections.add(projection);

            float distance = MathVector2.dst2(projection, circleWorldCenter);
            if (distance < minDistanceSquared) {
                minDistanceSquared = distance;
                closestProjectionIndex = i;
            }
        }

        boolean collide = false;
        boolean polygonContainsCenter = polygon.contains(circleWorldCenter);
        if (polygonContainsCenter) collide = true;
        for (MathVector2 projection : projections) {
            if (circle.contains(projection)) {
                collide = true;
                break;
            }
        }
        if (!collide) return;

        // build manifold
        Physics2DWorldCollisionManifold manifold = new Physics2DWorldCollisionManifold();
        MathVector2 projection = projections.get(closestProjectionIndex);
        manifold.contactPoint1 = new MathVector2(projection);
        final float minDstEdge = (float) Math.sqrt(minDistanceSquared);
        if (polygonContainsCenter) {
            manifold.normal = new MathVector2(projection).sub(circleWorldCenter).nor();
            manifold.depth = minDstEdge + circle.getWorldRadius();
        } else {
            manifold.normal = new MathVector2(circleWorldCenter).sub(projection).nor();
            manifold.depth = circle.getWorldRadius() - minDstEdge;
        }
        manifolds.add(manifold);
    }

    private static void circleVsRectangle(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorldCollisionManifold> manifolds) {
        Shape2DCircle circle = (Shape2DCircle) a.shape;
        Shape2DRectangle rect = (Shape2DRectangle) b.shape;

        MathVector2 circleWorldCenter = circle.getWorldCenter();
        MathVector2 c1 = rect.c1();
        MathVector2 c2 = rect.c2();
        MathVector2 c3 = rect.c3();
        MathVector2 c4 = rect.c4();

        float dx1 = c2.x - c1.x;
        float dy1 = c2.y - c1.y;
        if (MathUtils.isZero(dx1) && MathUtils.isZero(dy1)) return;
        float scale1 = MathVector2.dot(circleWorldCenter.x - c1.x, circleWorldCenter.y - c1.y, dx1, dy1) / MathVector2.len2(dx1, dy1);
        MathVector2 projection1 = new MathVector2(dx1, dy1).scl(scale1).add(c1);
        projection1.clamp(c1, c2);

        float dx2 = c3.x - c2.x;
        float dy2 = c3.y - c2.y;
        if (MathUtils.isZero(dx2) && MathUtils.isZero(dy2)) return;
        float scale2 = MathVector2.dot(circleWorldCenter.x - c2.x, circleWorldCenter.y - c2.y, dx2, dy2) / MathVector2.len2(dx2, dy2);
        MathVector2 projection2 = new MathVector2(dx2, dy2).scl(scale2).add(c2);
        projection2.clamp(c2, c3);

        float dx3 = c4.x - c3.x;
        float dy3 = c4.y - c3.y;
        if (MathUtils.isZero(dx3) && MathUtils.isZero(dy3)) return;
        float scale3 = MathVector2.dot(circleWorldCenter.x - c3.x, circleWorldCenter.y - c3.y, dx3, dy3) / MathVector2.len2(dx3, dy3);
        MathVector2 projection3 = new MathVector2(dx3, dy3).scl(scale3).add(c3);
        projection3.clamp(c3, c4);

        float dx4 = c1.x - c4.x;
        float dy4 = c1.y - c4.y;
        if (MathUtils.isZero(dx4) && MathUtils.isZero(dy4)) return;
        float scale4 = MathVector2.dot(circleWorldCenter.x - c4.x, circleWorldCenter.y - c4.y, dx4, dy4) / MathVector2.len2(dx4, dy4);
        MathVector2 projection4 = new MathVector2(dx4, dy4).scl(scale4).add(c4);
        projection4.clamp(c4, c1);

        final boolean rectContainsCenter = rect.contains(circleWorldCenter);
        final boolean circleContainsEdge1 = circle.contains(projection1);
        final boolean circleContainsEdge2 = circle.contains(projection2);
        final boolean circleContainsEdge3 = circle.contains(projection3);
        final boolean circleContainsEdge4 = circle.contains(projection4);

        final boolean collide = rectContainsCenter || circleContainsEdge1 || circleContainsEdge2
                || circleContainsEdge3 || circleContainsEdge4;
        if (!collide) return;

        // create manifold
        final float dstEdge1Squared = MathVector2.dst2(circleWorldCenter, projection1);
        final float dstEdge2Squared = MathVector2.dst2(circleWorldCenter, projection2);
        final float dstEdge3Squared = MathVector2.dst2(circleWorldCenter, projection3);
        final float dstEdge4Squared = MathVector2.dst2(circleWorldCenter, projection4);

        final float minDstEdgeSquared = MathUtils.min(dstEdge1Squared, dstEdge2Squared, dstEdge3Squared, dstEdge4Squared);

        MathVector2 projection;
        if (MathUtils.isEqual(minDstEdgeSquared, dstEdge1Squared)) {
            projection = projection1;
        } else if (MathUtils.isEqual(minDstEdgeSquared, dstEdge2Squared)) {
            projection = projection2;
        } else if (MathUtils.isEqual(minDstEdgeSquared, dstEdge3Squared)) {
            projection = projection3;
        } else {
            projection = projection4;
        }

        Physics2DWorldCollisionManifold manifold = new Physics2DWorldCollisionManifold();
        manifold.contactPoint1 = new MathVector2(projection);
        final float minDstEdge = (float) Math.sqrt(minDstEdgeSquared);
        if (rectContainsCenter) {
            manifold.normal = new MathVector2(projection).sub(circleWorldCenter).nor();
            manifold.depth = minDstEdge + circle.getWorldRadius();
        } else {
            manifold.normal = new MathVector2(circleWorldCenter).sub(projection).nor();
            manifold.depth = circle.getWorldRadius() - minDstEdge;
        }
        manifolds.add(manifold);
    }

    /** Morphed vs ___ **/
    private static boolean morphedVsAABB(Shape2DComposite morphed, Shape2DAABB aabb, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean morphedVsCircle(Shape2DComposite morphed, Shape2DCircle circle, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean morphedVsMorphed(Shape2DComposite morphed1, Shape2DComposite morphed2, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean morphedVsPolygon(Shape2DComposite morphed, Shape2DPolygon polygon, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean morphedVsRectangle(Shape2DComposite morphed, Shape2DRectangle rectangle, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    /** Polygon vs ____ **/
    private static boolean polygonVsAABB(Shape2DPolygon polygon, Shape2DAABB aabb, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean polygonVsCircle(Shape2DPolygon polygon, Shape2DCircle circle, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean polygonVsMorphed(Shape2DPolygon polygon, Shape2DComposite morphed, Physics2DWorldCollisionManifold manifold) {

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

    private static boolean rectangleVsMorphed(Shape2DRectangle rectangle, Shape2DComposite morphed, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean rectangleVsPolygon(Shape2DRectangle rectangle, Shape2DPolygon polygon, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

    private static boolean rectangleVsRectangle(Shape2DRectangle r1, Shape2DRectangle r2, Physics2DWorldCollisionManifold manifold) {

        return false;
    }

}
