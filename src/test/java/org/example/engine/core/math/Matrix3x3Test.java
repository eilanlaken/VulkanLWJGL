package org.example.engine.core.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class Matrix3x3Test {

    @Test
    void solve3x3() {
        Matrix3x3 I = new Matrix3x3();
        Vector3 b = new Vector3(3,4,5);
        Vector3 solution = new Vector3();
        Matrix3x3.solve3x3(I, b, solution);
        Assertions.assertEquals(3, solution.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(4, solution.y, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(5, solution.z, MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix3x3 zero = new Matrix3x3();
        zero.set(new float[] {0,0,0,0,0,0,0,0,0});
        Matrix3x3.solve3x3(zero, b, solution);
        Assertions.assertEquals(0, solution.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0, solution.y, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0, solution.z, MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix3x3 m1 = new Matrix3x3();
        m1.set(9, -5, 7, 4, -3, 1, -6, 6, -3);
        b.set(-6, -5, -5);
        Matrix3x3.solve3x3(m1, b, solution);
        Assertions.assertEquals(-2, solution.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(-3, solution.y, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(-4, solution.z, MathUtils.FLOAT_ROUNDING_ERROR);
    }

}