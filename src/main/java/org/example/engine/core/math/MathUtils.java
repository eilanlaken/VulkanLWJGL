package org.example.engine.core.math;

import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.collections.CollectionsUtils;
import org.example.engine.core.memory.MemoryPool;
import org.example.engine.core.shape.Shape2D;
import org.example.engine.core.shape.ShapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.example.engine.core.math.Matrix2x2.*;

// TODO: implement init() block that will take care of configuration.
public final class MathUtils {

    public static  final float   NANO_TO_SEC          = 1.0f / 1000000000f;
    public static  final float   FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
    public static  final float   PI                   = (float) Math.PI;
    public static  final float   PI2                  = PI * 2;
    public static  final float   HALF_PI              = PI / 2;
    public static  final float   E                    = (float) Math.E;
    public static  final float   radiansToDegrees     = 180f / PI;
    public static  final float   degreesToRadians     = PI / 180;
    private static final int     SIN_BITS             = 14; // 16KB. Adjust for accuracy.
    private static final int     SIN_MASK             = ~(-1 << SIN_BITS);
    private static final int     SIN_COUNT            = SIN_MASK + 1;
    private static final float   RADIANS_FULL         = PI2;
    private static final float   DEGREES_FULL         = 360.0f;
    private static final float   RADIANS_TO_INDEX     = SIN_COUNT / RADIANS_FULL;
    private static final float   DEGREES_TO_INDEX     = SIN_COUNT / DEGREES_FULL;
    private static final Random  random               = new Random();

