package org.example.engine.core.physics2d;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayFloat;
import org.example.engine.core.collections.CollectionsUtils;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector2;

public final class Physics2DUtils {

    private Physics2DUtils() {}

    public static float calculateMomentOfInertia(BodyCollider collider) {

        if (collider instanceof BodyColliderCircle) {
            float mass = collider.density * collider.area();
            BodyColliderCircle circle = (BodyColliderCircle) collider;
            return 0.5f * mass * circle.r * circle.r;
        }

        if (collider instanceof BodyColliderRectangle) {
            BodyColliderRectangle rectangle = (BodyColliderRectangle) collider;
            float mass = collider.density * collider.area();
            float w = rectangle.width;
            float h = rectangle.height;
            return (1f / 12.0f) * mass * (w * w + h * h);
        }

        if (collider instanceof BodyColliderPolygon) {
            BodyColliderPolygon polygon = (BodyColliderPolygon) collider;

            float[] vertices = polygon.vertices;
            int[] indices = polygon.indices;
            int triangles = indices.length / 3;

            ArrayFloat masses = new ArrayFloat(true, triangles);
            ArrayFloat Is = new ArrayFloat(true, triangles);
            Array<Vector2> centroids = new Array<>(true, triangles);

            Vector2 combinedCentroid = new Vector2();
            float totalMass = 0;

            for (int i = 0; i < indices.length; i += 3) {
                // Get the indices of the current triangle's vertices
                int indexA = indices[i] * 2;
                int indexB = indices[i + 1] * 2;
                int indexC = indices[i + 2] * 2;
                // Extract the vertices of the current triangle
                float ax = CollectionsUtils.getCyclic(vertices, indexA);
                float ay = CollectionsUtils.getCyclic(vertices,indexA + 1);
                float bx = CollectionsUtils.getCyclic(vertices, indexB);
                float by = CollectionsUtils.getCyclic(vertices,indexB + 1);
                float cx = CollectionsUtils.getCyclic(vertices, indexC);
                float cy = CollectionsUtils.getCyclic(vertices,indexC + 1);
                // calculate mass of the triangle
                float tri_mass = MathUtils.getAreaTriangle_old(ax, ay, bx, by, cx, cy) * collider.density;
                totalMass += tri_mass;
                masses.add(tri_mass);
                // calculate local centroid of the triangle
                float centerX = (ax + bx + cx) / 3.0f;
                float centerY = (ay + by + cy) / 3.0f;
                centroids.add(new Vector2(centerX, centerY));
                combinedCentroid.x += tri_mass * centerX;
                combinedCentroid.y += tri_mass * centerY;
                // calculate moment of inertia of the triangle with respect to its centroid
                Vector2 a = new Vector2(ax - centerX, ay - centerY);
                Vector2 b = new Vector2(bx - centerX, by - centerY);
                Vector2 c = new Vector2(cx - centerX, cy - centerY);
                float aa = Vector2.dot(a, a);
                float bb = Vector2.dot(b, b);
                float cc = Vector2.dot(c, c);
                float ab = Vector2.dot(a, b);
                float bc = Vector2.dot(b, c);
                float ca = Vector2.dot(c, a);
                float tri_I = (aa + bb + cc + ab + bc + ca) * tri_mass / 6f;
                Is.add(tri_I);
            }

            combinedCentroid.scl(1.0f / totalMass);

            float I = 0;
            for (int i = 0; i < triangles; ++i) {
                I += Is.get(i) + masses.get(i) * (Vector2.dst2(centroids.get(i), combinedCentroid));
            }

            return I;
        }

        return 0;
    }

    /**
     * Returns the relative angle between the two bodies given the reference angle.
     * @return double
     */
    public static float getRelativeRotationRad(Body body_1, Body body_2, float referenceAngleRad) {
        float rr = (body_1.aRad - body_2.aRad) - referenceAngleRad;
        if (rr < -MathUtils.PI) rr += MathUtils.PI_TWO;
        if (rr >  MathUtils.PI) rr -= MathUtils.PI_TWO;
        return rr;
    }

}
