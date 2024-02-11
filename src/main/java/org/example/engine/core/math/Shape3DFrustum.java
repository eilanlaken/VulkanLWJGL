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

    private static final Vector3[] clipSpacePlanePoints = {new Vector3(-1, -1, -1), new Vector3(1, -1, -1),
            new Vector3(1, 1, -1), new Vector3(-1, 1, -1), // near clip
            new Vector3(-1, -1, 1), new Vector3(1, -1, 1), new Vector3(1, 1, 1), new Vector3(-1, 1, 1)}; // far clip

    public static final int PLANE_NEAR = 0;
    public static final int PLANE_FAR = 1;
    public static final int PLANE_LEFT = 2;
    public static final int PLANE_RIGHT = 3;
    public static final int PLANE_TOP = 4;
    public static final int PLANE_BOTTOM = 5;

    public Shape3DPlane[] planes;
    public Vector3[] planePoints = {new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3(),
            new Vector3(), new Vector3()};

    public Shape3DFrustum() {
        this.planes = new Shape3DPlane[6];
        for (int i = 0; i < 6; i++) {
            this.planes[i] = new Shape3DPlane(new Vector3(), 0);
        }
    }

    // yanked from libGDX
    public void set(Matrix4 invPrjView) {
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

    public boolean containsSphere(final Shape3DSphere sphere) {
        final float x = sphere.center.x;
        final float y = sphere.center.y;
        final float z = sphere.center.z;
        final float radius = sphere.radius;
        for (int i = 0; i < 6; i++) {
            if ((planes[i].normal.x * x + planes[i].normal.y * y + planes[i].normal.z * z) < (-radius - planes[i].d)) return false;
        }
        return true;
    }

    public boolean containsCube(final Shape3DCube cube) {
        for (int i = 0; i < planes.length; i++) {
            if (planes[i].distance(cube.computeCorner(0, vector)) < 1) continue;
            if (planes[i].distance(cube.computeCorner(1, vector)) < 1) continue;
            if (planes[i].distance(cube.computeCorner(2, vector)) < 1) continue;
            if (planes[i].distance(cube.computeCorner(3, vector)) < 1) continue;
            if (planes[i].distance(cube.computeCorner(4, vector)) < 1) continue;
            if (planes[i].distance(cube.computeCorner(5, vector)) < 1) continue;
            if (planes[i].distance(cube.computeCorner(6, vector)) < 1) continue;
            if (planes[i].distance(cube.computeCorner(7, vector)) < 1) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean contains(float x, float y, float z) {
        for (int i = 0; i < planes.length; i++) {
            if (planes[i].distance(x,y,z) < 0) return false;
        }
        return true;
    }

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
