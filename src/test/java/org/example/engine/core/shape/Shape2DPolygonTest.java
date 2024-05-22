package org.example.engine.core.shape;

import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Shape2DPolygonTest {

    private static Shape2DPolygon polygon_1;
    private static Shape2DPolygon polygon_2;
    private static Shape2DPolygon polygon_3;
    private static Shape2DPolygon polygon_4;
    private static Shape2DPolygon polygon_5;
    private static Shape2DPolygon polygon_6;
    private static Shape2DPolygon polygon_7;
    private static Shape2DPolygon polygon_8;
    private static Shape2DPolygon polygon_9;
    private static Shape2DPolygon polygon_10;

    @BeforeEach
    private void setup() {
        // convex
        polygon_1 = new Shape2DPolygon(new float[] {0,0,1,0,1,1,0,1});
        polygon_2 = new Shape2DPolygon(new float[] {1,3,-2,3,-2,-1,1,-1});
        polygon_3 = new Shape2DPolygon(new float[] {1,4,-2,3,-3,1});
        polygon_4 = new Shape2DPolygon(new float[] {-1,-1,9,-1,1,1,-1,1});
        polygon_5 = new Shape2DPolygon(new float[] {1,2,-1,1,-1,-1,1,0});

        // concave
//        polygon_6 = new Shape2DPolygon(new float[] {});
//        polygon_7 = new Shape2DPolygon(new float[] {});
//        polygon_8 = new Shape2DPolygon(new float[] {});
//        polygon_9 = new Shape2DPolygon(new float[] {});
//        polygon_10 = new Shape2DPolygon(new float[] {});
    }

    @Test
    void getBoundingRadius() {

    }

    @Test
    void contains() {
        Shape2DPolygon p1 = new Shape2DPolygon(new float[] {-1,1,-1,-1,1,-1,1,1});
        Assertions.assertTrue(p1.contains(0,0));
        Assertions.assertTrue(p1.contains(0.5f,0.5f));
        Assertions.assertTrue(p1.contains(0.5f,-0.5f));
        Assertions.assertTrue(p1.contains(-0.5f,0.5f));
        Assertions.assertTrue(p1.contains(-0.5f,-0.5f));
        Assertions.assertFalse(p1.contains(1,1.5f));
    }

    @Test
    void area() {
        Assertions.assertEquals(1.0f, polygon_1.getArea(), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void geometryCenter() {
        Assertions.assertTrue(polygon_1.geometryCenter().epsilonEquals(new MathVector2(0.5f,0.5f), MathUtils.FLOAT_ROUNDING_ERROR));
    }

    @Test
    void getVertexX() {
    }

    @Test
    void getVertexY() {
    }

    @Test
    void getWorldEdge() {
        Shape2DPolygon polygon1 = new Shape2DPolygon(new float[] {2,1,1,2,-1,2,-2,1,-2,-1,-1,-2,1,-2,2,-1});

        MathVector2 tail1 = new MathVector2();
        MathVector2 head1 = new MathVector2();
        polygon1.getWorldEdge(0, tail1, head1);
        Assertions.assertEquals(2.0f, tail1.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, tail1.y, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, head1.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f, head1.y, MathUtils.FLOAT_ROUNDING_ERROR);

        MathVector2 tail2 = new MathVector2();
        MathVector2 head2 = new MathVector2();
        polygon1.getWorldEdge(-1, tail2, head2);
        Assertions.assertEquals(2.0f, tail2.x,  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(-1.0f, tail2.y, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f, head2.x,  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, head2.y,  MathUtils.FLOAT_ROUNDING_ERROR);

        MathVector2 tail3 = new MathVector2();
        MathVector2 head3 = new MathVector2();
        polygon1.getWorldEdge(polygon1.vertexCount - 1, tail3, head3);
        Assertions.assertEquals(2.0f, tail2.x,  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(-1.0f, tail2.y, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f, head2.x,  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, head2.y,  MathUtils.FLOAT_ROUNDING_ERROR);

        // polygon with holes
        Shape2DPolygon polygon2 = new Shape2DPolygon(new float[] { 0, 0, 5, 0, 5, 5, 0, 5, 1, 1, 4, 1, 4, 4, 1, 4 }, new int[] { 4 });

        MathVector2 tail4 = new MathVector2();
        MathVector2 head4 = new MathVector2();
        polygon2.getWorldEdge(0, tail4, head4);
        Assertions.assertEquals(0.0f, tail4.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, tail4.y, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(5.0f, head4.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, head4.y, MathUtils.FLOAT_ROUNDING_ERROR);

        MathVector2 tail5 = new MathVector2();
        MathVector2 head5 = new MathVector2();
        polygon2.getWorldEdge(3, tail5, head5);
        Assertions.assertEquals(0.0f, tail5.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(5.0f, tail5.y, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, head5.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, head5.y, MathUtils.FLOAT_ROUNDING_ERROR);
    }

}