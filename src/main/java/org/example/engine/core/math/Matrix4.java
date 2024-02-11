package org.example.engine.core.math;

/*
<pre>
    A column major matrix:

    M00 M10 M20 M30
    M01 M11 M21 M31
    M02 M12 M22 M32
    M03 M13 M23 M33

</pre>
*/

public class Matrix4 {

    protected final Matrix4 tmpMtx = new Matrix4();

    public static final int M00 = 0;
    public static final int M01 = 4;
    public static final int M02 = 8;
    public static final int M03 = 12;

    public static final int M10 = 1;
    public static final int M11 = 5;
    public static final int M12 = 9;
    public static final int M13 = 13;

    public static final int M20 = 2;
    public static final int M21 = 6;
    public static final int M22 = 10;
    public static final int M23 = 14;

    public static final int M30 = 3;
    public static final int M31 = 7;
    public static final int M32 = 11;
    public static final int M33 = 15;

    public final float val[] = new float[16];

    public Matrix4() {
        val[M00] = 1;
        val[M11] = 1;
        val[M22] = 1;
        val[M33] = 1;
    }

    public Matrix4(final Matrix4 matrix4) {
        System.arraycopy(matrix4.val, 0, val, 0, 16);
    }

    public Matrix4 setTo(final Matrix4 matrix4) {
        System.arraycopy(matrix4.val, 0, val, 0, 16);
        return this;
    }

    public Matrix4 setToTranslationRotationScale(Vector3 position, Quaternion rotation, Vector3 scale) {
        return setToTranslationRotationScale(position.x, position.y, position.z,
                rotation.x, rotation.y, rotation.z, rotation.w,
                scale.x, scale.y, scale.z);
    }

    public Matrix4 setToTranslationRotationScale(float dx, float dy, float dz, float qx, float qy,
                                                 float qz, float qw, float sx, float sy, float sz) {
        final float xs = qx * 2f, ys = qy * 2f, zs = qz * 2f;
        final float wx = qw * xs, wy = qw * ys, wz = qw * zs;
        final float xx = qx * xs, xy = qx * ys, xz = qx * zs;
        final float yy = qy * ys, yz = qy * zs, zz = qz * zs;

        val[M00] = sx * (1.0f - (yy + zz));
        val[M01] = sy * (xy - wz);
        val[M02] = sz * (xz + wy);
        val[M03] = dx;

        val[M10] = sx * (xy + wz);
        val[M11] = sy * (1.0f - (xx + zz));
        val[M12] = sz * (yz - wx);
        val[M13] = dy;

        val[M20] = sx * (xz - wy);
        val[M21] = sy * (yz + wx);
        val[M22] = sz * (1.0f - (xx + yy));
        val[M23] = dz;

        val[M30] = 0f;
        val[M31] = 0f;
        val[M32] = 0f;
        val[M33] = 1f;
        return this;
    }

    public Matrix4 translate(Vector3 translation) {
        val[M03] += translation.x;
        val[M13] += translation.y;
        val[M23] += translation.z;
        return this;
    }

    public Matrix4 translate(float dx, float dy, float dz) {
        val[M03] += dx;
        val[M13] += dy;
        val[M23] += dz;
        return this;
    }

    // a.mul(b) :: a -> a * b
    public Matrix4 mul(final Matrix4 matrix4) {
        mul(matrix4.val);
        return this;
    }

    // a.mulLeft(b) :: a -> b * a
    public Matrix4 mulLeft(final Matrix4 matrix4) {
        tmpMtx.setTo(matrix4);
        tmpMtx.mul(val);
        return setTo(tmpMtx);
    }

    public Matrix4 idt() {
        val[M00] = 1f;
        val[M01] = 0f;
        val[M02] = 0f;
        val[M03] = 0f;
        val[M10] = 0f;
        val[M11] = 1f;
        val[M12] = 0f;
        val[M13] = 0f;
        val[M20] = 0f;
        val[M21] = 0f;
        val[M22] = 1f;
        val[M23] = 0f;
        val[M30] = 0f;
        val[M31] = 0f;
        val[M32] = 0f;
        val[M33] = 1f;
        return this;
    }

