package org.example.engine.core.shape;

import org.example.engine.core.collections.*;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

//https://github.com/earcut4j/earcut4j/blob/master/src/test/java/earcut4j/Test01.java
public final class ShapeUtils {

    private static final int[] rectanglePolygonIndices = new int[] {2, 3, 0, 0, 1, 2};
    private static final Map<CollectionsTuple3<Float, Float, Float>, int[]> cachedHollowRectangleIndices = new HashMap<>();
    private static final Map<CollectionsTuple2<Float, Integer>, float[]> cachedFilledCirclesVertices = new HashMap<>();
    private static final Map<CollectionsTuple4<Float, Integer, Float, Float>, float[]> cachedFilledCircleArcsVertices = new HashMap<>();
    private static final Map<CollectionsTuple2<Float, Integer>, int[]> cachedFilledCirclesIndices = new HashMap<>();
    private static final Map<CollectionsTuple4<Float, Integer, Float, Float>, int[]> cachedFilledCircleArcsIndices = new HashMap<>();
    private static final Map<CollectionsTuple3<Float, Integer, Float>, float[]> cachedHollowCirclesVertices = new HashMap<>();
    private static final Map<CollectionsTuple5<Float, Integer, Float, Float, Float>, float[]> cachedHollowCircleArcsVertices = new HashMap<>();
    private static final Map<CollectionsTuple3<Float, Integer, Float>, int[]> cachedHollowCirclesIndices = new HashMap<>();
    private static final Map<CollectionsTuple5<Float, Integer, Float, Float, Float>, int[]> cachedHollowCircleArcsIndices = new HashMap<>();
    private static final Map<float[], int[]> cachedFilledPolygonIndices = new HashMap<>();
    private static final Map<float[], float[]> cachedHollowPolygonVertices = new HashMap<>();
    private static final Map<float[], int[]> cachedHollowPolygonIndices = new HashMap<>();

    private ShapeUtils() {}

