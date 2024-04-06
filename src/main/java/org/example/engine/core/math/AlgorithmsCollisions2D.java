package org.example.engine.core.math;

@Deprecated
public class AlgorithmsCollisions2D {

    private AlgorithmsCollisions2D() {}

    public static boolean collide(final Shape2D a, final Shape2D b, Penetration penetration) {
        a.update();
        b.update();
        if (!boundingCirclesCollide(a, b)) return false;
        else if (a instanceof Shape2DCircle && b instanceof Shape2DCircle) return circleVsCircle((Shape2DCircle) a, (Shape2DCircle) b, penetration);
        else if (a instanceof Shape2DAABB && b instanceof Shape2DAABB) return AABBvsAABB((Shape2DAABB) a, (Shape2DAABB) b, penetration);
        return false;
    }

    public static boolean boundingCirclesCollide(final Shape2D a, final Shape2D b) {
        final float dx = b.x - a.x;
        final float dy = b.y - a.y;
        final float sum = a.getBoundingRadius() + b.getBoundingRadius();
        return dx * dx + dy * dy < sum * sum;
    }

    /** AABB vs ____ **/
    private static boolean AABBvsAABB(Shape2DAABB a, Shape2DAABB b, Penetration penetration) {
        if (a.worldMax.x < b.worldMin.x || a.worldMin.x > b.worldMax.x) return false;
        if (a.worldMax.y < b.worldMin.y || a.worldMin.y > b.worldMax.y) return false;

        return true;
    }

    private static boolean AABBvsCircle(Shape2DAABB aabb, Shape2DCircle circle, Penetration penetration) {

        return false;
    }

    private static boolean AABBvsMorphed(Shape2DAABB aabb, Shape2DMorphed morphed, Penetration penetration) {

        return false;
    }

    private static boolean AABBvsPolygon(Shape2DAABB aabb, Shape2DPolygon polygon, Penetration penetration) {

        return false;
    }

    private static boolean AABBvsRectangle(Shape2DAABB aabb, Shape2DRectangle rectangle, Penetration penetration) {

        return false;
    }

    /** Circle vs ____ **/
    private static boolean circleVsAABB(Shape2DCircle circle, Shape2DAABB aabb, Penetration penetration) {

        return false;
    }

    private static boolean circleVsCircle(Shape2DCircle c1, Shape2DCircle c2, Penetration penetration) {
        final float dx = c2.x - c1.x;
        final float dy = c2.y - c1.y;
        final float radiusSum = c1.getBoundingRadius() + c2.getBoundingRadius();
        final float distanceSquared = dx * dx + dy * dy;
        if (distanceSquared > radiusSum * radiusSum) return false;
        penetration.normal.set(dx, dy).nor();
        penetration.depth = radiusSum - (float) Math.sqrt(distanceSquared);
        return true;
    }

    private static boolean circleVsMorphed(Shape2DCircle circle, Shape2DMorphed morphed, Penetration penetration) {

        return false;
    }

    private static boolean circleVsPolygon(Shape2DCircle circle, Shape2DPolygon polygon, Penetration penetration) {

        return false;
    }

    private static boolean circleVsRectangle(Shape2DCircle circle, Shape2DRectangle rectangle, Penetration penetration) {

        return false;
    }

    /** Morphed vs ___ **/
    private static boolean morphedVsAABB(Shape2DMorphed morphed, Shape2DAABB aabb, Penetration penetration) {

        return false;
    }

    private static boolean morphedVsCircle(Shape2DMorphed morphed, Shape2DCircle circle, Penetration penetration) {

        return false;
    }

    private static boolean morphedVsMorphed(Shape2DMorphed morphed1, Shape2DMorphed morphed2, Penetration penetration) {

        return false;
    }

    private static boolean morphedVsPolygon(Shape2DMorphed morphed, Shape2DPolygon polygon, Penetration penetration) {

        return false;
    }

    private static boolean morphedVsRectangle(Shape2DMorphed morphed, Shape2DRectangle rectangle, Penetration penetration) {

        return false;
    }

    /** Polygon vs ____ **/
    private static boolean polygonVsAABB(Shape2DPolygon polygon, Shape2DAABB aabb, Penetration penetration) {

        return false;
    }

    private static boolean polygonVsCircle(Shape2DPolygon polygon, Shape2DCircle circle, Penetration penetration) {

        return false;
    }

    private static boolean polygonVsMorphed(Shape2DPolygon polygon, Shape2DMorphed morphed, Penetration penetration) {

        return false;
    }

    private static boolean polygonVsPolygon(Shape2DPolygon p1, Shape2DPolygon p2, Penetration penetration) {

        return false;
    }

    private static boolean polygonVsRectangle(Shape2DPolygon polygon, Shape2DRectangle rectangle, Penetration penetration) {

        return false;
    }

    /** Rectangle vs ____ **/
    private static boolean rectangleVsAABB(Shape2DRectangle rectangle, Shape2DAABB aabb, Penetration penetration) {

        return false;
    }

    private static boolean rectangleVsCircle(Shape2DRectangle rectangle, Shape2DCircle circle, Penetration penetration) {

        return false;
    }

    private static boolean rectangleVsMorphed(Shape2DRectangle rectangle, Shape2DMorphed morphed, Penetration penetration) {

        return false;
    }

    private static boolean rectangleVsPolygon(Shape2DRectangle rectangle, Shape2DPolygon polygon, Penetration penetration) {

        return false;
    }

    private static boolean rectangleVsRectangle(Shape2DRectangle r1, Shape2DRectangle r2, Penetration penetration) {

        return false;
    }

    public static final class Penetration {

        public Vector2 normal = new Vector2();
        public float depth = 0;

    }

}