    public Matrix4 inv() {
        float l_det = val[M30] * val[M21] * val[M12] * val[M03] - val[M20] * val[M31] * val[M12] * val[M03]
                - val[M30] * val[M11] * val[M22] * val[M03] + val[M10] * val[M31] * val[M22] * val[M03]
                + val[M20] * val[M11] * val[M32] * val[M03] - val[M10] * val[M21] * val[M32] * val[M03]
                - val[M30] * val[M21] * val[M02] * val[M13] + val[M20] * val[M31] * val[M02] * val[M13]
                + val[M30] * val[M01] * val[M22] * val[M13] - val[M00] * val[M31] * val[M22] * val[M13]
                - val[M20] * val[M01] * val[M32] * val[M13] + val[M00] * val[M21] * val[M32] * val[M13]
                + val[M30] * val[M11] * val[M02] * val[M23] - val[M10] * val[M31] * val[M02] * val[M23]
                - val[M30] * val[M01] * val[M12] * val[M23] + val[M00] * val[M31] * val[M12] * val[M23]
                + val[M10] * val[M01] * val[M32] * val[M23] - val[M00] * val[M11] * val[M32] * val[M23]
                - val[M20] * val[M11] * val[M02] * val[M33] + val[M10] * val[M21] * val[M02] * val[M33]
                + val[M20] * val[M01] * val[M12] * val[M33] - val[M00] * val[M21] * val[M12] * val[M33]
                - val[M10] * val[M01] * val[M22] * val[M33] + val[M00] * val[M11] * val[M22] * val[M33];
        if (l_det == 0f) throw new RuntimeException("Matrix \n" + this + "\n has no inverse.");
        float m00 = val[M12] * val[M23] * val[M31] - val[M13] * val[M22] * val[M31] + val[M13] * val[M21] * val[M32]
                - val[M11] * val[M23] * val[M32] - val[M12] * val[M21] * val[M33] + val[M11] * val[M22] * val[M33];
        float m01 = val[M03] * val[M22] * val[M31] - val[M02] * val[M23] * val[M31] - val[M03] * val[M21] * val[M32]
                + val[M01] * val[M23] * val[M32] + val[M02] * val[M21] * val[M33] - val[M01] * val[M22] * val[M33];
        float m02 = val[M02] * val[M13] * val[M31] - val[M03] * val[M12] * val[M31] + val[M03] * val[M11] * val[M32]
                - val[M01] * val[M13] * val[M32] - val[M02] * val[M11] * val[M33] + val[M01] * val[M12] * val[M33];
        float m03 = val[M03] * val[M12] * val[M21] - val[M02] * val[M13] * val[M21] - val[M03] * val[M11] * val[M22]
                + val[M01] * val[M13] * val[M22] + val[M02] * val[M11] * val[M23] - val[M01] * val[M12] * val[M23];
        float m10 = val[M13] * val[M22] * val[M30] - val[M12] * val[M23] * val[M30] - val[M13] * val[M20] * val[M32]
                + val[M10] * val[M23] * val[M32] + val[M12] * val[M20] * val[M33] - val[M10] * val[M22] * val[M33];
        float m11 = val[M02] * val[M23] * val[M30] - val[M03] * val[M22] * val[M30] + val[M03] * val[M20] * val[M32]
                - val[M00] * val[M23] * val[M32] - val[M02] * val[M20] * val[M33] + val[M00] * val[M22] * val[M33];
        float m12 = val[M03] * val[M12] * val[M30] - val[M02] * val[M13] * val[M30] - val[M03] * val[M10] * val[M32]
                + val[M00] * val[M13] * val[M32] + val[M02] * val[M10] * val[M33] - val[M00] * val[M12] * val[M33];
        float m13 = val[M02] * val[M13] * val[M20] - val[M03] * val[M12] * val[M20] + val[M03] * val[M10] * val[M22]
                - val[M00] * val[M13] * val[M22] - val[M02] * val[M10] * val[M23] + val[M00] * val[M12] * val[M23];
        float m20 = val[M11] * val[M23] * val[M30] - val[M13] * val[M21] * val[M30] + val[M13] * val[M20] * val[M31]
                - val[M10] * val[M23] * val[M31] - val[M11] * val[M20] * val[M33] + val[M10] * val[M21] * val[M33];
        float m21 = val[M03] * val[M21] * val[M30] - val[M01] * val[M23] * val[M30] - val[M03] * val[M20] * val[M31]
                + val[M00] * val[M23] * val[M31] + val[M01] * val[M20] * val[M33] - val[M00] * val[M21] * val[M33];
        float m22 = val[M01] * val[M13] * val[M30] - val[M03] * val[M11] * val[M30] + val[M03] * val[M10] * val[M31]
                - val[M00] * val[M13] * val[M31] - val[M01] * val[M10] * val[M33] + val[M00] * val[M11] * val[M33];
        float m23 = val[M03] * val[M11] * val[M20] - val[M01] * val[M13] * val[M20] - val[M03] * val[M10] * val[M21]
                + val[M00] * val[M13] * val[M21] + val[M01] * val[M10] * val[M23] - val[M00] * val[M11] * val[M23];
        float m30 = val[M12] * val[M21] * val[M30] - val[M11] * val[M22] * val[M30] - val[M12] * val[M20] * val[M31]
                + val[M10] * val[M22] * val[M31] + val[M11] * val[M20] * val[M32] - val[M10] * val[M21] * val[M32];
        float m31 = val[M01] * val[M22] * val[M30] - val[M02] * val[M21] * val[M30] + val[M02] * val[M20] * val[M31]
                - val[M00] * val[M22] * val[M31] - val[M01] * val[M20] * val[M32] + val[M00] * val[M21] * val[M32];
        float m32 = val[M02] * val[M11] * val[M30] - val[M01] * val[M12] * val[M30] - val[M02] * val[M10] * val[M31]
                + val[M00] * val[M12] * val[M31] + val[M01] * val[M10] * val[M32] - val[M00] * val[M11] * val[M32];
        float m33 = val[M01] * val[M12] * val[M20] - val[M02] * val[M11] * val[M20] + val[M02] * val[M10] * val[M21]
                - val[M00] * val[M12] * val[M21] - val[M01] * val[M10] * val[M22] + val[M00] * val[M11] * val[M22];
        float inv_det = 1.0f / l_det;
        val[M00] = m00 * inv_det;
        val[M10] = m10 * inv_det;
        val[M20] = m20 * inv_det;
        val[M30] = m30 * inv_det;
        val[M01] = m01 * inv_det;
        val[M11] = m11 * inv_det;
        val[M21] = m21 * inv_det;
        val[M31] = m31 * inv_det;
        val[M02] = m02 * inv_det;
        val[M12] = m12 * inv_det;
        val[M22] = m22 * inv_det;
        val[M32] = m32 * inv_det;
        val[M03] = m03 * inv_det;
        val[M13] = m13 * inv_det;
        val[M23] = m23 * inv_det;
        val[M33] = m33 * inv_det;
        return this;
    }

