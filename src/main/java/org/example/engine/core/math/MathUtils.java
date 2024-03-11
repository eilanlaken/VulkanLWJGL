package org.example.engine.core.math;

import java.util.Random;

public final class MathUtils {

    static public final float nanoToSec = 1 / 1000000000f;

    // ---
    static public final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
    static public final float PI = (float)Math.PI;
    static public final float PI2 = PI * 2;
    static public final float HALF_PI = PI / 2;
    static public final float E = (float)Math.E;
    static private final int SIN_BITS = 14; // 16KB. Adjust for accuracy.
    static private final int SIN_MASK = ~(-1 << SIN_BITS);
    static private final int SIN_COUNT = SIN_MASK + 1;
    static private final float radFull = PI2;
    static private final float degFull = 360;
    static private final float radToIndex = SIN_COUNT / radFull;
    static private final float degToIndex = SIN_COUNT / degFull;
    static public final float radiansToDegrees = 180f / PI;
    static public final float radDeg = radiansToDegrees;
    static public final float degreesToRadians = PI / 180;
    static public final float degRad = degreesToRadians;

    static private class Sin {
        static final float[] lookup = new float[SIN_COUNT];

        static {
            for (int i = 0; i < SIN_COUNT; i++) lookup[i] = (float)Math.sin((i + 0.5f) / SIN_COUNT * radFull);
            lookup[0] = 0f;
            lookup[(int)(90 * degToIndex) & SIN_MASK] = 1f;
            lookup[(int)(180 * degToIndex) & SIN_MASK] = 0f;
            lookup[(int)(270 * degToIndex) & SIN_MASK] = -1f;
        }
    }


    private static final Random random = new Random();

    public static int random(final int range) {
        return random.nextInt(range);
    }

    public static int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    static public short clamp(short value, short min, short max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public long clamp(long value, long min, long max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static int nextPowerOfTwo(int x) {
        int counter = 0;
        while (x > 0) {
            counter++;
            x = x >> 1;
        }
        return 1 << counter;
    }

    // yanked from libGDX MathUtils
    public static float atanUnchecked(double i) {
        double n = Math.abs(i);
        double c = (n - 1.0) / (n + 1.0);
        double c2 = c * c;
        double c3 = c * c2;
        double c5 = c3 * c2;
        double c7 = c5 * c2;
        double c9 = c7 * c2;
        double c11 = c9 * c2;
        return (float)(Math.signum(i) * ((Math.PI * 0.25)
                + (0.99997726 * c - 0.33262347 * c3 + 0.19354346 * c5 - 0.11643287 * c7 + 0.05265332 * c9 - 0.0117212 * c11)));
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

    public static float areaTriangle(Vector2 a, Vector2 b, Vector2 c) { return 0.5f * Math.abs((a.x - c.x) * (b.y - a.y) - (a.x - b.x) * (c.y - a.y)); }

    public static float areaTriangle(float x1, float y1, float x2, float y2, float x3, float y3) { return 0.5f * Math.abs((x1 - x3) * (y2 - y2) - (x1 - x2) * (y3 - y1)); }

    public static float max(float a, float b, float c) {
        return Math.max(a, Math.max(b, c));
    }

    static public float sin(float radians) {
        return Sin.lookup[(int)(radians * radToIndex) & SIN_MASK];
    }

    static public float cos(float radians) {
        return Sin.lookup[(int)((radians + HALF_PI) * radToIndex) & SIN_MASK];
    }

    static public float sinDeg(float degrees) {
        return Sin.lookup[(int)(degrees * degToIndex) & SIN_MASK];
    }

    static public float cosDeg(float degrees) { return Sin.lookup[(int)((degrees + 90) * degToIndex) & SIN_MASK]; }

}
