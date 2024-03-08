package org.example.engine.core.math;

/**
 * A pyramid with its top sliced off. Mainly used by a camera.
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

    // the 6 clipping planes: near, far, left, right, top, bottom
    public Shape3DPlane[] planes;

    public Shape3DFrustum() {
        this.planes = new Shape3DPlane[6];
        for (int i = 0; i < 6; i++) {
            this.planes[i] = new Shape3DPlane(new Vector3(), 0);
        }
    }

    public void set(final Vector3[] frustumCorners) {
        if (frustumCorners == null) throw new IllegalArgumentException("Cannot set frustum planes based on null corners array.");
        if (frustumCorners.length != 8) throw new IllegalArgumentException("Must provide exactly 8 points to set frustum planes.");
        planes[0].set(frustumCorners[1], frustumCorners[0], frustumCorners[2]);
        planes[1].set(frustumCorners[4], frustumCorners[5], frustumCorners[7]);
        planes[2].set(frustumCorners[0], frustumCorners[4], frustumCorners[3]);
        planes[3].set(frustumCorners[5], frustumCorners[1], frustumCorners[6]);
        planes[4].set(frustumCorners[2], frustumCorners[3], frustumCorners[6]);
        planes[5].set(frustumCorners[4], frustumCorners[0], frustumCorners[1]);
    }


    @Deprecated public boolean containsSphere(final Shape3DSphere sphere) {
        final float x = sphere.center.x;
        final float y = sphere.center.y;
        final float z = sphere.center.z;
        final float radius = sphere.radius;
        for (int i = 0; i < 8; i++) {
            if ((planes[i].normal.x * x + planes[i].normal.y * y + planes[i].normal.z * z) < (-radius - planes[i].d)) return false;
        }
        return true;
    }

    // TODO: test
    public boolean intersectsSphere(final Shape3DSphere sphere) {
        sphere.computeCenter(vector);
        final float x = vector.x;
        final float y = vector.y;
        final float z = vector.z;
        final float radius = sphere.radius;

//        for (int i = 0; i < planes.length; i++) {
//            final float signedDistance = planes[i].normal.x * x + planes[i].normal.y * y + planes[i].normal.z * z + planes[i].d;
//            System.out.println("test: " + (signedDistance-radius));
//            if (signedDistance + radius > 0) return false;
//        }
//

        for (int i = 0; i < planes.length; i++) {
            float signedDistance = planes[i].normal.x * x + planes[i].normal.y * y + planes[i].normal.z * z + planes[i].d;
            float diff = signedDistance + radius;
            if (diff < 0) return false;
        }
        return true;

//        float signedDistance = planes[2].normal.x * x + planes[2].normal.y * y + planes[2].normal.z * z + planes[2].d;
//        float diff = signedDistance + radius;
//        System.out.println("diff left: " + diff);
//            //if ((planes[i].normal.x * x + planes[i].normal.y * y + planes[i].normal.z * z + radius) > planes[i].d) return true;
//        if (contains(sphere.computeCenter(vector))) return true;
//        return false;
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
        for (int i = 0; i < planes.length; i++) {
            if (planes[i].distance(x,y,z) < 0) return false;
        }
        return true;
    }

    @Override
    public String toString () {
        return "<Frustum: \n" +
                "near: " + planes[0] + "\n" +
                "far: " + planes[1] + "\n" +
                "left: " + planes[2] + "\n" +
                "right: " + planes[3] + "\n" +
                "top: " + planes[4] + "\n" +
                "bottom: " + planes[5] + "\n"
                + "\n>";
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