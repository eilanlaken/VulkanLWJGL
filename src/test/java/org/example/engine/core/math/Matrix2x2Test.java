package org.example.engine.core.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.example.engine.core.math.Matrix2x2.*;
import static org.junit.jupiter.api.Assertions.*;

class Matrix2x2Test {

    @Test
    void idt() {
        Matrix2x2 idt = new Matrix2x2();
        Assertions.assertEquals(1, idt.val[M00], MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0, idt.val[M01], MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0, idt.val[M10], MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1, idt.val[M11], MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 idt2 = new Matrix2x2(new float[] {1,2,3,4});
        idt2.idt();
        Assertions.assertEquals(1, idt2.val[M00], MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0, idt2.val[M01], MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0, idt2.val[M10], MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1, idt2.val[M11], MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void mul() {
        Matrix2x2 idt = new Matrix2x2();
        Vector2 a = new Vector2(1,2);
        Vector2 a1 = idt.mul(a);
        Assertions.assertEquals(1, a1.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2, a1.y, MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 m1 = new Matrix2x2();
        m1.set(new float[] {0,0,0,0});
        Vector2 a2 = m1.mul(a);
        Assertions.assertEquals(0, a2.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0, a2.x, MathUtils.FLOAT_ROUNDING_ERROR);

        Vector2 b = new Vector2(1,1);
        Matrix2x2 m2 = new Matrix2x2(new float[] {1, -1, 2, 4});
        Vector2 m2xb = m2.mul(b);
        Assertions.assertEquals(3, m2xb.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(3, m2xb.y, MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 m3 = new Matrix2x2(new float[] {2,0,0,2});
        Vector2 m3xb = m3.mul(b);
        Assertions.assertEquals(2, m3xb.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2, m3xb.y, MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void testMul() {
        Matrix2x2 m1 = new Matrix2x2(new float[] {1,3,-4,-1});
        Matrix2x2 m2 = new Matrix2x2(new float[] {1,3,-4,-1});
        Matrix2x2 m1xm2 = m1.mul(m2);

        Assertions.assertEquals(-11, m1xm2.val[M00], MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0, m1xm2.val[M10],   MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0, m1xm2.val[M01],   MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(-11, m1xm2.val[M11], MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 m3 = new Matrix2x2(new float[] {-2, 0, 1, 5});
        Matrix2x2 m4 = new Matrix2x2(new float[] {-9, 8, 7, 1});
        Matrix2x2 m3xm4 = m3.mul(m4);
        Assertions.assertEquals(26, m3xm4.val[M00],  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(40, m3xm4.val[M10],  MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(-13, m3xm4.val[M01], MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(5, m3xm4.val[M11],   MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void setToRotationDeg() {
        Matrix2x2 rot1 = new Matrix2x2();
        rot1.setToRotationDeg(0);
        Assertions.assertEquals(1, rot1.val[M00], MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0, rot1.val[M10], MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0, rot1.val[M01], MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1, rot1.val[M11], MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 rot2 = new Matrix2x2();
        rot2.setToRotationDeg(90);
        Assertions.assertEquals(0,  rot2.val[M00], MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1,  rot2.val[M10], MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(-1, rot2.val[M01], MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0,  rot2.val[M11], MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void det() {

    }

    @Test
    void setToScaling() {
    }

    @Test
    void inv() {
    }

    @Test
    void transpose() {
    }

}