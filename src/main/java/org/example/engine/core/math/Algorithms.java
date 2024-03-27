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
        int last = vertices.length - 1;
        float x1 = vertices[last - 1], y1 = vertices[last];
        for (int i = 0; i < last - 1; i += 2) {
            float x2 = vertices[i], y2 = vertices[i + 1];
            area += x1 * y2 - x2 * y1;
            x1 = x2;
            y1 = y2;
        }
        return area * 0.5f;
    }

}
