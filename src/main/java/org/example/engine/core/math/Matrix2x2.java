package org.example.engine.core.math;

import org.example.engine.core.memory.MemoryPool;

/*
<pre>
    A column major matrix:

    M00 M10
    M01 M11

</pre>
*/
// TODO: test
public class Matrix2x2 implements MemoryPool.Reset {

    public static final int M00 = 0;
    public static final int M10 = 1;
    public static final int M01 = 2;
    public static final int M11 = 3;

    public float[] val = new float[4];

    public Matrix2x2() {
        idt();
    }

    public Matrix2x2(Matrix2x2 matrix) {
        set(matrix);
    }

    /** Constructs a matrix from the given float array. The array must have at least 9 elements; the first 9 will be copied.
     * @param values The float array to copy. Remember that this matrix is in
     *           <a href="http://en.wikipedia.org/wiki/Row-major_order#Column-major_order">column major</a> order. (The float array
     *           is not modified.) */
    public Matrix2x2(float[] values) {
        this.set(values);
    }

    /** Sets this matrix to the identity matrix
     * @return This matrix for the purpose of chaining operations. */
    public Matrix2x2 idt () {
        float[] val = this.val;
        val[M00] = 1;
        val[M10] = 0;
        val[M01] = 0;
        val[M11] = 1;
        return this;
    }

    public Vector2 mul(Vector2 v) {
        Vector2 result = new Vector2();
        result.set(val[M00] * v.x + val[M01] * v.y, val[M10] * v.x + val[M11] * v.y);
        return result;
    }

    public void mul(Vector2 v, Vector2 out) {
        out.set(val[M00] * v.x + val[M01] * v.y, val[M10] * v.x + val[M11] * v.y);
    }

    /** Postmultiplies this matrix with the provided matrix and stores the result in this matrix. For example:
     *
     * <pre>
     * A.mul(B) results in A := AB
     * </pre>
     *
     * @param m Matrix to multiply by.
     * @return This matrix for the purpose of chaining operations together. */
    public Matrix2x2 mul(Matrix2x2 m) {
        float[] val = this.val;

        float v00 = val[M00] * m.val[M00] + val[M01] * m.val[M10];
        float v01 = val[M00] * m.val[M01] + val[M01] * m.val[M11];

        float v10 = val[M10] * m.val[M00] + val[M11] * m.val[M10];
        float v11 = val[M10] * m.val[M01] + val[M11] * m.val[M11];

        val[M00] = v00;
        val[M10] = v10;
        val[M01] = v01;
        val[M11] = v11;

        return this;
    }

//    /** Premultiplies this matrix with the provided matrix and stores the result in this matrix. For example:
//     *
//     * <pre>
//     * A.mulLeft(B) results in A := BA
//     * </pre>
//     *
//     * @param m The other Matrix to multiply by
//     * @return This matrix for the purpose of chaining operations. */
//    public MathMatrix3 mulLeft (MathMatrix3 m) {
//        float[] val = this.val;
//
//        float v00 = m.val[M00] * val[M00] + m.val[M01] * val[M10] + m.val[M02] * val[M20];
//        float v01 = m.val[M00] * val[M01] + m.val[M01] * val[M11] + m.val[M02] * val[M21];
//        float v02 = m.val[M00] * val[M02] + m.val[M01] * val[M12] + m.val[M02] * val[M22];
//
//        float v10 = m.val[M10] * val[M00] + m.val[M11] * val[M10] + m.val[M12] * val[M20];
//        float v11 = m.val[M10] * val[M01] + m.val[M11] * val[M11] + m.val[M12] * val[M21];
//        float v12 = m.val[M10] * val[M02] + m.val[M11] * val[M12] + m.val[M12] * val[M22];
//
//        float v20 = m.val[M20] * val[M00] + m.val[M21] * val[M10] + m.val[M22] * val[M20];
//        float v21 = m.val[M20] * val[M01] + m.val[M21] * val[M11] + m.val[M22] * val[M21];
//        float v22 = m.val[M20] * val[M02] + m.val[M21] * val[M12] + m.val[M22] * val[M22];
//
//        val[M00] = v00;
//        val[M10] = v10;
//        val[M20] = v20;
//        val[M01] = v01;
//        val[M11] = v11;
//        val[M21] = v21;
//        val[M02] = v02;
//        val[M12] = v12;
//        val[M22] = v22;
//
//        return this;
//    }

