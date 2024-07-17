package org.example.engine.core.math;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayFloat;
import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.collections.CollectionsUtils;
import org.example.engine.core.memory.MemoryPool;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Random;

// TODO: implement init() block that will take care of configuration.
public final class MathUtils {

    public static  final float   FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
    public static  final float   PI                   = (float) Math.PI;
    public static  final float   PI_TWO               = PI * 2;
    public static  final float   PI_HALF              = PI / 2;
    public static  final float   E                    = (float) Math.E;
    public static  final float   radiansToDegrees     = 180f / PI;
    public static  final float   degreesToRadians     = PI / 180;
    private static final int     SIN_BITS             = 14; // 16KB. Adjust for accuracy.
    private static final int     SIN_MASK             = ~(-1 << SIN_BITS);
    private static final int     SIN_COUNT            = SIN_MASK + 1;
    private static final float   RADIANS_FULL         = PI_TWO;
    private static final float   DEGREES_FULL         = 360.0f;
    private static final float   RADIANS_TO_INDEX     = SIN_COUNT / RADIANS_FULL;
    private static final float   DEGREES_TO_INDEX     = SIN_COUNT / DEGREES_FULL;
    private static final Random  random               = new Random();

    //private static final MemoryPool<Array> array = new MemoryPool<>(Array.class, 5); // TODO: see what's up
    private static final MemoryPool<ArrayFloat> floatArrayPool = new MemoryPool<>(ArrayFloat.class, 5);
    private static final MemoryPool<ArrayInt>   intArrayPool   = new MemoryPool<>(ArrayInt.class, 5);
    private static final MemoryPool<Vector2>    vectors2Pool   = new MemoryPool<>(Vector2.class, 5);
    private static final MemoryPool<Matrix2x2>  matrix2x2Pool  = new MemoryPool<>(Matrix2x2.class, 2);


    /* polygon triangulation */
    private static final Array<Vector2> polygonVertices = new Array<>(false, 10);
    @Deprecated private static final ArrayInt       indexList       = new ArrayInt(); // TODO: use pool


    private MathUtils() {}

    public static float random() {
        return random.nextFloat();
    }

    public static int random(final int range) {
        return random.nextInt(range);
    }

    public static int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    public static int clampInt(int value, int min, int max) {
        if (min < max) {
            if (value < min) return min;
            return Math.min(value, max);
        }
        if (value < max) return max;
        return Math.min(value, min);
    }

    public static long clampLong(long value, long min, long max) {
        if (min < max) {
            if (value < min) return min;
            return Math.min(value, max);
        }
        if (value < max) return max;
        return Math.min(value, min);
    }

    public static float clampFloat(float value, float min, float max) {
        if (min < max) {
            if (value < min) return min;
            return Math.min(value, max);
        }
        if (value < max) return max;
        return Math.min(value, min);
    }

    public static double clampDouble(double value, double min, double max) {
        if (min < max) {
            if (value < min) return min;
            return Math.min(value, max);
        }
        if (value < max) return max;
        return Math.min(value, min);
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
            return x + PI_HALF;
        else if (y < 0) return x - PI_HALF;
        return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
    }

    public static float areaTriangle(float x1, float y1, float x2, float y2, float x3, float y3) { return 0.5f * Math.abs(x1*(y2 - y3) + x2*(y3 - y1) + x3*(y1 - y2)); }

    public static float areaTriangleSigned(float x1, float y1, float x2, float y2, float x3, float y3) { return 0.5f * (x1*(y2 - y3) + x2*(y3 - y1) + x3*(y1 - y2)); }

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
    public static float intervalsOverlap(float a_min, float a_max, float b_min, float b_max) {
        if (a_min > a_max) {
            float tmp = a_min;
            a_min = a_max;
            a_max = tmp;
        }
        if (b_min > b_max) {
            float tmp = b_min;
            b_min = b_max;
            b_max = tmp;
        }
        if (a_max <= b_min) return 0;
        if (b_max <= a_min) return 0;

        if (b_min <= a_min) {
            if (b_max <= a_max) return b_max - a_min;
            else return a_max - a_min;
        } else { //  a_min < b_min < a_max
            if (b_max <= a_max) return b_max - b_min;
            else return a_max - b_min;
        }
    }

    public static float sinRad(float radians) {
        return Sin.lookup[(int)(radians * RADIANS_TO_INDEX) & SIN_MASK];
    }

    public static float cosRad(float radians) {
        return Sin.lookup[(int)((radians + PI_HALF) * RADIANS_TO_INDEX) & SIN_MASK];
    }

    public static float sinDeg(float degrees) {
        return Sin.lookup[(int)(degrees * DEGREES_TO_INDEX) & SIN_MASK];
    }

    public static float cosDeg(float degrees) { return Sin.lookup[(int)((degrees + 90) * DEGREES_TO_INDEX) & SIN_MASK]; }

