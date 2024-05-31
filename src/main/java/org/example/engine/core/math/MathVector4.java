package org.example.engine.core.math;

import org.example.engine.core.memory.MemoryPool;

public class MathVector4 implements MemoryPool.Reset {

    public static final MathVector4 X_UNIT = new MathVector4(1, 0, 0, 0);
    public static final MathVector4 Y_UNIT = new MathVector4(0, 1, 0, 0);
    public static final MathVector4 Z_UNIT = new MathVector4(0, 0, 1, 0);
    public static final MathVector4 W_UNIT = new MathVector4(0, 0, 0, 1);
    public static final MathVector4 Zero   = new MathVector4(0, 0, 0, 0);

    public float x;
    public float y;
    public float z;
    public float w;

    /** Constructs a vector at (0,0,0,0) */
    public MathVector4() {
    }

    /** Creates a vector with the given components
     * @param x The x-component
     * @param y The y-component
     * @param z The z-component
     * @param w The w-component * */
    public MathVector4(float x, float y, float z, float w) {
        this.set(x, y, z, w);
    }

    /** Creates a vector from the given Vector4
     * @param vector The vector */
    public MathVector4(final MathVector4 vector) {
        this.set(vector.x, vector.y, vector.z, vector.w);
    }

    /** Creates a vector from the given array. The array must have at least 4 elements.
     *
     * @param values The array */
    public MathVector4(final float[] values) {
        this.set(values[0], values[1], values[2], values[3]);
    }

    /** Creates a vector from the given Vector2 and z- and w-components
     *
     * @param vector The vector
     * @param z The z-component
     * @param w The w-component */
    public MathVector4(final MathVector2 vector, float z, float w) {
        this.set(vector.x, vector.y, z, w);
    }

    /** Creates a vector from the given Vector3 and w-component
     *
     * @param vector The vector
     * @param w The w-component */
    public MathVector4(final MathVector3 vector, float w) {
        this.set(vector.x, vector.y, vector.z, w);
    }

    /** Sets the vector to the given components
     *
     * @param x The x-component
     * @param y The y-component
     * @param z The z-component
     * @param w The w-component
     * @return this vector for chaining */
    public MathVector4 set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public MathVector4 set(final MathVector4 vector) {
        return this.set(vector.x, vector.y, vector.z, vector.w);
    }

    /** Sets the components from the array. The array must have at least 4 elements
     *
     * @param values The array
     * @return this vector for chaining */
    public MathVector4 set(final float[] values) {
        return this.set(values[0], values[1], values[2], values[3]);
    }

    /** Sets the components to the given Vector2, z-component and w-component
     *
     * @param vector The vector2 holding the x- and y-components
     * @param z The z-component
     * @param w The w-component
     * @return This vector for chaining */
    public MathVector4 set(final MathVector3 vector, float z, float w) {
        return this.set(vector.x, vector.y, z, w);
    }

    /** Sets the components of the given vector3 and w-component
     *
     * @param vector The vector
     * @param w The w-component
     * @return This vector for chaining */
    public MathVector4 set(final MathVector3 vector, float w) {
        return this.set(vector.x, vector.y, vector.z, w);
    }

    public MathVector4 setToRandomDirection () {
        // The algorithm here is #19 at
        // https://extremelearning.com.au/how-to-generate-uniformly-random-points-on-n-spheres-and-n-balls/ .
        // It is the only recommended way to randomly generate a point on the surface of the unit 4D hypersphere.

        // From the documentation of Random.nextGaussian(), but using float math.
        float v1, v2, s, multiplier;
        do {
            v1 = (MathUtils.random() - 0.5f) * 2; // between -1.0 and 1.0
            v2 = (MathUtils.random() - 0.5f) * 2; // between -1.0 and 1.0
            s = v1 * v1 + v2 * v2;
        } while (s >= 1 || s == 0);
        multiplier = (float)Math.sqrt(-2 * Math.log(s) / s);
        x = v1 * multiplier;
        y = v2 * multiplier;
        // Each run of the Marsaglia polar method produces two normal-distributed variates.
        do {
            v1 = (MathUtils.random() - 0.5f) * 2; // between -1.0 and 1.0
            v2 = (MathUtils.random() - 0.5f) * 2; // between -1.0 and 1.0
            s = v1 * v1 + v2 * v2;
        } while (s >= 1 || s == 0);
        multiplier = (float)Math.sqrt(-2 * Math.log(s) / s);
        z = v1 * multiplier;
        w = v2 * multiplier;
        // Once we normalize four normal-distributed floats, we have a point on the unit hypersphere's surface.
        return this.nor();
    }

