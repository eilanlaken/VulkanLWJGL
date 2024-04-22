package org.example.engine.core.graphics;

import org.example.engine.core.math.MathMatrix4;
import org.example.engine.core.math.MathVector3;
import org.example.engine.core.shape.Shape3DFrustum;

public class CameraLens {

    private final MathVector3 tmp = new MathVector3();
    protected Mode mode;
    protected MathMatrix4 projection;
    protected MathMatrix4 view;
    protected MathMatrix4 combined;
    protected MathMatrix4 invProjectionView;
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
        this.projection = new MathMatrix4();
        this.view = new MathMatrix4();
        this.combined = new MathMatrix4();
        this.invProjectionView = new MathMatrix4();
        this.frustum = new Shape3DFrustum();
    }

    public void update(MathVector3 position, MathVector3 direction, MathVector3 up) {
        switch (mode) {
            case ORTHOGRAPHIC -> projection.setToOrthographicProjection(zoom * -viewportWidth / 2.0f, zoom * (viewportWidth / 2.0f), zoom * -(viewportHeight / 2.0f), zoom * viewportHeight / 2.0f, 0, far);
            case PERSPECTIVE  -> this.projection.setToPerspectiveProjection(Math.abs(near), Math.abs(far), fov, viewportWidth / viewportHeight);
        }
        view.setToLookAt(position, tmp.set(position).add(direction), up);
        combined.set(projection);
        MathMatrix4.mul(combined.val, view.val);
        invProjectionView.set(combined);
        MathMatrix4.inv(invProjectionView.val);
        frustum.update(invProjectionView);
    }

    public MathVector3 unproject(MathVector3 screenCoordinates) {
        unproject(screenCoordinates, 0, 0, GraphicsUtils.getWindowWidth(), GraphicsUtils.getWindowHeight());
        return screenCoordinates;
    }

    public MathVector3 unproject(MathVector3 screenCoordinates, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
        float x = screenCoordinates.x - viewportX, y = GraphicsUtils.getWindowHeight() - screenCoordinates.y - viewportY;
        screenCoordinates.x = (2 * x) / viewportWidth - 1;
        screenCoordinates.y = (2 * y) / viewportHeight - 1;
        screenCoordinates.z = 2 * screenCoordinates.z - 1;
        screenCoordinates.prj(invProjectionView);
        return screenCoordinates;
    }

    public MathVector3 project(MathVector3 worldCoordinates) {
        project(worldCoordinates, 0, 0, GraphicsUtils.getWindowWidth(), GraphicsUtils.getWindowHeight());
        return worldCoordinates;
    }

    public MathVector3 project(MathVector3 worldCoordinates, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
        worldCoordinates.prj(combined);
        worldCoordinates.x = viewportWidth * (worldCoordinates.x + 1) / 2 + viewportX;
        worldCoordinates.y = viewportHeight * (worldCoordinates.y + 1) / 2 + viewportY;
        worldCoordinates.z = (worldCoordinates.z + 1) / 2;
        return worldCoordinates;
    }

    public float getViewportWidth() {
        return viewportWidth;
    }

    public float getViewportHeight() {
        return viewportHeight;
    }

    public enum Mode {

        ORTHOGRAPHIC,
        PERSPECTIVE,
        ;

    }
}
