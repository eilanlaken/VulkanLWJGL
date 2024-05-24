package org.example.engine.core.math;

public class MathVector2 {

    public final static MathVector2 X_UNIT = new MathVector2(1, 0);
    public final static MathVector2 Y_UNIT = new MathVector2(0, 1);
    public final static MathVector2 Zero   = new MathVector2(0, 0);

    public float x;
    public float y;

    public MathVector2() {
    }

    public MathVector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public MathVector2(MathVector2 v) {
        set(v);
    }

    public MathVector2 cpy() {
        return new MathVector2(this);
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

    public MathVector2 zero() {
        this.x = 0;
        this.y = 0;
        return this;
    }

    public MathVector2 set(MathVector2 v) {
        x = v.x;
        y = v.y;
        return this;
    }

    public MathVector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public MathVector2 sub(MathVector2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    public MathVector2 sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public MathVector2 nor() {
        float len = len();
        if (len != 0) {
            x /= len;
            y /= len;
        }
        return this;
    }

    public MathVector2 add(MathVector2 v) {
        x += v.x;
        y += v.y;
        return this;
    }

    public MathVector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public static float dot(float x1, float y1, float x2, float y2) {
        return x1 * x2 + y1 * y2;
    }

    public static float dot(final MathVector2 a, final MathVector2 b) {
        return a.x * b.x + a.y * b.y;
    }

    public float dot(MathVector2 v) {
        return x * v.x + y * v.y;
    }

    public float dot(float ox, float oy) {
        return x * ox + y * oy;
    }

    public MathVector2 scl(float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public MathVector2 scl(float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public MathVector2 scl(MathVector2 v) {
        this.x *= v.x;
        this.y *= v.y;
        return this;
    }

    public MathVector2 mulAdd(MathVector2 vec, float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        return this;
    }

    public MathVector2 mulAdd(MathVector2 vec, MathVector2 mulVec) {
        this.x += vec.x * mulVec.x;
        this.y += vec.y * mulVec.y;
        return this;
    }

    public boolean idt(final MathVector2 vector) {
        return x == vector.x && y == vector.y;
    }

    public float dst(MathVector2 v) {
        final float x_d = v.x - x;
        final float y_d = v.y - y;
        return (float)Math.sqrt(x_d * x_d + y_d * y_d);
    }

    public float dst(float x, float y) {
        final float x_d = x - this.x;
        final float y_d = y - this.y;
        return (float)Math.sqrt(x_d * x_d + y_d * y_d);
    }

    public float dst2(MathVector2 v) {
        final float x_d = v.x - x;
        final float y_d = v.y - y;
        return x_d * x_d + y_d * y_d;
    }

    public float dst2(float x, float y) {
        final float x_d = x - this.x;
        final float y_d = y - this.y;
        return x_d * x_d + y_d * y_d;
    }

    public MathVector2 limit(float limit) {
        return limit2(limit * limit);
    }

    public MathVector2 limit2(float limit2) {
        float len2 = len2();
        if (len2 > limit2) {
            return scl((float)Math.sqrt(limit2 / len2));
        }
        return this;
    }

    public MathVector2 clamp(float min, float max) {
        final float len2 = len2();
        if (len2 == 0f) return this;
        float max2 = max * max;
        if (len2 > max2) return scl((float)Math.sqrt(max2 / len2));
        float min2 = min * min;
        if (len2 < min2) return scl((float)Math.sqrt(min2 / len2));
        return this;
    }

    public MathVector2 setLength(float len) {
        return setLength2(len * len);
    }

    public MathVector2 setLength2(float len2) {
        float oldLen2 = len2();
        return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float)Math.sqrt(len2 / oldLen2));
    }

    public MathVector2 fromString(String v) {
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

    public float crs(MathVector2 v) {
        return this.x * v.y - this.y * v.x;
    }

    public float crs(float x, float y) {
        return this.x * y - this.y * x;
    }

    // TODO: move to physics
    public static MathVector2 crs(final MathVector2 a, float s) {
        return new MathVector2(s * a.y, -s * a.x);
    }

    // TODO: move to physics
    public static MathVector2 crs(float s, final MathVector2 a) {
        return new MathVector2(-s * a.y, s * a.x);
    }

    public static float crs(MathVector2 a, MathVector2 b) {
        return a.x * b.y - a.y * b.x;
    }

    public float angleDeg() {
        float angle = (float)Math.atan2(y, x) * MathUtils.radiansToDegrees;
        if (angle < 0) angle += 360;
        return angle;
    }

    public float angleDeg(MathVector2 reference) {
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

    public float angleRad(MathVector2 reference) {
        return (float)Math.atan2(reference.crs(this), reference.dot(this));
    }

    public static float angleRad(float x, float y) {
        return (float)Math.atan2(y, x);
    }

    public MathVector2 setAngleDeg(float degrees) {
        return setAngleRad(degrees * MathUtils.degreesToRadians);
    }

    public MathVector2 setAngleRad(float radians) {
        this.set(len(), 0f);
        this.rotateRad(radians);
        return this;
    }

    public MathVector2 rotateDeg(float degrees) {
        return rotateRad(degrees * MathUtils.degreesToRadians);
    }

    public MathVector2 rotateRad(float radians) {
        float cos = (float)Math.cos(radians);
        float sin = (float)Math.sin(radians);

        float newX = this.x * cos - this.y * sin;
        float newY = this.x * sin + this.y * cos;

        this.x = newX;
        this.y = newY;

        return this;
    }

    public MathVector2 rotateAroundDeg(MathVector2 reference, float degrees) {
        return this.sub(reference).rotateDeg(degrees).add(reference);
    }

    public MathVector2 rotateAroundRad(MathVector2 reference, float radians) {
        return this.sub(reference).rotateRad(radians).add(reference);
    }

    public MathVector2 rotate90(int dir) {
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

    public MathVector2 flip() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public MathVector2 clamp(MathVector2 min, MathVector2 max) {
        this.x = MathUtils.clampFloat(this.x, min.x, max.x);
        this.y = MathUtils.clampFloat(this.y, min.y, max.y);
        return this;
    }

    public MathVector2 lerp(MathVector2 target, float alpha) {
        final float invAlpha = 1.0f - alpha;
        this.x = (x * invAlpha) + (target.x * alpha);
        this.y = (y * invAlpha) + (target.y * alpha);
        return this;
    }

    public boolean epsilonEquals(MathVector2 other, float epsilon) {
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

    public boolean epsilonEquals(final MathVector2 other) {
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

    public boolean isOnLine(MathVector2 other) {
        return x * other.y - y * other.x == 0;
    }

    public boolean isPerpendicular(MathVector2 vector) {
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
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MathVector2 other = (MathVector2)obj;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) return false;
        return true;
    }

    public static boolean nearlyEqual(MathVector2 a, MathVector2 b, float tolerance) {
        return MathVector2.dst2(a, b) < tolerance * tolerance;
    }

    @Override
    public String toString () {
        return "(" + x + "," + y + ")";
    }

    public static float dst(float x1, float y1, float x2, float y2) {
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static float dst(final MathVector2 a, final MathVector2 b) {
        final float dx = b.x - a.x;
        final float dy = b.y - a.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static float dst2(float x1, float y1, float x2, float y2) {
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        return dx * dx + dy * dy;
    }

    public static float dst2(final MathVector2 a, final MathVector2 b) {
        final float dx = b.x - a.x;
        final float dy = b.y - a.y;
        return dx * dx + dy * dy;
    }

}
