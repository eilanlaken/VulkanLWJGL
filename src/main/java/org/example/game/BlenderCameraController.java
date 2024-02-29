package org.example.game;

import org.example.engine.core.graphics.Camera;
import org.example.engine.core.graphics.CameraLens;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.input.Mouse;
import org.example.engine.core.math.Vector3;

public class BlenderCameraController {

    public CameraLens lens;
    private Vector3 zoom;
    private Vector3 pan;


    public BlenderCameraController(Camera camera) {
        this.lens = camera.lens;
        this.zoom = new Vector3();
        this.pan = new Vector3();
    }

    public void update(float deltaTime) {
        if (Mouse.getVerticalScroll() != 0) zoom(deltaTime);
        else if (Keyboard.isKeyPressed(Keyboard.Key.LEFT_SHIFT) && Mouse.isButtonPressed(Mouse.Button.MIDDLE)) pan(deltaTime);
        else if (Mouse.isButtonPressed(Mouse.Button.MIDDLE)) rotate(deltaTime);
        lens.update();
    }

    private void zoom(float deltaTime) {
        zoom.set(lens.direction);
        zoom.scl(100 * Mouse.getVerticalScroll() * deltaTime);
        lens.position.add(zoom);
    }

    private void pan(float deltaTime) {
        pan.set(0,0,0);
        Vector3 horizontalPan = new Vector3(lens.left);
        horizontalPan.scl(Mouse.getCursorDeltaX());
        pan.add(horizontalPan);
        Vector3 verticalPan = new Vector3(lens.up);
        verticalPan.scl(Mouse.getCursorDeltaY());
        pan.add(verticalPan);
        pan.scl(deltaTime);
        lens.position.add(pan);
    }

    private void rotate(float deltaTime) {
//        lens.direction.rotate(lens.up, 100 * deltaTime * Mouse.getCursorDeltaX());
//        lens.direction.rotate(lens.left, 100 * deltaTime * Mouse.getCursorDeltaX());
        Vector3 around = new Vector3();
        around.add(lens.direction).scl(44).add(lens.position);

        lens.rotateAround(lens.position,lens.up, -Mouse.getCursorDeltaX());
        lens.rotateAround(lens.position,lens.left, -Mouse.getCursorDeltaY());

    }

}