    public MathVector4 cpy() {
        return new MathVector4(this);
    }

    public MathVector4 add(final MathVector4 vector) {
        return this.add(vector.x, vector.y, vector.z, vector.w);
    }

    /** Adds the given components to this vector
     * @param x Added to the x-component
     * @param y Added to the y-component
     * @param z Added to the z-component
     * @param w Added to the w-component
     * @return This vector for chaining. */
    public MathVector4 add(float x, float y, float z, float w) {
        return this.set(this.x + x, this.y + y, this.z + z, this.w + w);
    }

    /** Adds the given value to all four components of the vector.
     *
     * @param values The value
     * @return This vector for chaining */
    public MathVector4 add(float values) {
        return this.set(this.x + values, this.y + values, this.z + values, this.w + values);
    }

    public MathVector4 sub(final MathVector4 a_vec) {
        return this.sub(a_vec.x, a_vec.y, a_vec.z, a_vec.w);
    }

    /** Subtracts the given components from this vector.
     *
     * @param x Subtracted from the x-component
     * @param y Subtracted from the y-component
     * @param z Subtracted from the z-component
     * @param w Subtracted from the w-component
     * @return This vector for chaining */
    public MathVector4 sub (float x, float y, float z, float w) {
        return this.set(this.x - x, this.y - y, this.z - z, this.w - w);
    }

    /** Subtracts the given value from all components of this vector
     *
     * @param value The value
     * @return This vector for chaining */
    public MathVector4 sub (float value) {
        return this.set(this.x - value, this.y - value, this.z - value, this.w - value);
    }

    /** Multiplies each component of this vector by the given scalar
     * @param scalar Each component will be multiplied by this float
     * @return This vector for chaining */
    public MathVector4 scl(float scalar) {
        return this.set(this.x * scalar, this.y * scalar, this.z * scalar, this.w * scalar);
    }

    /** Multiplies each component of this vector by the corresponding component in other
     * @param other Another Vector4 that will be used to scale this
     * @return This vector for chaining */
    public MathVector4 scl(final MathVector4 other) {
        return this.set(x * other.x, y * other.y, z * other.z, w * other.w);
    }

    /** Scales this vector by the given values
     * @param vx Multiplied with the X value
     * @param vy Multiplied with the Y value
     * @param vz Multiplied with the Z value
     * @param vw Multiplied with the W value
     * @return This vector for chaining */
    public MathVector4 scl (float vx, float vy, float vz, float vw) {
        return this.set(this.x * vx, this.y * vy, this.z * vz, this.w * vw);
    }

    public MathVector4 mulAdd(MathVector4 vec, float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        this.z += vec.z * scalar;
        this.w += vec.w * scalar;
        return this;
    }

    public MathVector4 mulAdd(MathVector4 vec, MathVector4 mulVec) {
        this.x += vec.x * mulVec.x;
        this.y += vec.y * mulVec.y;
        this.z += vec.z * mulVec.z;
        this.w += vec.w * mulVec.w;
        return this;
    }

