package org.example.engine.core.graphics;

import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Quaternion;
import org.example.engine.core.math.Shape3DFrustum;
import org.example.engine.core.math.Vector3;

public class CameraLens_old {

    private final Vector3[] clipSpacePlanePoints = { // This is the clipping volume - a cube with 8 corners: (+-1, +-1, +-1)
            new Vector3(-1, -1, -1), new Vector3(1, -1, -1), new Vector3(1, 1, -1), new Vector3(-1, 1, -1), // near clipping plane corners
            new Vector3(-1, -1, 1), new Vector3(1, -1, 1), new Vector3(1, 1, 1), new Vector3(-1, 1, 1), // far clipping plane corners
    };
    private Vector3[] frustumCorners = {
            new Vector3(), new Vector3(), new Vector3(), new Vector3(), // near frustum plane corners
            new Vector3(), new Vector3(), new Vector3(), new Vector3(), // far frustum plane corners
    };
    private final Vector3 tmp = new Vector3();

    private CameraLensProjectionType projectionType;
    public Matrix4 projection = new Matrix4();
    public Matrix4 view = new Matrix4();
    public Matrix4 combined = new Matrix4();
    public Matrix4 invProjectionView = new Matrix4();
    public float near = 0.1f;
    public float far = 100;
    public float fieldOfView = 67;
    public float zoom = 1;
    public float viewportWidth = GraphicsUtils.getWindowWidth();
    public float viewportHeight = GraphicsUtils.getWindowHeight();
    public Vector3 position;
    public Vector3 direction;
    public Vector3 up;
    public Vector3 left;
    public Shape3DFrustum frustum;

    // TODO: change
    public CameraLens_old(CameraLensProjectionType type) {
        this.projectionType = type;
        this.position = new Vector3(0,0,0);
        this.direction = new Vector3(0,0,-1);
        this.up = new Vector3(0,1,0);
        this.left = new Vector3(this.up);
        this.left.crs(this.direction);
        this.frustum = new Shape3DFrustum();
        update();
    }

    @Deprecated
    public CameraLens_old() {
        this.projectionType = CameraLensProjectionType.PERSPECTIVE_PROJECTION;
        this.position = new Vector3(0,0,0);
        this.direction = new Vector3(0,0,-1);
        this.up = new Vector3(0,1,0);
        this.left = new Vector3(this.up);
        this.left.crs(this.direction);
        this.frustum = new Shape3DFrustum();
        update();
    }

    public void update() {
        switch (projectionType) {
            case PERSPECTIVE_PROJECTION: {
                float aspect = viewportWidth / viewportHeight;
                projection.setToPerspectiveProjection(Math.abs(near), Math.abs(far), fieldOfView, aspect);
                view.setToLookAt(position, tmp.set(position).add(direction), up);
                combined.set(projection);
                Matrix4.mul(combined.val, view.val);
                invProjectionView.set(combined);
                Matrix4.inv(invProjectionView.val);
                left.set(up).crs(direction);
            }
            case ORTHOGRAPHIC_PROJECTION: {
                // TODO: implement
            }
        }
        updateFrustum(invProjectionView);
    }

    private void updateFrustum(final Matrix4 invPrjView) {
        // calculate corners of the frustum by un-projecting the clip space cube using invPrjView
        for (int i = 0; i < 8; i++) {
            frustumCorners[i].set(clipSpacePlanePoints[i]);
            frustumCorners[i].prj(invPrjView);
        }
        frustum.set(frustumCorners);
    }

    public void lookAt(float x, float y, float z) {
        tmp.set(x, y, z).sub(position).nor();
        if (!tmp.isZero()) {
            float dot = Vector3.dot(tmp, up); // up and direction must ALWAYS be orthonormal vectors
            if (Math.abs(dot - 1) < 0.000000001f) {
                // Collinear
                up.set(direction).scl(-1);
            } else if (Math.abs(dot + 1) < 0.000000001f) {
                // Collinear opposite
                up.set(direction);
            }
            direction.set(tmp);
            normalizeUp();
            left.set(up).crs(direction);
        }
    }

    public void lookAt(Vector3 target) {
        lookAt(target.x, target.y, target.z);
    }

    public void normalizeUp() {
        tmp.set(direction).crs(up);
        up.set(tmp).crs(direction).nor();
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
        direction.rot(transform);
        up.rot(transform);
    }

    public void rotate(final Quaternion q) {
        q.transform(direction);
        q.transform(up);
    }

    public void rotateAround(Vector3 point, Vector3 axis, float angle) {
        tmp.set(point);
        tmp.sub(position);
        translate(tmp);
        rotate(axis, angle);
        tmp.rotate(axis, angle);
        translate(-tmp.x, -tmp.y, -tmp.z);
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

    public Vector3 unproject(Vector3 screenCoordinates, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
        float x = screenCoordinates.x - viewportX, y = GraphicsUtils.getWindowHeight() - screenCoordinates.y - viewportY;
        screenCoordinates.x = (2 * x) / viewportWidth - 1;
        screenCoordinates.y = (2 * y) / viewportHeight - 1;
        screenCoordinates.z = 2 * screenCoordinates.z - 1;
        screenCoordinates.prj(invProjectionView);
        return screenCoordinates;
    }

    public Vector3 unproject(Vector3 screenCoordinates) {
        unproject(screenCoordinates, 0, 0, GraphicsUtils.getWindowWidth(), GraphicsUtils.getWindowHeight());
        return screenCoordinates;
    }

    public Vector3 project(Vector3 worldCoordinates) {
        project(worldCoordinates, 0, 0, GraphicsUtils.getWindowWidth(), GraphicsUtils.getWindowHeight());
        return worldCoordinates;
    }

    public Vector3 project(Vector3 worldCoordinates, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
        worldCoordinates.prj(combined);
        worldCoordinates.x = viewportWidth * (worldCoordinates.x + 1) / 2 + viewportX;
        worldCoordinates.y = viewportHeight * (worldCoordinates.y + 1) / 2 + viewportY;
        worldCoordinates.z = (worldCoordinates.z + 1) / 2;
        return worldCoordinates;
    }

}
