package org.example.engine.core.physics2d_new;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.*;
import org.jetbrains.annotations.NotNull;

public class Physics2DCollisionDetection {

    private final MemoryPool<Projection> projectionsPool = new MemoryPool<>(Projection.class, 200);
    private final Physics2DWorld         world;

    Physics2DCollisionDetection(Physics2DWorld world) {
        this.world = world;
    }

    Physics2DWorld.CollisionManifold detectCollision(Physics2DBody body_a, Physics2DBody body_b) {
        Physics2DWorld.CollisionManifold manifold = null;
        Shape2D shape_a = body_a.shape;
        Shape2D shape_b = body_b.shape;

        if (shape_a instanceof Shape2DCircle) {
            if      (shape_b instanceof Shape2DCircle)    manifold = circleVsCircle   (shape_a, shape_b); // TODO
            else if (shape_b instanceof Shape2DRectangle) manifold = circleVsRectangle(shape_a, shape_b);
            //else if (shape_b instanceof Shape2DPolygon)   manifold = circleVsPolygon  (shape_a, shape_b);
            //else if (shape_b instanceof Shape2DUnion)     manifold = circleVsUnion    (shape_a, shape_b); // TODO
        }

        else if (shape_a instanceof Shape2DRectangle) {
            if      (shape_b instanceof Shape2DRectangle) manifold = rectangleVsRectangle(shape_a, shape_b);
            else if (shape_b instanceof Shape2DCircle)    manifold = rectangleVsCircle   (shape_a, shape_b);
            //else if (shape_b instanceof Shape2DPolygon)   manifold = rectangleVsPolygon  (shape_a, shape_b);
            //else if (shape_b instanceof Shape2DUnion)     manifold = rectangleVsUnion    (shape_a, shape_b); // TODO
        }

//        else if (shape_a instanceof Shape2DPolygon) {
//            if      (shape_b instanceof Shape2DCircle)    manifold = polygonVsCircle   (shape_a, shape_b);
//            else if (shape_b instanceof Shape2DRectangle) manifold = polygonVsRectangle(shape_a, shape_b);
//            else if (shape_b instanceof Shape2DPolygon)   manifold = polygonVsPolygon  (shape_a, shape_b);
//            else if (shape_b instanceof Shape2DUnion)     manifold = polygonVsUnion    (shape_a, shape_b); // TODO
//        }

//        else if (shape_a instanceof Shape2DUnion) {
//            if      (shape_b instanceof Shape2DCircle)    manifold = unionVsCircle   (shape_a, shape_b); // TODO
//            else if (shape_b instanceof Shape2DRectangle) manifold = unionVsRectangle(shape_a, shape_b); // TODO
//            else if (shape_b instanceof Shape2DPolygon)   manifold = unionVsPolygon  (shape_a, shape_b); // TODO
//            else if (shape_b instanceof Shape2DUnion)     manifold = unionVsUnion    (shape_a, shape_b); // TODO
//        }

        return manifold;
    }

    private Physics2DWorld.CollisionManifold circleVsCircle(Shape2D a, Shape2D b) {
        final Shape2DCircle c1 = (Shape2DCircle) a;
        final Shape2DCircle c2 = (Shape2DCircle) b;

        MathVector2 c2Center = c2.geometryCenter();
        MathVector2 c1Center = c1.geometryCenter();
        final float dx = c2Center.x - c1Center.x;
        final float dy = c2Center.y - c1Center.y;

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
        manifold.shape_a = a;
        manifold.shape_b = b;
        return manifold;
    }

    private Physics2DWorld.CollisionManifold circleVsRectangle(Shape2D a, Shape2D b) {
        Shape2DCircle    circle = (Shape2DCircle)    a;
        Shape2DRectangle rect   = (Shape2DRectangle) b;

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
        manifold.shape_a = a;
        manifold.shape_b = b;
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

    private Physics2DWorld.CollisionManifold rectangleVsCircle(Shape2D rect, Shape2D circle) {
        return circleVsRectangle(circle, rect);
    }

    // https://www.jkh.me/files/tutorials/Separating%20Axis%20Theorem%20for%20Oriented%20Bounding%20Boxes.pdf
    private Physics2DWorld.CollisionManifold rectangleVsRectangle(Shape2D a, Shape2D b) {
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
        manifold.shape_a = a;
        manifold.shape_b = b;
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
