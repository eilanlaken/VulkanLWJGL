package org.example.engine.core.physics2d;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class Physics2DWorldPhaseD {

    private final MemoryPool<Projection> projectionsPool = new MemoryPool<>(Projection.class, 200);
    private final Physics2DWorld         world;

    Physics2DWorldPhaseD(Physics2DWorld world) {
        this.world = world;
    }

    public void update() {
        Set<Physics2DWorld.CollisionPair> collisionPairs = world.collisionCandidates;
        for (Physics2DWorld.CollisionPair collisionCandidate : collisionPairs) {
            narrowPhaseCollision(world, collisionCandidate.a, collisionCandidate.b, world.collisionManifolds);
        }
    }

    private void narrowPhaseCollision(Physics2DWorld world, Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {
        Shape2D shape_a = a.shape;
        Shape2D shape_b = b.shape;

        Physics2DWorld.CollisionManifold manifold = null;

        if (shape_a instanceof Shape2DCircle) {
            if (shape_b instanceof Shape2DCircle) manifold = circleVsCircle(shape_a, shape_b, world);
            else if (shape_b instanceof Shape2DRectangle) manifold = circleVsRectangle(shape_a, shape_b, world);
        }

        else if (shape_a instanceof Shape2DRectangle) {
            if (shape_b instanceof Shape2DRectangle) manifold = rectangleVsRectangle(shape_a, shape_b, world);
            else if (shape_b instanceof Shape2DCircle) manifold = rectangleVsCircle(shape_a, shape_b, world);
        }

        if (manifold == null) return;

        manifold.a = a;
        manifold.b = b;
        manifolds.add(manifold);
    }

    /** AABB vs ____ **/
    private void AABBvsAABB(Shape2DAABB aabb1, Shape2DAABB aabb2, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {
        MathVector2 a_min = aabb1.getWorldMin();
        MathVector2 a_max = aabb1.getWorldMax();
        MathVector2 b_min = aabb2.getWorldMin();
        MathVector2 b_max = aabb2.getWorldMax();

        if (a_min.x > b_max.x || a_max.x < b_min.x || a_min.y > b_max.y || a_max.y < b_min.y) return; // no collision

        float x_overlap = MathUtils.intervalsOverlap(a_min.x, a_max.x, b_min.x, b_max.x);
        float y_overlap = MathUtils.intervalsOverlap(a_min.y, a_max.y, b_min.y, b_max.y);

        Physics2DWorld.CollisionManifold manifold = new Physics2DWorld.CollisionManifold();
        manifold.depth = Math.min(x_overlap, y_overlap);
        manifold.contacts = 2;
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

    private void AABBvsCircle(Shape2DAABB aabb, Shape2DCircle circle, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {
        circleVsAABB(circle, aabb, manifolds);
    }

    private boolean AABBvsComplex(Shape2DAABB aabb, Shape2DUnion union, Physics2DWorld.CollisionManifold manifold) {

        return false;
    }

    private void AABBvsPolygon(Shape2DAABB aabb, Shape2DPolygon polygon, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {
        // SAT - AABB normals (x & y axis)
        float aabb_min_x = aabb.getWorldMin().x;
        float aabb_max_x = aabb.getWorldMax().x;
        float aabb_min_y = aabb.getWorldMin().y;
        float aabb_max_y = aabb.getWorldMax().y;
        float polygon_min_x = Float.MAX_VALUE;
        float polygon_max_x = -Float.MAX_VALUE;
        float polygon_min_y = Float.MAX_VALUE;
        float polygon_max_y = -Float.MAX_VALUE;
        for (MathVector2 vertex : polygon.worldVertices()) {
            float x = vertex.x;
            float y = vertex.y;
            if (x > polygon_max_x) polygon_max_x = x;
            if (x < polygon_min_x) polygon_min_x = x;
            if (y > polygon_max_y) polygon_max_y = y;
            if (y < polygon_min_y) polygon_min_y = y;
        }
        float x_overlap = MathUtils.intervalsOverlap(aabb_min_x, aabb_max_x, polygon_min_x, polygon_max_x);
        if (MathUtils.isZero(x_overlap)) return; // no collision
        float y_overlap = MathUtils.intervalsOverlap(aabb_min_y, aabb_max_y, polygon_min_y, polygon_max_y);
        if (MathUtils.isZero(y_overlap)) return; // no collision

        // SAT - polygon normals
        // AABB corners
        CollectionsArray<MathVector2> aabb_vertices = aabb.worldVertices();
        MathVector2 aabb_c0 = aabb_vertices.get(0);
        MathVector2 aabb_c1 = aabb_vertices.get(1);
        MathVector2 aabb_c2 = aabb_vertices.get(2);
        MathVector2 aabb_c3 = aabb_vertices.get(3);
        MathVector2 tail = new MathVector2();
        MathVector2 head = new MathVector2();
        MathVector2 normal = new MathVector2();
        float minOverlapPolygon = Float.MAX_VALUE;
        MathVector2 minOverlapPolygonAxis = new MathVector2();
        for (int i = 0; i < polygon.vertexCount; i++) {
            polygon.getWorldEdge(i, tail, head);
            normal.set(head).sub(tail).nor().rotate90(1);
            // project aabb on the axis
            float dot_c0 = MathVector2.dot(normal.x, normal.y, aabb_c0.x - tail.x, aabb_c0.y - tail.y);
            float dot_c1 = MathVector2.dot(normal.x, normal.y, aabb_c1.x - tail.x, aabb_c1.y - tail.y);
            float dot_c2 = MathVector2.dot(normal.x, normal.y, aabb_c2.x - tail.x, aabb_c2.y - tail.y);
            float dot_c3 = MathVector2.dot(normal.x, normal.y, aabb_c3.x - tail.x, aabb_c3.y - tail.y);
            float aabb_axis_min = MathUtils.min(dot_c0, dot_c1, dot_c2, dot_c3);
            float aabb_axis_max = MathUtils.max(dot_c0, dot_c1, dot_c2, dot_c3);
            // project polygon on the axis
            float polygon_axis_min = Float.MAX_VALUE;
            float polygon_axis_max = -Float.MAX_VALUE;
            CollectionsArray<MathVector2> worldVertices = polygon.worldVertices();
            for (MathVector2 vertex : worldVertices) {
                float dot = MathVector2.dot(normal.x, normal.y, vertex.x - tail.x, vertex.y - tail.y);
                if (dot < polygon_axis_min) polygon_axis_min = dot;
                if (dot > polygon_axis_max) polygon_axis_max = dot;
            }

            float axis_overlap = MathUtils.intervalsOverlap(aabb_axis_min, aabb_axis_max, polygon_axis_min, polygon_axis_max);
            if (MathUtils.isZero(axis_overlap)) return;

            if (minOverlapPolygon > axis_overlap) {
                minOverlapPolygon = axis_overlap;
                minOverlapPolygonAxis.set(normal);
            }
        }

        Physics2DWorld.CollisionManifold manifold = new Physics2DWorld.CollisionManifold();
        setContactPoints(aabb.worldVertices(), polygon.worldVertices(), manifold);
        float min_overlap = MathUtils.min(x_overlap, y_overlap, minOverlapPolygon);
        if (MathUtils.floatsEqual(min_overlap, x_overlap)) manifold.normal = new MathVector2(1,0);
        else if (MathUtils.floatsEqual(min_overlap, y_overlap)) manifold.normal = new MathVector2(0,1);
        else manifold.normal = new MathVector2(minOverlapPolygonAxis);
        manifold.depth = min_overlap;
        manifolds.add(manifold);
    }

    private void AABBvsRectangle(Shape2DAABB aabb, Shape2DRectangle rect, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {
        // aabb corners
        MathVector2 aabb_min = aabb.getWorldMin();
        MathVector2 aabb_max = aabb.getWorldMax();

        // rect corners
        MathVector2 c0 = rect.c0();
        MathVector2 c1 = rect.c1();
        MathVector2 c2 = rect.c2();
        MathVector2 c3 = rect.c3();

        // SAT - x axis
        float aabb_min_x = aabb_min.x;
        float aabb_max_x = aabb_max.x;
        float rect_min_x = MathUtils.min(c0.x, c1.x, c2.x, c3.x);
        float rect_max_x = MathUtils.max(c0.x, c1.x, c2.x, c3.x);
        float x_overlap = MathUtils.intervalsOverlap(aabb_min_x, aabb_max_x, rect_min_x, rect_max_x);
        if (MathUtils.isZero(x_overlap)) return; // no collision

        // SAT - y axis
        float aabb_min_y = aabb_min.y;
        float aabb_max_y = aabb_max.y;
        float rect_min_y = MathUtils.min(c0.y, c1.y, c2.y, c3.y);
        float rect_max_y = MathUtils.max(c0.y, c1.y, c2.y, c3.y);
        float y_overlap = MathUtils.intervalsOverlap(aabb_min_y, aabb_max_y, rect_min_y, rect_max_y);
        if (MathUtils.isZero(y_overlap)) return; // no collision

        // c1-c2 axis
        float ax = c2.x - c1.x;
        float ay = c2.y - c1.y;
        float aLen = MathVector2.len(ax, ay);
        float aabb_c0_axis1 = MathVector2.dot(ax, ay, aabb_min.x - c1.x, aabb_max.y - c1.y) / aLen;
        float aabb_c1_axis1 = MathVector2.dot(ax, ay, aabb_min.x - c1.x, aabb_min.y - c1.y) / aLen;
        float aabb_c2_axis1 = MathVector2.dot(ax, ay, aabb_max.x - c1.x, aabb_min.y - c1.y) / aLen;
        float aabb_c3_axis1 = MathVector2.dot(ax, ay, aabb_max.x - c1.x, aabb_max.y - c1.y) / aLen;
        float aabb_min_axis1 = MathUtils.min(aabb_c0_axis1, aabb_c1_axis1, aabb_c2_axis1, aabb_c3_axis1);
        float aabb_max_axis1 = MathUtils.max(aabb_c0_axis1, aabb_c1_axis1, aabb_c2_axis1, aabb_c3_axis1);
        float axis1_overlap = MathUtils.intervalsOverlap(aabb_min_axis1, aabb_max_axis1, 0, rect.unscaledWidth * rect.scaleX());
        if (MathUtils.isZero(axis1_overlap)) return; // no collision

        // c2-c3 axis
        float bx = c3.x - c2.x;
        float by = c3.y - c2.y;
        float bLen = MathVector2.len(bx, by);
        float aabb_c0_axis2 = MathVector2.dot(bx, by, aabb_min.x - c2.x, aabb_max.y - c2.y) / bLen;
        float aabb_c1_axis2 = MathVector2.dot(bx, by, aabb_min.x - c2.x, aabb_min.y - c2.y) / bLen;
        float aabb_c2_axis2 = MathVector2.dot(bx, by, aabb_max.x - c2.x, aabb_min.y - c2.y) / bLen;
        float aabb_c3_axis2 = MathVector2.dot(bx, by, aabb_max.x - c2.x, aabb_max.y - c2.y) / bLen;
        float aabb_min_axis2 = MathUtils.min(aabb_c0_axis2, aabb_c1_axis2, aabb_c2_axis2, aabb_c3_axis2);
        float aabb_max_axis2 = MathUtils.max(aabb_c0_axis2, aabb_c1_axis2, aabb_c2_axis2, aabb_c3_axis2);
        float axis2_overlap = MathUtils.intervalsOverlap(aabb_min_axis2, aabb_max_axis2, 0, rect.unscaledHeight * rect.scaleY());
        if (MathUtils.isZero(axis2_overlap)) return; // no collision

        // TODO: see if correct.
        // TODO: fix normal direction.
        Physics2DWorld.CollisionManifold manifold = new Physics2DWorld.CollisionManifold();
        setContactPoints(aabb.worldVertices(), rect.worldVertices(), manifold);
        float min_overlap = MathUtils.min(x_overlap, y_overlap, axis1_overlap, axis2_overlap);
        if (MathUtils.floatsEqual(min_overlap, x_overlap)) manifold.normal = new MathVector2(1,0);
        else if (MathUtils.floatsEqual(min_overlap, y_overlap)) manifold.normal = new MathVector2(0,1);
        else if (MathUtils.floatsEqual(min_overlap, axis1_overlap)) manifold.normal = new MathVector2(ax, ay).nor();
        else manifold.normal = new MathVector2(bx, by).nor();
        manifold.depth = min_overlap;
        manifolds.add(manifold);
    }

    /** Circle vs ____ **/
    private void circleVsAABB(Shape2DCircle circle, Shape2DAABB aabb, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {
        MathVector2 circleWorldCenter = circle.getWorldCenter();
        float circleWorldRadius = circle.getWorldRadius();

        MathVector2 worldMin = aabb.getWorldMin();
        MathVector2 worldMax = aabb.getWorldMax();

        float eX = Math.max(0, worldMin.x - circleWorldCenter.x) + Math.max(0, circleWorldCenter.x - worldMax.x);
        if (eX > circleWorldRadius) return;

        float eY = Math.max(0, worldMin.y - circleWorldCenter.y) + Math.max(0, circleWorldCenter.y - worldMax.y);
        if (eY > circleWorldRadius) return;

        if (eX * eX + eY * eY > circleWorldRadius * circleWorldRadius) return;

        Physics2DWorld.CollisionManifold manifold = new Physics2DWorld.CollisionManifold();
        manifold.contacts = 1;
        manifold.normal = new MathVector2();
        manifold.contactPoint1 = new MathVector2();

        if (aabb.contains(circleWorldCenter)) {
            float dstASquared = MathVector2.dst2(circleWorldCenter.x, circleWorldCenter.y, worldMin.x, circleWorldCenter.y);
            float dstBSquared = MathVector2.dst2(circleWorldCenter.x, circleWorldCenter.y, circleWorldCenter.x, worldMax.y);
            float dstCSquared = MathVector2.dst2(circleWorldCenter.x, circleWorldCenter.y, worldMax.x, circleWorldCenter.y);
            float dstDSquared = MathVector2.dst2(circleWorldCenter.x, circleWorldCenter.y, circleWorldCenter.x, worldMin.y);
            float minDstSquared = MathUtils.min(dstASquared, dstBSquared, dstCSquared, dstDSquared);
            MathVector2 closest = new MathVector2();
            if (MathUtils.floatsEqual(minDstSquared, dstASquared)) closest.set(worldMin.x, circleWorldCenter.y);
            else if (MathUtils.floatsEqual(minDstSquared, dstBSquared)) closest.set(circleWorldCenter.x, worldMax.y);
            else if (MathUtils.floatsEqual(minDstSquared, dstCSquared)) closest.set(worldMax.x, circleWorldCenter.y);
            else if (MathUtils.floatsEqual(minDstSquared, dstDSquared)) closest.set(circleWorldCenter.x, worldMin.y);
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

    private Physics2DWorld.CollisionManifold circleVsCircle(Shape2D a, Shape2D b, Physics2DWorld world) {
        final Shape2DCircle c1 = (Shape2DCircle) a;
        final Shape2DCircle c2 = (Shape2DCircle) b;

        final float dx = c2.x() - c1.x();
        final float dy = c2.y() - c1.y();
        final float radiusSum = c1.getWorldRadius() + c2.getWorldRadius();
        final float distanceSquared = dx * dx + dy * dy;

        if (distanceSquared > radiusSum * radiusSum) return null;

        Physics2DWorld.CollisionManifold manifold = world.manifoldMemoryPool.allocate();
        manifold.contacts = 1;
        final float distance = (float) Math.sqrt(distanceSquared);

        if (distance != 0) {
            manifold.depth = radiusSum - distance;
            manifold.normal.set(dx, dy).scl(-1.0f / distance);
        } else {
            manifold.depth = c1.getWorldRadius();
            manifold.normal.set(1, 0);
        }
        manifold.contactPoint1.set(manifold.normal).scl(-c1.getWorldRadius()).add(c1.getWorldCenter());
        return manifold;
    }

    private boolean circleVsUnion(Shape2DCircle circle, Shape2DUnion union, Physics2DWorld.CollisionManifold manifold) {

        return false;
    }

    private void circleVsPolygon(Shape2DCircle circle, Shape2DPolygon polygon, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {
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
        Physics2DWorld.CollisionManifold manifold = new Physics2DWorld.CollisionManifold();
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

    private Physics2DWorld.CollisionManifold circleVsRectangle(Shape2D a, Shape2D b, Physics2DWorld world) {
        Shape2DCircle circle = (Shape2DCircle) a;
        Shape2DRectangle rect = (Shape2DRectangle) b;

        MathVector2 circleWorldCenter = circle.getWorldCenter();
        MathVector2 c1 = rect.c0();
        MathVector2 c2 = rect.c1();
        MathVector2 c3 = rect.c2();
        MathVector2 c4 = rect.c3();

        float dx1 = c2.x - c1.x;
        float dy1 = c2.y - c1.y;
        if (MathUtils.isZero(dx1) && MathUtils.isZero(dy1)) return null;
        float scale1 = MathVector2.dot(circleWorldCenter.x - c1.x, circleWorldCenter.y - c1.y, dx1, dy1) / MathVector2.len2(dx1, dy1);
        MathVector2 projection1 = new MathVector2(dx1, dy1).scl(scale1).add(c1);
        projection1.clamp(c1, c2);

        float dx2 = c3.x - c2.x;
        float dy2 = c3.y - c2.y;
        if (MathUtils.isZero(dx2) && MathUtils.isZero(dy2)) return null;
        float scale2 = MathVector2.dot(circleWorldCenter.x - c2.x, circleWorldCenter.y - c2.y, dx2, dy2) / MathVector2.len2(dx2, dy2);
        MathVector2 projection2 = new MathVector2(dx2, dy2).scl(scale2).add(c2);
        projection2.clamp(c2, c3);

        float dx3 = c4.x - c3.x;
        float dy3 = c4.y - c3.y;
        if (MathUtils.isZero(dx3) && MathUtils.isZero(dy3)) return null;
        float scale3 = MathVector2.dot(circleWorldCenter.x - c3.x, circleWorldCenter.y - c3.y, dx3, dy3) / MathVector2.len2(dx3, dy3);
        MathVector2 projection3 = new MathVector2(dx3, dy3).scl(scale3).add(c3);
        projection3.clamp(c3, c4);

        float dx4 = c1.x - c4.x;
        float dy4 = c1.y - c4.y;
        if (MathUtils.isZero(dx4) && MathUtils.isZero(dy4)) return null;
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
        if (!collide) return null;

        // create manifold
        final float dstEdge1Squared = MathVector2.dst2(circleWorldCenter, projection1);
        final float dstEdge2Squared = MathVector2.dst2(circleWorldCenter, projection2);
        final float dstEdge3Squared = MathVector2.dst2(circleWorldCenter, projection3);
        final float dstEdge4Squared = MathVector2.dst2(circleWorldCenter, projection4);

        final float minDstEdgeSquared = MathUtils.min(dstEdge1Squared, dstEdge2Squared, dstEdge3Squared, dstEdge4Squared);

        MathVector2 projection;
        if (MathUtils.floatsEqual(minDstEdgeSquared, dstEdge1Squared)) {
            projection = projection1;
        } else if (MathUtils.floatsEqual(minDstEdgeSquared, dstEdge2Squared)) {
            projection = projection2;
        } else if (MathUtils.floatsEqual(minDstEdgeSquared, dstEdge3Squared)) {
            projection = projection3;
        } else {
            projection = projection4;
        }

        Physics2DWorld.CollisionManifold manifold = world.manifoldMemoryPool.allocate();
        manifold.contacts = 1;
        manifold.contactPoint1.set(projection);
        final float minDstEdge = (float) Math.sqrt(minDstEdgeSquared);
        if (rectContainsCenter) {
            manifold.normal.set(projection).sub(circleWorldCenter).nor();
            manifold.depth = minDstEdge + circle.getWorldRadius();
        } else {
            manifold.normal.set(circleWorldCenter).sub(projection).nor();
            manifold.depth = circle.getWorldRadius() - minDstEdge;
        }

        return manifold;
    }

    // TODO:
    /** Union vs ___ **/
    private void unionVsAABB(Shape2DUnion union, Shape2DAABB aabb, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {
        CollectionsArray<Physics2DWorld.CollisionManifold> individualShapeManifolds = new CollectionsArray<>(true, union.shapes.size);
        for (Shape2D shape : union.shapes) {

        }

        if (individualShapeManifolds.size == 0) return; // no collision

        // select the best manifold

        System.out.println("omadam");
    }

    private void unionVsCircle(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {

    }

    private void unionVsUnion(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {

    }

    private void unionVsPolygon(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {

    }

    private void unionVsRectangle(Physics2DBody a, Physics2DBody b, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {

    }

    /** Polygon vs ____ **/
    private void polygonVsAABB(Shape2DPolygon polygon, Shape2DAABB aabb, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {
        AABBvsPolygon(aabb, polygon, manifolds);
    }

    private void polygonVsCircle(Shape2DPolygon polygon, Shape2DCircle circle, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {
        circleVsPolygon(circle, polygon, manifolds);
    }

    private boolean polygonVsUnion(Shape2DPolygon polygon, Shape2DUnion union, Physics2DWorld.CollisionManifold manifold) {

        return false;
    }

    private void polygonVsPolygon(Shape2DPolygon p1, Shape2DPolygon p2, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {
        CollectionsArray<MathVector2> p1_vertices = p1.worldVertices();
        CollectionsArray<MathVector2> p2_vertices = p2.worldVertices();

        MathVector2 axis = new MathVector2();
        float depth = Float.MAX_VALUE;
        float normal_x = 0;
        float normal_y = 0;

        MathVector2 tail = new MathVector2();
        MathVector2 head = new MathVector2();
        // p1 edges normals as axis
        for (int i = 0; i < p1.vertexCount; i++) {
            p1.getWorldEdge(i, tail, head);
            axis.set(head).sub(tail).nor();
            // project p1 on the axis
            float min_p1_axis = Float.MAX_VALUE;
            float max_p1_axis = -Float.MAX_VALUE;
            for (MathVector2 vertex : p1_vertices) {
                float projection = MathVector2.dot(axis.x, axis.y, vertex.x - tail.x, vertex.y - tail.y);
                if (projection < min_p1_axis) min_p1_axis = projection;
                if (projection > max_p1_axis) max_p1_axis = projection;
            }
            // project p2 on the axis
            float min_p2_axis = Float.MAX_VALUE;
            float max_p2_axis = -Float.MAX_VALUE;
            for (MathVector2 vertex : p2_vertices) {
                float projection = MathVector2.dot(axis.x, axis.y, vertex.x - tail.x, vertex.y - tail.y);
                if (projection < min_p2_axis) min_p2_axis = projection;
                if (projection > max_p2_axis) max_p2_axis = projection;
            }

            float axis_overlap = MathUtils.intervalsOverlap(min_p1_axis, max_p1_axis, min_p2_axis, max_p2_axis);
            if (MathUtils.isZero(axis_overlap)) return;

            if (axis_overlap < depth) {
                depth = axis_overlap;
                normal_x = axis.x;
                normal_y = axis.y;
            }
        }

        // p1 edges normals as axis
        for (int i = 0; i < p2.vertexCount; i++) {
            p2.getWorldEdge(i, tail, head);
            axis.set(head).sub(tail).nor();
            // project p1 on the axis
            float min_p1_axis = Float.MAX_VALUE;
            float max_p1_axis = -Float.MAX_VALUE;
            for (MathVector2 vertex : p1_vertices) {
                float projection = MathVector2.dot(axis.x, axis.y, vertex.x - tail.x, vertex.y - tail.y);
                if (projection < min_p1_axis) min_p1_axis = projection;
                if (projection > max_p1_axis) max_p1_axis = projection;
            }
            // project p2 on the axis
            float min_p2_axis = Float.MAX_VALUE;
            float max_p2_axis = -Float.MAX_VALUE;
            for (MathVector2 vertex : p2_vertices) {
                float projection = MathVector2.dot(axis.x, axis.y, vertex.x - tail.x, vertex.y - tail.y);
                if (projection < min_p2_axis) min_p2_axis = projection;
                if (projection > max_p2_axis) max_p2_axis = projection;
            }

            float axis_overlap = MathUtils.intervalsOverlap(min_p1_axis, max_p1_axis, min_p2_axis, max_p2_axis);
            if (MathUtils.isZero(axis_overlap)) return;

            if (axis_overlap < depth) {
                depth = axis_overlap;
                normal_x = axis.x;
                normal_y = axis.y;
            }
        }

        Physics2DWorld.CollisionManifold manifold = new Physics2DWorld.CollisionManifold();
        setContactPoints(p1_vertices, p2_vertices, manifold);
        manifold.normal = new MathVector2(normal_x, normal_y);
        manifold.depth = depth;
        manifolds.add(manifold);
    }

    private void polygonVsRectangle(Shape2DPolygon polygon, Shape2DRectangle rect, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {
        rectangleVsPolygon(rect, polygon, manifolds);
    }

    /** Rectangle vs ____ **/
    private void rectangleVsAABB(Shape2DRectangle rect, Shape2DAABB aabb, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {
        AABBvsRectangle(aabb, rect, manifolds);
    }

    private Physics2DWorld.CollisionManifold rectangleVsCircle(Shape2D rect, Shape2D circle, Physics2DWorld world) {
        return circleVsRectangle(circle, rect, world);
    }

    private boolean rectangleVsUnion(Shape2DRectangle rectangle, Shape2DUnion union, Physics2DWorld.CollisionManifold manifold) {

        return false;
    }

    private void rectangleVsPolygon(Shape2DRectangle rect, Shape2DPolygon polygon, CollectionsArray<Physics2DWorld.CollisionManifold> manifolds) {
        // rect corners
        CollectionsArray<MathVector2> rect_v = rect.worldVertices();
        MathVector2 rect_c0 = rect_v.get(0);
        MathVector2 rect_c1 = rect_v.get(1);
        MathVector2 rect_c2 = rect_v.get(2);
        MathVector2 rect_c3 = rect_v.get(3);

        // polygon corners
        CollectionsArray<MathVector2> polygon_v = polygon.worldVertices();

        MathVector2 axis = new MathVector2();
        float depth;
        float normal_x;
        float normal_y;

        // rect axis1: c1-c2 axis
        {
            axis.set(rect_c2.x - rect_c1.x, rect_c2.y - rect_c1.y).nor();
            float min_polygon_overlap = Float.MAX_VALUE;
            float max_polygon_overlap = -Float.MAX_VALUE;
            for (MathVector2 p_vertex : polygon_v) {
                float projection = MathVector2.dot(axis.x, axis.y, p_vertex.x - rect_c1.x, p_vertex.y - rect_c1.y);
                if (projection < min_polygon_overlap) min_polygon_overlap = projection;
                if (projection > max_polygon_overlap) max_polygon_overlap = projection;
            }
            float axis_overlap = MathUtils.intervalsOverlap(min_polygon_overlap, max_polygon_overlap, 0, rect.unscaledWidth * rect.scaleX());
            if (MathUtils.isZero(axis_overlap)) return; // no collision

            depth = axis_overlap;
            normal_x = axis.x;
            normal_y = axis.y;
        }

        // rect axis2: c2-c3 axis
        {
            axis.set(rect_c3.x - rect_c2.x, rect_c3.y - rect_c2.y).nor();
            float min_axis_overlap = Float.MAX_VALUE;
            float max_axis_overlap = -Float.MAX_VALUE;
            for (MathVector2 p_vertex : polygon_v) {
                float projection = MathVector2.dot(axis.x, axis.y, p_vertex.x - rect_c2.x, p_vertex.y - rect_c2.y);
                if (projection < min_axis_overlap) min_axis_overlap = projection;
                if (projection > max_axis_overlap) max_axis_overlap = projection;
            }
            float axis_overlap = MathUtils.intervalsOverlap(min_axis_overlap, max_axis_overlap, 0, rect.unscaledHeight * rect.scaleY());
            if (MathUtils.isZero(axis_overlap)) return; // no collision

            if (axis_overlap < depth) {
                depth = axis_overlap;
                normal_x = axis.x;
                normal_y = axis.y;
            }
        }

        // polygon axis
        {
            MathVector2 tail = new MathVector2();
            MathVector2 head = new MathVector2();
            for (int i = 0; i < polygon.vertexCount; i++) {
                polygon.getWorldEdge(i, tail, head);
                axis.set(head).sub(tail).nor().rotate90(1);

                // project rectangle on the axis
                float prj_rect_c0 = MathVector2.dot(axis.x, axis.y, rect_c0.x - tail.x, rect_c0.y - tail.y);
                float prj_rect_c1 = MathVector2.dot(axis.x, axis.y, rect_c1.x - tail.x, rect_c1.y - tail.y);
                float prj_rect_c2 = MathVector2.dot(axis.x, axis.y, rect_c2.x - tail.x, rect_c2.y - tail.y);
                float prj_rect_c3 = MathVector2.dot(axis.x, axis.y, rect_c3.x - tail.x, rect_c3.y - tail.y);
                float min_prj_rect = MathUtils.min(prj_rect_c0, prj_rect_c1, prj_rect_c2, prj_rect_c3);
                float max_prj_rect = MathUtils.max(prj_rect_c0, prj_rect_c1, prj_rect_c2, prj_rect_c3);

                // project polygon on the axis
                float min_prj_vertex = Float.MAX_VALUE;
                float max_prj_vertex = -Float.MAX_VALUE;
                for (MathVector2 vertex : polygon_v) {
                    float prj_vertex = MathVector2.dot(axis.x, axis.y, vertex.x - tail.x, vertex.y - tail.y);
                    if (prj_vertex < min_prj_vertex) min_prj_vertex = prj_vertex;
                    if (prj_vertex > max_prj_vertex) max_prj_vertex = prj_vertex;
                }
                float axis_overlap = MathUtils.intervalsOverlap(min_prj_rect, max_prj_rect, min_prj_vertex, max_prj_vertex);
                if (MathUtils.isZero(axis_overlap)) return;

                if (axis_overlap < depth) {
                    depth = axis_overlap;
                    normal_x = axis.x;
                    normal_y = axis.y;
                }
            }
        }

        Physics2DWorld.CollisionManifold manifold = new Physics2DWorld.CollisionManifold();
        setContactPoints(rect_v, polygon_v, manifold);
        manifold.normal = new MathVector2(normal_x, normal_y);
        manifold.depth = depth;
        manifolds.add(manifold);
    }

    // FIXME: setting collision normal is possibly wrong here.
    /***  here is the problem  ***/
    // FIXME: order of operations is possibly wrong here.
    // TODO ??? : after each manifold generation, we need to resolve, then recreate manifolds?
    // FIXME: Maybe the problem is only for multiple contact points.
    @Deprecated private Physics2DWorld.CollisionManifold rectangleVsRectangle_old(Shape2D a, Shape2D b, Physics2DWorld world) {
        Shape2DRectangle rect1 = (Shape2DRectangle) a;
        Shape2DRectangle rect2 = (Shape2DRectangle) b;

        // rect1 corners
        CollectionsArray<MathVector2> vertices_1 = rect1.worldVertices();
        MathVector2 rect1_c0 = vertices_1.get(0);
        MathVector2 rect1_c1 = vertices_1.get(1);
        MathVector2 rect1_c2 = vertices_1.get(2);
        MathVector2 rect1_c3 = vertices_1.get(3);

        // rect2 corners
        CollectionsArray<MathVector2> vertices_2 = rect2.worldVertices();
        MathVector2 rect2_c0 = vertices_2.get(0);
        MathVector2 rect2_c1 = vertices_2.get(1);
        MathVector2 rect2_c2 = vertices_2.get(2);
        MathVector2 rect2_c3 = vertices_2.get(3);

        MathVector2 axis = new MathVector2();
        float min_axis_overlap   = Float.POSITIVE_INFINITY;
        float min_overlap_axis_x = Float.POSITIVE_INFINITY;
        float min_overlap_axis_y = Float.POSITIVE_INFINITY;
        // rect1, axis1 (c1-c2)
        {
            axis.set(rect2_c2.x - rect2_c1.x, rect2_c2.y - rect2_c1.y).nor();
            float rect1_c0_axis = MathVector2.dot(axis.x, axis.y, rect1_c0.x - rect2_c1.x, rect1_c0.y - rect2_c1.y);
            float rect1_c1_axis = MathVector2.dot(axis.x, axis.y, rect1_c1.x - rect2_c1.x, rect1_c1.y - rect2_c1.y);
            float rect1_c2_axis = MathVector2.dot(axis.x, axis.y, rect1_c2.x - rect2_c1.x, rect1_c2.y - rect2_c1.y);
            float rect1_c3_axis = MathVector2.dot(axis.x, axis.y, rect1_c3.x - rect2_c1.x, rect1_c3.y - rect2_c1.y);
            float rect1_min_axis = MathUtils.min(rect1_c0_axis, rect1_c1_axis, rect1_c2_axis, rect1_c3_axis);
            float rect1_max_axis = MathUtils.max(rect1_c0_axis, rect1_c1_axis, rect1_c2_axis, rect1_c3_axis);
            float axis_overlap = MathUtils.intervalsOverlap(rect1_min_axis, rect1_max_axis, 0, rect2.unscaledWidth * rect2.scaleX());
            if (MathUtils.isZero(axis_overlap)) return null; // no collision

            if (axis_overlap < min_axis_overlap) {
                min_axis_overlap = axis_overlap;
                min_overlap_axis_x = axis.x;
                min_overlap_axis_y = axis.y;
            }
        }

        // rect1, axis2 (c2-c3)
        {
            axis.set(rect2_c3.x - rect2_c2.x, rect2_c3.y - rect2_c2.y).nor();
            float rect1_c0_axis = MathVector2.dot(axis.x, axis.y, rect1_c0.x - rect2_c2.x, rect1_c0.y - rect2_c2.y);
            float rect1_c1_axis = MathVector2.dot(axis.x, axis.y, rect1_c1.x - rect2_c2.x, rect1_c1.y - rect2_c2.y);
            float rect1_c2_axis = MathVector2.dot(axis.x, axis.y, rect1_c2.x - rect2_c2.x, rect1_c2.y - rect2_c2.y);
            float rect1_c3_axis = MathVector2.dot(axis.x, axis.y, rect1_c3.x - rect2_c2.x, rect1_c3.y - rect2_c2.y);
            float rect1_min_axis = MathUtils.min(rect1_c0_axis, rect1_c1_axis, rect1_c2_axis, rect1_c3_axis);
            float rect1_max_axis = MathUtils.max(rect1_c0_axis, rect1_c1_axis, rect1_c2_axis, rect1_c3_axis);
            float axis_overlap = MathUtils.intervalsOverlap(rect1_min_axis, rect1_max_axis, 0, rect2.unscaledHeight * rect2.scaleY());
            if (MathUtils.isZero(axis_overlap)) return null; // no collision

            if (axis_overlap < min_axis_overlap) {
                min_axis_overlap = axis_overlap;
                min_overlap_axis_x = axis.x;
                min_overlap_axis_y = axis.y;
            }
        }

        // rect2 axis1: c1-c2 axis
        {
            axis.set(rect1_c2.x - rect1_c1.x, rect1_c2.y - rect1_c1.y).nor();
            float rect2_c0_axis = MathVector2.dot(axis.x, axis.y, rect2_c0.x - rect1_c1.x, rect2_c0.y - rect1_c1.y);
            float rect2_c1_axis = MathVector2.dot(axis.x, axis.y, rect2_c1.x - rect1_c1.x, rect2_c1.y - rect1_c1.y);
            float rect2_c2_axis = MathVector2.dot(axis.x, axis.y, rect2_c2.x - rect1_c1.x, rect2_c2.y - rect1_c1.y);
            float rect2_c3_axis = MathVector2.dot(axis.x, axis.y, rect2_c3.x - rect1_c1.x, rect2_c3.y - rect1_c1.y);
            float rect2_min_axis = MathUtils.min(rect2_c0_axis, rect2_c1_axis, rect2_c2_axis, rect2_c3_axis);
            float rect2_max_axis = MathUtils.max(rect2_c0_axis, rect2_c1_axis, rect2_c2_axis, rect2_c3_axis);
            float axis_overlap = MathUtils.intervalsOverlap(rect2_min_axis, rect2_max_axis, 0, rect1.unscaledWidth * rect1.scaleX());
            if (MathUtils.isZero(axis_overlap)) return null; // no collision

            if (axis_overlap < min_axis_overlap) {
                min_axis_overlap = axis_overlap;
                min_overlap_axis_x = axis.x;
                min_overlap_axis_y = axis.y;
            }
        }

        // rect2 axis2: c2-c3 axis
        {
            axis.set(rect1_c3.x - rect1_c2.x, rect1_c3.y - rect1_c2.y).nor();
            float rect2_c0_axis = MathVector2.dot(axis.x, axis.y, rect2_c0.x - rect1_c2.x, rect2_c0.y - rect1_c2.y);
            float rect2_c1_axis = MathVector2.dot(axis.x, axis.y, rect2_c1.x - rect1_c2.x, rect2_c1.y - rect1_c2.y);
            float rect2_c2_axis = MathVector2.dot(axis.x, axis.y, rect2_c2.x - rect1_c2.x, rect2_c2.y - rect1_c2.y);
            float rect2_c3_axis = MathVector2.dot(axis.x, axis.y, rect2_c3.x - rect1_c2.x, rect2_c3.y - rect1_c2.y);
            float rect2_min_axis = MathUtils.min(rect2_c0_axis, rect2_c1_axis, rect2_c2_axis, rect2_c3_axis);
            float rect2_max_axis = MathUtils.max(rect2_c0_axis, rect2_c1_axis, rect2_c2_axis, rect2_c3_axis);
            float axis_overlap = MathUtils.intervalsOverlap(rect2_min_axis, rect2_max_axis, 0, rect1.unscaledHeight * rect1.scaleY());
            if (MathUtils.isZero(axis_overlap)) return null; // no collision

            if (axis_overlap < min_axis_overlap) {
                min_axis_overlap = axis_overlap;
                min_overlap_axis_x = axis.x;
                min_overlap_axis_y = axis.y;
            }
        }

        Physics2DWorld.CollisionManifold manifold = world.manifoldMemoryPool.allocate();
        setContactPoints(vertices_1, vertices_2, manifold);
        manifold.normal.set(min_overlap_axis_x, min_overlap_axis_y).nor();
        manifold.depth = min_axis_overlap;
        return manifold;
    }

    // TODO: read
    // https://www.jkh.me/files/tutorials/Separating%20Axis%20Theorem%20for%20Oriented%20Bounding%20Boxes.pdf
    private Physics2DWorld.CollisionManifold rectangleVsRectangle(Shape2D a, Shape2D b, Physics2DWorld world) {
        Shape2DRectangle rect_1 = (Shape2DRectangle) a;
        Shape2DRectangle rect_2 = (Shape2DRectangle) b;

        MathVector2 T = new MathVector2(rect_2.x() - rect_1.x(),rect_2.y() - rect_1.y());
        float wa = rect_1.getWidth() * 0.5f;
        float ha = rect_1.getHeight() * 0.5f;
        float wb = rect_2.getWidth() * 0.5f;
        float hb = rect_2.getHeight() * 0.5f;

        MathVector2 L  = new MathVector2();
        MathVector2 Ax = new MathVector2(rect_1.c2()).sub(rect_1.c1()).nor();
        MathVector2 Ay = new MathVector2(rect_1.c3()).sub(rect_1.c2()).nor();
        MathVector2 Bx = new MathVector2(rect_2.c2()).sub(rect_2.c1()).nor();
        MathVector2 By = new MathVector2(rect_2.c3()).sub(rect_2.c2()).nor();

        float minOverlap = Float.POSITIVE_INFINITY;
        float nx = 0, ny = 0;

        // L = Ax
        {
            L.set(Ax);
            float leftSide   = Math.abs(T.dot(L));
            float firstTerm  = Math.abs(wb * Bx.dot(Ax));
            float secondTerm = Math.abs(hb * By.dot(Ax));
            float sum = wa + firstTerm + secondTerm;
            if (leftSide >= sum) return null;

            float diff = sum - leftSide;
            if (diff < minOverlap) {
                minOverlap = diff;
                nx = L.x;
                ny = L.y;
            }
        }

        // L = Ay
        {
            L.set(Ay);
            float leftSide = Math.abs(T.dot(L));
            float firstTerm = Math.abs(wb * Bx.dot(Ay));
            float secondTerm = Math.abs(hb * By.dot(Ay));
            float sum = ha + firstTerm + secondTerm;
            if (leftSide >= sum) return null;

            float diff = sum - leftSide;
            if (diff < minOverlap) {
                minOverlap = diff;
                nx = L.x;
                ny = L.y;
            }
        }

        // L = Bx
        {
            L.set(Bx);
            float leftSide = Math.abs(T.dot(L));
            float firstTerm = Math.abs(wa * Ax.dot(Bx));
            float secondTerm = Math.abs(ha * Ay.dot(Bx));
            float sum = wb + firstTerm + secondTerm;
            if (leftSide >= sum) return null;

            float diff = sum - leftSide;
            if (diff < minOverlap) {
                minOverlap = diff;
                nx = L.x;
                ny = L.y;
            }
        }

        // L = By
        {
            L.set(By);
            float leftSide = Math.abs(T.dot(L));
            float firstTerm = Math.abs(wa * Ax.dot(By));
            float secondTerm = Math.abs(ha * Ay.dot(By));
            float sum = hb + firstTerm + secondTerm;
            if (leftSide >= sum) return null;

            float diff = sum - leftSide;
            if (diff < minOverlap) {
                minOverlap = diff;
                nx = L.x;
                ny = L.y;
            }
        }

        Physics2DWorld.CollisionManifold manifold = world.manifoldMemoryPool.allocate();
        setContactPoints(rect_1.worldVertices(), rect_2.worldVertices(), manifold);
        manifold.normal.set(nx, ny);
        manifold.depth = minOverlap;
        return manifold;
    }

    private void setContactPoints(CollectionsArray<MathVector2> verticesA, CollectionsArray<MathVector2> verticesB, Physics2DWorld.CollisionManifold manifold) {
        CollectionsArray<Projection> projections = new CollectionsArray<>();

        // first polygon vs second
        for (MathVector2 point : verticesA) {
            Projection p = getClosestProjection(point, verticesB);
            projections.add(p);
        }

        // second polygon vs first
        for (MathVector2 point : verticesB) {
            Projection p = getClosestProjection(point, verticesA);
            projections.add(p);
        }

        projections.sort();
        Projection p0 = projections.get(0);
        Projection p1 = projections.get(1);
        if (p0 != null) {
            manifold.contactPoint1.set(p0.px, p0.py);
            manifold.contacts = 1;
        }
        if (p0 != null && p1 != null && MathUtils.floatsEqual(p0.dst, p1.dst)) {
            manifold.contactPoint2.set(p1.px, p1.py);
            manifold.contacts = 2;
        }

        // free projections
        projectionsPool.freeAll(projections);
    }

    private Projection getClosestProjection(MathVector2 point, CollectionsArray<MathVector2> vertices) {
        float minDst2 = Float.MAX_VALUE;
        float px = Float.NaN;
        float py = Float.NaN;
        for (int i = 0; i < vertices.size; i++) {
            MathVector2 tail = vertices.getCyclic(i);
            MathVector2 head = vertices.getCyclic(i + 1);
            float dx = head.x - tail.x;
            float dy = head.y - tail.y;
            if (MathUtils.isZero(dx) && MathUtils.isZero(dy)) continue;
            float scale = MathVector2.dot(point.x - tail.x, point.y - tail.y, dx, dy) / MathVector2.len2(dx, dy);
            MathVector2 projectedPoint = new MathVector2(dx, dy).scl(scale).add(tail);
            projectedPoint.clamp(tail, head);
            float dst2 = MathVector2.dst2(point, projectedPoint);
            if (dst2 <= minDst2) {
                minDst2 = dst2;
                px = projectedPoint.x;
                py = projectedPoint.y;
            }
        }

        Projection p = projectionsPool.allocate();
        p.px = px;
        p.py = py;
        p.dst = (float) Math.sqrt(minDst2);
        return p;
    }

    public static final class Projection implements Comparable<Projection>, MemoryPool.Reset {

        public float px;
        public float py;
        public float dst;

        public Projection() {}

        @Override
        public int compareTo(@NotNull Projection other) {
            return Float.compare(this.dst, other.dst);
        }

        @Override
        public void reset() {

        }

    }


}
