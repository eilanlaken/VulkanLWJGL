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

    private static final Quaternion quaternion = new Quaternion();
    private static final Matrix4 tmpMtx = new Matrix4();

    static final Vector3 l_vez = new Vector3();
    static final Vector3 l_vex = new Vector3();
    static final Vector3 l_vey = new Vector3();
    private static final Vector3 tmpVec = new Vector3();
    private static final Matrix4 tmpMat = new Matrix4();
    private static final Vector3 right = new Vector3();
    private static final Vector3 tmpForward = new Vector3();
    private static final Vector3 tmpUp = new Vector3();

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

    public Matrix4 set (Vector3 xAxis, Vector3 yAxis, Vector3 zAxis, Vector3 pos) {
        val[M00] = xAxis.x;
        val[M01] = xAxis.y;
        val[M02] = xAxis.z;
        val[M10] = yAxis.x;
        val[M11] = yAxis.y;
        val[M12] = yAxis.z;
        val[M20] = zAxis.x;
        val[M21] = zAxis.y;
        val[M22] = zAxis.z;
        val[M03] = pos.x;
        val[M13] = pos.y;
        val[M23] = pos.z;
        val[M30] = 0f;
        val[M31] = 0f;
        val[M32] = 0f;
        val[M33] = 1f;
        return this;
    }

    public Matrix4 setToLookAt (Vector3 position, Vector3 target, Vector3 up) {
        tmpVec.set(target).sub(position);
        setToLookAt(tmpVec, up);
        mul(tmpMat.setToTranslation(-position.x, -position.y, -position.z));
        return this;
    }

    public Matrix4 setToLookAt(Vector3 direction, Vector3 up) {
        l_vez.set(direction).normalize();
        l_vex.set(direction).cross(up).normalize();
        l_vey.set(l_vex).cross(l_vez).normalize();
        idt();
        val[M00] = l_vex.x;
        val[M01] = l_vex.y;
        val[M02] = l_vex.z;
        val[M10] = l_vey.x;
        val[M11] = l_vey.y;
        val[M12] = l_vey.z;
        val[M20] = -l_vez.x;
        val[M21] = -l_vez.y;
        val[M22] = -l_vez.z;
        return this;
    }

    public Matrix4 setToTranslation(Vector3 vector) {
        idt();
        val[M03] = vector.x;
        val[M13] = vector.y;
        val[M23] = vector.z;
        return this;
    }

    public Matrix4 setToTranslation(float x, float y, float z) {
        idt();
        val[M03] = x;
        val[M13] = y;
        val[M23] = z;
        return this;
    }

    public Matrix4 setToWorld(Vector3 position, Vector3 forward, Vector3 up) {
        tmpForward.set(forward).normalize();
        right.set(tmpForward).cross(up).normalize();
        tmpUp.set(right).cross(tmpForward).normalize();
        set(right, tmpUp, tmpForward.scl(-1), position);
        return this;
    }
    public Matrix4 rotate(float x, float y, float z, float w) {
        float xx = x * x;
        float xy = x * y;
        float xz = x * z;
        float xw = x * w;
        float yy = y * y;
        float yz = y * z;
        float yw = y * w;
        float zz = z * z;
        float zw = z * w;
        // Set matrix from quaternion
        float r00 = 1 - 2 * (yy + zz);
        float r01 = 2 * (xy - zw);
        float r02 = 2 * (xz + yw);
        float r10 = 2 * (xy + zw);
        float r11 = 1 - 2 * (xx + zz);
        float r12 = 2 * (yz - xw);
        float r20 = 2 * (xz - yw);
        float r21 = 2 * (yz + xw);
        float r22 = 1 - 2 * (xx + yy);
        float m00 = val[M00] * r00 + val[M01] * r10 + val[M02] * r20;
        float m01 = val[M00] * r01 + val[M01] * r11 + val[M02] * r21;
        float m02 = val[M00] * r02 + val[M01] * r12 + val[M02] * r22;
        float m10 = val[M10] * r00 + val[M11] * r10 + val[M12] * r20;
        float m11 = val[M10] * r01 + val[M11] * r11 + val[M12] * r21;
        float m12 = val[M10] * r02 + val[M11] * r12 + val[M12] * r22;
        float m20 = val[M20] * r00 + val[M21] * r10 + val[M22] * r20;
        float m21 = val[M20] * r01 + val[M21] * r11 + val[M22] * r21;
        float m22 = val[M20] * r02 + val[M21] * r12 + val[M22] * r22;
        float m30 = val[M30] * r00 + val[M31] * r10 + val[M32] * r20;
        float m31 = val[M30] * r01 + val[M31] * r11 + val[M32] * r21;
        float m32 = val[M30] * r02 + val[M31] * r12 + val[M32] * r22;
        val[M00] = m00;
        val[M10] = m10;
        val[M20] = m20;
        val[M30] = m30;
        val[M01] = m01;
        val[M11] = m11;
        val[M21] = m21;
        val[M31] = m31;
        val[M02] = m02;
        val[M12] = m12;
        val[M22] = m22;
        val[M32] = m32;
        return this;
    }

    public Matrix4 rotate(Quaternion rotation) {
        return this.rotate(rotation.x, rotation.y, rotation.z, rotation.w);
    }

    public Matrix4 translate(Vector3 translation) {
        return translate(translation.x, translation.y, translation.z);
    }

    public Matrix4 translate(float x, float y, float z) {
        val[M03] += val[M00] * x + val[M01] * y + val[M02] * z;
        val[M13] += val[M10] * x + val[M11] * y + val[M12] * z;
        val[M23] += val[M20] * x + val[M21] * y + val[M22] * z;
        val[M33] += val[M30] * x + val[M31] * y + val[M32] * z;
        return this;
    }

    public Matrix4 scale(float scaleX, float scaleY, float scaleZ) {
        val[M00] *= scaleX;
        val[M01] *= scaleY;
        val[M02] *= scaleZ;
        val[M10] *= scaleX;
        val[M11] *= scaleY;
        val[M12] *= scaleZ;
        val[M20] *= scaleX;
        val[M21] *= scaleY;
        val[M22] *= scaleZ;
        val[M30] *= scaleX;
        val[M31] *= scaleY;
        val[M32] *= scaleZ;
        return this;
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

    public Matrix4 setToRotation(Quaternion quaternion) {
        return this.setToRotation(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
    }

    public Matrix4 setToRotation(float quaternionX, float quaternionY, float quaternionZ, float quaternionW) {
        return this.setTranslationRotation(0.0f, 0.0f, 0.0f, quaternionX, quaternionY, quaternionZ, quaternionW);
    }

    public Matrix4 setTranslationRotation(float translationX, float translationY, float translationZ, float quaternionX, float quaternionY, float quaternionZ, float quaternionW) {
        float xs = quaternionX * 2.0f;
        float ys = quaternionY * 2.0f;
        float zs = quaternionZ * 2.0f;
        float wx = quaternionW * xs;
        float wy = quaternionW * ys;
        float wz = quaternionW * zs;
        float xx = quaternionX * xs;
        float xy = quaternionX * ys;
        float xz = quaternionX * zs;
        float yy = quaternionY * ys;
        float yz = quaternionY * zs;
        float zz = quaternionZ * zs;
        this.val[0] = 1.0f - (yy + zz);
        this.val[4] = xy - wz;
        this.val[8] = xz + wy;
        this.val[12] = translationX;
        this.val[1] = xy + wz;
        this.val[5] = 1.0f - (xx + zz);
        this.val[9] = yz - wx;
        this.val[13] = translationY;
        this.val[2] = xz - wy;
        this.val[6] = yz + wx;
        this.val[10] = 1.0f - (xx + yy);
        this.val[14] = translationZ;
        this.val[3] = 0.0f;
        this.val[7] = 0.0f;
        this.val[11] = 0.0f;
        this.val[15] = 1.0f;
        return this;
    }

    public Matrix4 setToRotation(Vector3 axis, float radians) {
        if (radians == 0) {
            idt();
            return this;
        }
        return setToRotation(quaternion.setFromAxis(axis, radians));
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

    public Matrix4 setToScale(float sx, float sy, float sz) {
        idt();
        val[M00] = sx;
        val[M11] = sy;
        val[M22] = sz;
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

    @Override
    public String toString () {
        return "[" + val[M00] + "|" + val[M01] + "|" + val[M02] + "|" + val[M03] + "]\n" //
                + "[" + val[M10] + "|" + val[M11] + "|" + val[M12] + "|" + val[M13] + "]\n" //
                + "[" + val[M20] + "|" + val[M21] + "|" + val[M22] + "|" + val[M23] + "]\n" //
                + "[" + val[M30] + "|" + val[M31] + "|" + val[M32] + "|" + val[M33] + "]\n";
    }

}
