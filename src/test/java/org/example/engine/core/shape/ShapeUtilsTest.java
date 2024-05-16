package org.example.engine.core.shape;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ShapeUtilsTest {

    @Test
    void createPolygonLine() {
    }

    @Test
    void createPolygonRectangleFilled() {
    }

    @Test
    void createPolygonRectangleHollow() {
    }

    @Test
    void createPolygonCircleFilled() {
    }

    @Test
    void createPolygonCircleHollow() {
    }

    @Test
    void createPolygonFilled() {
    }

    @Test
    void createPolygonHollow() {
    }

    @Test
    void isPolygonConvex() {
        float[] convex_1 = new float[] {1,4,-2,3,-3,1,-2,-1,1,-1,3,2};
        float[] convex_2 = new float[] {1,3,-2,3,-2,-1,1,-1};
        float[] convex_3 = new float[] {1,4,-2,3,-3,1};
        float[] convex_4 = new float[] {-1,-1,9,-1,1,1,-1,1};
        float[] convex_5 = new float[] {1,2,-1,1,-1,-1,1,0};
        Assertions.assertTrue(ShapeUtils.isPolygonConvex(convex_1));
        Assertions.assertTrue(ShapeUtils.isPolygonConvex(convex_2));
        Assertions.assertTrue(ShapeUtils.isPolygonConvex(convex_3));
        Assertions.assertTrue(ShapeUtils.isPolygonConvex(convex_4));
        Assertions.assertTrue(ShapeUtils.isPolygonConvex(convex_5));

        float[] concave_1 = new float[] {-1,1,-1,-3,0,0,3,1};
        float[] concave_2 = new float[] {-2,1,-2,-2,3,1,0,1}; // collinear
        float[] concave_3 = new float[] {0,0,-1,-1,0,-2,1,1};
        float[] concave_4 = new float[] {0,4,-2,2,-2,0,0,-2,1,0,3,0};
        float[] concave_5 = new float[] {0,2,-2,1,-2,-1,0,0,2,-1,2,1};
        Assertions.assertFalse(ShapeUtils.isPolygonConvex(concave_1));
        Assertions.assertFalse(ShapeUtils.isPolygonConvex(concave_2));
        Assertions.assertFalse(ShapeUtils.isPolygonConvex(concave_3));
        Assertions.assertFalse(ShapeUtils.isPolygonConvex(concave_4));
        Assertions.assertFalse(ShapeUtils.isPolygonConvex(concave_5));
    }

    @Test
    void triangulatePolygon() {
    }

}