package org.example.engine.core.math;

import java.util.Random;

public final class MathUtils {

    public static final float NANO_TO_SEC = 1 / 1000000000f;
    public static final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
    public static final float PI = (float)Math.PI;
    public static final float PI2 = PI * 2;
    public static final float HALF_PI = PI / 2;
    public static final float E = (float) Math.E;
    public static final float radiansToDegrees = 180f / PI;
    public static final float degreesToRadians = PI / 180;

    private static final int SIN_BITS = 14; // 16KB. Adjust for accuracy.
    private static final int SIN_MASK = ~(-1 << SIN_BITS);
    private static final int SIN_COUNT = SIN_MASK + 1;
    private static final float RADIANS_FULL = PI2;
    private static final float DEGREES_FULL = 360.0f;
    private static final float RADIANS_TO_INDEX = SIN_COUNT / RADIANS_FULL;
    private static final float DEGREES_TO_INDEX = SIN_COUNT / DEGREES_FULL;
    private static final Random random = new Random();
    public static final MathVector3[] canonicalCubeCorners = { // This is the clipping volume - a cube with 8 corners: (+-1, +-1, +-1)
            new MathVector3(-1, -1, -1), new MathVector3(1, -1, -1), new MathVector3(1, 1, -1), new MathVector3(-1, 1, -1), // near clipping plane corners
            new MathVector3(-1, -1, 1), new MathVector3(1, -1, 1), new MathVector3(1, 1, 1), new MathVector3(-1, 1, 1), // far clipping plane corners
    };

    private MathUtils() {}

    public static int random(final int range) {
        return random.nextInt(range);
    }

