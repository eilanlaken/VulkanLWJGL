package org.example.engine.core.math;

import org.example.engine.core.collections.CyclicLinkedList;

public class Algorithms {

    // used by the triangulatePolygon method
    private static final Vector2 tmpA = new Vector2();
    private static final Vector2 tmpB = new Vector2();
    private static final Vector2 tmpC = new Vector2();
    private static final Vector2 tmpAB = new Vector2();
    private static final Vector2 tmpAC = new Vector2();
    private static final Vector2 tmpBC = new Vector2();
    private static final Vector2 tmpCA = new Vector2();
    private static final Vector2 tmpAP = new Vector2();
    private static final Vector2 tmpBP = new Vector2();
    private static final Vector2 tmpCP = new Vector2();
    private static final Vector2 tmpP = new Vector2();

    public static int[] triangulatePolygon(final float[] vertices) {
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Vertices array not valid: must contain an even number of float values in interleaved format: [x0,y0,x1,y1,...].");
        if (vertices.length < 6) throw new IllegalArgumentException("Vertices array must contain at least 6 values to represent a valid polygon. Input contains: " + vertices.length);
        final int vertexCount = vertices.length / 2;

        CyclicLinkedList<Integer> indices = new CyclicLinkedList<>();
        for (int i = 0; i < vertexCount; i++) indices.add(i);
        final int triangleCount = vertexCount - 2;
        final int triangleIndexCount = triangleCount * 3;
        int[] output = new int[triangleIndexCount];
        int currentIndex = 0;

        while (indices.size() > 3) {
            for (int i = 0; i < indices.size(); i++) {

                int a = indices.get(i);
                int b = indices.get(i-1);
                int c = indices.get(i+1);


                // test if angle is convex or reflex
                Shape2DPolygon.getVertex(vertices, a, tmpA);
                Shape2DPolygon.getVertex(vertices, b, tmpB);
                Shape2DPolygon.getVertex(vertices, c, tmpC);
                tmpAB.set(tmpB).sub(tmpA); // a->b
                tmpAC.set(tmpC).sub(tmpA); // a->c
                float cross = tmpAB.crs(tmpAC);
                if (cross < 0) continue;

                boolean isEar = true;
                for (int j = 0; j < indices.size(); j++) {
                    if (j == a || j == b || j == c) continue;
                    Shape2DPolygon.getVertex(vertices, j, tmpP);
                    if (isPointInTriangle(tmpP, tmpB, tmpA, tmpC)) {
                        isEar = false;
                        break;
                    }
                }

                if (isEar) {
                    output[currentIndex++] = b;
                    output[currentIndex++] = a;
                    output[currentIndex++] = c;
                    indices.remove(i);
                    break;
                }
            }
        }

        // add final 3
        output[currentIndex++] = indices.get(0);
        output[currentIndex++] = indices.get(1);
        output[currentIndex++] = indices.get(2);

        return output;
    }

    public static boolean isPointInTriangle(Vector2 p, Vector2 a, Vector2 b, Vector2 c) {
        tmpAB.set(b).sub(a); // a->b
        tmpBC.set(c).sub(b); // b->c
        tmpCA.set(a).sub(c); // c->a
        tmpAP.set(p).sub(a); // a->p
        tmpBP.set(p).sub(b); // b->p
        tmpCP.set(p).sub(c); // c->p

        float cross1 = tmpAB.crs(tmpAP);
        float cross2 = tmpBC.crs(tmpBP);
        float cross3 = tmpCA.crs(tmpCP);

        return !(cross1 > 0f) && !(cross2 > 0f) && !(cross3 > 0f);
    }

    public static float calculatePolygonSignedArea(final float[] vertices) {
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

}
