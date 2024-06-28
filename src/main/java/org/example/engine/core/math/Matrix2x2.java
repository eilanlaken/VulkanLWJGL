package org.example.engine.core.math;

import org.example.engine.core.memory.MemoryPool;

/*
<pre>
    A column major matrix:

    M00 M01
    M10 M11

</pre>
*/
// TODO: test
public class Matrix2x2 implements MemoryPool.Reset {

    public static final int M00 = 0;
    public static final int M01 = 1;
    public static final int M10 = 2;
    public static final int M11 = 3;

    public float[] val = new float[4];

    public Matrix2x2() {
        idt();
    }

    public Matrix2x2(Matrix2x2 matrix) {
        set(matrix);
    }

    /** Constructs a matrix from the given float array. The array must have at least 4 elements; the first 9 will be copied.
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
        val[M01] = 0;
        val[M10] = 0;
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

    /** Sets this matrix to a rotation matrix that will rotate any vector in counter-clockwise direction around the z-axis.
     * @param degrees the angle in degrees.
     * @return This matrix for the purpose of chaining operations. */
    public Matrix2x2 setToRotationDeg(float degrees) {
        return setToRotationRad(MathUtils.degreesToRadians * degrees);
    }

    /** Sets this matrix to a rotation matrix that will rotate any vector in counter-clockwise direction around the z-axis.
     * @param radians the angle in radians.
     * @return This matrix for the purpose of chaining operations. */
    public Matrix2x2 setToRotationRad(float radians) {
        float cos = (float)Math.cos(radians);
        float sin = (float)Math.sin(radians);
        float[] val = this.val;

        // col 1
        val[M00] = cos;
        val[M10] = sin;

        // col 2
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
        return val[M00] * val[M11] - val[M01] * val[M10];
    }

    /** Inverts this matrix given that the determinant is != 0.
     * @return This matrix for the purpose of chaining operations.
     * */
    public Matrix2x2 inv() {
        float det = det();
        if (MathUtils.isZero(det)) throw new MathException(Matrix2x2.class.getSimpleName() + " " + this + System.lineSeparator() + " not invertible. (det == 0)");

        float inv_det = 1.0f / det;
        float[] val = this.val;

        float a = val[M00];
        float b = val[M01];
        float c = val[M10];
        float d = val[M11];

        val[M00] =  inv_det * d;
        val[M01] = -inv_det * b;
        val[M10] = -inv_det * c;
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
        if (values.length != 4) throw new MathException("Number of arguments must be 4 for a Matrix2x2.set()");
        System.arraycopy(values, 0, val, 0, val.length);
        this.val[M00] = values[0];
        this.val[M10] = values[1];
        this.val[M01] = values[2];
        this.val[M11] = values[3];
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

    /**
     * Solves the system of linear equations:
     * <p style="white-space: pre;"> Ax = B
     * Multiply by A<sup>-1</sup> on both sides
     * x = A<sup>-1</sup>B</p>
     * @param B the B {@link Vector2}
     * @return {@link Vector2} the x vector
     */
    public static void solve22(Matrix2x2 A, Vector2 B, Vector2 out) {
        // get the determinant
        float detInv = A.det();
        // check for zero determinant
        if (Math.abs(detInv) > MathUtils.FLOAT_ROUNDING_ERROR) {
            detInv = 1.0f / detInv;
        } else {
            detInv = 0.0f;
        }

        final float Dx = B.x * A.val[M11] - A.val[M01] * B.y;
        final float Dy = A.val[M00] * B.y - B.x * A.val[M10];

        out.x = detInv * Dx;
        out.y = detInv * Dy;
    }

    @Override
    public void reset() {
        this.idt();
    }

}
