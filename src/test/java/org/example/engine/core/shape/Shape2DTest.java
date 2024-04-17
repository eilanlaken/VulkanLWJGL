package org.example.engine.core.shape;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Shape2DTest {

    private static Shape2D shape;

    @BeforeEach
    private void setup() {
        shape = new Shape2D() {
            @Override
            public boolean containsPoint(float x, float y) {
                return false;
            }

            @Override
            protected void updateWorldCoordinates() {

            }

            @Override
            protected float getUnscaledArea() {
                return 0;
            }

            @Override
            protected float getUnscaledBoundingRadius() {
                return 0;
            }

            @Override
            protected CollectionsArray<MathVector2> getWorldVertices() {
                return null;
            }
        };
    }

    @Test
    void testInit() {
        Assertions.assertEquals(0.0, shape.x(),      MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0, shape.y(),      MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0, shape.angle(),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0, shape.scaleX(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0, shape.scaleY(), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void dx() {
        shape.dx(0.0f);
        Assertions.assertEquals(0.0, shape.x(),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0, shape.y(),  MathUtils.FLOAT_ROUNDING_ERROR);
        shape.dx(30.0f);
        shape.dx(15.0f);
        Assertions.assertEquals(45.0, shape.x(), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void dy() {
        shape.dy(Float.MAX_VALUE);
        Assertions.assertEquals(0.0,     shape.x(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(Float.MAX_VALUE, shape.y(), MathUtils.FLOAT_ROUNDING_ERROR);
        shape.dy(-Float.MAX_VALUE);
        Assertions.assertEquals(0.0f, shape.y(),    MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void dx_dy_rot() {
        shape.dx_dy_rot(1.0f, -1.0f, 30.0f);
        Assertions.assertEquals(1.0, shape.x(),      MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(-1.0, shape.y(),     MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(30.0, shape.angle(), MathUtils.FLOAT_ROUNDING_ERROR);
        shape.dx_dy_rot(1.0f, -1.0f, 30.0f);
        Assertions.assertEquals(2.0, shape.x(),      MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(-2.0, shape.y(),     MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(60.0, shape.angle(), MathUtils.FLOAT_ROUNDING_ERROR);
        shape.dx_dy_rot(1.0f, -1.0f, 360.0f);
        Assertions.assertEquals(60.0, shape.angle(), MathUtils.FLOAT_ROUNDING_ERROR);
        shape.dx_dy_rot(1.0f, -1.0f, -70.0f);
        Assertions.assertEquals(350, shape.angle(),  MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void rot() {
        shape.rot(Float.MAX_VALUE);
        Assertions.assertAll("value should be in range",
                () -> assertTrue(shape.angle() >= 0),
                () -> assertTrue(shape.angle() < 360)
        );
        shape.angle(0);
        shape.rot(-450.0f);
        Assertions.assertEquals(270.0f, shape.angle(), MathUtils.FLOAT_ROUNDING_ERROR);
        shape.rot(90.0f);
        Assertions.assertEquals(0.0f, shape.angle(),   MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void testSetTransform() {
        shape.setTransform(0,0,90,-1,1);
        Assertions.assertEquals(90.0f, shape.angle(),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(-1.0f, shape.scaleX(), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void testX() {
        shape.x(-88.5f);
        Assertions.assertEquals(-88.5f, shape.x(), MathUtils.FLOAT_ROUNDING_ERROR);
        shape.x(-3.3f);
        Assertions.assertEquals(-3.3f, shape.x(),  MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void testY() {
        shape.y(-88.5f);
        Assertions.assertEquals(-88.5f, shape.y(), MathUtils.FLOAT_ROUNDING_ERROR);
        shape.y(-3.3f);
        Assertions.assertEquals(-3.3f, shape.y(),  MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void testAngle() {
        shape.angle(-0f);
        Assertions.assertEquals(0.0f, shape.angle(),    MathUtils.FLOAT_ROUNDING_ERROR);
        shape.angle(30f);
        Assertions.assertEquals(30.0f, shape.angle(),   MathUtils.FLOAT_ROUNDING_ERROR);
        shape.angle(370.0f);
        Assertions.assertEquals(10.0f, shape.angle(),   MathUtils.FLOAT_ROUNDING_ERROR);
        shape.angle(-88.5f);
        Assertions.assertEquals(271.5f, shape.angle(),  MathUtils.FLOAT_ROUNDING_ERROR);
        shape.angle(450.0f);
        Assertions.assertEquals(90.0f, shape.angle(),   MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void testScaleX() {
        shape.scaleX(0);
        Assertions.assertEquals(0.0f, shape.scaleX(),   MathUtils.FLOAT_ROUNDING_ERROR);
        shape.scaleX(Float.NaN);
        Assertions.assertEquals(Float.NaN, shape.scaleX(),       MathUtils.FLOAT_ROUNDING_ERROR);
        shape.scaleX(1.0f);
        Assertions.assertEquals(1.0f, shape.scaleX(),   MathUtils.FLOAT_ROUNDING_ERROR);
        shape.scaleX(Float.MAX_VALUE);
        Assertions.assertEquals(Float.MAX_VALUE, shape.scaleX(), MathUtils.FLOAT_ROUNDING_ERROR);
        shape.scaleX(Float.MIN_VALUE);
        Assertions.assertEquals(Float.MIN_VALUE, shape.scaleX(), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void testScaleY() {
        shape.scaleY(0);
        Assertions.assertEquals(0.0f, shape.scaleY(),   MathUtils.FLOAT_ROUNDING_ERROR);
        shape.scaleY(Float.NaN);
        Assertions.assertEquals(Float.NaN, shape.scaleY(),       MathUtils.FLOAT_ROUNDING_ERROR);
        shape.scaleY(1.0f);
        Assertions.assertEquals(1.0f, shape.scaleY(),   MathUtils.FLOAT_ROUNDING_ERROR);
        shape.scaleY(Float.MAX_VALUE);
        Assertions.assertEquals(Float.MAX_VALUE, shape.scaleY(), MathUtils.FLOAT_ROUNDING_ERROR);
        shape.scaleY(Float.MIN_VALUE);
        Assertions.assertEquals(Float.MIN_VALUE, shape.scaleY(), MathUtils.FLOAT_ROUNDING_ERROR);
    }

}