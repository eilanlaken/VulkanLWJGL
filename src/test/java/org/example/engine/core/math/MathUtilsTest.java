package org.example.engine.core.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MathUtilsTest {

    @Test
    void random() {
    }

    @Test
    void testRandom() {
    }

    @Test
    void normalizeAngleDeg() {
        Assertions.assertEquals(0.0f, MathUtils.normalizeAngleDeg(0.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, MathUtils.normalizeAngleDeg(360.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, MathUtils.normalizeAngleDeg(1.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(359.0f, MathUtils.normalizeAngleDeg(-1.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(20.0f, MathUtils.normalizeAngleDeg(380.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(20.0f, MathUtils.normalizeAngleDeg(380.0f + 360.0f * 5), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void normalizeAngleRad() {
        Assertions.assertEquals(0.0f, MathUtils.normalizeAngleRad(0.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, MathUtils.normalizeAngleRad(MathUtils.PI2), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, MathUtils.normalizeAngleRad(1.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.1f, MathUtils.normalizeAngleRad(MathUtils.PI2 + 0.1f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(MathUtils.PI2 - 0.1f, MathUtils.normalizeAngleRad(-0.1f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.1f, MathUtils.normalizeAngleRad(MathUtils.PI2 * 3 + 0.1f), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    public void clampFloat() {
        float v1 = MathUtils.clampFloat(0.0f, -1.0f, 1.0f);
        Assertions.assertEquals(0.0f, v1, MathUtils.FLOAT_ROUNDING_ERROR);

        float v2 = MathUtils.clampFloat(-3.0f, -1.0f, 1.0f);
        Assertions.assertEquals(-1.0f, v2, MathUtils.FLOAT_ROUNDING_ERROR);

        float v3 = MathUtils.clampFloat(2.0f, -1.0f, 1.0f);
        Assertions.assertEquals(1.0f, v3, MathUtils.FLOAT_ROUNDING_ERROR);

        float v4 = MathUtils.clampFloat(0.0f, 1.0f, -1.0f);
        Assertions.assertEquals(0.0f, v4, MathUtils.FLOAT_ROUNDING_ERROR);

        float v5 = MathUtils.clampFloat(8.0f, -1.0f, 10.0f);
        Assertions.assertEquals(8.0f, v5, MathUtils.FLOAT_ROUNDING_ERROR);

        float v6 = MathUtils.clampFloat(4.0f, 3.0f, -1.0f);
        Assertions.assertEquals(3.0f, v6, MathUtils.FLOAT_ROUNDING_ERROR);

        float v7 = MathUtils.clampFloat(2.0f, 2.0f, 2.0f);
        Assertions.assertEquals(2.0f, v7, MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void nextPowerOfTwo() {
    }

    @Test
    void atanUnchecked() {
    }

    @Test
    void atan2() {
    }

    @Test
    void areaTriangle() {
    }

    @Test
    void testAreaTriangle() {
    }

    @Test
    void max() {
    }

    @Test
    void testMax() {
    }

    @Test
    void sin() {
    }

    @Test
    void cos() {
    }

    @Test
    void sinDeg() {
    }

    @Test
    void cosDeg() {
    }

    @Test
    void tan() {
    }

    @Test
    void acos() {
    }

    @Test
    void asin() {
    }

    @Test
    void tanDeg() {
    }

    @Test
    void atan() {
    }

    @Test
    void asinDeg() {
    }

    @Test
    void acosDeg() {
    }

    @Test
    void atanDeg() {
    }

    @Test
    void isZero() {
    }

    @Test
    void testIsZero() {
    }

    @Test
    void isEqual() {
    }

    @Test
    void testIsEqual() {
    }

    @Test
    void log() {
    }

    @Test
    void testRandom1() {
    }

    @Test
    void testRandom2() {
    }

    @Test
    void testClamp4() {
    }

    @Test
    void testClamp5() {
    }

    @Test
    void testClamp6() {
    }

    @Test
    void testClamp7() {
    }

    @Test
    void testClamp8() {
    }

    @Test
    void testNextPowerOfTwo() {
    }

    @Test
    void testAtanUnchecked() {
    }

    @Test
    void testAtan2() {
    }

    @Test
    void testAreaTriangle1() {
    }

    @Test
    void testAreaTriangle2() {
    }

    @Test
    void testMax1() {
    }

    @Test
    void testMax2() {
    }

    @Test
    void min() {
    }

    @Test
    void testMin() {
    }

    @Test
    void intervalsOverlap() {
        Assertions.assertEquals(0.0f, MathUtils.intervalsOverlap(0.0f, 1.0f, 2.0f, 4.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, MathUtils.intervalsOverlap(9.0f, 8.0f, 4.0f, 2.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f, MathUtils.intervalsOverlap(0.0f, 4.0f, 1.0f, 3.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(3.0f, MathUtils.intervalsOverlap(1.0f, 5.0f, 2.0f, 6.5f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.2f, MathUtils.intervalsOverlap(-1.2f, 1.2f,0.0f, 1.2f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f, MathUtils.intervalsOverlap(2.0f, 4.0f, 2.0f, 4.0f), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void testSin() {
    }

    @Test
    void testCos() {
    }

    @Test
    void testSinDeg() {
    }

    @Test
    void testCosDeg() {
    }

    @Test
    void testTan() {
    }

    @Test
    void testAcos() {
    }

    @Test
    void testAsin() {
    }

    @Test
    void testTanDeg() {
    }

    @Test
    void testAtan() {
    }

    @Test
    void testAsinDeg() {
    }

    @Test
    void testAcosDeg() {
    }

    @Test
    void testAtanDeg() {
    }

    @Test
    void testIsZero1() {
    }

    @Test
    void testIsZero2() {
    }

    @Test
    void testIsEqual1() {
    }

    @Test
    void testIsEqual2() {
    }

    @Test
    void testLog() {
    }
}