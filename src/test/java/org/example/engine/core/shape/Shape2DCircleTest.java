package org.example.engine.core.shape;

import org.example.engine.core.math.MathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Shape2DCircleTest {

    private static Shape2DCircle circle_1;
    private static Shape2DCircle circle_2;
    private static Shape2DCircle circle_3;
    private static Shape2DCircle circle_4;
    private static Shape2DCircle circle_5;
    private static Shape2DCircle circle_6;
    private static Shape2DCircle circle_7;

    @BeforeEach
    private void setup() {
        circle_1 = new Shape2DCircle(1);

        circle_2 = new Shape2DCircle(2);

        circle_3 = new Shape2DCircle(1,1,1);

        circle_4 = new Shape2DCircle(1);
        circle_4.scaleXY(2.0f, 2.0f);

        circle_5 = new Shape2DCircle(5.0f);
        circle_5.dx_dy_rot(1,1,90);

        circle_6 = new Shape2DCircle(6.0f);
        circle_6.scaleXY(-2.0f, -2.0f);

        circle_7 = new Shape2DCircle(1, 2, 0);
        circle_7.setTransform(-3.0f,-3.0f,90.0f, 2.0f, 2.0f);
    }

    @Test
    void getBoundingRadius() {
        Assertions.assertEquals(1.0f,  circle_1.getBoundingRadius(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f,  circle_2.getBoundingRadius(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f + (float) Math.sqrt(2.0f), circle_3.getBoundingRadius(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f,  circle_4.getBoundingRadius(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(5.0f,  circle_5.getBoundingRadius(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(12.0f, circle_6.getBoundingRadius(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(6.0f,  circle_7.getBoundingRadius(), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void contains() {
        Assertions.assertTrue (circle_1.contains( 0   ,0));
        Assertions.assertTrue (circle_1.contains( 1.0f,0));
        Assertions.assertFalse(circle_1.contains( 2.0f,0));
        Assertions.assertFalse(circle_1.contains(-2.0f,-2.0f));

        Assertions.assertTrue (circle_2.contains( 0   ,0));
        Assertions.assertTrue (circle_2.contains( 1.0f,0));
        Assertions.assertFalse(circle_2.contains( 3.5f,0));
        Assertions.assertFalse(circle_2.contains(-10.0f,-2.0f));

        Assertions.assertTrue (circle_3.contains( 1 ,1));
        Assertions.assertTrue (circle_3.contains( 1.0f,1.2f));
        Assertions.assertFalse(circle_3.contains( 0,0));
        Assertions.assertFalse(circle_3.contains(-2.0f,-2.0f));

        Assertions.assertTrue (circle_4.contains( 0   ,0));
        Assertions.assertTrue (circle_4.contains( 2.0f,0));
        Assertions.assertFalse(circle_4.contains( 4.0f,0));
        Assertions.assertFalse(circle_4.contains(-5.0f,-2.0f));

        Assertions.assertTrue (circle_5.contains( 0   ,0));
        Assertions.assertTrue (circle_5.contains( 2.5f,2.5f));
        Assertions.assertFalse(circle_5.contains( -4.5f,0));
        Assertions.assertFalse(circle_5.contains(Float.POSITIVE_INFINITY,-2.0f));

        Assertions.assertTrue (circle_6.contains( 0   ,0));
        Assertions.assertTrue (circle_6.contains( 1.0f,0));
        Assertions.assertFalse(circle_6.contains( 24.0f,0));
        Assertions.assertFalse(circle_6.contains(-24.0f,-2.0f));

        Assertions.assertTrue (circle_7.contains( -3.0f,1.0f)); // center
        Assertions.assertTrue (circle_7.contains( -5.0f,1.0f));
        Assertions.assertTrue (circle_7.contains( -1.0f,1.0f));
        Assertions.assertTrue (circle_7.contains( -3.0f,3.0f));
        Assertions.assertTrue (circle_7.contains( -3.0f,-1.0f));
        Assertions.assertFalse(circle_7.contains(  2.0f, Float.POSITIVE_INFINITY));
        Assertions.assertFalse(circle_7.contains(-20.0f,-2.0f));
    }

    @Test
    void area() {
        Assertions.assertEquals(MathUtils.PI * circle_1.getWorldRadius() * circle_1.getWorldRadius(), circle_1.getArea(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(MathUtils.PI * circle_2.getWorldRadius() * circle_2.getWorldRadius(), circle_2.getArea(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(MathUtils.PI * circle_3.getWorldRadius() * circle_3.getWorldRadius(), circle_3.getArea(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(MathUtils.PI * circle_4.getWorldRadius() * circle_4.getWorldRadius(), circle_4.getArea(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(MathUtils.PI * circle_5.getWorldRadius() * circle_5.getWorldRadius(), circle_5.getArea(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(MathUtils.PI * circle_6.getWorldRadius() * circle_6.getWorldRadius(), circle_6.getArea(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(MathUtils.PI * circle_7.getWorldRadius() * circle_7.getWorldRadius(), circle_7.getArea(), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void geometryCenter() {
        Assertions.assertTrue(circle_1.getWorldCenter().epsilonEquals(circle_1.geometryCenter(), MathUtils.FLOAT_ROUNDING_ERROR));
        Assertions.assertTrue(circle_2.getWorldCenter().epsilonEquals(circle_2.geometryCenter(), MathUtils.FLOAT_ROUNDING_ERROR));
        Assertions.assertTrue(circle_3.getWorldCenter().epsilonEquals(circle_3.geometryCenter(), MathUtils.FLOAT_ROUNDING_ERROR));
        Assertions.assertTrue(circle_4.getWorldCenter().epsilonEquals(circle_4.geometryCenter(), MathUtils.FLOAT_ROUNDING_ERROR));
        Assertions.assertTrue(circle_5.getWorldCenter().epsilonEquals(circle_5.geometryCenter(), MathUtils.FLOAT_ROUNDING_ERROR));
        Assertions.assertTrue(circle_6.getWorldCenter().epsilonEquals(circle_6.geometryCenter(), MathUtils.FLOAT_ROUNDING_ERROR));
        Assertions.assertTrue(circle_7.getWorldCenter().epsilonEquals(circle_7.geometryCenter(), MathUtils.FLOAT_ROUNDING_ERROR));
    }

}