package org.example.engine.core.math;

import java.util.Arrays;

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
    private static final Vector3 vector = new Vector3();
    public static final int PLANE_NEAR = 0;
    public static final int PLANE_FAR = 1;
    public static final int PLANE_LEFT = 2;
    public static final int PLANE_RIGHT = 3;
    public static final int PLANE_TOP = 4;
    public static final int PLANE_BOTTOM = 5;
    public Shape3DPlane[] planes; // the 6 clipping planes: near, far, left, right, top, bottom
    private Vector3[] frustumCorners = {
            new Vector3(), new Vector3(), new Vector3(), new Vector3(), // near frustum plane corners
            new Vector3(), new Vector3(), new Vector3(), new Vector3(), // far frustum plane corners
    };

    public Shape3DFrustum() {
        this.planes = new Shape3DPlane[6];
        for (int i = 0; i < 6; i++) {
            this.planes[i] = new Shape3DPlane(new Vector3(), 0);
        }
    }

    public void update(final Matrix4 invPrjView) {
        for (int i = 0; i < 8; i++) frustumCorners[i].set(MathUtils.canonicalCubeCorners[i]).prj(invPrjView);
        planes[0].set(frustumCorners[1], frustumCorners[0], frustumCorners[2]);
        planes[1].set(frustumCorners[4], frustumCorners[5], frustumCorners[7]);
        planes[2].set(frustumCorners[0], frustumCorners[4], frustumCorners[3]);
        planes[3].set(frustumCorners[5], frustumCorners[1], frustumCorners[6]);
        planes[4].set(frustumCorners[2], frustumCorners[3], frustumCorners[6]);
        planes[5].set(frustumCorners[4], frustumCorners[0], frustumCorners[1]);
    }

    @Deprecated public void set(final Vector3[] frustumCorners) {
        if (frustumCorners == null) throw new IllegalArgumentException("Cannot set frustum planes based on null corners array.");
        if (frustumCorners.length != 8) throw new IllegalArgumentException("Must provide exactly 8 points to set frustum planes.");
        planes[0].set(frustumCorners[1], frustumCorners[0], frustumCorners[2]);
        planes[1].set(frustumCorners[4], frustumCorners[5], frustumCorners[7]);
        planes[2].set(frustumCorners[0], frustumCorners[4], frustumCorners[3]);
        planes[3].set(frustumCorners[5], frustumCorners[1], frustumCorners[6]);
        planes[4].set(frustumCorners[2], frustumCorners[3], frustumCorners[6]);
        planes[5].set(frustumCorners[4], frustumCorners[0], frustumCorners[1]);
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

    public Shape3DPlane getNear() {
        return planes[PLANE_NEAR];
    }

    public Shape3DPlane getFar() {
        return planes[PLANE_FAR];
    }

    public Shape3DPlane getLeft() {
        return planes[PLANE_LEFT];
    }

    public Shape3DPlane getRight() {
        return planes[PLANE_RIGHT];
    }

    public Shape3DPlane getTop() {
        return planes[PLANE_TOP];
    }

    public Shape3DPlane getBottom() {
        return planes[PLANE_BOTTOM];
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

/**
 private static final Vector3[] clipSpacePlanePoints = { // This is the clipping volume
 // near clipping plane
 new Vector3(-1, -1, -1),
 new Vector3(1, -1, -1),
 new Vector3(1, 1, -1),
 new Vector3(-1, 1, -1),
 // far clipping plane
 new Vector3(-1, -1, 1),
 new Vector3(1, -1, 1),
 new Vector3(1, 1, 1),
 new Vector3(-1, 1, 1)
 };
 private static Vector3[] planePoints = {
 // near
 new Vector3(),
 new Vector3(),
 new Vector3(),
 new Vector3(),
 // far
 new Vector3(),
 new Vector3(),
 new Vector3(),
 new Vector3()
 };
 // yanked from libGDX
 // TODO: see if there's a more modular solution, without sacrificing clearness.
 @Deprecated public void set(Matrix4 invPrjView) {
 for (int i = 0; i < 8; i++) {
 Vector3.project(invPrjView, clipSpacePlanePoints[i], planePoints[i]);
 }
 // update the planes
 planes[0].set(planePoints[1], planePoints[0], planePoints[2]);
 planes[1].set(planePoints[4], planePoints[5], planePoints[7]);
 planes[2].set(planePoints[0], planePoints[4], planePoints[3]);
 planes[3].set(planePoints[5], planePoints[1], planePoints[6]);
 planes[4].set(planePoints[2], planePoints[3], planePoints[6]);
 planes[5].set(planePoints[4], planePoints[0], planePoints[1]);
 }
 */