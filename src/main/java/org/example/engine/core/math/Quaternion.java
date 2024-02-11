package org.example.engine.core.math;

// TODO: complete and test
public class Quaternion {

    private static Quaternion quaternion1 = new Quaternion(0.0f, 0.0f, 0.0f, 0.0f);
    private static Quaternion quaternion2 = new Quaternion(0.0f, 0.0f, 0.0f, 0.0f);
    public float x;
    public float y;
    public float z;
    public float w;

    public Quaternion(float x, float y, float z, float w) {
        this.set(x, y, z, w);
    }

    public Quaternion() {
        this.idt();
    }

    public Quaternion(Quaternion quaternion) {
        this.set(quaternion);
    }

    public Quaternion(Vector3 axis, float angle) {
        this.set(axis, angle);
    }

    public Quaternion set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Quaternion set(Quaternion quaternion) {
        return this.set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
    }

    public Quaternion set(Vector3 axis, float angle) {
        return this.setFromAxis(axis.x, axis.y, axis.z, angle);
    }

    public Quaternion cpy() {
        return new Quaternion(this);
    }

    public static final float len(float x, float y, float z, float w) {
        return (float)Math.sqrt((double)(x * x + y * y + z * z + w * w));
    }

    public float len() {
        return (float)Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w));
    }

    public String toString() {
        return "[" + this.x + "," + this.y + "," + this.z + "|" + this.w + "]";
    }

    public Quaternion setEulerAngles(float yaw, float pitch, float roll) {
        float hr = roll * 0.5f;
        float shr = (float)Math.sin(hr);
        float chr = (float)Math.cos(hr);
        float hp = pitch * 0.5f;
        float shp = (float)Math.sin(hp);
        float chp = (float)Math.cos(hp);
        float hy = yaw * 0.5f;
        float shy = (float)Math.sin(hy);
        float chy = (float)Math.cos(hy);
        float chy_shp = chy * shp;
        float shy_chp = shy * chp;
        float chy_chp = chy * chp;
        float shy_shp = shy * shp;
        this.x = chy_shp * chr + shy_chp * shr;
        this.y = shy_chp * chr - chy_shp * shr;
        this.z = chy_chp * shr - shy_shp * chr;
        this.w = chy_chp * chr + shy_shp * shr;
        return this;
    }

    public int getGimbalPole() {
        float t = this.y * this.x + this.z * this.w;
        return t > 0.499f ? 1 : (t < -0.499f ? -1 : 0);
    }

    public float getRollRad() {
        int pole = this.getGimbalPole();
        return pole == 0 ? MathUtils.atan2(2.0F * (this.w * this.z + this.y * this.x), 1.0F - 2.0F * (this.x * this.x + this.z * this.z)) : (float)pole * 2.0F * MathUtils.atan2(this.y, this.w);
    }

    public float getRoll() {
        return this.getRollRad() * 57.295776F;
    }

    public float getPitchRad() {
        int pole = this.getGimbalPole();
        return pole == 0 ? (float)Math.asin(MathUtils.clamp(2.0F * (this.w * this.x - this.z * this.y), -1.0F, 1.0F)) : (float)pole * 3.1415927F * 0.5F;
    }

    public float getPitch() {
        return this.getPitchRad() * 57.295776F;
    }

    public float getYawRad() {
        return this.getGimbalPole() == 0 ? MathUtils.atan2(2.0F * (this.y * this.w + this.x * this.z), 1.0F - 2.0F * (this.y * this.y + this.x * this.x)) : 0.0F;
    }

    public float getYaw() {
        return this.getYawRad() * 57.295776F;
    }

    public static final float len2(float x, float y, float z, float w) {
        return x * x + y * y + z * z + w * w;
    }

    public float len2() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    public Quaternion nor() {
        float len = this.len2();
        if (len != 0.0f && !(len == 1.0f)) {
            len = (float)Math.sqrt((double)len);
            this.w /= len;
            this.x /= len;
            this.y /= len;
            this.z /= len;
        }

        return this;
    }

    public Quaternion conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    public Vector3 transform(Vector3 v) {
        quaternion2.set(this);
        quaternion2.conjugate();
        quaternion2.mulLeft(quaternion1.set(v.x, v.y, v.z, 0.0F)).mulLeft(this);
        v.x = quaternion2.x;
        v.y = quaternion2.y;
        v.z = quaternion2.z;
        return v;
    }

    public Quaternion mul(Quaternion other) {
        float newX = this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y;
        float newY = this.w * other.y + this.y * other.w + this.z * other.x - this.x * other.z;
        float newZ = this.w * other.z + this.z * other.w + this.x * other.y - this.y * other.x;
        float newW = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        return this;
    }

    public Quaternion mul(float x, float y, float z, float w) {
        float newX = this.w * x + this.x * w + this.y * z - this.z * y;
        float newY = this.w * y + this.y * w + this.z * x - this.x * z;
        float newZ = this.w * z + this.z * w + this.x * y - this.y * x;
        float newW = this.w * w - this.x * x - this.y * y - this.z * z;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        return this;
    }

    public Quaternion mulLeft(Quaternion other) {
        float newX = other.w * this.x + other.x * this.w + other.y * this.z - other.z * this.y;
        float newY = other.w * this.y + other.y * this.w + other.z * this.x - other.x * this.z;
        float newZ = other.w * this.z + other.z * this.w + other.x * this.y - other.y * this.x;
        float newW = other.w * this.w - other.x * this.x - other.y * this.y - other.z * this.z;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        return this;
    }

    public Quaternion mulLeft(float x, float y, float z, float w) {
        float newX = w * this.x + x * this.w + y * this.z - z * this.y;
        float newY = w * this.y + y * this.w + z * this.x - x * this.z;
        float newZ = w * this.z + z * this.w + x * this.y - y * this.x;
        float newW = w * this.w - x * this.x - y * this.y - z * this.z;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        return this;
    }

    public Quaternion add(Quaternion quaternion) {
        this.x += quaternion.x;
        this.y += quaternion.y;
        this.z += quaternion.z;
        this.w += quaternion.w;
        return this;
    }

    public Quaternion add(float qx, float qy, float qz, float qw) {
        this.x += qx;
        this.y += qy;
        this.z += qz;
        this.w += qw;
        return this;
    }

    public void toMatrix(float[] matrix) {
        float xx = this.x * this.x;
        float xy = this.x * this.y;
        float xz = this.x * this.z;
        float xw = this.x * this.w;
        float yy = this.y * this.y;
        float yz = this.y * this.z;
        float yw = this.y * this.w;
        float zz = this.z * this.z;
        float zw = this.z * this.w;
        matrix[0] = 1.0F - 2.0F * (yy + zz);
        matrix[4] = 2.0F * (xy - zw);
        matrix[8] = 2.0F * (xz + yw);
        matrix[12] = 0.0F;
        matrix[1] = 2.0F * (xy + zw);
        matrix[5] = 1.0F - 2.0F * (xx + zz);
        matrix[9] = 2.0F * (yz - xw);
        matrix[13] = 0.0F;
        matrix[2] = 2.0F * (xz - yw);
        matrix[6] = 2.0F * (yz + xw);
        matrix[10] = 1.0F - 2.0F * (xx + yy);
        matrix[14] = 0.0F;
        matrix[3] = 0.0F;
        matrix[7] = 0.0F;
        matrix[11] = 0.0F;
        matrix[15] = 1.0F;
    }

    public Quaternion idt() {
        return this.set(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public boolean isIdentity() {
        return this.x == 0 && this.y == 0 && this.z == 0 && this.w == 1.0f;
    }

    public Quaternion setFromAxis(Vector3 axis, float radians) {
        return this.setFromAxis(axis.x, axis.y, axis.z, radians);
    }

    public Quaternion setFromAxis(float x, float y, float z, float radians) {
        float d = Vector3.len(x, y, z);
        if (d == 0.0F) {
            return this.idt();
        } else {
            d = 1.0F / d;
            float l_ang = radians < 0.0F ? 6.2831855F - -radians % 6.2831855F : radians % 6.2831855F;
            float l_sin = (float)Math.sin(l_ang / 2.0f);
            float l_cos = (float)Math.cos(l_ang / 2.0f);
            return this.set(d * x * l_sin, d * y * l_sin, d * z * l_sin, l_cos).nor();
        }
    }

    public Quaternion setFromMatrix(boolean normalizeAxes, Matrix4 matrix) {
        return this.setFromAxes(normalizeAxes, matrix.val[0], matrix.val[4], matrix.val[8], matrix.val[1], matrix.val[5], matrix.val[9], matrix.val[2], matrix.val[6], matrix.val[10]);
    }

    public Quaternion setFromMatrix(Matrix4 matrix) {
        return this.setFromMatrix(false, matrix);
    }

    public Quaternion setFromAxes(float xx, float xy, float xz, float yx, float yy, float yz, float zx, float zy, float zz) {
        return this.setFromAxes(false, xx, xy, xz, yx, yy, yz, zx, zy, zz);
    }

    public Quaternion setFromAxes(boolean normalizeAxes, float xx, float xy, float xz, float yx, float yy, float yz, float zx, float zy, float zz) {
        float t;
        float s;
        if (normalizeAxes) {
            t = 1.0F / Vector3.len(xx, xy, xz);
            s = 1.0F / Vector3.len(yx, yy, yz);
            float lz = 1.0F / Vector3.len(zx, zy, zz);
            xx *= t;
            xy *= t;
            xz *= t;
            yx *= s;
            yy *= s;
            yz *= s;
            zx *= lz;
            zy *= lz;
            zz *= lz;
        }

        t = xx + yy + zz;
        if (t >= 0.0F) {
            s = (float)Math.sqrt((double)(t + 1.0F));
            this.w = 0.5F * s;
            s = 0.5F / s;
            this.x = (zy - yz) * s;
            this.y = (xz - zx) * s;
            this.z = (yx - xy) * s;
        } else if (xx > yy && xx > zz) {
            s = (float)Math.sqrt(1.0D + (double)xx - (double)yy - (double)zz);
            this.x = s * 0.5F;
            s = 0.5F / s;
            this.y = (yx + xy) * s;
            this.z = (xz + zx) * s;
            this.w = (zy - yz) * s;
        } else if (yy > zz) {
            s = (float)Math.sqrt(1.0D + (double)yy - (double)xx - (double)zz);
            this.y = s * 0.5F;
            s = 0.5F / s;
            this.x = (yx + xy) * s;
            this.z = (zy + yz) * s;
            this.w = (xz - zx) * s;
        } else {
            s = (float)Math.sqrt(1.0D + (double)zz - (double)xx - (double)yy);
            this.z = s * 0.5F;
            s = 0.5F / s;
            this.x = (xz + zx) * s;
            this.y = (zy + yz) * s;
            this.w = (yx - xy) * s;
        }

        return this;
    }

    public Quaternion setFromCross(Vector3 v1, Vector3 v2) {
        float dot = MathUtils.clamp(Vector3.dot(v1,v2), -1.0f, 1.0f);
        float angle = (float)Math.acos(dot);
        return this.setFromAxis(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x, angle);
    }

    public Quaternion setFromCross(float x1, float y1, float z1, float x2, float y2, float z2) {
        float dot = MathUtils.clamp(Vector3.dot(x1, y1, z1, x2, y2, z2), -1.0f, 1.0f);
        float angle = (float)Math.acos(dot);
        return this.setFromAxis(y1 * z2 - z1 * y2, z1 * x2 - x1 * z2, x1 * y2 - y1 * x2, angle);
    }

    public Quaternion slerp(Quaternion end, float alpha) {
        float d = this.x * end.x + this.y * end.y + this.z * end.z + this.w * end.w;
        float absDot = d < 0.0f ? -d : d;
        float scale0 = 1.0f - alpha;
        float scale1 = alpha;
        if ((double)(1.0f - absDot) > 0.1) {
            float angle = (float)Math.acos(absDot);
            float invSinTheta = 1.0f / (float)Math.sin(angle);
            scale0 = (float)Math.sin((1.0F - alpha) * angle) * invSinTheta;
            scale1 = (float)Math.sin(alpha * angle) * invSinTheta;
        }

        if (d < 0.0f) {
            scale1 = -scale1;
        }

        this.x = scale0 * this.x + scale1 * end.x;
        this.y = scale0 * this.y + scale1 * end.y;
        this.z = scale0 * this.z + scale1 * end.z;
        this.w = scale0 * this.w + scale1 * end.w;
        return this;
    }

    public Quaternion slerp(Quaternion[] q) {
        float w = 1.0F / (float)q.length;
        this.set(q[0]).exp(w);

        for(int i = 1; i < q.length; ++i) {
            this.mul(quaternion1.set(q[i]).exp(w));
        }

        this.nor();
        return this;
    }

    public Quaternion slerp(Quaternion[] q, float[] w) {
        this.set(q[0]).exp(w[0]);

        for(int i = 1; i < q.length; ++i) {
            this.mul(quaternion1.set(q[i]).exp(w[i]));
        }

        this.nor();
        return this;
    }

    public Quaternion exp(float alpha) {
        float norm = this.len();
        float normExp = (float)Math.pow(norm, alpha);
        float theta = (float)Math.acos(this.w / norm);
        float coefficient = Math.abs(theta) < 0.001 ? normExp * alpha / norm : (float)((double)normExp * Math.sin(alpha * theta) / ((double)norm * Math.sin(theta)));
        this.w = (float)((double)normExp * Math.cos(alpha * theta));
        this.x *= coefficient;
        this.y *= coefficient;
        this.z *= coefficient;
        this.nor();
        return this;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Quaternion)) return false;
        Quaternion other = (Quaternion)obj;
        return this.w == other.w && this.x == other.x && this.y == other.y && this.z == other.z;
    }

    public static final float dot(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
        return x1 * x2 + y1 * y2 + z1 * z2 + w1 * w2;
    }

    public float dot(Quaternion other) {
        return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
    }

    public float dot(float x, float y, float z, float w) {
        return this.x * x + this.y * y + this.z * z + this.w * w;
    }

    public Quaternion mul(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        this.w *= scalar;
        return this;
    }

    public float getAxisAngle(Vector3 axis) {
        if (this.w > 1.0f) {
            this.nor();
        }

        float angle = (float)(2.0 * Math.acos(this.w));
        double s = Math.sqrt(1.0f - this.w * this.w);
        if (s < 9.999999974752427E-7) {
            axis.x = this.x;
            axis.y = this.y;
            axis.z = this.z;
        } else {
            axis.x = (float)((double)this.x / s);
            axis.y = (float)((double)this.y / s);
            axis.z = (float)((double)this.z / s);
        }

        return angle;
    }

    public float getAngle() {
        return (float)(2.0 * Math.acos(this.w > 1.0f ? (double)(this.w / this.len()) : (double)this.w));
    }

    public void getSwingTwist(float axisX, float axisY, float axisZ, Quaternion swing, Quaternion twist) {
        float d = Vector3.dot(this.x, this.y, this.z, axisX, axisY, axisZ);
        twist.set(axisX * d, axisY * d, axisZ * d, this.w).nor();
        if (d < 0.0f) {
            twist.mul(-1.0f);
        }

        swing.set(twist).conjugate().mulLeft(this);
    }

    public void getSwingTwist(Vector3 axis, Quaternion swing, Quaternion twist) {
        this.getSwingTwist(axis.x, axis.y, axis.z, swing, twist);
    }

    public float getAngleAroundRad(float axisX, float axisY, float axisZ) {
        float d = Vector3.dot(this.x, this.y, this.z, axisX, axisY, axisZ);
        float l2 = len2(axisX * d, axisY * d, axisZ * d, this.w);
        return l2 == 0 ? 0.0f : (float)(2.0 * Math.acos(MathUtils.clamp((float)((double)(d < 0.0f ? -this.w : this.w) / Math.sqrt(l2)), -1.0f, 1.0f)));
    }

    public float getAngleAroundRad(Vector3 axis) {
        return this.getAngleAroundRad(axis.x, axis.y, axis.z);
    }

    public float getAngleAround(float axisX, float axisY, float axisZ) {
        return this.getAngleAroundRad(axisX, axisY, axisZ) * 57.295776F;
    }

    public float getAngleAround(Vector3 axis) {
        return this.getAngleAround(axis.x, axis.y, axis.z);
    }



}