    public float det() {
        return val[M30] * val[M21] * val[M12] * val[M03] - val[M20] * val[M31] * val[M12] * val[M03]
                - val[M30] * val[M11] * val[M22] * val[M03] + val[M10] * val[M31] * val[M22] * val[M03]
                + val[M20] * val[M11] * val[M32] * val[M03] - val[M10] * val[M21] * val[M32] * val[M03]
                - val[M30] * val[M21] * val[M02] * val[M13] + val[M20] * val[M31] * val[M02] * val[M13]
                + val[M30] * val[M01] * val[M22] * val[M13] - val[M00] * val[M31] * val[M22] * val[M13]
                - val[M20] * val[M01] * val[M32] * val[M13] + val[M00] * val[M21] * val[M32] * val[M13]
                + val[M30] * val[M11] * val[M02] * val[M23] - val[M10] * val[M31] * val[M02] * val[M23]
                - val[M30] * val[M01] * val[M12] * val[M23] + val[M00] * val[M31] * val[M12] * val[M23]
                + val[M10] * val[M01] * val[M32] * val[M23] - val[M00] * val[M11] * val[M32] * val[M23]
                - val[M20] * val[M11] * val[M02] * val[M33] + val[M10] * val[M21] * val[M02] * val[M33]
                + val[M20] * val[M01] * val[M12] * val[M33] - val[M00] * val[M21] * val[M12] * val[M33]
                - val[M10] * val[M01] * val[M22] * val[M33] + val[M00] * val[M11] * val[M22] * val[M33];
    }

