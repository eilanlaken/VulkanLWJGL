package org.example.engine.core.graphics;

import org.example.engine.core.math.MathMatrix4;
import org.example.engine.core.math.MathQuaternion;
import org.example.engine.core.math.MathVector3;

public class GraphicsCamera {

    private final MathVector3 tmp = new MathVector3();
    public MathVector3 position;
    public MathVector3 direction;
    public MathVector3 up;
    public MathVector3 left;
    public GraphicsCameraLens lens;

    public GraphicsCamera(float viewportWidth, float viewportHeight, float zoom, float near, float far, float fov) {
        this.position = new MathVector3(0,0,0);
        this.direction = new MathVector3(0,0,-1);
        this.up = new MathVector3(0,1,0);
        this.left = new MathVector3();
        this.lens = new GraphicsCameraLens(GraphicsCameraLens.Mode.ORTHOGRAPHIC, viewportWidth, viewportHeight, zoom, near, far, fov);
        update();
    }

    public GraphicsCamera(float viewportWidth, float viewportHeight, float zoom) {
        this.position = new MathVector3(0,0,0);
        this.direction = new MathVector3(0,0,-1);
        this.up = new MathVector3(0,1,0);
        this.left = new MathVector3();
        this.lens = new GraphicsCameraLens(GraphicsCameraLens.Mode.ORTHOGRAPHIC, viewportWidth, viewportHeight, zoom, 0.1f, 100, 70);
    }

    public GraphicsCamera update() {
        left.set(up).crs(direction);
        lens.update(position, direction, up);
        return this;
    }

    public GraphicsCamera update(float viewportWidth, float viewportHeight) {
        lens.viewportWidth  = viewportWidth;
        lens.viewportHeight = viewportHeight;
        return update();
    }

    public void switchToOrthographicMode() {
        lens.mode = GraphicsCameraLens.Mode.ORTHOGRAPHIC;
    }

    public void switchToPerspectiveMode() {
        lens.mode = GraphicsCameraLens.Mode.PERSPECTIVE;
    }

    public void lookAt(float x, float y, float z) {
        tmp.set(x, y, z).sub(position).nor();
        if (tmp.isZero()) return;
        float dot = MathVector3.dot(tmp, up);
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

    public void rotate(MathVector3 axis, float angle) {
        direction.rotate(axis, angle);
        up.rotate(axis, angle);
    }

    public void rotate(final MathMatrix4 transform) {
        direction.rot(transform);
        up.rot(transform);
    }

    public void rotate(final MathQuaternion q) {
        q.transform(direction);
        q.transform(up);
    }

    public void rotateAround(MathVector3 point, MathVector3 axis, float angle) {
        tmp.set(point);
        tmp.sub(position);
        translate(tmp);
        rotate(axis, angle);
        tmp.rotate(axis, angle);
        translate(-tmp.x, -tmp.y, -tmp.z);
    }

    public void transform(final MathMatrix4 transform) {
        position.mul(transform);
        rotate(transform);
    }

    public void translate(float x, float y, float z) {
        position.add(x, y, z);
    }

    public void translate(MathVector3 vec) {
        position.add(vec);
    }

}
