package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.graphics.Camera;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.input.Keyboard;
import org.example.engine.core.input.Mouse;
import org.example.engine.core.math.Vector3;
import org.example.engine.core.physics2d.Body;
import org.example.engine.core.physics2d.World;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class ScenePhysics2D_Better_1 extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;
    private World world = new World();

    private Body body_a;
    private Body body_b;

    public ScenePhysics2D_Better_1() {
        renderer2D = new Renderer2D();
    }

    @Override
    public void show() {
        camera = new Camera(640f/32,480f/32, 1);
        camera.update();

        world.createBodyRectangle(null, Body.MotionType.STATIC,
                -2.5f, 4,-30,
                0f,0f,0,
                1000, 1, 1, 0.8f, false, 1,
                5, 0.5f, 0, 0, 0);

        world.createBodyRectangle(null, Body.MotionType.STATIC,
                2.5f, 0f,30,
                0f,0f,0,
                1000, 1, 1, 0.8f, false, 1,
                5, 0.5f, 0, 0, 0);

        world.createBodyRectangle(null, Body.MotionType.STATIC,
                0, -5,0,
                0f,0f,0,
                1000, 1, 1, 0.8f, false, 1,
                10, 0.5f, 0, 0, 0);

        world.createForceField((body, force) -> { force.set(0, -9.8f / body.invM); });
    }


    @Override
    protected void refresh() {
        world.update(GraphicsUtils.getDeltaTime());
        Vector3 screen = new Vector3(Mouse.getCursorX(), Mouse.getCursorY(), 0);
        camera.lens.unproject(screen);
        world.castRay((intersections) -> {

        }, 0,0, screen.x, screen.y, Float.POSITIVE_INFINITY);

        if (Keyboard.isKeyJustPressed(Keyboard.Key.A)) {
            body_a = world.createBodyCircle(null, Body.MotionType.NEWTONIAN,
                    screen.x, screen.y, 0,
                    0f, 0f, 0,
                    1, 1, 1,0.2f, false, 1,
                    0.5f);
        }

        if (Keyboard.isKeyJustPressed(Keyboard.Key.S)) {
            body_b = world.createBodyRectangle(null, Body.MotionType.NEWTONIAN,
                    screen.x, screen.y, -30,
                    0f, 0f, 0,
                    1, 1, 1, 0.2f, false, 1,
                    1, 1f, 0, 0, 0);
        }

        if (Keyboard.isKeyPressed(Keyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (Keyboard.isKeyPressed(Keyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);

        renderer2D.begin(camera);
        world.render(renderer2D);
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
