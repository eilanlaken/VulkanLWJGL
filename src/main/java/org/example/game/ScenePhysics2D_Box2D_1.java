package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.graphics.Camera;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.input.InputMouse;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.math.Vector3;
import org.jbox2d.dynamics.Body;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class ScenePhysics2D_Box2D_1 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;

    public ScenePhysics2D_Box2D_1() {
        renderer2D = new Renderer2D();
        Body b;
    }

    @Override
    public void show() {
        camera = new Camera(640f/32,480f/32, 1);
        camera.update();


    }


    @Override
    protected void refresh() {

        Vector3 screen = new Vector3(InputMouse.getCursorX(), InputMouse.getCursorY(), 0);
        camera.lens.unproject(screen);

        if (InputMouse.isButtonClicked(InputMouse.Button.LEFT)) {

        }



        if (InputKeyboard.isKeyJustPressed(InputKeyboard.Key.S)) {
        }

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.R)) {
        }

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.SPACE)) {
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);

        renderer2D.begin(camera);
        renderer2D.end();
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void hide() {
        renderer2D.deleteAll();
    }

    @Override
    public void deleteAll() {

    }

}