    private static final MemoryPool<Vector2>   vector2MemoryPool   = new MemoryPool<>(Vector2.class, 5);
    private static final MemoryPool<Matrix2x2> matrix2x2MemoryPool = new MemoryPool<>(Matrix2x2.class, 2);

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
            return x + HALF_PI;
        else if (y < 0) return x - HALF_PI;
        return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
    }

    public static float areaTriangle(float x1, float y1, float x2, float y2, float x3, float y3) { return 0.5f * Math.abs(x1*(y2 - y3) + x2*(y3 - y1) + x3*(y1 - y2)); }

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
        return Sin.lookup[(int)((radians + HALF_PI) * RADIANS_TO_INDEX) & SIN_MASK];
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
        rad %= PI2;
        if (rad < 0) rad += PI2;
        return rad;
    }

    public static void findIntersection(Vector2 a1, Vector2 a2, Vector2 b1, Vector2 b2, Vector2 out) throws MathException {
        Vector2 D1 = vector2MemoryPool.allocate();
        D1.x = a2.x - a1.x;
        D1.y = a2.y - a1.y;

        Vector2 D2 = vector2MemoryPool.allocate();
        D2.x = b2.x - b1.x;
        D2.y = b2.y - b1.y;

        Matrix2x2 A = matrix2x2MemoryPool.allocate();
        A.val[M00] =  D2.x;
        A.val[M01] = -D1.x;
        A.val[M10] =  D2.y;
        A.val[M11] = -D1.y;

        Vector2 B = vector2MemoryPool.allocate();
        B.x = a1.x - b1.x;
        B.y = a1.y - b1.y;

        Matrix2x2.solve22(A, B, out);
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

    /** @return the logarithm of value with base a */
    public static float log(float a, float value) {
        return (float)(Math.log(value) / Math.log(a));
    }

    // TODO: move to init block
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

    /**
     * Returns the relative angle between the two bodies given the reference angle.
     * @return double
     */
    public static float getRelativeRotationRad(final Shape2D shape_1, final Shape2D shape_2) {
        float rr = (shape_1.angle() - shape_2.angle()) * MathUtils.degreesToRadians;
        if (rr < -MathUtils.PI) rr += MathUtils.PI2;
        if (rr >  MathUtils.PI) rr -= MathUtils.PI2;
        return rr;
    }

    /**
     * Returns the relative angle between the two bodies given the reference angle.
     * @return double
     */
    public static float getRelativeRotationDeg(final Shape2D shape_1, final Shape2D shape_2) {
        float rr = shape_1.angle() - shape_2.angle();
        if (rr < -180) rr += 360;
        if (rr >  180) rr -= 360;
        return rr;
    }

    public static float getAreaTriangle(float ax, float ay, float bx, float by, float cx, float cy) {
        return 0.5f * Math.abs((bx - ax) * (cy - ay) - (by - ay) * (cx - ax));
    }

    /**
     * Returns the winding order of a polygon.
     * @return int 1 if clockwise, -1 if counter-clockwise
     */
    public static int polygonWindingOrder(final float[] vertices) {
        float sum = 0;
        for (int i = 0; i < vertices.length - 1; i += 2) {
            float x1 = CollectionsUtils.getCyclic(vertices, i);
            float y1 = CollectionsUtils.getCyclic(vertices, i+1);

            float x2 = CollectionsUtils.getCyclic(vertices, i+2);
            float y2 = CollectionsUtils.getCyclic(vertices, i+3);

            sum += (x2 - x1) * (y2 + y1);
        }
        if (sum > 0) return 1;
        return -1;
    }

    /**
     * Returns true if the polygon in convex.
     * @param vertices is a flat array of vertex coordinates: [x0,y0, x1,y1, x2,y2, ...].
     * @return boolean
     */
    public static boolean isPolygonConvex(final float[] vertices) {
        if (vertices.length == 6) return true;
        Vector2 tmp1 = new Vector2();
        Vector2 tmp2 = new Vector2();

        tmp1.set(getVertexX(0, vertices), getVertexY(0, vertices))
                .sub(getVertexX(-1, vertices), getVertexY(-1, vertices));
        tmp2.set(getVertexX(+1, vertices), getVertexY(+1, vertices))
                .sub(getVertexX(0, vertices), getVertexY(0, vertices));

        float crossSign = Math.signum(tmp1.crs(tmp2));

        for (int i = 1; i < vertices.length; i++) {
            tmp1.set(getVertexX(i, vertices), getVertexY(i, vertices))
                    .sub(getVertexX(i-1, vertices), getVertexY(i-1, vertices));
            tmp2.set(getVertexX(i+1, vertices), getVertexY(i+1, vertices))
                    .sub(getVertexX(i, vertices), getVertexY(i, vertices));
            float crossSignCurrent = Math.signum(tmp1.crs(tmp2));
            if (crossSignCurrent != crossSign) return false;
        }
        return true;
    }

    /**
     * Triangulates the given polygon
     *
     * @param vertices is a flat array of vertice coordinates like [x0,y0, x1,y1, x2,y2, ...].
     * @return List containing groups of three vertice indices in the resulting array forms a triangle.
     */
    public static int[] triangulate2DPolygon(float[] vertices) {
        return triangulatePolygon(vertices, null, 2);
    }

    /**
     * Triangulates the given polygon
     *
     * @param data is a flat array of vertice coordinates like [x0,y0, x1,y1, x2,y2, ...].
     * @param holeIndices is an array of hole indices if any (e.g. [5, 8] for a 12-vertice input would mean one hole with vertices 5-7 and another with 8-11).
     * @param dim  is the number of coordinates per vertice in the input array
     * @return List containing groups of three vertice indices in the resulting array forms a triangle.
     */
    public static int[] triangulatePolygon(float[] data, int[] holeIndices, int dim) {
        boolean hasHoles = holeIndices != null && holeIndices.length > 0;
        int outerLen = hasHoles ? holeIndices[0] * dim : data.length;
        Node outerNode = linkedList(data, 0, outerLen, dim, true);
        ArrayInt triangles = new ArrayInt();

        if (outerNode == null || outerNode.next == outerNode.prev)
            return triangles.pack().items;

        float minX = 0;
        float minY = 0;
        float maxX = 0;
        float maxY = 0;
        float invSize = Float.MIN_VALUE;

        if (hasHoles)
            outerNode = eliminateHoles(data, holeIndices, outerNode, dim);

        // if the shape is not too simple, we'll use z-order curve hash later;
        // calculate polygon bbox
        if (data.length > 80 * dim) {
            minX = maxX = data[0];
            minY = maxY = data[1];

            for (int i = dim; i < outerLen; i += dim) {
                float x = data[i];
                float y = data[i + 1];
                if (x < minX)
                    minX = x;
                if (y < minY)
                    minY = y;
                if (x > maxX)
                    maxX = x;
                if (y > maxY)
                    maxY = y;
            }

            // minX, minY and size are later used to transform coords into
            // integers for z-order calculation
            invSize = Math.max(maxX - minX, maxY - minY);
            invSize = invSize != 0.0f ? 1.0f / invSize : 0.0f;
        }

        triangulateLinked(outerNode, triangles, dim, minX, minY, invSize, Integer.MIN_VALUE);

        return triangles.pack().items;
    }

    private static void triangulateLinked(Node ear, ArrayInt triangles, int dim, float minX, float minY, float invSize, int pass) {
        if (ear == null)
            return;

        // interlink polygon nodes in z-order
        if (pass == Integer.MIN_VALUE && invSize != Float.MIN_VALUE)
            indexCurve(ear, minX, minY, invSize);

        Node stop = ear;

        // iterate through ears, slicing them one by one
        while (ear.prev != ear.next) {
            Node prev = ear.prev;
            Node next = ear.next;

            if (invSize != Float.MIN_VALUE ? isEarHashed(ear, minX, minY, invSize) : isEar(ear)) {
                // cut off the triangle
                triangles.add(prev.i / dim);
                triangles.add(ear.i / dim);
                triangles.add(next.i / dim);
                removeNode(ear);
                ear = next.next;
                stop = next.next;
                continue;
            }

            ear = next;

            if (ear == stop) {
                // try filtering points and slicing again
                if (pass == Integer.MIN_VALUE) {
                    triangulateLinked(filterPoints(ear, null), triangles, dim, minX, minY, invSize, 1);
                } else if (pass == 1) {
                    ear = cureLocalIntersections(filterPoints(ear, null), triangles, dim);
                    triangulateLinked(ear, triangles, dim, minX, minY, invSize, 2);
                } else if (pass == 2) {
                    splitPolygon(ear, triangles, dim, minX, minY, invSize);
                }
                break;
            }
        }
    }

    private static void splitPolygon(Node start, ArrayInt triangles, int dim, float minX, float minY, float size) {
        // look for a valid diagonal that divides the polygon into two
        Node a = start;
        do {
            Node b = a.next.next;
            while (b != a.prev) {
                if (a.i != b.i && isValidDiagonal(a, b)) {
                    // split the polygon in two by the diagonal
                    Node c = splitPolygon(a, b);
                    a = filterPoints(a, a.next);
                    c = filterPoints(c, c.next);
                    triangulateLinked(a, triangles, dim, minX, minY, size, Integer.MIN_VALUE);
                    triangulateLinked(c, triangles, dim, minX, minY, size, Integer.MIN_VALUE);
                    return;
                }
                b = b.next;
            }
            a = a.next;
        } while (a != start);
    }

    private static boolean isValidDiagonal(Node a, Node b) {
        return a.next.i != b.i && a.prev.i != b.i && !intersectsPolygon(a, b) && // doesn't intersect other edges
                (locallyInside(a, b) && locallyInside(b, a) && middleInside(a, b) && // locally visible
                        (area(a.prev, a, b.prev) != 0 || area(a, b.prev, b) != 0) || // does not create opposite-facing sectors
                        equals(a, b) && area(a.prev, a, a.next) > 0 && area(b.prev, b, b.next) > 0); // special zero-length case
    }

    private static boolean middleInside(Node a, Node b) {
        Node p = a;
        boolean inside = false;
        float px = (a.x + b.x) / 2;
        float py = (a.y + b.y) / 2;
        do {
            if (((p.y > py) != (p.next.y > py)) && (px < (p.next.x - p.x) * (py - p.y) / (p.next.y - p.y) + p.x))
                inside = !inside;
            p = p.next;
        } while (p != a);

        return inside;
    }

    private static boolean intersectsPolygon(Node a, Node b) {
        Node p = a;
        do {
            if (p.i != a.i && p.next.i != a.i && p.i != b.i && p.next.i != b.i && intersects(p, p.next, a, b))
                return true;
            p = p.next;
        } while (p != a);

        return false;
    }

    private static boolean intersects(Node p1, Node q1, Node p2, Node q2) {
        if ((equals(p1, p2) && equals(q1, q2)) || (equals(p1, q2) && equals(p2, q1)))
            return true;
        float o1 = Math.signum(area(p1, q1, p2));
        float o2 = Math.signum(area(p1, q1, q2));
        float o3 = Math.signum(area(p2, q2, p1));
        float o4 = Math.signum(area(p2, q2, q1));

        if (o1 != o2 && o3 != o4)
            return true; // general case

        if (o1 == 0 && onSegment(p1, p2, q1))
            return true; // p1, q1 and p2 are collinear and p2 lies on p1q1
        if (o2 == 0 && onSegment(p1, q2, q1))
            return true; // p1, q1 and q2 are collinear and q2 lies on p1q1
        if (o3 == 0 && onSegment(p2, p1, q2))
            return true; // p2, q2 and p1 are collinear and p1 lies on p2q2
        if (o4 == 0 && onSegment(p2, q1, q2))
            return true; // p2, q2 and q1 are collinear and q1 lies on p2q2
        return false;
    }

    private static boolean onSegment(Node p, Node q, Node r) {
        return q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) && q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y);
    }

    private static Node cureLocalIntersections(Node start, ArrayInt triangles, int dim) {
        Node p = start;
        do {
            Node a = p.prev, b = p.next.next;

            if (!equals(a, b) && intersects(a, p, p.next, b) && locallyInside(a, b) && locallyInside(b, a)) {
                triangles.add(a.i / dim);
                triangles.add(p.i / dim);
                triangles.add(b.i / dim);
                removeNode(p);
                removeNode(p.next);
                p = start = b;
            }
            p = p.next;
        } while (p != start);

        return filterPoints(p, null);
    }

    private static boolean isEar(Node ear) {
        Node a = ear.prev, b = ear, c = ear.next;

        if (area(a, b, c) >= 0)
            return false; // reflex, can't be an ear

        // now make sure we don't have other points inside the potential ear
        Node p = ear.next.next;
        while (p != ear.prev) {
            if (pointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, p.x, p.y) && area(p.prev, p, p.next) >= 0)
                return false;
            p = p.next;
        }

        return true;
    }

    private static boolean isEarHashed(Node ear, float minX, float minY, float invSize) {
        Node a = ear.prev;
        Node b = ear;
        Node c = ear.next;

        if (area(a, b, c) >= 0)
            return false; // reflex, can't be an ear

        float minTX = a.x < b.x ? (Math.min(a.x, c.x)) : (Math.min(b.x, c.x));
        float minTY = a.y < b.y ? (Math.min(a.y, c.y)) : (Math.min(b.y, c.y));
        float maxTX = a.x > b.x ? (Math.max(a.x, c.x)) : (Math.max(b.x, c.x));
        float maxTY = a.y > b.y ? (Math.max(a.y, c.y)) : (Math.max(b.y, c.y));
        float minZ = zOrder(minTX, minTY, minX, minY, invSize);
        float maxZ = zOrder(maxTX, maxTY, minX, minY, invSize);

        // first look for points inside the triangle in increasing z-order
        Node p = ear.prevZ;
        Node n = ear.nextZ;

        while (p != null && p.z >= minZ && n != null && n.z <= maxZ) {
            if (p != ear.prev && p != ear.next && pointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, p.x, p.y) && area(p.prev, p, p.next) >= 0)
                return false;
            p = p.prevZ;

            if (n != ear.prev && n != ear.next && pointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, n.x, n.y) && area(n.prev, n, n.next) >= 0)
                return false;
            n = n.nextZ;
        }

        // look for remaining points in decreasing z-order
        while (p != null && p.z >= minZ) {
            if (p != ear.prev && p != ear.next && pointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, p.x, p.y) && area(p.prev, p, p.next) >= 0)
                return false;
            p = p.prevZ;
        }

        // look for remaining points in increasing z-order
        while (n != null && n.z <= maxZ) {
            if (n != ear.prev && n != ear.next && pointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, n.x, n.y) && area(n.prev, n, n.next) >= 0)
                return false;
            n = n.nextZ;
        }

        return true;
    }

    // z-order of a point given coords and inverse of the longer side of data bbox
    private static float zOrder(float x, float y, float minX, float minY, float invSize) {
        // coords are transformed into non-negative 15-bit integer range
        int lx = Float.valueOf(32767 * (x - minX) * invSize).intValue();
        int ly = Float.valueOf(32767 * (y - minY) * invSize).intValue();

        lx = (lx | (lx << 8)) & 0x00FF00FF;
        lx = (lx | (lx << 4)) & 0x0F0F0F0F;
        lx = (lx | (lx << 2)) & 0x33333333;
        lx = (lx | (lx << 1)) & 0x55555555;

        ly = (ly | (ly << 8)) & 0x00FF00FF;
        ly = (ly | (ly << 4)) & 0x0F0F0F0F;
        ly = (ly | (ly << 2)) & 0x33333333;
        ly = (ly | (ly << 1)) & 0x55555555;

        return lx | (ly << 1);
    }

    private static void indexCurve(Node start, float minX, float minY, float invSize) {
        Node p = start;
        do {
            if (p.z == Float.MIN_VALUE)
                p.z = zOrder(p.x, p.y, minX, minY, invSize);
            p.prevZ = p.prev;
            p.nextZ = p.next;
            p = p.next;
        } while (p != start);

        p.prevZ.nextZ = null;
        p.prevZ = null;

        sortLinked(p);
    }

    private static void sortLinked(Node list) {
        int inSize = 1;
        int numMerges;

        do {
            Node p = list;
            list = null;
            Node tail = null;
            numMerges = 0;

            while (p != null) {
                numMerges++;
                Node q = p;
                int pSize = 0;
                for (int i = 0; i < inSize; i++) {
                    pSize++;
                    q = q.nextZ;
                    if (q == null)
                        break;
                }

                int qSize = inSize;

                while (pSize > 0 || (qSize > 0 && q != null)) {
                    Node e;
                    if (pSize == 0) {
                        e = q;
                        q = q.nextZ;
                        qSize--;
                    } else if (qSize == 0 || q == null) {
                        e = p;
                        p = p.nextZ;
                        pSize--;
                    } else if (p.z <= q.z) {
                        e = p;
                        p = p.nextZ;
                        pSize--;
                    } else {
                        e = q;
                        q = q.nextZ;
                        qSize--;
                    }

                    if (tail != null)
                        tail.nextZ = e;
                    else
                        list = e;

                    e.prevZ = tail;
                    tail = e;
                }

                p = q;
            }

            tail.nextZ = null;
            inSize *= 2;

        } while (numMerges > 1);
    }

    private static Node eliminateHoles(float[] data, int[] holeIndices, Node outerNode, int dim) {
        List<Node> queue = new ArrayList<>();

        int len = holeIndices.length;
        for (int i = 0; i < len; i++) {
            int start = holeIndices[i] * dim;
            int end = i < len - 1 ? holeIndices[i + 1] * dim : data.length;
            Node list = linkedList(data, start, end, dim, false);
            if (list == list.next)
                list.steiner = true;
            queue.add(getLeftmost(list));
        }

        queue.sort((o1, o2) -> {
            if (o1.x - o2.x > 0)
                return 1;
            else if (o1.x - o2.x < 0)
                return -2;
            return 0;
        });

        for (Node node : queue) {
            eliminateHole(node, outerNode);
            outerNode = filterPoints(outerNode, outerNode.next);
        }

        return outerNode;
    }

    private static Node filterPoints(Node start, Node end) {
        if (start == null)
            return null;
        if (end == null)
            end = start;

        Node p = start;
        boolean again;

        do {
            again = false;

            if (!p.steiner && equals(p, p.next) || area(p.prev, p, p.next) == 0) {
                removeNode(p);
                p = end = p.prev;
                if (p == p.next)
                    break;
                again = true;
            } else {
                p = p.next;
            }
        } while (again || p != end);

        return end;
    }

    private static boolean equals(Node p1, Node p2) {
        return p1.x == p2.x && p1.y == p2.y;
    }

    private static float area(Node p, Node q, Node r) {
        return (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
    }

    private static void eliminateHole(Node hole, Node outerNode) {
        outerNode = findHoleBridge(hole, outerNode);
        if (outerNode != null) {
            Node b = splitPolygon(outerNode, hole);
            filterPoints(outerNode, outerNode.next);
            filterPoints(b, b.next);
        }
    }

    private static Node splitPolygon(Node a, Node b) {
        Node a2 = new Node(a.i, a.x, a.y);
        Node b2 = new Node(b.i, b.x, b.y);
        Node an = a.next;
        Node bp = b.prev;

        a.next = b;
        b.prev = a;

        a2.next = an;
        an.prev = a2;

        b2.next = a2;
        a2.prev = b2;

        bp.next = b2;
        b2.prev = bp;

        return b2;
    }

    // David Eberly's algorithm for finding a bridge between hole and outer polygon
    private static Node findHoleBridge(Node hole, Node outerNode) {
        Node p = outerNode;
        float hx = hole.x;
        float hy = hole.y;
        float qx = -Float.MAX_VALUE;
        Node m = null;

        do {
            if (hy <= p.y && hy >= p.next.y) {
                float x = p.x + (hy - p.y) * (p.next.x - p.x) / (p.next.y - p.y);
                if (x <= hx && x > qx) {
                    qx = x;
                    if (x == hx) {
                        if (hy == p.y)
                            return p;
                        if (hy == p.next.y)
                            return p.next;
                    }
                    m = p.x < p.next.x ? p : p.next;
                }
            }
            p = p.next;
        } while (p != outerNode);

        if (m == null)
            return null;

        if (hx == qx) return m; // hole touches outer segment; pick leftmost endpoint

        // look for points inside the triangle of hole point, segment
        // intersection and endpoint;
        // if there are no points found, we have a valid connection;
        // otherwise choose the point of the minimum angle with the ray as
        // connection point

        Node stop = m;
        float mx = m.x;
        float my = m.y;
        float tanMin = Float.MAX_VALUE;
        float tan;

        p = m;

        do {
            if (hx >= p.x && p.x >= mx && pointInTriangle(hy < my ? hx : qx, hy, mx, my, hy < my ? qx : hx, hy, p.x, p.y)) {

                tan = Math.abs(hy - p.y) / (hx - p.x); // tangential

                if (locallyInside(p, hole) && (tan < tanMin || (tan == tanMin && (p.x > m.x || (p.x == m.x && sectorContainsSector(m, p)))))) {
                    m = p;
                    tanMin = tan;
                }
            }

            p = p.next;
        } while (p != stop);

        return m;
    }

    private static boolean locallyInside(Node a, Node b) {
        return area(a.prev, a, a.next) < 0 ? area(a, b, a.next) >= 0 && area(a, a.prev, b) >= 0 : area(a, b, a.prev) < 0 || area(a, a.next, b) < 0;
    }

    private static boolean sectorContainsSector(Node m, Node p) {
        return area(m.prev, m, p.prev) < 0 && area(p.next, m, m.next) < 0;
    }

    private static boolean pointInTriangle(float ax, float ay, float bx, float by, float cx, float cy, float px, float py) {
        return (cx - px) * (ay - py) - (ax - px) * (cy - py) >= 0 && (ax - px) * (by - py) - (bx - px) * (ay - py) >= 0
                && (bx - px) * (cy - py) - (cx - px) * (by - py) >= 0;
    }

    private static Node getLeftmost(Node start) {
        Node p = start;
        Node leftmost = start;
        do {
            if (p.x < leftmost.x || (p.x == leftmost.x && p.y < leftmost.y))
                leftmost = p;
            p = p.next;
        } while (p != start);
        return leftmost;
    }

    private static Node linkedList(float[] data, int start, int end, int dim, boolean clockwise) {
        Node last = null;
        if (clockwise == (signedArea(data, start, end, dim) > 0)) {
            for (int i = start; i < end; i += dim) {
                last = insertNode(i, data[i], data[i + 1], last);
            }
        } else {
            for (int i = (end - dim); i >= start; i -= dim) {
                last = insertNode(i, data[i], data[i + 1], last);
            }
        }

        if (last != null && equals(last, last.next)) {
            removeNode(last);
            last = last.next;
        }
        return last;
    }

    private static void removeNode(Node p) {
        p.next.prev = p.prev;
        p.prev.next = p.next;

        if (p.prevZ != null) {
            p.prevZ.nextZ = p.nextZ;
        }
        if (p.nextZ != null) {
            p.nextZ.prevZ = p.prevZ;
        }
    }

    private static Node insertNode(int i, float x, float y, Node last) {
        Node p = new Node(i, x, y);

        if (last == null) {
            p.prev = p;
            p.next = p;
        } else {
            p.next = last.next;
            p.prev = last;
            last.next.prev = p;
            last.next = p;
        }
        return p;
    }

    private static float getVertexX(int index, float[] vertices) {
        int n2 = vertices.length / 2;
        if (index >= n2) return vertices[(index % n2) * 2];
        else if (index < 0) return vertices[(index % n2 + n2) * 2];
        return vertices[index * 2];
    }

    private static float getVertexY(int index, float[] vertices) {
        int n2 = vertices.length / 2;
        if (index >= n2) return vertices[(index % n2) * 2 + 1];
        else if (index < 0) return vertices[(index % n2 + n2) * 2 + 1];
        return vertices[index * 2 + 1];
    }

    private static float signedArea(float[] data, int start, int end, int dim) {
        float sum = 0;
        int j = end - dim;
        for (int i = start; i < end; i += dim) {
            sum += (data[j] - data[i]) * (data[i + 1] + data[j + 1]);
            j = i;
        }
        return sum;
    }

    private static class Node {

        private int i; // vertex index in coordinates array
        private float x; // vertex x coordinate
        private float y; // vertex y coordinate
        private float z; // z-order curve value
        private boolean steiner; // indicates whether this is a steiner point

        // previous and next vertex nodes in a polygon ring
        private Node prev;
        private Node next;
        private Node prevZ;
        private Node nextZ;

        Node(int i, float x, float y) {
            this.i = i;
            this.x = x;
            this.y = y;
            this.prev = null;
            this.next = null;
            this.z = Float.MIN_VALUE;
            this.prevZ = null;
            this.nextZ = null;
            this.steiner = false;
        }

    }

}