    public static float tanRad(float radians) {
        radians /= PI;
        radians += 0.5f;
        radians -= Math.floor(radians);
        radians -= 0.5f;
        radians *= PI;
        final float x2 = radians * radians, x4 = x2 * x2;
        return radians * ((0.0010582010582010583f) * x4 - (0.1111111111111111f) * x2 + 1f) / ((0.015873015873015872f) * x4 - (0.4444444444444444f) * x2 + 1f);
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

    public static float normalizeAngleDeg(float degrees) {
        degrees %= 360;
        if (degrees < 0) degrees += 360;
        return degrees;
    }

    public static float normalizeAngleRad(float rad) {
        rad %= PI_TWO;
        if (rad < 0) rad += PI_TWO;
        return rad;
    }

    /**
     * Finds the intersection of two line segments: S1 & S2
     * where S1 is the line segment between (a1, a2)
     * and   S2 is the line between (b1, b2).
     * Stores the result in out.
     * @returns 0 if lines are parallel
     * @returns 1 if lines intersect at a unique point
     * @returns 2 if lines coincide
     * @returns 3 if the intersection lies outside segment 1
     * @returns 4 if the intersection lies outside segment 2
     * @returns 5 if the intersection lies outside both segments
     * @param P1
     * @param P2
     * @param P3
     * @param P4
     * @param out
     */
    public static int segmentsIntersection_copied(Vector2 P1, Vector2 P2, Vector2 P3, Vector2 P4, Vector2 out) {
        float denom  = (P4.y-P3.y) * (P2.x-P1.x) - (P4.x-P3.x) * (P2.y-P1.y);
        float numera = (P4.x-P3.x) * (P1.y-P3.y) - (P4.y-P3.y) * (P1.x-P3.x);
        float numerb = (P2.x-P1.x) * (P1.y-P3.y) - (P2.y-P1.y) * (P1.x-P3.x);

        if (Math.abs(numera) < FLOAT_ROUNDING_ERROR && Math.abs(numerb) < FLOAT_ROUNDING_ERROR && Math.abs(denom) < FLOAT_ROUNDING_ERROR) {
            /* Segments are parallel - they are either completely separate, or have some overlap. */
            /* It is still possible that they have a unique intersection - if segment b "continues" a; i.e.:
                a1 == b1 or a1 == b2, or a2 == b1 or a2 == b2.
             */
            if (P1.equals(P3) || P1.equals(P4)) {
                out.set(P1);
                return 1;
            } else if (P2.equals(P3) || P2.equals(P4)) {
                out.set(P2);
                return 1;
            }

            out.set(Float.NaN, Float.NaN);
            return 2; //meaning the lines coincide
        }

        if (Math.abs(denom) < FLOAT_ROUNDING_ERROR) {
            out.x = Float.NaN;
            out.y = Float.NaN;
            return 0; //meaning lines are parallel
        }

        float mua = numera / denom;
        float mub = numerb / denom;
        out.x = P1.x + mua * (P2.x - P1.x);
        out.y = P1.y + mua * (P2.y - P1.y);
        boolean out1 = mua < 0 || mua > 1;
        boolean out2 = mub < 0 || mub > 1;

        if (out1 & out2) return 5; //the intersection lies outside both segments
        else if (out1) return 3; //the intersection lies outside segment 1
        else if (out2) return 4; //the intersection lies outside segment 2
        else return 1; //the intersection lies inside both segments
    }

    public static int segmentsIntersection(float a1x, float a1y, float a2x, float a2y, float b1x, float b1y, float b2x, float b2y, Vector2 out) {
        float Ax = a2x - a1x;
        float Ay = a2y - a1y;
        float Bx = b2x - b1x;
        float By = b2y - b1y;
        float Cx = b1x - a1x;
        float Cy = b1y - a1y;

        float det = Ax * By - Ay * Bx;
        float t = (Cx * By - Cy * Bx) / det;
        float u = (Ay * Cx - Ax * Cy) / det;

        /* handle degenerate cases */
        if (isZero(det)) {
            /* a1 == a2 , b1 == b2 */
            if (floatsEqual(a1x, a2x) && floatsEqual(a1y, a2y) && floatsEqual(b1x, b2x) && floatsEqual(b1y, b2y)) {
                if (floatsEqual(a1x, b2x) && floatsEqual(a1y, b2y)) {
                    out.set(a1x, a1y);
                    return 2;
                } else {
                    out.set(Float.NaN, Float.NaN);
                    return 0;
                }
            }

            /* a1 == a2 */
            if (floatsEqual(a1x, a2x) && floatsEqual(a1y, a2y) && pointOnSegment(a1x, a1y,b1x, b1y,b2x, b2y)) {
                out.set(a1x, a1y);
                return 1;
            }

            /* b1 == b2 */
            if (floatsEqual(b1x, b2x) && floatsEqual(b1y, b2y) && pointOnSegment(b1x, b1y,a1x, a1y,a2x, a2y)) {
                out.set(b1x, b1y);
                return 1;
            }

            /* Segments are parallel or coincide. */
            /* It is still possible that they have a unique intersection - if segment b "continues" a; i.e.:
                a1 == b1 or a1 == b2, or a2 == b1 or a2 == b2.
             */
            if ((floatsEqual(a1x, b1x) && floatsEqual(a1y, b1y)) || ((floatsEqual(a1x, b2x) && floatsEqual(a1y, b2y)))) {
                out.set(a1x, a1y);
                return 1;
            }

            if ((floatsEqual(a2x, b1x) && floatsEqual(a2y, b1y)) || ((floatsEqual(a2x, b2x) && floatsEqual(a2y, b2y)))) {
                out.set(a2x, a2y);
                return 1;
            }

            out.set(Float.NaN, Float.NaN);
            if (areCollinear(a1x, a1y, a2x, a2y, b1x, b1y) && areCollinear(a1x, a1y, a2x, a2y, b2x, b2y)) return 2;

            return 0;
        }

        out.x = a1x + t * (a2x - a1x);
        out.y = a1y + t * (a2y - a1y);

        boolean onSegment1 = t >= 0 && t <= 1;
        boolean onSegment2 = u >= 0 && u <= 1;
        if (onSegment1 && onSegment2) return 1;
        if (onSegment1) return 3;
        if (onSegment2) return 4;
        return 5;
    }

    /**
     * Finds the intersection of two line segments: S1 & S2
     * where S1 is the line segment between (a1, a2)
     * and   S2 is the line between (b1, b2).
     * Stores the result in out.
     * @returns 0 if lines are parallel
     * @returns 1 if lines intersect at a unique point
     * @returns 2 if lines coincide
     * @returns 3 if the intersection lies outside segment 1
     * @returns 4 if the intersection lies outside segment 2
     * @returns 5 if the intersection lies outside both segments
     * @param a1
     * @param a2
     * @param b1
     * @param b2
     * @param out
     * @throws MathException
     */
    public static int segmentsIntersection(Vector2 a1, Vector2 a2, Vector2 b1, Vector2 b2, Vector2 out) {
        float Ax = a2.x - a1.x;
        float Ay = a2.y - a1.y;
        float Bx = b2.x - b1.x;
        float By = b2.y - b1.y;
        float Cx = b1.x - a1.x;
        float Cy = b1.y - a1.y;

        float det = Ax * By - Ay * Bx;
        float t = (Cx * By - Cy * Bx) / det;
        float u = (Ay * Cx - Ax * Cy) / det;

        /* handle degenerate cases */
        if (isZero(det)) {
            /* a1 == a2 , b1 == b2 */
            if (a1.equals(a2) && b1.equals(b2)) {
                if (a1.equals(b2)) {
                    out.set(a1);
                    return 2;
                } else {
                    out.set(Float.NaN, Float.NaN);
                    return 0;
                }
            }

            /* a1 == a2 */
            if (a1.equals(a2) && pointOnSegment(a1,b1,b2)) {
                out.set(a1);
                return 1;
            }

            /* b1 == b2 */
            if (b1.equals(b2) && pointOnSegment(b1,a1,a2)) {
                out.set(b1);
                return 1;
            }

            /* Segments are parallel or coincide. */
            /* It is still possible that they have a unique intersection - if segment b "continues" a; i.e.:
                a1 == b1 or a1 == b2, or a2 == b1 or a2 == b2.
             */
            if (a1.equals(b1) || a1.equals(b2)) {
                out.set(a1);
                return 1;
            }

            if (a2.equals(b1) || a2.equals(b2)) {
                out.set(a2);
                return 1;
            }

            out.set(Float.NaN, Float.NaN);
            if (areCollinear(a1, a2, b1) && areCollinear(a1, a2, b2)) return 2;

            return 0;
        }

        out.x = a1.x + t * (a2.x - a1.x);
        out.y = a1.y + t * (a2.y - a1.y);

        boolean onSegment1 = t >= 0 && t <= 1;
        boolean onSegment2 = u >= 0 && u <= 1;
        if (onSegment1 && onSegment2) return 1;
        if (onSegment1) return 3;
        if (onSegment2) return 4;
        return 5;
    }

    /**
     * Finds the intersection of two line segments: S1 & S2
     * where S1 is the line segment between (a1, a2)
     * and   S2 is the line between (b1, b2).
     * Stores the result in out.
     * @param a1
     * @param a2
     * @param b1
     * @param b2
     * @param out
     * @throws MathException
     */
    // TODO: remove
    @Deprecated public static boolean segmentsIntersection_old(Vector2 a1, Vector2 a2, Vector2 b1, Vector2 b2, Vector2 out) {
        float Ax = a2.x - a1.x;
        float Ay = a2.y - a1.y;
        float Bx = b2.x - b1.x;
        float By = b2.y - b1.y;
        float Cx = b1.x - a1.x;
        float Cy = b1.y - a1.y;

        float det = Ax * By - Ay * Bx;
        float t = (Cx * By - Cy * Bx) / det;
        float u = (Ay * Cx - Ax * Cy) / det;

        /* handle degenerate cases */
        if (isZero(det)) {
            /* a1 == a2 == b1 == b2 */
            if (a1.equals(a2) && b1.equals(b2) && a1.equals(b2)) {
                out.set(a1);
                return true;
            }

            /* a1 == a2 */
            if (a1.equals(a2) && pointOnSegment(a1,b1,b2)) {
                out.set(a1);
                return true;
            }

            /* b1 == b2 */
            if (b1.equals(b2) && pointOnSegment(b1,a1,a2)) {
                out.set(b1);
                return true;
            }

            /* Segments are parallel - they are either completely separate, or have some overlap. */
            /* It is still possible that they have a unique intersection - if segment b "continues" a; i.e.:
                a1 == b1 or a1 == b2, or a2 == b1 or a2 == b2.
             */
            if (a1.equals(b1) || a1.equals(b2)) {
                out.set(a1);
                return true;
            }

            if (a2.equals(b1) || a2.equals(b2)) {
                out.set(a2);
                return true;
            }

            out.set(Float.NaN, Float.NaN);
            if (pointOnSegment(b1, a1, a2)) return true;
            if (pointOnSegment(b2, a1, a2)) return true;
            if (pointOnSegment(a1, b1, b2)) return true;
            if (pointOnSegment(a2, b1, b2)) return true;

            return false;
        }

        out.x = a1.x + t * (a2.x - a1.x);
        out.y = a1.y + t * (a2.y - a1.y);

        return t >= 0 && t <= 1 && u >= 0 && u <= 1;
    }

    /** Point p is on-line segment S: (a1, a2) if:
     * segments (a1, p) and (p, a2) are co-linear
     * and p is within the bounding box enclosing S.
     * @param p the point to check
     * @param a1 first point on the line segment
     * @param a2 second point on the line segment
    */
    public static boolean pointOnSegment(Vector2 p, Vector2 a1, Vector2 a2) {
        /* check co-linearity */
        float crs = Vector2.crs(p.x - a1.x, p.y - a1.y, a2.x - p.x, a2.y - p.y);
        /* handle degenerate cases */
        if (Float.isNaN(crs)) {
            if (floatsEqual(p.x, a1.x) && floatsEqual(p.y, a1.y)) return true;
            if (floatsEqual(p.x,a2.x) && floatsEqual(p.y, a2.y)) return true;
            if (p.x == a1.x && p.y == a1.y) return true;
            if (p.x == a2.x && p.y == a2.y) return true;
        } else if (!isZero(crs)) return false;

        /* check if point within bounding box */
        if (p.x > Math.max(a1.x, a2.x)) return false;
        if (p.x < Math.min(a1.x, a2.x)) return false;
        if (p.y > Math.max(a1.y, a2.y)) return false;
        if (p.y < Math.min(a1.y, a2.y)) return false;

        return true;
    }

    public static boolean pointOnSegment(float px, float py, float a1x, float a1y, float a2x, float a2y) {
        /* check co-linearity */
        float crs = Vector2.crs(px - a1x, py - a1y, a2x - px, a2y - py);
        /* handle degenerate cases */
        if (Float.isNaN(crs)) {
            if (floatsEqual(px, a1x) && floatsEqual(py, a1y)) return true;
            if (floatsEqual(px,a2x) && floatsEqual(py, a2y)) return true;
        } else if (!isZero(crs)) return false;

        /* check if point within bounding box */
        if (px > Math.max(a1x, a2x)) return false;
        if (px < Math.min(a1x, a2x)) return false;
        if (py > Math.max(a1y, a2y)) return false;
        if (py < Math.min(a1y, a2y)) return false;

        return true;
    }

    public static boolean areCollinear(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
        return floatsEqual (
                (p2x - p1x) * (p3y - p1y)
                ,
                (p3x - p1x) * (p2y - p1y))
                ;
    }

    public static boolean areCollinear(Vector2 p1, Vector2 p2, Vector2 p3) {
        return floatsEqual (
                (p2.x - p1.x) * (p3.y - p1.y)
                    ,
                (p3.x - p1.x) * (p2.y - p1.y))
                ;
    }

    public static boolean isZero(float value) {
        return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
    }

    public static boolean isZero(float value, float tolerance) {
        return Math.abs(value) <= tolerance;
    }

    public static boolean floatsEqual(float a, float b) {
        return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
    }

    public static boolean floatsEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    public static boolean isNumeric(float a) {
        if (Float.isNaN(a)) return false;
        if (!Float.isFinite(a)) return false;
        return true;
    }

    /** @return the logarithm of value with base a */
    public static float log(float a, float value) {
        return (float)(Math.log(value) / Math.log(a));
    }

    public static float lerp(float step, float a, float b) {
        return a + step * (b - a);
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

    @Deprecated public static float getAreaTriangle_old(float ax, float ay, float bx, float by, float cx, float cy) {
        return 0.5f * Math.abs((bx - ax) * (cy - ay) - (by - ay) * (cx - ax));
    }

    // TODO: test
    public static float getAreaTriangle(float ax, float ay, float bx, float by, float cx, float cy) {
        return 0.5f * Math.abs((ax * (by - cy) + bx * (cy - ay) + cx * (ay - by)));
    }

    // TODO: test
    public static float getAreaTriangle(Vector2 A, Vector2 B, Vector2 C) {
        return 0.5f * Math.abs((A.x * (B.y - C.y) + B.x * (C.y - A.y) + C.x * (A.y - B.y)));
    }

    public static boolean pointInTriangle(Vector2 P, Vector2 A, Vector2 B, Vector2 C) {
        float areaABC = getAreaTriangle(A, B, C);
        float areaPAB = getAreaTriangle(P, A, B);
        float areaPBC = getAreaTriangle(P, B, C);
        float areaPCA = getAreaTriangle(P, C, A);

        // Check if the sum of the areas of PAB, PBC, and PCA is the same as the area of ABC
        return floatsEqual(areaPAB + areaPBC + areaPCA, areaABC);
    }

    // FIXME
    public static boolean pointInTriangle(float px, float py, float ax, float ay, float bx, float by, float cx, float cy) {
        float areaABC = getAreaTriangle(ax, ay, bx, by, cx, cy);
        float areaPAB = getAreaTriangle(px, py, ax, ay, bx, by);
        float areaPBC = getAreaTriangle(px, py, bx, by, cx, cy);
        float areaPCA = getAreaTriangle(px, py, cx, cy, ax, ay);

        // Check if the sum of the areas of PAB, PBC, and PCA is the same as the area of ABC
        return floatsEqual(areaPAB + areaPBC + areaPCA, areaABC);
    }

    /**
     * Returns the winding order of a polygon.
     * @return int 1 if clockwise, -1 if counter-clockwise
     */
    public static int polygonWindingOrder(final float[] polygon) {
        float sum = 0;
        for (int i = 0; i < polygon.length - 1; i += 2) {
            float x1 = CollectionsUtils.getCyclic(polygon, i);
            float y1 = CollectionsUtils.getCyclic(polygon, i+1);

            float x2 = CollectionsUtils.getCyclic(polygon, i+2);
            float y2 = CollectionsUtils.getCyclic(polygon, i+3);

            sum += (x2 - x1) * (y2 + y1);
        }
        return sum > 0.0f ? 1 : -1; // Clockwise: 1, Counter-Clockwise: -1
    }

    /**
     * Returns the winding order of a polygon.
     * @return int 1 if clockwise, -1 if counter-clockwise
     */
    public static int polygonWindingOrder(final ArrayFloat vertices) {
        float sum = 0;
        for (int i = 0; i < vertices.size - 1; i += 2) {
            float x1 = vertices.getCyclic(i);
            float y1 = vertices.getCyclic(i+1);

            float x2 = vertices.getCyclic(i+2);
            float y2 = vertices.getCyclic(i+3);

            sum += (x2 - x1) * (y2 + y1);
        }
        return sum > 0.0f ? 1 : -1; // Clockwise: 1, Counter-Clockwise: -1
    }

    /**
     * Returns the winding order of a polygon.
     * @return int 1 if clockwise, -1 if counter-clockwise
     */
    public static int polygonWindingOrder(Array<Vector2> vertices) {
        float sum = 0.0f;
        for (int i = 0; i < vertices.size; i++) {
            Vector2 v1 = vertices.get(i);
            Vector2 v2 = vertices.get((i + 1) % vertices.size);
            sum += (v2.x - v1.x) * (v2.y + v1.y);
        }
        return sum > 0.0f ? 1 : -1; // Clockwise: 1, Counter-Clockwise: -1
    }

    /**
     * Returns true if the polygon in convex.
     * @param vertices is a flat array of vertex coordinates: [x0,y0, x1,y1, x2,y2, ...].
     * @return boolean
     */
    public static boolean polygonIsConvex(final float[] vertices) {
        if (vertices.length == 6) return true;
        Vector2 tmp1 = vectors2Pool.allocate();
        Vector2 tmp2 = vectors2Pool.allocate();

        tmp1.x = vertices[0] - vertices[vertices.length - 2];
        tmp1.y = vertices[1] - vertices[vertices.length - 1];
        tmp2.x = vertices[2] - vertices[0];
        tmp2.y = vertices[3] - vertices[1];

        float crossSign = Math.signum(tmp1.crs(tmp2));

        for (int i = 2; i < vertices.length; i += 2) {
            tmp1.x = CollectionsUtils.getCyclic(vertices, i) - CollectionsUtils.getCyclic(vertices, i - 2);
            tmp1.y = CollectionsUtils.getCyclic(vertices,i + 1) - CollectionsUtils.getCyclic(vertices, i - 1);

            tmp2.x = CollectionsUtils.getCyclic(vertices, i + 2) - CollectionsUtils.getCyclic(vertices, i);
            tmp2.y = CollectionsUtils.getCyclic(vertices,i + 3) - CollectionsUtils.getCyclic(vertices, i + 1);

            float crossSignCurrent = Math.signum(tmp1.crs(tmp2));
            if (crossSignCurrent != crossSign) return false;
        }

        vectors2Pool.free(tmp1);
        vectors2Pool.free(tmp2);

        return true;
    }

    public static void polygonRemoveDegenerateVertices(@NotNull Array<Vector2> polygon, @NotNull Array<Vector2> outPolygon) {
        if (polygon.size < 3) throw new MathException("A polygon requires a minimum of 3 vertices. Got: " + polygon.size);
        if (polygon == outPolygon) throw new IllegalArgumentException("Argument outPolygon cannot be == polygon.");

        /* remove sequential duplicates: [A, B, B, B, C, D, D] -> [A, B, C, D] */
        polygonVertices.clear();
        Vector2 previous = polygon.get(0);
        polygonVertices.add(previous);
        for (int i = 1; i < polygon.size; i++) {
            Vector2 curr = polygon.get(i);
            if (!curr.equals(previous)) {
                polygonVertices.add(curr);
                previous = curr;
            }
        }
        Vector2 first = polygonVertices.first();
        Vector2 last = polygonVertices.last();
        if (last.equals(first) && last != first) {
            polygonVertices.pop();
        }

        /* remove collinear vertices */
        outPolygon.clear();
        for (int i = 0; i < polygonVertices.size; i++) {

            Vector2 prev = polygonVertices.getCyclic(i - 1);
            Vector2 curr = polygonVertices.get(i);
            Vector2 next = polygonVertices.getCyclic(i + 1);

            if (!Vector2.areCollinear(prev, curr, next)) {
                outPolygon.add(curr);
            }
        }
    }

    public static void polygonRemoveDegenerateVertices(@NotNull float[] polygon, @NotNull ArrayFloat outPolygon) {
        if (polygon.length < 6) throw new MathException("A polygon requires a minimum of 3 vertices. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new MathException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even. Got: " + polygon.length);
        outPolygon.clear();

        /* remove sequential duplicates: [A, B, B, B, C, D, D] -> [A, B, C, D]. Stores the result in a "compact" polygon (ArrayFloat). */
        ArrayFloat compact = floatArrayPool.allocate();
        float v_x = polygon[0];
        float v_y = polygon[1];
        compact.add(v_x);
        compact.add(v_y);
        for (int i = 2; i < polygon.length; i += 2) {
            float curr_x = polygon[i];
            float curr_y = polygon[i + 1];
            if (!floatsEqual(curr_x, v_x) || !floatsEqual(curr_y, v_y)) {
                compact.add(curr_x);
                compact.add(curr_y);
                v_x = curr_x;
                v_y = curr_y;
            }
        }
        float first_x = compact.get(0);
        float first_y = compact.get(1);
        float last_x  = compact.get(compact.size - 2);
        float last_y  = compact.get(compact.size - 1);
        if (floatsEqual(first_x, last_x) && floatsEqual(first_y, last_y) && compact.size != 2) {
            compact.pop();
            compact.pop();
        }

        /* remove collinear vertices */
        for (int i = 0; i < compact.size - 1; i += 2) {

            float prev_x = compact.getCyclic(i - 2);
            float prev_y = compact.getCyclic(i - 1);

            float curr_x = compact.get(i);
            float curr_y = compact.get(i + 1);

            float next_x = compact.getCyclic(i + 2);
            float next_y = compact.getCyclic(i + 3);

            if (!Vector2.areCollinear(prev_x, prev_y, curr_x, curr_y, next_x, next_y)) {
                outPolygon.add(curr_x);
                outPolygon.add(curr_y);
            }
        }

        floatArrayPool.free(compact);
    }

    public static void polygonRemoveDegenerateVertices(@NotNull Array<Vector2> polygon) {
        if (polygon.size < 3) throw new MathException("A polygon requires a minimum of 3 vertices. Got: " + polygon.size);

        /* remove sequential duplicates: [A, B, B, B, C, D, D] -> [A, B, C, D] */
        ArrayFloat compact = floatArrayPool.allocate();
        Vector2 previous = polygon.get(0);
        compact.add(previous.x);
        compact.add(previous.y);
        for (int i = 1; i < polygon.size; i++) {
            Vector2 curr = polygon.get(i);
            if (!curr.equals(previous)) {
                compact.add(curr.x);
                compact.add(curr.y);
                previous = curr;
            }
        }
        float first_x = compact.get(0);
        float first_y = compact.get(1);
        float last_x  = compact.get(compact.size - 2);
        float last_y  = compact.get(compact.size - 1);
        if (floatsEqual(first_x, last_x) && floatsEqual(first_y, last_y) && compact.size != 1) {
            compact.pop();
            compact.pop();
        }

        /* remove collinear vertices */
        ArrayFloat cleanPolygon = floatArrayPool.allocate();
        for (int i = 0; i < compact.size - 1; i += 2) {

            float prev_x = compact.getCyclic(i - 2);
            float prev_y = compact.getCyclic(i - 1);

            float curr_x = compact.get(i);
            float curr_y = compact.get(i + 1);

            float next_x = compact.getCyclic(i + 2);
            float next_y = compact.getCyclic(i + 3);

            if (!Vector2.areCollinear(prev_x, prev_y, curr_x, curr_y, next_x, next_y)) {
                cleanPolygon.add(curr_x);
                cleanPolygon.add(curr_y);
            }
        }

        /* copy back everything to the original polygon */
        for (int i = 0; i < cleanPolygon.size / 2; i++) {
            Vector2 vertex = polygon.get(i);
            float vx = cleanPolygon.get(2 * i);
            float vy = cleanPolygon.get(2 * i + 1);
            vertex.set(vx, vy);
        }
        polygon.setSize(cleanPolygon.size / 2);

        floatArrayPool.free(compact);
        floatArrayPool.free(cleanPolygon);
    }

    public static void polygonTriangulate(@NotNull Array<Vector2> polygon, @NotNull Array<Vector2> outVertices, @NotNull ArrayInt outIndices) {
        if (polygon.size < 3) throw new MathException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.size);
        polygonRemoveDegenerateVertices(polygon, outVertices);
        if (outVertices.size < 3) throw new MathException("Polygon contains " + (polygon.size - outVertices.size) + " collinear vertices; When removed, that total vertex count is: " + outVertices.size + "< 3.");

        int windingOrder = MathUtils.polygonWindingOrder(outVertices);
        if (windingOrder > 0) outVertices.reverse();

        indexList.clear();
        for (int i = 0; i < outVertices.size; i++) {
            indexList.add(i);
        }

        int totalTriangleCount = outVertices.size - 2;
        int totalTriangleIndexCount = totalTriangleCount * 3;

        outIndices.clear();
        outIndices.ensureCapacity(totalTriangleIndexCount);

        Vector2 va_to_vb = vectors2Pool.allocate();
        Vector2 va_to_vc = vectors2Pool.allocate();

        while (indexList.size > 3) {
            for (int i = 0; i < indexList.size; i++) {
                int a = indexList.get(i);
                int b = indexList.getCyclic(i - 1);
                int c = indexList.getCyclic(i + 1);

                Vector2 va = outVertices.get(a);
                Vector2 vb = outVertices.get(b);
                Vector2 vc = outVertices.get(c);

                va_to_vb.x = vb.x - va.x;
                va_to_vb.y = vb.y - va.y;

                va_to_vc.x = vc.x - va.x;
                va_to_vc.y = vc.y - va.y;

                // Is ear test vertex convex?
                if (Vector2.crs(va_to_vb, va_to_vc) > 0f) {
                    continue;
                }

                boolean isEar = true;

                // Test: does ear contain any polygon vertices?
                for (int j = 0; j < outVertices.size; j++) {
                    if (j == a || j == b || j == c) continue;
                    Vector2 p = outVertices.get(j);
                    if (pointInTriangle(p.x, p.y, vb.x, vb.y, va.x, va.y, vc.x, vc.y)) {
                        isEar = false;
                        break;
                    }
                }

                if (isEar) {
                    outIndices.add(b);
                    outIndices.add(a);
                    outIndices.add(c);

                    indexList.removeIndex(i);
                    break;
                }
            }
        }

        outIndices.add(indexList.get(0));
        outIndices.add(indexList.get(1));
        outIndices.add(indexList.get(2));

        /* free resources */
        vectors2Pool.free(va_to_vb);
        vectors2Pool.free(va_to_vc);
    }

    public static void polygonTriangulate(@NotNull float[] polygon, @NotNull ArrayFloat outVertices, @NotNull ArrayInt outIndices) {
        if (polygon.length < 6) throw new MathException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new MathException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even. Got: " + polygon.length);
        polygonRemoveDegenerateVertices(polygon, outVertices);
        if (outVertices.size < 6) throw new MathException("Polygon contains " + (polygon.length - outVertices.size) / 2 + " collinear vertices; When removed, that total vertex count is: " + outVertices.size / 2 + ". Must have at least 3 non-collinear vertices.");

        int windingOrder = MathUtils.polygonWindingOrder(outVertices);
        if (windingOrder > 0) {
            outVertices.reverseInPairs();
        }

        indexList.clear();
        for (int i = 0; i < outVertices.size / 2; i++) {
            indexList.add(i);
        }

        int totalTriangleCount = outVertices.size / 2 - 2;
        int totalTriangleIndexCount = totalTriangleCount * 3;

        outIndices.clear();
        outIndices.ensureCapacity(totalTriangleIndexCount);

        Vector2 va_to_vb = vectors2Pool.allocate();
        Vector2 va_to_vc = vectors2Pool.allocate();

        while (indexList.size > 3) {
            for (int i = 0; i < indexList.size; i++) {
                int a = indexList.get(i);
                int b = indexList.getCyclic(i - 1);
                int c = indexList.getCyclic(i + 1);

                float vax = outVertices.get(a*2);
                float vay = outVertices.get(a*2+1);
                float vbx = outVertices.get(b*2);
                float vby = outVertices.get(b*2+1);
                float vcx = outVertices.get(c*2);
                float vcy = outVertices.get(c*2+1);

                va_to_vb.x = vbx - vax;
                va_to_vb.y = vby - vay;

                va_to_vc.x = vcx - vax;
                va_to_vc.y = vcy - vay;

                // Is ear test vertex convex?
                if (Vector2.crs(va_to_vb, va_to_vc) > 0f) {
                    continue;
                }

                boolean isEar = true;

                // Does test ear contain any polygon vertices?
                for (int j = 0; j < outVertices.size - 1; j+=2) {
                    int index = j / 2;
                    if (index == a || index == b || index == c) continue;
                    float px = outVertices.get(j);
                    float py = outVertices.get(j+1);
                    if (pointInTriangle(px, py, vbx, vby, vax, vay, vcx, vcy)) {
                        isEar = false;
                        break;
                    }
                }

                if (isEar) {
                    outIndices.add(b);
                    outIndices.add(a);
                    outIndices.add(c);

                    indexList.removeIndex(i);
                    break;
                }
            }
        }

        outIndices.add(indexList.get(0));
        outIndices.add(indexList.get(1));
        outIndices.add(indexList.get(2));

        /* free resources */
        vectors2Pool.free(va_to_vb);
        vectors2Pool.free(va_to_vc);
    }

    // TODO: test
    @Deprecated public static void triangulatePolygon_old(@NotNull float[] polygon, @NotNull ArrayInt out) {
        if (polygon.length < 6) throw new MathException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new MathException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even.");

        polygonVertices.clear();
        Vector2 prev    = vectors2Pool.allocate();
        Vector2 current = vectors2Pool.allocate();
        Vector2 next    = vectors2Pool.allocate();
        for (int i = 0; i < polygon.length; i += 2) {

            prev.x = CollectionsUtils.getCyclic(polygon, i - 2);
            prev.y = CollectionsUtils.getCyclic(polygon, i - 1);

            current.x = polygon[i];
            current.y = polygon[i + 1];

            next.x = CollectionsUtils.getCyclic(polygon, i + 2);
            next.y = CollectionsUtils.getCyclic(polygon, i + 3);

            Vector2 vertex = vectors2Pool.allocate();
            if (!Vector2.areCollinear(prev, current, next)) {
                vertex.x = current.x;
                vertex.y = current.y;
            } else { /* this is a very unfortunate degenerate case that we handle using an estimate */
                Vector2 perturb = vectors2Pool.allocate();
                perturb.x = next.x - current.x;
                perturb.y = next.y - current.y;
                perturb.rotate90(1);
                perturb.nor().scl(FLOAT_ROUNDING_ERROR);

                vertex.x = current.x + perturb.x;
                vertex.y = current.y + perturb.y;
                vectors2Pool.free(perturb);
            }

            polygonVertices.add(vertex);
        }

        if (polygonVertices.size < 3) throw new MathException("Polygon contains 1 or more collinear vertices; When removed, that total vertex count is: " + polygonVertices.size + ". Must have at least 3 non-collinear vertices.");

        int windingOrder = MathUtils.polygonWindingOrder(polygon);
        if (windingOrder > 0) polygonVertices.reverse();

        out.clear();

        indexList.clear();
        for (int i = 0; i < polygonVertices.size; i++) {
            indexList.add(i);
        }

        int totalTriangleCount = polygonVertices.size - 2;
        int totalTriangleIndexCount = totalTriangleCount * 3;

        out.clear();
        out.ensureCapacity(totalTriangleIndexCount);

        Vector2 va_to_vb = vectors2Pool.allocate();
        Vector2 va_to_vc = vectors2Pool.allocate();

        while (indexList.size > 3) {
            for (int i = 0; i < indexList.size; i++) {
                int a = indexList.get(i);
                int b = indexList.getCyclic(i - 1);
                int c = indexList.getCyclic(i + 1);

                Vector2 va = polygonVertices.get(a);
                Vector2 vb = polygonVertices.get(b);
                Vector2 vc = polygonVertices.get(c);

                va_to_vb.x = vb.x - va.x;
                va_to_vb.y = vb.y - va.y;

                va_to_vc.x = vc.x - va.x;
                va_to_vc.y = vc.y - va.y;

                // Is ear test vertex convex?
                if (Vector2.crs(va_to_vb, va_to_vc) > 0f) {
                    continue;
                }

                boolean isEar = true;

                // Does test ear contain any polygon vertices?
                for (int j = 0; j < polygonVertices.size; j++) {
                    if (j == a || j == b || j == c) continue;
                    Vector2 p = polygonVertices.get(j);
                    if (pointInTriangle(p.x, p.y, vb.x, vb.y, va.x, va.y, vc.x, vc.y)) {
                        isEar = false;
                        break;
                    }
                }

                if (isEar) {
                    out.add(b);
                    out.add(a);
                    out.add(c);

                    indexList.removeIndex(i);
                    break;
                }
            }
        }

        out.add(indexList.get(0));
        out.add(indexList.get(1));
        out.add(indexList.get(2));

        /* free resources */
        vectors2Pool.free(prev);
        vectors2Pool.free(current);
        vectors2Pool.free(next);
        vectors2Pool.free(va_to_vb);
        vectors2Pool.free(va_to_vc);
        vectors2Pool.freeAll(polygonVertices);
    }

}
