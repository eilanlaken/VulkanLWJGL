package org.example.engine.core.math;

// look at:
// libgdx GeometryUtils.java
// libgdx EarClippingTriangulator.java
public class Algorithms {

    // TODO: implement
    public static short[] triangulatePolygon(final float[] vertices) {
        throw new UnsupportedOperationException("TODO: implement");
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
