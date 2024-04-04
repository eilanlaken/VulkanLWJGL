package org.example.engine.core.graphics;

import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Shape3DFrustum;
import org.example.engine.core.math.Vector3;

public class CameraLens {

    private final Vector3 tmp = new Vector3();
    protected Mode mode;
    protected Matrix4 projection;
    protected Matrix4 view;
    protected Matrix4 combined;
    protected Matrix4 invProjectionView;
    protected float near;
    protected float far;
    protected float fov;
    protected float zoom;
    protected float viewportWidth;
    protected float viewportHeight;
    protected final Shape3DFrustum frustum;

    public CameraLens(Mode mode, float viewportWidth, float viewportHeight, float zoom, float near, float far, float fov) {
        this.mode = mode;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.zoom = zoom;
        this.near = near;
        this.far = far;
        this.fov = fov;
        this.projection = new Matrix4();
        this.view = new Matrix4();
        this.combined = new Matrix4();
        this.invProjectionView = new Matrix4();
        this.frustum = new Shape3DFrustum();
    }

    public void update(Vector3 position, Vector3 direction, Vector3 up) {
        if (mode == Mode.ORTHOGRAPHIC) projection.setToOrthographicProjection(zoom * -viewportWidth / 2.0f, zoom * (viewportWidth / 2.0f), zoom * -(viewportHeight / 2.0f), zoom * viewportHeight / 2.0f, 0, Float.MAX_VALUE);
        else if (mode == Mode.PERSPECTIVE) this.projection.setToPerspectiveProjection(Math.abs(near), Math.abs(far), fov, viewportWidth / viewportHeight);
        view.setToLookAt(position, tmp.set(position).add(direction), up);
        combined.set(projection);
        Matrix4.mul(combined.val, view.val);
        invProjectionView.set(combined);
        Matrix4.inv(invProjectionView.val);
        frustum.update(invProjectionView);
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

    public enum Mode {

        ORTHOGRAPHIC,
        PERSPECTIVE,
        ;

    }
}
