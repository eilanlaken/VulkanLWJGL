package org.example.engine.core.graphics;

import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Quaternion;
import org.example.engine.core.math.Vector3;

public class Camera {

    private final Vector3 tmp = new Vector3();
    public Vector3 position;
    public Vector3 direction;
    public Vector3 up;
    public Vector3 left;
    public CameraLens lens;

    public Camera(float viewportWidth, float viewportHeight, float zoom, float near, float far, float fov) {
        this.position = new Vector3(0,0,0);
        this.direction = new Vector3(0,0,-1);
        this.up = new Vector3(0,1,0);
        this.left = new Vector3();
        this.lens = new CameraLens(CameraLens.Mode.ORTHOGRAPHIC, viewportWidth, viewportHeight, zoom, near, far, fov);
        update();
    }

    public Camera(float viewportWidth, float viewportHeight, float zoom) {
        this.position = new Vector3(0,0,0);
        this.direction = new Vector3(0,0,-1);
        this.up = new Vector3(0,1,0);
        this.left = new Vector3();
        this.lens = new CameraLens(CameraLens.Mode.ORTHOGRAPHIC, viewportWidth, viewportHeight, zoom, 0.1f, 100, 70);
    }

    public void update() {
        left.set(up).crs(direction);
        lens.update(position, direction, up);
    }

    public void switchToOrthographicMode() {
        lens.mode = CameraLens.Mode.ORTHOGRAPHIC;
    }

    public void switchToPerspectiveMode() {
        lens.mode = CameraLens.Mode.PERSPECTIVE;
    }

    public void lookAt(float x, float y, float z) {
        tmp.set(x, y, z).sub(position).nor();
        if (tmp.isZero()) return;
        float dot = Vector3.dot(tmp, up);
        if (Math.abs(dot - 1) < 0.000000001f) up.set(direction).scl(-1);
        else if (Math.abs(dot + 1) < 0.000000001f) up.set(direction);
        direction.set(tmp);
        normalizeUp();
        left.set(up).crs(direction);
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

}