    private void mul(float[] otherValues) {
        float m00 = val[0] * otherValues[0] + val[4] * otherValues[1] + val[8] * otherValues[2] + val[12] * otherValues[3];
        float m01 = val[0] * otherValues[4] + val[4] * otherValues[5] + val[8] * otherValues[6] + val[12] * otherValues[7];
        float m02 = val[0] * otherValues[8] + val[4] * otherValues[9] + val[8] * otherValues[10] + val[12] * otherValues[11];
        float m03 = val[0] * otherValues[12] + val[4] * otherValues[13] + val[8] * otherValues[14] + val[12] * otherValues[15];
        float m10 = val[1] * otherValues[0] + val[5] * otherValues[1] + val[9] * otherValues[2] + val[13] * otherValues[3];
        float m11 = val[1] * otherValues[4] + val[5] * otherValues[5] + val[9] * otherValues[6] + val[13] * otherValues[7];
        float m12 = val[1] * otherValues[8] + val[5] * otherValues[9] + val[9] * otherValues[10] + val[13] * otherValues[11];
        float m13 = val[1] * otherValues[12] + val[5] * otherValues[13] + val[9] * otherValues[14] + val[13] * otherValues[15];
        float m20 = val[2] * otherValues[0] + val[6] * otherValues[1] + val[10] * otherValues[2] + val[14] * otherValues[3];
        float m21 = val[2] * otherValues[4] + val[6] * otherValues[5] + val[10] * otherValues[6] + val[14] * otherValues[7];
        float m22 = val[2] * otherValues[8] + val[6] * otherValues[9] + val[10] * otherValues[10] + val[14] * otherValues[11];
        float m23 = val[2] * otherValues[12] + val[6] * otherValues[13] + val[10] * otherValues[14] + val[14] * otherValues[15];
        float m30 = val[3] * otherValues[0] + val[7] * otherValues[1] + val[11] * otherValues[2] + val[15] * otherValues[3];
        float m31 = val[3] * otherValues[4] + val[7] * otherValues[5] + val[11] * otherValues[6] + val[15] * otherValues[7];
        float m32 = val[3] * otherValues[8] + val[7] * otherValues[9] + val[11] * otherValues[10] + val[15] * otherValues[11];
        float m33 = val[3] * otherValues[12] + val[7] * otherValues[13] + val[11] * otherValues[14] + val[15] * otherValues[15];
        val[0] = m00;
        val[1] = m10;
        val[2] = m20;
        val[3] = m30;
        val[4] = m01;
        val[5] = m11;
        val[6] = m21;
        val[7] = m31;
        val[8] = m02;
        val[9] = m12;
        val[10] = m22;
        val[11] = m32;
        val[12] = m03;
        val[13] = m13;
        val[14] = m23;
        val[15] = m33;
    }

    public String toString () {
        return "[" + val[M00] + "|" + val[M01] + "|" + val[M02] + "|" + val[M03] + "]\n" //
                + "[" + val[M10] + "|" + val[M11] + "|" + val[M12] + "|" + val[M13] + "]\n" //
                + "[" + val[M20] + "|" + val[M21] + "|" + val[M22] + "|" + val[M23] + "]\n" //
                + "[" + val[M30] + "|" + val[M31] + "|" + val[M32] + "|" + val[M33] + "]\n";
    }

}
