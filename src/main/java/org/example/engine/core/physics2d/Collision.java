package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.memory.MemoryPool;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Collision {

    private final MemoryPool<Projection> projectionsPool = new MemoryPool<>(Projection.class, 200);
    private final World                  world;

    Collision(final World world) {
        this.world = world;
    }

    protected CollisionManifold detectCollision(BodyCollider collider_a, BodyCollider collider_b) {

        if (collider_a instanceof BodyColliderCircle) {
            if (collider_b instanceof BodyColliderCircle) return circleVsCircle(collider_a, collider_b);
            if (collider_b instanceof BodyColliderRectangle) return circleVsRectangle(collider_a, collider_b);
        }

        if (collider_a instanceof BodyColliderRectangle) {
            if (collider_b instanceof BodyColliderCircle) return circleVsRectangle(collider_b, collider_a);
            if (collider_b instanceof BodyColliderRectangle) return rectangleVsRectangle(collider_b, collider_a);
        }

        return null;
    }

    private CollisionManifold circleVsCircle(BodyCollider collider_a, BodyCollider collider_b) {
        final BodyColliderCircle c1 = (BodyColliderCircle) collider_a;
        final BodyColliderCircle c2 = (BodyColliderCircle) collider_b;

        Vector2 c2Center = c2.worldCenter;
        Vector2 c1Center = c1.worldCenter;
        final float dx = c2Center.x - c1Center.x;
        final float dy = c2Center.y - c1Center.y;

        final float radiusSum = c1.r + c2.r;
        final float distanceSquared = dx * dx + dy * dy;

        if (distanceSquared > radiusSum * radiusSum) return null;

        CollisionManifold manifold = world.manifoldsPool.allocate();
        manifold.contacts = 1;
        final float distance = (float) Math.sqrt(distanceSquared);
        if (distance != 0) {
            manifold.depth = radiusSum - distance;
            manifold.normal.set(dx, dy).scl(-1.0f / distance);
        } else {
            manifold.depth = c1.r;
            manifold.normal.set(1, 0);
        }
        manifold.contact_a.set(manifold.normal).scl(-c1.r).add(c1.worldCenter);
        manifold.collider_a = collider_a;
        manifold.collider_b = collider_b;

        Vector2 aCenter = collider_a.worldCenter;
        Vector2 bCenter = collider_b.worldCenter;
        Vector2 a_b     = new Vector2(bCenter.x - aCenter.x, bCenter.y - aCenter.y);
        if (a_b.dot(manifold.normal) < 0) manifold.normal.flip();

        return manifold;
    }

    private CollisionManifold circleVsRectangle(BodyCollider collider_a, BodyCollider collider_b) {
        BodyColliderCircle    circle = (BodyColliderCircle)    collider_a;
        BodyColliderRectangle rect   = (BodyColliderRectangle) collider_b;

        Vector2 circleWorldCenter = circle.worldCenter;
        Vector2 c1 = rect.c0;
        Vector2 c2 = rect.c1;
        Vector2 c3 = rect.c2;
        Vector2 c4 = rect.c3;

        float dx1 = c2.x - c1.x;
        float dy1 = c2.y - c1.y;
        if (MathUtils.isZero(dx1) && MathUtils.isZero(dy1)) return null;
        float scale1 = Vector2.dot(circleWorldCenter.x - c1.x, circleWorldCenter.y - c1.y, dx1, dy1) / Vector2.len2(dx1, dy1);
        Vector2 projection1 = new Vector2(dx1, dy1).scl(scale1).add(c1);
        projection1.clamp(c1, c2);

        float dx2 = c3.x - c2.x;
        float dy2 = c3.y - c2.y;
        if (MathUtils.isZero(dx2) && MathUtils.isZero(dy2)) return null;
        float scale2 = Vector2.dot(circleWorldCenter.x - c2.x, circleWorldCenter.y - c2.y, dx2, dy2) / Vector2.len2(dx2, dy2);
        Vector2 projection2 = new Vector2(dx2, dy2).scl(scale2).add(c2);
        projection2.clamp(c2, c3);

        float dx3 = c4.x - c3.x;
        float dy3 = c4.y - c3.y;
        if (MathUtils.isZero(dx3) && MathUtils.isZero(dy3)) return null;
        float scale3 = Vector2.dot(circleWorldCenter.x - c3.x, circleWorldCenter.y - c3.y, dx3, dy3) / Vector2.len2(dx3, dy3);
        Vector2 projection3 = new Vector2(dx3, dy3).scl(scale3).add(c3);
        projection3.clamp(c3, c4);

        float dx4 = c1.x - c4.x;
        float dy4 = c1.y - c4.y;
        if (MathUtils.isZero(dx4) && MathUtils.isZero(dy4)) return null;
        float scale4 = Vector2.dot(circleWorldCenter.x - c4.x, circleWorldCenter.y - c4.y, dx4, dy4) / Vector2.len2(dx4, dy4);
        Vector2 projection4 = new Vector2(dx4, dy4).scl(scale4).add(c4);
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
        final float dstEdge1Squared = Vector2.dst2(circleWorldCenter, projection1);
        final float dstEdge2Squared = Vector2.dst2(circleWorldCenter, projection2);
        final float dstEdge3Squared = Vector2.dst2(circleWorldCenter, projection3);
        final float dstEdge4Squared = Vector2.dst2(circleWorldCenter, projection4);

        final float minDstEdgeSquared = MathUtils.min(dstEdge1Squared, dstEdge2Squared, dstEdge3Squared, dstEdge4Squared);

        Vector2 projection;
        if (MathUtils.floatsEqual(minDstEdgeSquared, dstEdge1Squared)) {
            projection = projection1;
        } else if (MathUtils.floatsEqual(minDstEdgeSquared, dstEdge2Squared)) {
            projection = projection2;
        } else if (MathUtils.floatsEqual(minDstEdgeSquared, dstEdge3Squared)) {
            projection = projection3;
        } else {
            projection = projection4;
        }

        CollisionManifold manifold = world.manifoldsPool.allocate();
        manifold.contacts = 1;
        manifold.contact_a.set(projection);
        manifold.collider_a = collider_a;
        manifold.collider_b = collider_b;
        final float minDstEdge = (float) Math.sqrt(minDstEdgeSquared);
        if (rectContainsCenter) {
            manifold.normal.set(projection).sub(circleWorldCenter).nor();
            manifold.depth = minDstEdge + circle.r;
        } else {
            manifold.normal.set(circleWorldCenter).sub(projection).nor();
            manifold.depth = circle.r - minDstEdge;
        }

        Vector2 aCenter = collider_a.worldCenter;
        Vector2 bCenter = collider_b.worldCenter;
        Vector2 a_b     = new Vector2(bCenter.x - aCenter.x, bCenter.y - aCenter.y);
        if (a_b.dot(manifold.normal) < 0) manifold.normal.flip();

        return manifold;
    }

    private CollisionManifold rectangleVsRectangle(BodyCollider collider_a, BodyCollider collider_b) {
        BodyColliderRectangle rect_1 = (BodyColliderRectangle) collider_a;
        BodyColliderRectangle rect_2 = (BodyColliderRectangle) collider_b;

        Vector2 rect_1_center = rect_1.worldCenter;
        Vector2 rect_2_center = rect_2.worldCenter;
        Vector2 T = new Vector2(rect_2_center.x - rect_1_center.x,rect_2_center.y - rect_1_center.y);
        float wa = rect_1.widthHalf;
        float ha = rect_1.heightHalf;
        float wb = rect_2.widthHalf;
        float hb = rect_2.heightHalf;

        Vector2 L  = new Vector2();
        Vector2 Ax = new Vector2(rect_1.c2).sub(rect_1.c1).nor();
        Vector2 Ay = new Vector2(rect_1.c3).sub(rect_1.c2).nor();
        Vector2 Bx = new Vector2(rect_2.c2).sub(rect_2.c1).nor();
        Vector2 By = new Vector2(rect_2.c3).sub(rect_2.c2).nor();

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

        CollisionManifold manifold = world.manifoldsPool.allocate();
        setContactPoints(rect_1.worldVertices, rect_2.worldVertices, manifold);
        manifold.normal.set(nx, ny);
        manifold.depth = minOverlap;
        manifold.collider_a = collider_a;
        manifold.collider_b = collider_b;

        Vector2 aCenter = collider_a.worldCenter;
        Vector2 bCenter = collider_b.worldCenter;
        Vector2 a_b     = new Vector2(bCenter.x - aCenter.x, bCenter.y - aCenter.y);
        if (a_b.dot(manifold.normal) < 0) manifold.normal.flip();

        return manifold;
    }

    private void setContactPoints(Array<Vector2> verticesA, Array<Vector2> verticesB, CollisionManifold manifold) {
        Array<Projection> projections = new Array<>();

        // first polygon vs second
        for (Vector2 point : verticesA) {
            Projection p = getClosestProjection(point, verticesB);
            projections.add(p);
        }

        // second polygon vs first
        for (Vector2 point : verticesB) {
            Projection p = getClosestProjection(point, verticesA);
            projections.add(p);
        }

        projections.sort();
        Projection p0 = projections.get(0);
        Projection p1 = projections.get(1);
        if (p0 != null) {
            manifold.contact_a.set(p0.px, p0.py);
            manifold.contacts = 1;
        }
        if (p0 != null && p1 != null && MathUtils.floatsEqual(p0.dst, p1.dst, 0.005f)) {
            manifold.contact_b.set(p1.px, p1.py);
            manifold.contacts = 2;
        }

        // free projections
        projectionsPool.freeAll(projections);
    }

    private Projection getClosestProjection(Vector2 point, Array<Vector2> vertices) {
        float minDst2 = Float.MAX_VALUE;
        float px = Float.NaN;
        float py = Float.NaN;
        for (int i = 0; i < vertices.size; i++) {
            Vector2 tail = vertices.getCyclic(i);
            Vector2 head = vertices.getCyclic(i + 1);
            float dx = head.x - tail.x;
            float dy = head.y - tail.y;
            if (MathUtils.isZero(dx) && MathUtils.isZero(dy)) continue;
            float scale = Vector2.dot(point.x - tail.x, point.y - tail.y, dx, dy) / Vector2.len2(dx, dy);
            Vector2 projectedPoint = new Vector2(dx, dy).scl(scale).add(tail);
            projectedPoint.clamp(tail, head);
            float dst2 = Vector2.dst2(point, projectedPoint);
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

    public static final class GridCell implements MemoryPool.Reset {

        public Array<BodyCollider> colliders = new Array<>(false, 2);
        public boolean             active    = false;

        public GridCell() {}

        @Override
        public void reset() {
            colliders.clear();
            active = false;
        }

    }

    public static final class Pair implements MemoryPool.Reset {

        private BodyCollider a;
        private BodyCollider b;

        public Pair() {}

        void set(BodyCollider a, BodyCollider b) {
            if (a.body.index < b.body.index) {
                this.a = a;
                this.b = b;
            } else {
                this.a = b;
                this.b = a;
            }
        }

        BodyCollider getA() {
            return a;
        }

        BodyCollider getB() {
            return b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair that = (Pair) o;
            return Objects.equals(a, that.a) && Objects.equals(b, that.b);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(a) + Objects.hashCode(b); // A commutative operation to ensure symmetry
        }

        @Override
        public void reset() {}

    }
}
