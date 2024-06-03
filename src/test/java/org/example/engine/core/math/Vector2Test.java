package org.example.engine.core.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class Vector2Test {

    @Test
    void cpy() {
    }

    @Test
    void len() {
    }

    @Test
    void testLen() {
    }

    @Test
    void len2() {
    }

    @Test
    void testLen2() {
    }

    @Test
    void zero() {
    }

    @Test
    void set() {
    }

    @Test
    void testSet() {
    }

    @Test
    void sub() {
    }

    @Test
    void testSub() {
    }

    @Test
    void nor() {
    }

    @Test
    void add() {
    }

    @Test
    void testAdd() {
    }

    @Test
    void dot() {
    }

    @Test
    void testDot() {
    }

    @Test
    void testDot1() {
    }

    @Test
    void testDot2() {
    }

    @Test
    void scl() {
    }

    @Test
    void testScl() {
    }

    @Test
    void testScl1() {
    }

    @Test
    void mulAdd() {
    }

    @Test
    void testMulAdd() {
    }

    @Test
    void idt() {
    }

    @Test
    void dst() {
    }

    @Test
    void testDst() {
    }

    @Test
    void dst2() {
    }

    @Test
    void testDst2() {
    }

    @Test
    void limit() {
    }

    @Test
    void limit2() {
    }

    @Test
    void clamp() {
        Vector2 v1 = new Vector2(3,3);
        Vector2 min1 = new Vector2(1,1);
        Vector2 max1 = new Vector2(4,5);
        v1.clamp(min1, max1);
        Assertions.assertEquals(3, v1.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(3, v1.y, MathUtils.FLOAT_ROUNDING_ERROR);

        Vector2 v2 = new Vector2(3,6);
        Vector2 min2 = new Vector2(1,1);
        Vector2 max2 = new Vector2(2,2);
        v2.clamp(min2, max2);
        Assertions.assertEquals(2, v2.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2, v2.y, MathUtils.FLOAT_ROUNDING_ERROR);

        Vector2 v3 = new Vector2(-1,0);
        Vector2 min3 = new Vector2();
        Vector2 max3 = new Vector2();
        v3.clamp(min3, max3);
        Assertions.assertEquals(0, v3.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0, v3.y, MathUtils.FLOAT_ROUNDING_ERROR);

        Vector2 v4 = new Vector2(-3,3);
        Vector2 min4 = new Vector2(2,6);
        Vector2 max4 = new Vector2(-2,4);
        v4.clamp(min4, max4);
        Assertions.assertEquals(-2, v4.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(4, v4.y, MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void setLength() {
    }

    @Test
    void setLength2() {
    }

    @Test
    void fromString() {
    }

    @Test
    void crs() {
    }

    @Test
    void testCrs() {
    }

    @Test
    void angleDeg() {
    }

    @Test
    void testAngleDeg() {
    }

    @Test
    void testAngleDeg1() {
    }

    @Test
    void angleRad() {
    }

    @Test
    void testAngleRad() {
    }

    @Test
    void testAngleRad1() {
    }

    @Test
    void setAngleDeg() {
    }

    @Test
    void setAngleRad() {
    }

    @Test
    void rotateDeg() {
    }

    @Test
    void rotateRad() {
    }

    @Test
    void rotateAroundDeg() {
    }

    @Test
    void rotateAroundRad() {
    }

    @Test
    void rotate90() {
    }

    @Test
    void testClamp() {
    }

    @Test
    void lerp() {
    }

    @Test
    void epsilonEquals() {
    }

    @Test
    void testEpsilonEquals() {
    }

    @Test
    void testEpsilonEquals1() {
    }

    @Test
    void testEpsilonEquals2() {
    }

    @Test
    void isUnit() {
    }

    @Test
    void testIsUnit() {
    }

    @Test
    void isZero() {
    }

    @Test
    void testIsZero() {
    }

    @Test
    void isOnLine() {
    }

    @Test
    void isPerpendicular() {
    }

    @Test
    void testDst1() {
    }

    @Test
    void testDst3() {
    }

    @Test
    void testDst21() {
    }

    @Test
    void testDst22() {
    }
}