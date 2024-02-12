package org.example.engine.core.math;

public class Vector3 {

    public final static Vector3 UNIT_X = new Vector3(1, 0, 0);
    public final static Vector3 UNIT_Y = new Vector3(0, 1, 0);
    public final static Vector3 UNIT_Z = new Vector3(0, 0, 1);
    private final static Matrix4 mtx = new Matrix4();

    public float x;
    public float y;
    public float z;

    public Vector3() {
        this(0,0,0);
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(final Vector3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Vector3 set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3 set(final Vector3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        return this;
    }

    public Vector3 add(final Vector3 v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        return this;
    }

    public Vector3 add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector3 sub(final Vector3 v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        return this;
    }

    public Vector3 sub(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vector3 scl(float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        return this;
    }

    public float len() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public float len2() {
        return x * x + y * y + z * z;
    }

    public float dst(final Vector3 v) {
        final float dx = v.x - x;
        final float dy = v.y - y;
        final float dz = v.z - z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public float dst(float x, float y, float z) {
        final float dx = x - this.x;
        final float dy = y - this.y;
        final float dz = z - this.z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public float dst2(final Vector3 v) {
        final float dx = v.x - x;
        final float dy = v.y - y;
        final float dz = v.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public float dst2(float x, float y, float z) {
        final float dx = x - this.x;
        final float dy = y - this.y;
        final float dz = z - this.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public Vector3 normalize() {
        final float len2 = this.len2();
        if (len2 == 0f || len2 == 1f) return this;
        return this.scl(1f / (float)Math.sqrt(len2));
    }

    public boolean isZero() {
        return this.x == 0.0f && this.y == 0.0f && this.z == 0.0f;
    }

    public Vector3 mul(final Matrix4 matrix) {
        final float[] l_mat = matrix.val;
        return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02] + l_mat[Matrix4.M03],
                x * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12] + l_mat[Matrix4.M13],
                x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22] + l_mat[Matrix4.M23]);
    }

    public Vector3 rotate(final Matrix4 matrix) {
        final float[] l_mat = matrix.val;
        return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02],
                x * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12],
                x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22]);
    }

    public Vector3 project(final Matrix4 matrix) {
        final float[] l_mat = matrix.val;
        final float l_w = 1f / (x * l_mat[Matrix4.M30] + y * l_mat[Matrix4.M31] + z * l_mat[Matrix4.M32] + l_mat[Matrix4.M33]);
        return this.set((x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02] + l_mat[Matrix4.M03]) * l_w,
                (x * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12] + l_mat[Matrix4.M13]) * l_w,
                (x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22] + l_mat[Matrix4.M23]) * l_w);
    }

    public Vector3 cross(final Vector3 v) {
        return this.set(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public Vector3 cross(float x, float y, float z) {
        return this.set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
    }

    public Vector3 lerp(final Vector3 target, float ds) {
        x += ds * (target.x - x);
        y += ds * (target.y - y);
        z += ds * (target.z - z);
        return this;
    }

    public Vector3 mul(final Quaternion q) {
        return q.transform(this);
    }

    public Vector3 rotate(float radians, float axisX, float axisY, float axisZ) {
        return this.mul(mtx.setToRotation(axisX, axisY, axisZ, radians));
    }

    public Vector3 rotate(final Vector3 axis, float radians) {
        mtx.setToRotation(axis, radians);
        return this.mul(mtx);
    }

    public Vector3 setLength(float len) {
        return setLength2(len * len);
    }

    public Vector3 setLength2(float len2) {
        float oldLen2 = len2();
        return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float)Math.sqrt(len2 / oldLen2));
    }

    public Vector3 cap(float limit) {
        return cap2(limit * limit);
    }

    public Vector3 cap2(float limit2) {
        float len2 = len2();
        if (len2 > limit2) {
            scl((float)Math.sqrt(limit2 / len2));
        }
        return this;
    }

    @Override
    public String toString () {
        return "[" + x + "," + y + "," + z + "]";
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Vector3 other = (Vector3)obj;
        if (x != other.x) return false;
        if (y != other.y) return false;
        if (z != other.z) return false;
        return true;
    }

    public static float dot(final Vector3 a, final Vector3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static Vector3 cross(final Vector3 a, final Vector3 b, Vector3 result) {
        if (result == null) result = new Vector3();
        return result.set(b.y * a.z - b.z * a.y, b.z * a.x - b.x * a.z, b.x * a.y - b.y * a.x);
    }

    public static boolean arePerpendicular(final Vector3 a, final Vector3 b) {
        return dot(a,b) == 0;
    }

    public static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
        return x1 * x2 + y1 * y2 + z1 * z2;
    }

    public static Vector3 project(final Matrix4 matrix, final Vector3 in, Vector3 out) {
        final float l_mat[] = matrix.val;
        final float l_w = 1f / (in.x * l_mat[Matrix4.M30] + in.y * l_mat[Matrix4.M31] + in.z * l_mat[Matrix4.M32] + l_mat[Matrix4.M33]);
        return out.set((in.x * l_mat[Matrix4.M00] + in.y * l_mat[Matrix4.M01] + in.z * l_mat[Matrix4.M02] + l_mat[Matrix4.M03]) * l_w,
                (in.x * l_mat[Matrix4.M10] + in.y * l_mat[Matrix4.M11] + in.z * l_mat[Matrix4.M12] + l_mat[Matrix4.M13]) * l_w,
                (in.x * l_mat[Matrix4.M20] + in.y * l_mat[Matrix4.M21] + in.z * l_mat[Matrix4.M22] + l_mat[Matrix4.M23]) * l_w);
    }

    public static float len(float x, float y, float z) {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public static float len2(float x, float y, float z) {
        return x * x + y * y + z * z;
    }

}