    /** @return The Euclidean length */
    public static float len(final float x, final float y, final float z, float w) {
        return (float)Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public float len() {
        return (float)Math.sqrt(x * x + y * y + z * z + w * w);
    }

    /** @return The squared Euclidean length */
    public static float len2(final float x, final float y, final float z, float w) {
        return x * x + y * y + z * z + w * w;
    }

    public float len2() {
        return x * x + y * y + z * z + w * w;
    }

    /** Returns true if this vector and the vector parameter have identical components.
     * @param vector The other vector
     * @return Whether this and the other vector are equal with exact precision */
    public boolean idt (final MathVector4 vector) {
        return x == vector.x && y == vector.y && z == vector.z && w == vector.w;
    }

    /** @return The Euclidean distance between the two specified vectors */
    public static float dst(final float x1, final float y1, final float z1, final float w1, final float x2, final float y2,
                             final float z2, final float w2) {
        final float a = x2 - x1;
        final float b = y2 - y1;
        final float c = z2 - z1;
        final float d = w2 - w1;
        return (float)Math.sqrt(a * a + b * b + c * c + d * d);
    }

    public float dst(final MathVector4 vector) {
        final float a = vector.x - x;
        final float b = vector.y - y;
        final float c = vector.z - z;
        final float d = vector.w - w;
        return (float)Math.sqrt(a * a + b * b + c * c + d * d);
    }

    /** @return the distance between this point and the given point */
    public float dst (float x, float y, float z, float w) {
        final float a = x - this.x;
        final float b = y - this.y;
        final float c = z - this.z;
        final float d = w - this.w;
        return (float)Math.sqrt(a * a + b * b + c * c + d * d);
    }

    /** @return the squared distance between the given points */
    public static float dst2(final float x1, final float y1, final float z1, final float w1, final float x2, final float y2,
                              final float z2, final float w2) {
        final float a = x2 - x1;
        final float b = y2 - y1;
        final float c = z2 - z1;
        final float d = w2 - w1;
        return a * a + b * b + c * c + d * d;
    }

    public float dst2(MathVector4 point) {
        final float a = point.x - x;
        final float b = point.y - y;
        final float c = point.z - z;
        final float d = point.w - w;
        return a * a + b * b + c * c + d * d;
    }

    /** Returns the squared distance between this point and the given point
     * @param x The x-component of the other point
     * @param y The y-component of the other point
     * @param z The z-component of the other point
     * @param w The w-component of the other point
     * @return The squared distance */
    public float dst2 (float x, float y, float z, float w) {
        final float a = x - this.x;
        final float b = y - this.y;
        final float c = z - this.z;
        final float d = w - this.w;
        return a * a + b * b + c * c + d * d;
    }

    public MathVector4 nor() {
        final float len2 = this.len2();
        if (len2 == 0f || len2 == 1f) return this;
        return this.scl(1f / (float)Math.sqrt(len2));
    }

    /** @return The dot product between the two vectors */
    public static float dot (float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
        return x1 * x2 + y1 * y2 + z1 * z2 + w1 * w2;
    }

    public float dot(final MathVector4 vector) {
        return x * vector.x + y * vector.y + z * vector.z + w * vector.w;
    }

    /** Returns the dot product between this and the given vector (given as 4 components).
     * @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @param z The z-component of the other vector
     * @param w The w-component of the other vector
     * @return The dot product */
    public float dot(float x, float y, float z, float w) {
        return this.x * x + this.y * y + this.z * z + this.w * w;
    }

    public boolean isUnit() {
        return isUnit(0.000000001f);
    }

    public boolean isUnit(final float margin) {
        return Math.abs(len2() - 1f) < margin;
    }

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0 && w == 0;
    }

    public boolean isZero(final float margin) {
        return len2() < margin;
    }

