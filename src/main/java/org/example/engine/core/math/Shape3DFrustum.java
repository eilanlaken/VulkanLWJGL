package org.example.engine.core.math;

/**
 * A pyramid with its top sliced off. Used by a camera.
 *      ___
 *    /    \
 *  /_______\
 *  (this, but in 3D)
 *
 */
public class Shape3DFrustum implements Shape3D {

    // for the purpose of intermediate computations
    private final Vector3 vector = new Vector3();
    public Shape3DPlane[] planes; // the 6 clipping planes: near, far, left, right, top, bottom
    private final Vector3[] frustumCorners = {
            new Vector3(), new Vector3(), new Vector3(), new Vector3(), // near frustum plane corners
            new Vector3(), new Vector3(), new Vector3(), new Vector3(), // far frustum plane corners
    };

    public Shape3DFrustum() {
        this.planes = new Shape3DPlane[6];
        for (int i = 0; i < 6; i++) {
            this.planes[i] = new Shape3DPlane(new Vector3(), 0);
        }
    }

    public void set(final Matrix4 invPrjView) {
        for (int i = 0; i < 8; i++) frustumCorners[i].set(MathUtils.canonicalCubeCorners[i]).prj(invPrjView);
        planes[0].set(frustumCorners[1], frustumCorners[0], frustumCorners[2]); // near
        planes[1].set(frustumCorners[4], frustumCorners[5], frustumCorners[7]); // far
        planes[2].set(frustumCorners[0], frustumCorners[4], frustumCorners[3]); // left
        planes[3].set(frustumCorners[5], frustumCorners[1], frustumCorners[6]); // right
        planes[4].set(frustumCorners[2], frustumCorners[3], frustumCorners[6]); // top
        planes[5].set(frustumCorners[4], frustumCorners[0], frustumCorners[1]); // bottom
    }

    public boolean intersectsSphere(final Shape3DSphere sphere) {
        final float x = sphere.translatedCenter.x;
        final float y = sphere.translatedCenter.y;
        final float z = sphere.translatedCenter.z;
        final float radius = sphere.scaledRadius;

        for (Shape3DPlane plane : planes) {
            float signedDistance = plane.normal.x * x + plane.normal.y * y + plane.normal.z * z + plane.d;
            float diff = signedDistance + radius;
            if (diff < 0) return false;
        }
        return true;
    }

    public boolean intersectsAABB(final Shape3DAABB aabb) {
        for (int i = 0; i < 8; i++) {
            if (contains(aabb.computeCorner(i, vector))) return true;
        }
        return false;
    }

    @Override
    public boolean contains(float x, float y, float z) {
        for (Shape3DPlane plane : planes) {
            if (plane.distance(x, y, z) < 0) return false;
        }
        return true;
    }

    @Override
    public String toString () {
        return "Frustum: <" +
                "near: " + planes[0] + ", " +
                "far: " + planes[1] + ", " +
                "left: " + planes[2] + ", " +
                "right: " + planes[3] + ", " +
                "top: " + planes[4] + ", " +
                "bottom: " + planes[5] + ">";
    }

}