package org.example.engine.core.shape;

import org.example.engine.core.math.MathUtils;
import org.example.engine.core.shape.Shape2DRectangle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class Shape2DRectangleTest {

    private static Shape2DRectangle rectangle1;
    private static Shape2DRectangle rectangle2;
    private static Shape2DRectangle rectangle3;
    private static Shape2DRectangle rectangle4;
    private static Shape2DRectangle rectangle5;

    @BeforeAll
    private static void setup() {
        rectangle1 = new Shape2DRectangle(4,2);
        rectangle2 = new Shape2DRectangle(5,5);
        rectangle2.xy(10,0);
        rectangle3 = new Shape2DRectangle(3,2);
        rectangle3.angle(90);
        rectangle4 = new Shape2DRectangle(2,2,3,6,0);
        rectangle5 = new Shape2DRectangle(1,1,2,2,45);
    }

    @Test
    void calculateOriginalBoundingRadius() {
    }

    @Test
    void contains() {
        Assertions.assertTrue(rectangle1.contains(0,0));
        Assertions.assertTrue(rectangle1.contains(-1.5f,0.5f));
        Assertions.assertFalse(rectangle1.contains(-1f,5.5f));

        Assertions.assertTrue(rectangle2.contains(12,2.5f));
        Assertions.assertTrue(rectangle2.contains(12.3f,-2.4f));
        Assertions.assertFalse(rectangle2.contains(0,0.5f));

        Assertions.assertTrue(rectangle3.contains(1.0f,1.4f));
        Assertions.assertTrue(rectangle3.contains(0f,-0.5f));
        Assertions.assertFalse(rectangle3.contains(1.5f,1.0f));

        Assertions.assertTrue(rectangle4.contains(1.0f,1.0f));
        Assertions.assertTrue(rectangle4.contains(3.5f,5.0f));
        Assertions.assertFalse(rectangle4.contains(-3.0f,11.0f));

        Assertions.assertTrue(rectangle5.contains(1.0f,1.0f));
        Assertions.assertTrue(rectangle5.contains(1.5f,1.5f));
        Assertions.assertFalse(rectangle5.contains(2.0f,2.0f));
    }

    @Test
    void updateWorldCoordinates() {
    }

    @Test
    void c1() {
    }

    @Test
    void c2() {
    }

    @Test
    void c3() {
    }

    @Test
    void c4() {
    }

    @Test
    void getUnscaledBoundingRadius() {
    }

    @Test
    void getUnscaledArea() {
        Assertions.assertEquals(8,  rectangle1.getUnscaledArea(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(25, rectangle2.getUnscaledArea(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(6,  rectangle3.getUnscaledArea(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(18, rectangle4.getUnscaledArea(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(4,  rectangle5.getUnscaledArea(), MathUtils.FLOAT_ROUNDING_ERROR);
    }


}