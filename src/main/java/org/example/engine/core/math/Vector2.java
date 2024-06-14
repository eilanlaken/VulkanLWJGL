package org.example.engine.core.math;

import org.example.engine.core.memory.MemoryPool;

public class Vector2 implements MemoryPool.Reset {

    public final static Vector2 X_UNIT = new Vector2(1, 0);
    public final static Vector2 Y_UNIT = new Vector2(0, 1);
    public final static Vector2 Zero   = new Vector2(0, 0);

    public float x;
    public float y;

    public Vector2() {
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 v) {
        set(v);
    }

    public Vector2 cpy() {
        return new Vector2(this);
    }

    public static float len(float x, float y) {
        return (float)Math.sqrt(x * x + y * y);
    }

    public float len() {
        return (float)Math.sqrt(x * x + y * y);
    }

    public static float len2(float x, float y) {
        return x * x + y * y;
    }

    public float len2() {
        return x * x + y * y;
    }

    public Vector2 zero() {
        this.x = 0;
        this.y = 0;
        return this;
    }

    public Vector2 set(Vector2 v) {
        x = v.x;
        y = v.y;
        return this;
    }

    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 sub(Vector2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    public Vector2 sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2 nor() {
        float len = len();
        if (len != 0) {
            x /= len;
            y /= len;
        }
        return this;
    }

    public Vector2 negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public Vector2 add(Vector2 v) {
        x += v.x;
        y += v.y;
        return this;
    }

    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public static float dot(float x1, float y1, float x2, float y2) {
        return x1 * x2 + y1 * y2;
    }

    public static float dot(final Vector2 a, final Vector2 b) {
        return a.x * b.x + a.y * b.y;
    }

    public float dot(Vector2 v) {
        return x * v.x + y * v.y;
    }

    public float dot(float ox, float oy) {
        return x * ox + y * oy;
    }

    public Vector2 scl(float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public Vector2 scl(float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vector2 scl(Vector2 v) {
        this.x *= v.x;
        this.y *= v.y;
        return this;
    }

    public Vector2 mulAdd(Vector2 vec, float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        return this;
    }

    public Vector2 mulAdd(Vector2 vec, Vector2 mulVec) {
        this.x += vec.x * mulVec.x;
        this.y += vec.y * mulVec.y;
        return this;
    }

    public boolean idt(final Vector2 vector) {
        return x == vector.x && y == vector.y;
    }

    public float dst(Vector2 v) {
        final float x_d = v.x - x;
        final float y_d = v.y - y;
        return (float)Math.sqrt(x_d * x_d + y_d * y_d);
    }

    public float dst(float x, float y) {
        final float x_d = x - this.x;
        final float y_d = y - this.y;
        return (float)Math.sqrt(x_d * x_d + y_d * y_d);
    }

    public float dst2(Vector2 v) {
        final float x_d = v.x - x;
        final float y_d = v.y - y;
        return x_d * x_d + y_d * y_d;
    }

    public float dst2(float x, float y) {
        final float x_d = x - this.x;
        final float y_d = y - this.y;
        return x_d * x_d + y_d * y_d;
    }

    public Vector2 limit(float limit) {
        return limit2(limit * limit);
    }

    public Vector2 limit2(float limit2) {
        float len2 = len2();
        if (len2 > limit2) {
            return scl((float)Math.sqrt(limit2 / len2));
        }
        return this;
    }

    public Vector2 clamp(float min, float max) {
        final float len2 = len2();
        if (len2 == 0f) return this;
        float max2 = max * max;
        if (len2 > max2) return scl((float)Math.sqrt(max2 / len2));
        float min2 = min * min;
        if (len2 < min2) return scl((float)Math.sqrt(min2 / len2));
        return this;
    }

    public Vector2 setLength(float len) {
        return setLength2(len * len);
    }

    public Vector2 setLength2(float len2) {
        float oldLen2 = len2();
        return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float)Math.sqrt(len2 / oldLen2));
    }

    public Vector2 fromString(String v) {
        int s = v.indexOf(',', 1);
        if (s != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
            try {
                float x = Float.parseFloat(v.substring(1, s));
                float y = Float.parseFloat(v.substring(s + 1, v.length() - 1));
                return this.set(x, y);
            } catch (NumberFormatException ex) {
                // Throw a GdxRuntimeException
            }
        }
        throw new RuntimeException("Malformed Vector2: " + v);
    }

//    public Vector2 mul(Matrix3 mat) {
//        float x = this.x * mat.val[0] + this.y * mat.val[3] + mat.val[6];
//        float y = this.x * mat.val[1] + this.y * mat.val[4] + mat.val[7];
//        this.x = x;
//        this.y = y;
//        return this;
//    }

    public float crs(Vector2 v) {
        return this.x * v.y - this.y * v.x;
    }

    public float crs(float x, float y) {
        return this.x * y - this.y * x;
    }

    public static Vector2 crs(float s, final Vector2 a) {
        return new Vector2(-s * a.y, s * a.x);
    }

    public static void crs(float s, final Vector2 a, Vector2 out) {
        out.set(-s * a.y, s * a.x);
    }

    /**
     * Returns the cross product of this {@link Vector2} and the z value of the right {@link Vector2}.
     * @param z the z component of the {@link Vector2}
     * @return {@link Vector2}
     */
    public static Vector2 crs(Vector2 v, float z) {
        return new Vector2(-v.y * z, v.x * z);
    }

    /**
     * Returns the cross product of this {@link Vector2} and the z value of the right {@link Vector2}.
     * @param z the z component of the {@link Vector2}
     * @return {@link Vector2}
     */
    public static void crs(Vector2 v, float z, Vector2 out) {
        out.set(-v.y * z, v.x * z);
    }

    public static float crs(Vector2 a, Vector2 b) {
        return a.x * b.y - a.y * b.x;
    }

    public float angleDeg() {
        float angle = (float)Math.atan2(y, x) * MathUtils.radiansToDegrees;
        if (angle < 0) angle += 360;
        return angle;
    }

    public float angleDeg(Vector2 reference) {
        float angle = (float)Math.atan2(reference.crs(this), reference.dot(this)) * MathUtils.radiansToDegrees;
        if (angle < 0) angle += 360;
        return angle;
    }

    public static float angleDeg(float x, float y) {
        float angle = (float)Math.atan2(y, x) * MathUtils.radiansToDegrees;
        if (angle < 0) angle += 360;
        return angle;
    }

    public float angleRad() {
        return (float)Math.atan2(y, x);
    }

    public float angleRad(Vector2 reference) {
        return (float)Math.atan2(reference.crs(this), reference.dot(this));
    }

    public static float angleRad(float x, float y) {
        return (float)Math.atan2(y, x);
    }

    public Vector2 setAngleDeg(float degrees) {
        return setAngleRad(degrees * MathUtils.degreesToRadians);
    }

    public Vector2 setAngleRad(float radians) {
        this.set(len(), 0f);
        this.rotateRad(radians);
        return this;
    }

    public Vector2 rotateDeg(float degrees) {
        return rotateRad(degrees * MathUtils.degreesToRadians);
    }

    public Vector2 rotateRad(float radians) {
        if (MathUtils.isZero(radians)) return this;

        float cos = MathUtils.cosRad(radians);
        float sin = MathUtils.sinRad(radians);

        float newX = this.x * cos - this.y * sin;
        float newY = this.x * sin + this.y * cos;

        this.x = newX;
        this.y = newY;

        return this;
    }

    public Vector2 rotateAroundDeg(Vector2 reference, float degrees) {
        return this.sub(reference).rotateDeg(degrees).add(reference);
    }

    public Vector2 rotateAroundRad(Vector2 reference, float radians) {
        return this.sub(reference).rotateRad(radians).add(reference);
    }

    public Vector2 rotateAroundRad(float refX, float refY, float radians) {
        return this.sub(refX, refY).rotateRad(radians).add(refX, refY);
    }

    public Vector2 rotate90(int dir) {
        float x = this.x;
        if (dir >= 0) {
            this.x = -y;
            y = x;
        } else {
            this.x = y;
            y = -x;
        }
        return this;
    }

    public Vector2 flip() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public Vector2 clamp(Vector2 min, Vector2 max) {
        this.x = MathUtils.clampFloat(this.x, min.x, max.x);
        this.y = MathUtils.clampFloat(this.y, min.y, max.y);
        return this;
    }

    public Vector2 lerp(Vector2 target, float alpha) {
        final float invAlpha = 1.0f - alpha;
        this.x = (x * invAlpha) + (target.x * alpha);
        this.y = (y * invAlpha) + (target.y * alpha);
        return this;
    }

    public boolean epsilonEquals(Vector2 other, float epsilon) {
        if (other == null) return false;
        if (Math.abs(other.x - x) > epsilon) return false;
        if (Math.abs(other.y - y) > epsilon) return false;
        return true;
    }

    public boolean epsilonEquals(float x, float y, float epsilon) {
        if (Math.abs(x - this.x) > epsilon) return false;
        if (Math.abs(y - this.y) > epsilon) return false;
        return true;
    }

    public boolean epsilonEquals(final Vector2 other) {
        return epsilonEquals(other, MathUtils.FLOAT_ROUNDING_ERROR);
    }

    public boolean epsilonEquals(float x, float y) {
        return epsilonEquals(x, y, MathUtils.FLOAT_ROUNDING_ERROR);
    }

    public boolean isUnit() {
        return isUnit(0.000000001f);
    }

    public boolean isUnit(final float margin) {
        return Math.abs(len2() - 1f) < margin;
    }

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    public boolean isZero(final float margin) {
        return len2() < margin;
    }

    public boolean isOnLine(Vector2 other) {
        return x * other.y - y * other.x == 0;
    }

    public boolean isPerpendicular(Vector2 vector) {
        return dot(vector) == 0;
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        return result;
    }

    @Override
    public void reset() {
        this.x = 0;
        this.y = 0;
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Vector2 other = (Vector2)obj;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) return false;
        return true;
    }

    @Override
    public String toString () {
        return "(" + x + "," + y + ")";
    }

    public static boolean nearlyEqual(Vector2 a, Vector2 b, float tolerance) {
        return Vector2.dst2(a, b) < tolerance * tolerance;
    }

    public static float dst(float x1, float y1, float x2, float y2) {
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static float dst(final Vector2 a, final Vector2 b) {
        final float dx = b.x - a.x;
        final float dy = b.y - a.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static float dst2(float x1, float y1, float x2, float y2) {
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        return dx * dx + dy * dy;
    }

    public static float dst2(final Vector2 a, final Vector2 b) {
        final float dx = b.x - a.x;
        final float dy = b.y - a.y;
        return dx * dx + dy * dy;
    }

}