    public static Shape2DPolygon createPolygonLine(float x1, float y1, float x2, float y2, float stroke) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        MathVector2 strokeVector = new MathVector2(dx, dy).rotate90(1).nor().scl(stroke * 0.5f, stroke * 0.5f);
        float[] vertices = new float[] {x1 + strokeVector.x, y1 + strokeVector.y, x1 - strokeVector.x, y1 - strokeVector.y, x2 - strokeVector.x, y2 - strokeVector.y, x2 + strokeVector.x, y2 + strokeVector.y};
        return new Shape2DPolygon(rectanglePolygonIndices, vertices);
    }

    public static Shape2DPolygon createPolygonRectangleFilled(float width, float height) {
        final float widthHalf = width * 0.5f;
        final float heightHalf = height * 0.5f;
        float[] vertices = new float[] {-widthHalf, heightHalf, -widthHalf, -heightHalf, widthHalf, -heightHalf, widthHalf, heightHalf};
        return new Shape2DPolygon(rectanglePolygonIndices, vertices);
    }

    public static Shape2DPolygon createPolygonRectangleHollow(float width, float height, float stroke) {
        final float widthHalf = width * 0.5f;
        final float heightHalf = height * 0.5f;
        final float strokeHalf = stroke * 0.5f;
        float[] vertices = new float[] {
                -widthHalf - strokeHalf, heightHalf + strokeHalf, -widthHalf - strokeHalf, -heightHalf - strokeHalf, widthHalf + strokeHalf, -heightHalf - strokeHalf, widthHalf + strokeHalf, heightHalf + strokeHalf,
                -widthHalf + strokeHalf, heightHalf - strokeHalf, -widthHalf + strokeHalf, -heightHalf + strokeHalf, widthHalf - strokeHalf, -heightHalf + strokeHalf, widthHalf - strokeHalf, heightHalf - strokeHalf
        };
        final CollectionsTuple3<Float, Float, Float> widthHeightStroke = new CollectionsTuple3<>(width, height, stroke);
        int[] indices = cachedHollowRectangleIndices.get(widthHeightStroke);
        if (indices == null) {
            indices = ShapeUtils.triangulatePolygon(vertices, new int[] { 4 }, 2);
            cachedHollowRectangleIndices.put(widthHeightStroke, indices);
        }
        return new Shape2DPolygon(indices, vertices);
    }

    public static Shape2DPolygon createPolygonCircleFilled(float r, int refinement) {
        if (refinement < 3) throw new IllegalArgumentException("Refinement (the number of edge vertices) must be >= 3. Got: " + refinement);
        final CollectionsTuple2<Float, Integer> radiusRefinement = new CollectionsTuple2<>(r, refinement);
        float[] vertices = cachedFilledCirclesVertices.get(new CollectionsTuple2<>(r, refinement));
        if (vertices == null) {
            vertices = new float[refinement * 2];
            for (int i = 0; i < refinement * 2; i += 2) {
                float angle = 360f * (i * 0.5f) / refinement;
                vertices[i] = r * MathUtils.cosDeg(angle);
                vertices[i+1] = r * MathUtils.sinDeg(angle);
            }
            cachedFilledCirclesVertices.put(radiusRefinement, vertices);
        }
        int[] indices = cachedFilledCirclesIndices.get(radiusRefinement);
        if (indices == null) {
            indices = triangulatePolygon(vertices);
            cachedFilledCirclesIndices.put(radiusRefinement, indices);
        }
        return new Shape2DPolygon(indices, vertices);
    }

    public static Shape2DPolygon createPolygonCircleFilled(float r, int refinement, float degStart, float degEnd) {
        degStart = MathUtils.normalizeAngleDeg(degStart);
        float range = degEnd > degStart && MathUtils.isEqual(MathUtils.normalizeAngleDeg(degEnd), degStart) ? 360.0f : Math.abs(MathUtils.normalizeAngleDeg(degEnd) - degStart);
        boolean fullRange = MathUtils.isEqual(360.0f, range);
        if (fullRange) return createPolygonCircleFilled(r, refinement);
        if (refinement < 3) throw new IllegalArgumentException("Refinement (the number of edge vertices) must be >= 3. Got: " + refinement);
        final CollectionsTuple4<Float, Integer, Float, Float> tuple4 = new CollectionsTuple4<>(r, refinement, degStart, degEnd);
        float[] vertices = cachedFilledCircleArcsVertices.get(new CollectionsTuple4<>(r, refinement, degStart, degEnd));
        int size = refinement * 2 + 2;
        if (vertices == null) {
            vertices = new float[size]; // add extra vertex for center.
            for (int i = 0; i < refinement * 2; i += 2) {
                float angle = degStart + range * (i * 0.5f) / refinement;
                vertices[i] = r * MathUtils.cosDeg(angle);
                vertices[i+1] = r * MathUtils.sinDeg(angle);
            }
            vertices[refinement * 2] = 0;
            vertices[refinement * 2 + 1] = 0;
            cachedFilledCircleArcsVertices.put(tuple4, vertices);
        }
        int[] indices = cachedFilledCircleArcsIndices.get(tuple4);
        if (indices == null) {
            indices = triangulatePolygon(vertices);
            cachedFilledCircleArcsIndices.put(tuple4, indices);
        }
        return new Shape2DPolygon(indices, vertices);
    }

    public static Shape2DPolygon createPolygonCircleHollow(float r, int refinement, float stroke) {
        if (refinement < 3) throw new IllegalArgumentException("Refinement (the number of edge vertices) must be >= 3. Got: " + refinement);
        stroke = Math.abs(stroke);

        final CollectionsTuple3<Float, Integer, Float> radiusRefinementStroke = new CollectionsTuple3<>(r, refinement, stroke);
        float[] vertices = cachedHollowCirclesVertices.get(radiusRefinementStroke);
        if (vertices == null) {
            final float outerRadius = r + stroke * 0.5f;
            final float innerRadius = r - stroke * 0.5f;
            vertices = new float[refinement * 2 * 2];
            for (int i = 0; i < refinement * 2; i += 2) { // outer rim
                float angle = 360f * (i * 0.5f) / refinement;
                vertices[i] = outerRadius * MathUtils.cosDeg(angle);
                vertices[i+1] = outerRadius * MathUtils.sinDeg(angle);
            }
            for (int i = refinement * 2; i < refinement * 2 * 2; i += 2) { // inner rim
                float angle = 360f * (i * 0.5f) / refinement;
                vertices[i] = innerRadius * MathUtils.cosDeg(angle);
                vertices[i+1] = innerRadius * MathUtils.sinDeg(angle);
            }
            cachedHollowCirclesVertices.put(radiusRefinementStroke, vertices);
        }

        int[] indices = cachedHollowCirclesIndices.get(radiusRefinementStroke);
        if (indices == null) {
            indices = triangulatePolygon(vertices, new int[] { refinement }, 2);
            cachedHollowCirclesIndices.put(radiusRefinementStroke, indices);
        }
        return new Shape2DPolygon(indices, vertices);
    }

    public static Shape2DPolygon createPolygonCircleHollow(float r, int refinement, float stroke, float degStart, float degEnd) {
        degStart = MathUtils.normalizeAngleDeg(degStart);
        float range = degEnd > degStart && MathUtils.isEqual(MathUtils.normalizeAngleDeg(degEnd), degStart) ? 360.0f : Math.abs(MathUtils.normalizeAngleDeg(degEnd) - degStart);

        boolean fullRange = MathUtils.isEqual(360.0f, range);
        if (fullRange) return createPolygonCircleHollow(r, refinement, stroke);
        if (refinement < 3) throw new IllegalArgumentException("Refinement (the number of edge vertices) must be >= 3. Got: " + refinement);
        stroke = Math.abs(stroke);

        final CollectionsTuple5<Float, Integer, Float, Float, Float> tuple5 = new CollectionsTuple5<>(r, refinement, stroke, degStart, degEnd);
        float[] vertices = cachedHollowCircleArcsVertices.get(tuple5);
        if (vertices == null) {
            final float outerRadius = r + stroke * 0.5f;
            final float innerRadius = r - stroke * 0.5f;
            vertices = new float[refinement * 2 * 2]; // refinement * dimensions * rings count
            float angleStep = range * 0.5f / (refinement - 1);
            for (int i = 0; i < vertices.length / 2; i += 2) {
                float angle = degStart + angleStep * i;
                vertices[i] = outerRadius * MathUtils.cosDeg(angle);
                vertices[i + 1] = outerRadius * MathUtils.sinDeg(angle);
            }
            for (int i = vertices.length / 2; i < vertices.length; i += 2) { // inner rim
                float angle = (degStart + range) - angleStep * (i - vertices.length / 2f);
                vertices[i] = innerRadius * MathUtils.cosDeg(angle);
                vertices[i+1] = innerRadius * MathUtils.sinDeg(angle);
            }
            cachedHollowCircleArcsVertices.put(tuple5, vertices);
        }

        int[] indices = cachedHollowCircleArcsIndices.get(tuple5);
        if (indices == null) {
            indices = triangulatePolygon(vertices);
            cachedHollowCircleArcsIndices.put(tuple5, indices);
        }
        return new Shape2DPolygon(indices, vertices);
    }

    public static Shape2DPolygon createPolygonFilled(float[] vertices) {
        int[] indices = cachedFilledPolygonIndices.get(vertices);
        if (indices == null) {
            indices = triangulatePolygon(vertices, null, 2);
            cachedFilledPolygonIndices.put(vertices, indices);
        }
        return new Shape2DPolygon(indices, vertices);
    }

    public static Shape2DPolygon createPolygonHollow(float[] vertices, float stroke) {
        stroke = Math.abs(stroke);
        float[] expandedVertices = cachedHollowPolygonVertices.get(vertices);
        if (expandedVertices == null) {
            MathVector2 centerOfGeometry = calculateCenterOfGeometry(vertices);
            MathVector2 tmp = new MathVector2();
            expandedVertices = new float[vertices.length * 2];
            final float extrude = stroke * 0.5f;
            for (int i = 0; i < vertices.length; i += 2) {
                tmp.set(vertices[i], vertices[i+1]).sub(centerOfGeometry).nor().scl(extrude);
                // outer rim
                expandedVertices[i] = vertices[i] + tmp.x;
                expandedVertices[i + 1] = vertices[i + 1] + tmp.y;
                // inner rim
                expandedVertices[i + vertices.length] = vertices[i] - tmp.x;
                expandedVertices[i + vertices.length + 1] = vertices[i + 1] - tmp.y;
            }
            cachedHollowPolygonVertices.put(vertices, expandedVertices);
        }
        int[] indices = cachedHollowPolygonIndices.get(vertices);
        if (indices == null) {
            indices = triangulatePolygon(expandedVertices, new int[] { vertices.length / 2 }, 2);
            cachedHollowPolygonIndices.put(vertices, indices);
        }
        return new Shape2DPolygon(indices, expandedVertices);
    }

    public static Shape2DPolygon createPolygonCurve(float xMin, float xMax, int refinement, float stroke, Function<Float, Float> function) {
        if (refinement <= 0) throw new IllegalArgumentException("Refinement (the number of edge vertices) must be >= 0. Got: " + refinement);
        if (refinement == 1) return createPolygonCircleFilled(stroke, 15);
        if (xMin < xMax) {
            float tmp = xMin;
            xMin = xMax;
            xMax = tmp;
        }
        final float step = (xMax - xMin) / (refinement - 1);
        final float halfStroke = Math.abs(stroke) * 0.5f;
        float[] vertices = new float[refinement * 2 * 2]; // refinement * dimensions * paths
        // bottom line
        for (int i = 0; i < vertices.length / 2; i += 2) {
            float x = xMin + step * i;
            vertices[i] = x;
            vertices[i + 1] = function.apply(x) - halfStroke;
            vertices[vertices.length - i - 2] = x;
            vertices[vertices.length - i - 2 + 1] = function.apply(x) + halfStroke;
        }

        int[] indices = triangulatePolygon(vertices);
        return new Shape2DPolygon(indices, vertices);
    }

    public static boolean isPolygonConvex(final float[] vertices) {
        if (vertices.length <= 6) return true;
        MathVector2 tmp1 = new MathVector2();
        MathVector2 tmp2 = new MathVector2();

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

    // [5, 8] -> [0,4] [5,7] [8, length - 1] 
    // [4, 8, 11] -> [0,3] [4,7] [8,10] [11, length-1]
    static CollectionsArray<CollectionsTuple2<Integer, Integer>> getLoops(final int[] holes, int vertexCount) {
        CollectionsArray<CollectionsTuple2<Integer, Integer>> loops = new CollectionsArray<>();
        if (holes == null || holes.length == 0) {
            loops.add(new CollectionsTuple2<>(0, vertexCount - 1));
            return loops;
        }

        int start = 0;
        for (int hole : holes) {
            int end = hole - 1;
            loops.add(new CollectionsTuple2<>(start, end));
            start = hole;
        }
        if (start <= vertexCount - 1) {
            loops.add(new CollectionsTuple2<>(start, vertexCount - 1));
        }
        return loops;
    }

    public static MathVector2 calculateCenterOfGeometry(final float[] vertices) {
        MathVector2 center = new MathVector2();
        for (int i = 0; i < vertices.length - 1; i += 2) {
            center.add(vertices[i], vertices[i+1]);
        }
        return center.scl(1f / (vertices.length * 0.5f));
    }

    // TODO: test - doesn't work in the case of polygon with holes.
    @Deprecated public static float incorrect_calculatePolygonSignedArea(final float[] vertices) {
        float area = 0;
        final int n = vertices.length;
        for (int i = 0; i < n - 1; i += 2) {
            float x1 = vertices[i];
            float y1 = vertices[i+1];
            float x2 = vertices[(i + 2) % n];
            float y2 = vertices[(i + 3) % n];
            area += x1 * y2;
            area -= x2 * y1;
        }
        return area * 0.5f;
    }

    public static float calculatePolygonBoundingRadius(final float[] vertices) {
        float max = 0;
        for (int i = 0; i < vertices.length - 1; i += 2) {
            float l2 = vertices[i] * vertices[i] + vertices[i+1] * vertices[i+1];
            if (l2 > max) max = l2;
        }
        return (float) Math.sqrt(max);
    }

    // TODO: bonus implement.
    public CollectionsArray<Shape2D> fracture(Shape2D shape, MathVector2... controlPoints) {

        return null;
    }

    // TODO: bonus implement.
    public CollectionsArray<Shape2D> fracture(Shape2D shape, int n) {
        // generate an array of n random control points, contained in shape.
        return null;
    }

    /**
     * Triangulates the given polygon
     *
     * @param vertices is a flat array of vertice coordinates like [x0,y0, x1,y1, x2,y2, ...].
     * @return List containing groups of three vertice indices in the resulting array forms a triangle.
     */
    public static int[] triangulatePolygon(float[] vertices) {
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
        CollectionsArrayInt triangles = new CollectionsArrayInt();

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

    private static void triangulateLinked(Node ear, CollectionsArrayInt triangles, int dim, float minX, float minY, float invSize, int pass) {
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

    private static void splitPolygon(Node start, CollectionsArrayInt triangles, int dim, float minX, float minY, float size) {
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

    private static Node cureLocalIntersections(Node start, CollectionsArrayInt triangles, int dim) {
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
