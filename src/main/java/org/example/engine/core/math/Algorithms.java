package org.example.engine.core.math;

// look at:
// libgdx GeometryUtils.java
// libgdx EarClippingTriangulator.java
public class Algorithms {

    public static short[] triangulatePolygon(final float[] vertices) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    public static boolean isPolygonClockwise(final float[] vertices) {
        if (vertices.length <= 2) return false;
        float area = 0;
        int last = vertices.length - 2;
        float x1 = vertices[last], y1 = vertices[last + 1];
        for (int i = 0; i <= last; i += 2) {
            float x2 = vertices[i], y2 = vertices[i + 1];
            area += x1 * y2 - x2 * y1;
            x1 = x2;
            y1 = y2;
        }
        return area < 0;
    }

    public static boolean isPolygonCounterClockwise(final float[] vertices) {
        return !isPolygonClockwise(vertices);
    }

}
