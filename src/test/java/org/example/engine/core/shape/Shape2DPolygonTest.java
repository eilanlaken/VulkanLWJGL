package org.example.engine.core.shape;

import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Shape2DPolygonTest {

    @BeforeAll
    private static void setup() {
    }

    @Test
    void calculateOriginalBoundingRadius() {
    }

    @Test
    void updateWorldCoordinates() {
    }

    @Test
    void getWorldPoints() {
    }

    @Test
    void contains() {
    }

    @Test
    void calculateOriginalArea() {
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

    @Test
    void getWorldVertex() {
        Shape2DPolygon polygon1 = new Shape2DPolygon(new float[] {2,1,1,2,-1,2,-2,1,-2,-1,-1,-2,1,-2,2,-1});

        MathVector2 vertex1 = new MathVector2();
        polygon1.getWorldVertex(0, vertex1);
        Assertions.assertEquals(vertex1.x, 2.0f, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(vertex1.y, 1.0f, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(polygon1.getWorldVertex(0,null).x, 2.0f, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(polygon1.getWorldVertex(0,null).y, 1.0f, MathUtils.FLOAT_ROUNDING_ERROR);

        MathVector2 vertex2 = new MathVector2();
        polygon1.getWorldVertex(-1, vertex2);
        Assertions.assertEquals(vertex2.x, 2.0f, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(vertex2.y, -1.0f, MathUtils.FLOAT_ROUNDING_ERROR);

        MathVector2 vertex3 = new MathVector2();
        polygon1.getWorldVertex(9, vertex3);
        Assertions.assertEquals(vertex3.x, 1.0f, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(vertex3.y, 2.0f, MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void testGetVertexX() {
    }

    @Test
    void testGetVertexY() {
    }
}