    /** Sets this matrix to a rotation matrix that will rotate any vector in counter-clockwise direction around the z-axis.
     * @param degrees the angle in degrees.
     * @return This matrix for the purpose of chaining operations. */
    public Matrix2x2 setToRotation (float degrees) {
        return setToRotationRad(MathUtils.degreesToRadians * degrees);
    }

    /** Sets this matrix to a rotation matrix that will rotate any vector in counter-clockwise direction around the z-axis.
     * @param radians the angle in radians.
     * @return This matrix for the purpose of chaining operations. */
    public Matrix2x2 setToRotationRad (float radians) {
        float cos = (float)Math.cos(radians);
        float sin = (float)Math.sin(radians);
        float[] val = this.val;

        val[M00] = cos;
        val[M10] = sin;

        val[M01] = -sin;
        val[M11] = cos;

        return this;
    }

    /** Sets this matrix to a scaling matrix.
     *
     * @param scaleX the scale in x
     * @param scaleY the scale in y
     * @return This matrix for the purpose of chaining operations. */
    public Matrix2x2 setToScaling(float scaleX, float scaleY) {
        float[] val = this.val;
        val[M00] = scaleX;
        val[M10] = 0;
        val[M01] = 0;
        val[M11] = scaleY;
        return this;
    }

    public String toString() {
        float[] val = this.val;
        return "[" + val[M00] + "|" + val[M01] + "]\n" //
                + "[" + val[M10] + "|" + val[M11] + "]\n";
    }

    /** @return The determinant of this matrix */
    public float det() {
        float[] val = this.val;
        return val[M00] * val[M11] - val[M01] + val[M01] * val[M10];
    }

    /** Inverts this matrix given that the determinant is != 0.
     * @return This matrix for the purpose of chaining operations.
     * */
    public Matrix2x2 inv() {
        float det = det();
        if (det == 0) throw new MathException(Matrix2x2.class.getSimpleName() + " " + this + System.lineSeparator() + " not invertible. (det == 0)");

        float inv_det = 1.0f / det;
        float[] val = this.val;

        float a = val[M00];
        float b = val[M01];
        float c = val[M10];
        float d = val[M11];

        val[M00] =  inv_det * d;
        val[M10] = -inv_det * b;
        val[M01] = -inv_det * c;
        val[M11] =  inv_det * a;

        return this;
    }

    /** Copies the values from the provided matrix to this matrix.
     * @param mat The matrix to copy.
     * @return This matrix for the purposes of chaining. */
    public Matrix2x2 set(Matrix2x2 mat) {
        System.arraycopy(mat.val, 0, val, 0, val.length);
        return this;
    }

    /** Sets the matrix to the given matrix as a float array. The float array must have at least 9 elements; the first 9 will be
     * copied.
     *
     * @param values The matrix, in float form, that is to be copied. Remember that this matrix is in
     *           <a href="http://en.wikipedia.org/wiki/Row-major_order#Column-major_order">column major</a> order.
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix2x2 set(float[] values) {
        System.arraycopy(values, 0, val, 0, val.length);
        return this;
    }

    /** Get the values in this matrix.
     * @return The float values that make up this matrix in column-major order. */
    public float[] getValues() {
        return val;
    }

    /** Transposes the current matrix.
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix2x2 transpose() {
        float[] val = this.val;
        float b = val[M01];
        float c = val[M10];

        val[M01] = c;
        val[M10] = b;
        return this;
    }

    /** Multiplies matrix a with matrix b in the following manner:
     *
     * <pre>
     * mul(A, B) => A := AB
     * </pre>
     *
     * @param mata The float array representing the first matrix. Must have at least 9 elements.
     * @param matb The float array representing the second matrix. Must have at least 9 elements. */
    private static void mul(float[] mata, float[] matb) {
        float v00 = mata[M00] * matb[M00] + mata[M01] * matb[M10];
        float v01 = mata[M00] * matb[M01] + mata[M01] * matb[M11];

        float v10 = mata[M10] * matb[M00] + mata[M11] * matb[M10];
        float v11 = mata[M10] * matb[M01] + mata[M11] * matb[M11];

        mata[M00] = v00;
        mata[M10] = v10;
        mata[M01] = v01;
        mata[M11] = v11;
    }

    @Override
    public void reset() {
        this.idt();
    }

}
