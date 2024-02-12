package org.example.engine.core.graphics;

import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Quaternion;
import org.example.engine.core.math.Shape3DFrustum;
import org.example.engine.core.math.Vector3;

public class CameraLens {

    private final Vector3[] clipSpacePlanePoints = { // This is the clipping volume
            new Vector3(-1, -1, -1), new Vector3(1, -1, -1), new Vector3(1, 1, -1), new Vector3(-1, 1, -1), // near clipping plane corners
            new Vector3(-1, -1, 1), new Vector3(1, -1, 1), new Vector3(1, 1, 1), new Vector3(-1, 1, 1), // far clipping plane corners
    };
    private Vector3[] frustumCorners = {
            new Vector3(), new Vector3(), new Vector3(), new Vector3(), // near frustum plane corners
            new Vector3(), new Vector3(), new Vector3(), new Vector3(), // far frustum plane corners
    };
    private final Vector3 tmpVec = new Vector3();

    private CameraLensProjectionType projectionType;
    public final Matrix4 projection = new Matrix4();
    public final Matrix4 view = new Matrix4();
    public final Matrix4 combined = new Matrix4();
    public final Matrix4 invProjectionView = new Matrix4();
    public float near = 1;
    public float far = 100;
    public float viewportWidth = 0;
    public float viewportHeight = 0;
    public Vector3 position;
    public Vector3 direction;
    public Vector3 up;
    public Shape3DFrustum frustum;

    public CameraLens() {
        this.position = new Vector3(0,0,0);
        this.direction = new Vector3(0,0,-1);
        this.up = new Vector3(0,1,0);
        this.frustum = new Shape3DFrustum();
    }

    public void lookAt(float x, float y, float z) {
        tmpVec.set(x, y, z).sub(position).normalize();
        if (!tmpVec.isZero()) {
            float dot = Vector3.dot(tmpVec, up); // up and direction must ALWAYS be orthonormal vectors
            if (Math.abs(dot - 1) < 0.000000001f) {
                // Collinear
                up.set(direction).scl(-1);
            } else if (Math.abs(dot + 1) < 0.000000001f) {
                // Collinear opposite
                up.set(direction);
            }
            direction.set(tmpVec);
            normalizeUp();
        }
    }

    public void lookAt(Vector3 target) {
        lookAt(target.x, target.y, target.z);
    }

    public void normalizeUp() {
        tmpVec.set(direction).cross(up);
        up.set(tmpVec).cross(direction).normalize();
    }

    public void rotate(float angle, float axisX, float axisY, float axisZ) {
        direction.rotate(angle, axisX, axisY, axisZ);
        up.rotate(angle, axisX, axisY, axisZ);
    }

    public void rotate(Vector3 axis, float angle) {
        direction.rotate(axis, angle);
        up.rotate(axis, angle);
    }

    public void rotate(final Matrix4 transform) {
        direction.rotate(transform);
        up.rotate(transform);
    }

    public void rotate(final Quaternion q) {
        q.transform(direction);
        q.transform(up);
    }

    public void rotateAround (Vector3 point, Vector3 axis, float angle) {
        tmpVec.set(point);
        tmpVec.sub(position);
        translate(tmpVec);
        rotate(axis, angle);
        tmpVec.rotate(axis, angle);
        translate(-tmpVec.x, -tmpVec.y, -tmpVec.z);
    }

    public void transform(final Matrix4 transform) {
        position.mul(transform);
        rotate(transform);
    }

    public void translate(float x, float y, float z) {
        position.add(x, y, z);
    }

    public void translate(Vector3 vec) {
        position.add(vec);
    }

    // TODO: window get with and get height
//    public Vector3 unproject(Vector3 screenCoordinates, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
//        float x = screenCoordinates.x - viewportX, y = Gdx.graphics.getHeight() - screenCoordinates.y - viewportY;
//        screenCoordinates.x = (2 * x) / viewportWidth - 1;
//        screenCoordinates.y = (2 * y) / viewportHeight - 1;
//        screenCoordinates.z = 2 * screenCoordinates.z - 1;
//        screenCoordinates.project(invProjectionView);
//        return screenCoordinates;
//    }

    public void updateFrustum(final Matrix4 invPrjView) {
        // calculate corners of the frustum by un-projecting the clip space cube using invPrjView
        for (int i = 0; i < 8; i++) {
            Vector3.project(invPrjView, clipSpacePlanePoints[i], frustumCorners[i]);
        }
        frustum.set(frustumCorners);
    }

}