    public static int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    public static short clamp(short value, short min, short max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    public static long clamp(long value, long min, long max) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    public static int nextPowerOfTwo(int x) {
        int counter = 0;
        while (x > 0) {
            counter++;
            x = x >> 1;
        }
        return 1 << counter;
    }

    public static float atanUnchecked(double i) {
        double n = Math.abs(i);
        double c = (n - 1.0) / (n + 1.0);
        double c2 = c * c;
        double c3 = c * c2;
        double c5 = c3 * c2;
        double c7 = c5 * c2;
        double c9 = c7 * c2;
        double c11 = c9 * c2;
        return (float) (Math.signum(i) * ((Math.PI * 0.25) + (0.99997726 * c - 0.33262347 * c3 + 0.19354346 * c5 - 0.11643287 * c7 + 0.05265332 * c9 - 0.0117212 * c11)));
    }

    public static float atan2(final float y, float x) {
        float n = y / x;
        if (n != n)
            n = (y == x ? 1f : -1f); // if both y and x are infinite, n would be NaN
        else if (n - n != n - n) x = 0f; // if n is infinite, y is infinitely larger than x.
        if (x > 0)
            return atanUnchecked(n);
        else if (x < 0) {
            if (y >= 0) return atanUnchecked(n) + PI;
            return atanUnchecked(n) - PI;
        } else if (y > 0)
            return x + HALF_PI;
        else if (y < 0) return x - HALF_PI;
        return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
    }

    public static float areaTriangle(MathVector2 a, MathVector2 b, MathVector2 c) { return 0.5f * Math.abs((a.x - c.x) * (b.y - a.y) - (a.x - b.x) * (c.y - a.y)); }

    public static float areaTriangle(float x1, float y1, float x2, float y2, float x3, float y3) { return 0.5f * Math.abs((x1 - x3) * (y2 - y2) - (x1 - x2) * (y3 - y1)); }

    public static float max(float a, float b, float c) {
        return Math.max(a, Math.max(b, c));
    }

    public static float max(float a, float b, float c, float d) {
        return Math.max(a, MathUtils.max(b, c, d));
    }

    public static float min(float a, float b, float c) {
        return Math.min(a, Math.min(b, c));
    }

    public static float min(float a, float b, float c, float d) {
        return Math.min(a, MathUtils.min(b, c, d));
    }

    // [x1, x2] and [x3, x4] are intervals.
    public static float intervalsOverlap(float x1, float x2, float x3, float x4) {
        if (x1 > x2) {
            float tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        if (x3 > x4) {
            float tmp = x3;
            x3 = x4;
            x4 = tmp;
        }
        if (x2 <= x3 || x4 <= x1) return 0;
        if (x3 <= x1 && x4 < x2) return x4 - x1;
        if (x1 <= x3 && x4 <= x2) return x4 - x3;
        if (x1 <= x3) return x2 - x3;
        return x2 - x1;
    }

    public static float sin(float radians) {
        return Sin.lookup[(int)(radians * RADIANS_TO_INDEX) & SIN_MASK];
    }

    public static float cos(float radians) {
        return Sin.lookup[(int)((radians + HALF_PI) * RADIANS_TO_INDEX) & SIN_MASK];
    }

    public static float sinDeg(float degrees) {
        return Sin.lookup[(int)(degrees * DEGREES_TO_INDEX) & SIN_MASK];
    }

    public static float cosDeg(float degrees) { return Sin.lookup[(int)((degrees + 90) * DEGREES_TO_INDEX) & SIN_MASK]; }

    public static float tan(float radians) {
        radians /= PI;
        radians += 0.5f;
        radians -= Math.floor(radians);
        radians -= 0.5f;
        radians *= PI;
        final float x2 = radians * radians, x4 = x2 * x2;
        return radians * ((0.0010582010582010583f) * x4 - (0.1111111111111111f) * x2 + 1f) / ((0.015873015873015872f) * x4 - (0.4444444444444444f) * x2 + 1f);
    }

    static public float acos(float a) {
        float a2 = a * a; // a squared
        float a3 = a * a2; // a cubed
        if (a >= 0f) return (float) Math.sqrt(1f - a) * (1.5707288f - 0.2121144f * a + 0.0742610f * a2 - 0.0187293f * a3);
        return 3.14159265358979323846f - (float)Math.sqrt(1f + a) * (1.5707288f + 0.2121144f * a + 0.0742610f * a2 + 0.0187293f * a3);
    }


    public static float asin(float a) {
        float a2 = a * a; // a squared
        float a3 = a * a2; // a cubed
        if (a >= 0f) return 1.5707963267948966f - (float)Math.sqrt(1f - a) * (1.5707288f - 0.2121144f * a + 0.0742610f * a2 - 0.0187293f * a3);
        return -1.5707963267948966f + (float)Math.sqrt(1f + a) * (1.5707288f + 0.2121144f * a + 0.0742610f * a2 + 0.0187293f * a3);
    }

    public static float tanDeg(float degrees) {
        degrees *= (1f / 180f);
        degrees += 0.5f;
        degrees -= Math.floor(degrees);
        degrees -= 0.5f;
        degrees *= PI;
        final float x2 = degrees * degrees, x4 = x2 * x2;
        return degrees * ((0.0010582010582010583f) * x4 - (0.1111111111111111f) * x2 + 1f)
                / ((0.015873015873015872f) * x4 - (0.4444444444444444f) * x2 + 1f);
    }

    public static float atan(float i) {
        double n = Math.min(Math.abs(i), Double.MAX_VALUE);
        double c = (n - 1.0) / (n + 1.0);
        double c2 = c * c;
        double c3 = c * c2;
        double c5 = c3 * c2;
        double c7 = c5 * c2;
        double c9 = c7 * c2;
        double c11 = c9 * c2;
        return Math.signum(i) * (float)((Math.PI * 0.25) + (0.99997726 * c - 0.33262347 * c3 + 0.19354346 * c5 - 0.11643287 * c7 + 0.05265332 * c9 - 0.0117212 * c11));
    }

    public static float asinDeg(float a) {
        float a2 = a * a; // a squared
        float a3 = a * a2; // a cubed
        if (a >= 0f) return 90f - (float)Math.sqrt(1f - a) * (89.99613099964837f - 12.153259893949748f * a + 4.2548418824210055f * a2 - 1.0731098432343729f * a3);
        return (float) Math.sqrt(1f + a) * (89.99613099964837f + 12.153259893949748f * a + 4.2548418824210055f * a2 + 1.0731098432343729f * a3) - 90f;
    }

    public static float acosDeg(float a) {
        float a2 = a * a; // a squared
        float a3 = a * a2; // a cubed
        if (a >= 0f) return (float) Math.sqrt(1f - a) * (89.99613099964837f - 12.153259533621753f * a + 4.254842010910525f * a2 - 1.0731098035209208f * a3);
        return 180f - (float) Math.sqrt(1f + a) * (89.99613099964837f + 12.153259533621753f * a + 4.254842010910525f * a2 + 1.0731098035209208f * a3);
    }

    public static float atanDeg(float i) {
        double n = Math.min(Math.abs(i), Double.MAX_VALUE);
        double c = (n - 1.0) / (n + 1.0);
        double c2 = c * c;
        double c3 = c * c2;
        double c5 = c3 * c2;
        double c7 = c5 * c2;
        double c9 = c7 * c2;
        double c11 = c9 * c2;
        return (float)(Math.signum(i) * (45.0 + (57.2944766070562 * c - 19.05792099799635 * c3 + 11.089223410359068 * c5 - 6.6711120475953765 * c7 + 3.016813013351768 * c9 - 0.6715752908287405 * c11)));
    }

    public static boolean isZero(float value) {
        return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
    }

    public static boolean isZero(float value, float tolerance) {
        return Math.abs(value) <= tolerance;
    }

    public static boolean isEqual(float a, float b) {
        return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
    }

    public static boolean isEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    /** @return the logarithm of value with base a */
    public static float log(float a, float value) {
        return (float)(Math.log(value) / Math.log(a));
    }

    private static class Sin {

        private static final float[] lookup = new float[SIN_COUNT];

        static {
            for (int i = 0; i < SIN_COUNT; i++) lookup[i] = (float)Math.sin((i + 0.5f) / SIN_COUNT * RADIANS_FULL);
            lookup[0] = 0f;
            lookup[(int)(90 * DEGREES_TO_INDEX) & SIN_MASK] = 1f;
            lookup[(int)(180 * DEGREES_TO_INDEX) & SIN_MASK] = 0f;
            lookup[(int)(270 * DEGREES_TO_INDEX) & SIN_MASK] = -1f;
        }

    }

}
