package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.graphics.GraphicsCamera;
import org.example.engine.core.graphics.GraphicsRenderer2D;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.input.InputMouse;
import org.example.engine.core.math.MathVector2;
import org.example.engine.core.math.MathVector3;
import org.example.engine.core.physics2d.Physics2DBody;
import org.example.engine.core.physics2d.Physics2DWorld;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class ScenePhysics2D_3 extends ApplicationScreen {

    private GraphicsRenderer2D renderer2D;
    private GraphicsCamera camera;
    private Physics2DWorld world = new Physics2DWorld();

    private Physics2DBody body_a;
    private Physics2DBody body_b;

    public ScenePhysics2D_3() {
        renderer2D = new GraphicsRenderer2D();
    }

    @Override
    public void show() {
        camera = new GraphicsCamera(640f/32,480f/32, 1);
        camera.update();

        world.createBodyRectangle(null, Physics2DBody.MotionType.STATIC,
                -2.5f, 4,-30,
                0f,0f,0,
                1000, 1, 1, 0.8f, false, 1,
                5, 0.5f, 0, 0, 0);

        world.createBodyRectangle(null, Physics2DBody.MotionType.STATIC,
                2.5f, 0f,30,
                0f,0f,0,
                1000, 1, 1, 0.8f, false, 1,
                5, 0.5f, 0, 0, 0);

        world.createBodyRectangle(null, Physics2DBody.MotionType.STATIC,
                0, -5,0,
                0f,0f,0,
                1000, 1, 1, 0.8f, false, 1,
                10, 0.5f, 0, 0, 0);

        world.createForceField((body, force) -> { force.set(0, -9.8f / body.massInv); });
    }


    @Override
    protected void refresh() {
        world.update(GraphicsUtils.getDeltaTime());
        MathVector3 screen = new MathVector3(InputMouse.getCursorX(), InputMouse.getCursorY(), 0);
        camera.lens.unproject(screen);

        if (InputKeyboard.isKeyJustPressed(InputKeyboard.Key.A)) {
            body_a = world.createBodyCircle(null, Physics2DBody.MotionType.NEWTONIAN,
                    screen.x, screen.y, 0,
                    0f, 0f, 0,
                    1, 1, 1,0.2f, false, 1,
                    0.5f);
        }

        if (InputKeyboard.isKeyJustPressed(InputKeyboard.Key.S)) {
            body_b = world.createBodyRectangle(null, Physics2DBody.MotionType.NEWTONIAN,
                    screen.x, screen.y, -30,
                    0f, 0f, 0,
                    1, 1, 1, 0.2f, false, 1,
                    1, 1f, 0, 0, 0);
        }

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.R)) {
            body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.SPACE)) {
            world.createConstraintWeld(body_a, body_b, new MathVector2(1,0));
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
