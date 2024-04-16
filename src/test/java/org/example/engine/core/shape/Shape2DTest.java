package org.example.engine.core.shape;

import org.example.engine.core.math.MathUtils;
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
            public boolean contains(float x, float y) {
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
        Assertions.assertEquals(0.0, shape.x(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0, shape.y(), MathUtils.FLOAT_ROUNDING_ERROR);

        shape.dx(30.0f);
        shape.dx(15.0f);
        Assertions.assertEquals(45.0, shape.x(), MathUtils.FLOAT_ROUNDING_ERROR);

    }

    @Test
    void dy() {
    }

    @Test
    void dx_dy_rot() {

    }

    @Test
    void rot() {

    }

    @Test
    void x() {
    }

    @Test
    void y() {
    }

    @Test
    void xy() {
    }

    @Test
    void angle() {
    }

    @Test
    void scaleX() {
    }

    @Test
    void scaleY() {
    }

    @Test
    void scaleXY() {
    }

    @Test
    void transform() {
    }

    @Test
    void testX() {
    }

    @Test
    void testY() {
    }

    @Test
    void testAngle() {
    }

    @Test
    void testScaleX() {
    }

    @Test
    void testScaleY() {
    }
}