    /** @return true if this vector is in line with the other vector (either in the same or the opposite direction) */
    public boolean isOnLine(MathVector4 other, float epsilon) {
        // The algorithm used here is based on the one in yama, a C++ math library.
        // https://github.com/iboB/yama/blob/f08a71c6fd84df5eed62557000373f17f14e1ec7/include/yama/vector4.hpp#L566-L598
        // This code uses a flags variable to avoid allocating a float array.
        int flags = 0;
        float dx = 0, dy = 0, dz = 0, dw = 0;

        if (MathUtils.isZero(x, epsilon)) {
            if (!MathUtils.isZero(other.x, epsilon)) {
                return false;
            }
        } else {
            dx = x / other.x;
            flags |= 1;
        }
        if (MathUtils.isZero(y, epsilon)) {
            if (!MathUtils.isZero(other.y, epsilon)) {
                return false;
            }
        } else {
            dy = y / other.y;
            flags |= 2;
        }
        if (MathUtils.isZero(z, epsilon)) {
            if (!MathUtils.isZero(other.z, epsilon)) {
                return false;
            }
        } else {
            dz = z / other.z;
            flags |= 4;
        }
        if (MathUtils.isZero(w, epsilon)) {
            if (!MathUtils.isZero(other.w, epsilon)) {
                return false;
            }
        } else {
            dw = w / other.w;
            flags |= 8;
        }

        switch (flags) {
            case 0:
            case 1:
            case 2:
            case 4:
            case 8:
                return true;
            case 3:
                return MathUtils.floatsEqual(dx, dy, epsilon);
            case 5:
                return MathUtils.floatsEqual(dx, dz, epsilon);
            case 9:
                return MathUtils.floatsEqual(dx, dw, epsilon);
            case 6:
                return MathUtils.floatsEqual(dy, dz, epsilon);
            case 10:
                return MathUtils.floatsEqual(dy, dw, epsilon);
            case 12:
                return MathUtils.floatsEqual(dz, dw, epsilon);
            case 7:
                return MathUtils.floatsEqual(dx, dy, epsilon) && MathUtils.floatsEqual(dx, dz, epsilon);
            case 11:
                return MathUtils.floatsEqual(dx, dy, epsilon) && MathUtils.floatsEqual(dx, dw, epsilon);
            case 13:
                return MathUtils.floatsEqual(dx, dz, epsilon) && MathUtils.floatsEqual(dx, dw, epsilon);
            case 14:
                return MathUtils.floatsEqual(dy, dz, epsilon) && MathUtils.floatsEqual(dy, dw, epsilon);
            default: // this is essentially case 15:
                return MathUtils.floatsEqual(dx, dy, epsilon) && MathUtils.floatsEqual(dx, dz, epsilon) && MathUtils.floatsEqual(dx, dw, epsilon);
        }
    }

    /** @return true if this vector is in line with the other vector (either in the same or the opposite direction) */
    public boolean isOnLine(MathVector4 other) {
        return isOnLine(other, MathUtils.FLOAT_ROUNDING_ERROR);
    }

    /** @return true if this vector is collinear with the other vector ({@link #isOnLine(MathVector4, float)} &&
     *         {@link #hasSameDirection(MathVector4)}). */
    public boolean isCollinear(MathVector4 other, float epsilon) {
        return isOnLine(other, epsilon) && hasSameDirection(other);
    }

    /** @return true if this vector is collinear with the other vector ({@link #isOnLine(MathVector4)} &&
     *         {@link #hasSameDirection(MathVector4)}). */
    public boolean isCollinear(MathVector4 other) {
        return isOnLine(other) && hasSameDirection(other);
    }

    /** @return true if this vector is collinear with the other vector ({@link #isOnLine(MathVector4, float)} &&
     *         {@link #hasSameDirection(MathVector4)}). */
    public boolean isCollinearOpposite(MathVector4 other, float epsilon) {
        return isOnLine(other, epsilon) && hasOppositeDirection(other);
    }

    /** @return true if this vector is collinear with the other vector ({@link #isOnLine(MathVector4)} &&
     *         {@link #hasSameDirection(MathVector4)}). */
    public boolean isCollinearOpposite(MathVector4 other) {
        return isOnLine(other) && hasOppositeDirection(other);
    }

    public boolean isPerpendicular(MathVector4 vector) {
        return MathUtils.isZero(dot(vector));
    }

    public boolean isPerpendicular(MathVector4 vector, float epsilon) {
        return MathUtils.isZero(dot(vector), epsilon);
    }

    public boolean hasSameDirection(MathVector4 vector) {
        return dot(vector) > 0;
    }

    public boolean hasOppositeDirection(MathVector4 vector) {
        return dot(vector) < 0;
    }

    public MathVector4 lerp(final MathVector4 target, float alpha) {
        x += alpha * (target.x - x);
        y += alpha * (target.y - y);
        z += alpha * (target.z - z);
        w += alpha * (target.w - w);
        return this;
    }

    @Override
    public void reset() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    /** Converts this {@code Vector4} to a string in the format {@code (x,y,z,w)}. Strings with this exact format can be parsed
     * with {@link #fromString(String)}.
     * @return a string representation of this object. */
    @Override
    public String toString () {
        return "(" + x + "," + y + "," + z + "," + w + ")";
    }

