package org.example.engine.core.math;

public class AlgorithmsCollisions {

    private AlgorithmsCollisions() {}

    public static boolean doBoundingSpheresCollide(final Shape2D a, final Shape2D b) {
        final float sum = a.getBoundingRadius() + b.getBoundingRadius();
        float dc2 = Vector2.dst2(a.x, a.y, b.x, b.y);
        return dc2 < sum * sum;
    }

    public static boolean sat(final Shape2D a, final Shape2D b) {
        a.update();
        b.update();
        return false;
    }

    // TODO: modify signatures to capture collision manifold.
    /** AABB vs ____ **/
    private static boolean AABBvsAABB(Shape2DAABB a, Shape2DAABB b) {

        return false;
    }

    private static boolean AABBvsCircle(Shape2DAABB aabb, Shape2DCircle circle) {

        return false;
    }

    private static boolean AABBvsMorphed(Shape2DAABB aabb, Shape2DMorphed morphed) {

        return false;
    }

    private static boolean AABBvsPolygon(Shape2DAABB aabb, Shape2DPolygon polygon) {

        return false;
    }

    private static boolean AABBvsRectangle(Shape2DAABB aabb, Shape2DRectangle rectangle) {

        return false;
    }

    /** Circle vs ____ **/
    private static boolean circleVsAABB(Shape2DCircle circle, Shape2DAABB aabb) {

        return false;
    }

    private static boolean circleVsCircle(Shape2DCircle c1, Shape2DCircle c2) {
        final float sum = c1.getBoundingRadius() + c2.getBoundingRadius();
        float dc2 = Vector2.dst2(c1.x, c1.y, c2.x, c2.y);
        return dc2 < sum * sum;
    }

    private static boolean circleVsMorphed(Shape2DCircle circle, Shape2DMorphed morphed) {

        return false;
    }

    private static boolean circleVsPolygon(Shape2DCircle circle, Shape2DPolygon polygon) {

        return false;
    }

    private static boolean circleVsRectangle(Shape2DCircle circle, Shape2DRectangle rectangle) {

        return false;
    }

    /** Morphed vs ___ **/
    private static boolean morphedVsAABB(Shape2DMorphed morphed, Shape2DAABB aabb) {

        return false;
    }

    private static boolean morphedVsCircle(Shape2DMorphed morphed, Shape2DCircle circle) {

        return false;
    }

    private static boolean morphedVsMorphed(Shape2DMorphed morphed1, Shape2DMorphed morphed2) {

        return false;
    }

    private static boolean morphedVsPolygon(Shape2DMorphed morphed, Shape2DPolygon polygon) {

        return false;
    }

    private static boolean morphedVsRectangle(Shape2DMorphed morphed, Shape2DRectangle rectangle) {

        return false;
    }

    /** Polygon vs ____ **/
    private static boolean polygonVsAABB(Shape2DPolygon polygon, Shape2DAABB aabb) {

        return false;
    }

    private static boolean polygonVsCircle(Shape2DPolygon polygon, Shape2DCircle circle) {

        return false;
    }

    private static boolean polygonVsMorphed(Shape2DPolygon polygon, Shape2DMorphed morphed) {

        return false;
    }

    private static boolean polygonVsPolygon(Shape2DPolygon p1, Shape2DPolygon p2) {

        return false;
    }

    private static boolean polygonVsRectangle(Shape2DPolygon polygon, Shape2DRectangle rectangle) {

        return false;
    }

    /** Rectangle vs ____ **/
    private static boolean rectangleVsAABB(Shape2DRectangle rectangle, Shape2DAABB aabb) {

        return false;
    }

    private static boolean rectangleVsCircle(Shape2DRectangle rectangle, Shape2DCircle circle) {

        return false;
    }

    private static boolean rectangleVsMorphed(Shape2DRectangle rectangle, Shape2DMorphed morphed) {

        return false;
    }

    private static boolean rectangleVsPolygon(Shape2DRectangle rectangle, Shape2DPolygon polygon) {

        return false;
    }

    private static boolean rectangleVsRectangle(Shape2DRectangle r1, Shape2DRectangle r2) {

        return false;
    }

}
