package org.example.engine.core.graphics;

import org.example.engine.core.math.Matrix4x4;
import org.example.engine.core.math.Vector3;
import org.example.engine.core.shape.Shape3DPlane;
import org.example.engine.core.shape.Shape3D_old;

/**
 * A pyramid with its top sliced off. Used by a camera.
 *      ___
 *    /    \
 *  /_______\
 *  (this, but in 3D)
 *
 */
public class CameraLensFrustum {

    private final Vector3[] frustumCorners;
    private final Vector3[] normals;
    private final float[]   ds;

    CameraLensFrustum() {
        this.frustumCorners = new Vector3[8];
        for (int i = 0; i < 8; i++) {
            this.frustumCorners[i] = new Vector3();
        }
        this.normals = new Vector3[6];
        for (int i = 0; i < 6; i++) {
            this.normals[i] = new Vector3();
        }
        this.ds = new float[6];
    }

    public void update(final Matrix4x4 invPrjView) {
        /* Update frustum corners by taking the canonical cube and un-projecting it. */
        /* The canonical cube is a cube, centered at the origin, with 8 corners: (+-1, +-1, +-1). Also known as OpenGL "clipping volume".*/
        frustumCorners[0].set(-1,-1,-1).prj(invPrjView);
        frustumCorners[1].set( 1,-1,-1).prj(invPrjView);
        frustumCorners[2].set( 1, 1,-1).prj(invPrjView);
        frustumCorners[3].set(-1, 1,-1).prj(invPrjView);
        frustumCorners[4].set(-1,-1, 1).prj(invPrjView);
        frustumCorners[5].set( 1,-1, 1).prj(invPrjView);
        frustumCorners[6].set( 1, 1, 1).prj(invPrjView);
        frustumCorners[7].set(-1, 1, 1).prj(invPrjView);

        /* Update the frustum's clipping plane normal and d values. */
        setClippingPlane(0, frustumCorners[1], frustumCorners[0], frustumCorners[2]); // near
        setClippingPlane(1, frustumCorners[4], frustumCorners[5], frustumCorners[7]); // far
        setClippingPlane(2, frustumCorners[0], frustumCorners[4], frustumCorners[3]); // left
        setClippingPlane(3, frustumCorners[5], frustumCorners[1], frustumCorners[6]); // right
        setClippingPlane(4, frustumCorners[2], frustumCorners[3], frustumCorners[6]); // top
        setClippingPlane(5, frustumCorners[4], frustumCorners[0], frustumCorners[1]); // bottom
    }

    public boolean intersectsSphere(final Vector3 center, final float r) {
        for (int i = 0; i < 6; i++) {
            float signedDistance = normals[i].x * center.x + normals[i].y * center.y + normals[i].z * center.z + ds[i];
            float diff = signedDistance + r;
            if (diff < 0) return false;
        }
        return true;
    }

    private void setClippingPlane(int i, Vector3 point1, Vector3 point2, Vector3 point3) {
        this.normals[i].set(point1).sub(point2).crs(point2.x - point3.x, point2.y - point3.y, point2.z - point3.z).nor();
        this.ds[i] = -1 * Vector3.dot(point1, this.normals[i]);
    }

}