package org.example.engine.core.shape;

import org.example.engine.core.math.MathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// TODO: continue
class Shape3DTest {

    private static Shape3D shape;

    @BeforeEach
    private void setup() {
        shape = new Shape3D() {
            @Override
            public boolean contains(float x, float y, float z) {
                return false;
            }

            @Override
            protected void updateWorldCoordinates() {

            }

            @Override
            protected float getUnscaledBoundingRadius() {
                return 0;
            }
        };
    }

    @Test
    void testInit() {
        Assertions.assertEquals(0.0,  shape.x(),      MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0,  shape.y(),      MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0,  shape.z(),      MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, shape.scaleX(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, shape.scaleY(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, shape.scaleZ(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, shape.angleX(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, shape.angleY(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, shape.angleZ(), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void dx() {
        shape.dx(-1);
        Assertions.assertEquals(-1.0f, shape.x(), MathUtils.FLOAT_ROUNDING_ERROR);
        shape.dx(-1);
        Assertions.assertEquals(-2.0f, shape.x(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f,  shape.y(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f,  shape.z(), MathUtils.FLOAT_ROUNDING_ERROR);
        shape.x(-8.0f);
        Assertions.assertEquals(-8.0f, shape.x(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f,  shape.y(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f,  shape.z(), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void dy() {
        shape.dy(-1.2f);
        Assertions.assertEquals(-1.2f, shape.y(), MathUtils.FLOAT_ROUNDING_ERROR);
        shape.dy(5.0f);
        Assertions.assertEquals(0.0f,  shape.x(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(3.8f, shape.y(),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f,  shape.z(), MathUtils.FLOAT_ROUNDING_ERROR);
        shape.y(9.0f);
        Assertions.assertEquals(0.0f, shape.x(),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(9.0f,  shape.y(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f,  shape.z(), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void dz() {
        shape.dz(0.0f);
        Assertions.assertEquals(0f, shape.z(),   MathUtils.FLOAT_ROUNDING_ERROR);
        shape.dz(5.0f);
        Assertions.assertEquals(0.0f, shape.x(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, shape.y(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(5.0f, shape.z(), MathUtils.FLOAT_ROUNDING_ERROR);
        shape.z(0.2f);
        Assertions.assertEquals(0.0f, shape.x(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, shape.y(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.2f, shape.z(), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void dx_dy_dz() {
        shape.dx_dy_dz(-1.0f,-2.0f,3.0f);
        Assertions.assertEquals(-1.0f, shape.x(),      MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(-2.0f, shape.y(),      MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(3.0f,  shape.z(),      MathUtils.FLOAT_ROUNDING_ERROR);

        Assertions.assertEquals(0.0f,  shape.angleX(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f,  shape.angleY(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f,  shape.angleZ(), MathUtils.FLOAT_ROUNDING_ERROR);

        Assertions.assertEquals(1.0f,  shape.scaleX(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f,  shape.scaleY(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f,  shape.scaleZ(), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void rotX() {
        shape.rotX(30.0f);
        Assertions.assertEquals(30.0f, shape.angleX(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals( 0.0f, shape.angleY(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals( 0.0f, shape.angleZ(), MathUtils.FLOAT_ROUNDING_ERROR);

        shape.rotX(-30.0f);
        Assertions.assertEquals(0.0f, shape.angleX(),   MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, shape.angleY(),   MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, shape.angleZ(),   MathUtils.FLOAT_ROUNDING_ERROR);

        shape.rotX(Float.NaN);
        Assertions.assertEquals(Float.NaN, shape.angleX(),      MathUtils.FLOAT_ROUNDING_ERROR);
        shape.rotX(-Float.NaN);
        Assertions.assertEquals(Float.NaN, shape.angleX(),      MathUtils.FLOAT_ROUNDING_ERROR);

        shape.angleX(0);
        shape.rotX(-30);
        Assertions.assertEquals(330.0f, shape.angleX(), MathUtils.FLOAT_ROUNDING_ERROR);

        shape.angleX(0);
        shape.rotX(360);
        Assertions.assertEquals(0.0f, shape.angleX(),   MathUtils.FLOAT_ROUNDING_ERROR);

        shape.angleX(0);
        shape.rotX(390.0f);
        Assertions.assertEquals(30.0f, shape.angleX(),  MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void rotY() {
        shape.rotY(30.0f);
        Assertions.assertEquals( 0.0f, shape.angleX(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(30.0f, shape.angleY(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals( 0.0f, shape.angleZ(), MathUtils.FLOAT_ROUNDING_ERROR);

        shape.rotY(-30.0f);
        Assertions.assertEquals(0.0f, shape.angleX(),  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(00.0f, shape.angleY(), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, shape.angleZ(),  MathUtils.FLOAT_ROUNDING_ERROR);

        shape.rotY(Float.NaN);
        Assertions.assertEquals(Float.NaN, shape.angleY(),     MathUtils.FLOAT_ROUNDING_ERROR);
        shape.rotY(-Float.NaN);
        Assertions.assertEquals(Float.NaN, shape.angleY(),     MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void rotZ() {
    }

    @Test
    void x() {
    }

    @Test
    void y() {
    }

    @Test
    void z() {
    }

    @Test
    void xyz() {
    }

    @Test
    void angleX() {
    }

    @Test
    void angleY() {
    }

    @Test
    void angleZ() {
    }

    @Test
    void scaleX() {
    }

    @Test
    void scaleY() {
    }

    @Test
    void scaleZ() {
    }

    @Test
    void scaleXYZ() {
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
    void testZ() {
    }

    @Test
    void testAngleX() {
    }

    @Test
    void testAngleY() {
    }

    @Test
    void testAngleZ() {
    }

    @Test
    void testScaleX() {
    }

    @Test
    void testScaleY() {
    }

    @Test
    void testScaleZ() {
    }
}