    /** Sets this {@code Vector4} to the value represented by the specified string according to the format of {@link #toString()}.
     * @param v the string.
     * @return this vector, set with the value from v, for chaining */
    public MathVector4 fromString(String v) {
        int s0 = v.indexOf(',', 1);
        int s1 = v.indexOf(',', s0 + 1);
        int s2 = v.indexOf(',', s1 + 1);
        if (s0 != -1 && s1 != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
            try {
                float x = Float.parseFloat(v.substring(1, s0));
                float y = Float.parseFloat(v.substring(s0 + 1, s1));
                float z = Float.parseFloat(v.substring(s1 + 1, s2));
                float w = Float.parseFloat(v.substring(s2 + 1, v.length() - 1));
                return this.set(x, y, z, w);
            } catch (NumberFormatException ex) {
                // Throw a GdxRuntimeException...
            }
        }
        throw new IllegalArgumentException("Malformed Vector4: " + v);
    }

    public MathVector4 limit(float limit) {
        return limit2(limit * limit);
    }

    public MathVector4 limit2(float limit2) {
        float len2 = len2();
        if (len2 > limit2) {
            scl((float)Math.sqrt(limit2 / len2));
        }
        return this;
    }

    public MathVector4 setLength(float len) {
        return setLength2(len * len);
    }

    public MathVector4 setLength2(float len2) {
        float oldLen2 = len2();
        return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float)Math.sqrt(len2 / oldLen2));
    }

    public MathVector4 clamp(float min, float max) {
        if (min > max) {
            float tmp = min;
            min = max;
            max = tmp;
        }
        final float len2 = len2();
        if (len2 == 0f) return this;
        float max2 = max * max;
        if (len2 > max2) return scl((float)Math.sqrt(max2 / len2));
        float min2 = min * min;
        if (len2 < min2) return scl((float)Math.sqrt(min2 / len2));
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        result = prime * result + Float.floatToIntBits(z);
        result = prime * result + Float.floatToIntBits(w);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MathVector4 other = (MathVector4)obj;
        if (!MathUtils.floatsEqual(x, other.x)) return false;
        if (!MathUtils.floatsEqual(y, other.y)) return false;
        if (!MathUtils.floatsEqual(z, other.z)) return false;
        if (!MathUtils.floatsEqual(w, other.w)) return false;
        return true;
    }

    public boolean epsilonEquals(final MathVector4 other, float epsilon) {
        if (other == null) return false;
        if (Math.abs(other.x - x) > epsilon) return false;
        if (Math.abs(other.y - y) > epsilon) return false;
        if (Math.abs(other.z - z) > epsilon) return false;
        if (Math.abs(other.w - w) > epsilon) return false;
        return true;
    }

    /** Compares this vector with the other vector, using the supplied epsilon for fuzzy equality testing.
     * @param x x component of the other vector to compare
     * @param y y component of the other vector to compare
     * @param z z component of the other vector to compare
     * @param w w component of the other vector to compare
     * @param epsilon how much error to tolerate and still consider two floats equal
     * @return whether the vectors are the same. */
    public boolean epsilonEquals(float x, float y, float z, float w, float epsilon) {
        if (Math.abs(x - this.x) > epsilon) return false;
        if (Math.abs(y - this.y) > epsilon) return false;
        if (Math.abs(z - this.z) > epsilon) return false;
        if (Math.abs(w - this.w) > epsilon) return false;
        return true;
    }

    /** Compares this vector with the other vector using {@link MathUtils#FLOAT_ROUNDING_ERROR} for its epsilon.
     *
     * @param other other vector to compare
     * @return true if the vectors are equal, otherwise false */
    public boolean epsilonEquals(final MathVector4 other) {
        return epsilonEquals(other, MathUtils.FLOAT_ROUNDING_ERROR);
    }

    /** Compares this vector with the other vector using {@link MathUtils#FLOAT_ROUNDING_ERROR} for its epsilon.
     *
     * @param x x component of the other vector to compare
     * @param y y component of the other vector to compare
     * @param z z component of the other vector to compare
     * @param w w component of the other vector to compare
     * @return true if the vectors are equal, otherwise false */
    public boolean epsilonEquals(float x, float y, float z, float w) {
        return epsilonEquals(x, y, z, w, MathUtils.FLOAT_ROUNDING_ERROR);
    }

    public MathVector4 setZero() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
        return this;
    }

}
