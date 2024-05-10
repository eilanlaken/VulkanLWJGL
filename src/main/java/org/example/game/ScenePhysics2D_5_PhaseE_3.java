package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.graphics.GraphicsCamera;
import org.example.engine.core.graphics.GraphicsRenderer2D;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.input.InputMouse;
import org.example.engine.core.math.MathVector3;
import org.example.engine.core.physics2d.Physics2DBody;
import org.example.engine.core.physics2d.Physics2DWorld;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class ScenePhysics2D_5_PhaseE_3 extends ApplicationScreen {

    private GraphicsRenderer2D renderer2D;
    private GraphicsCamera camera;
    private Physics2DWorld world = new Physics2DWorld();
    Physics2DBody body1;
    Physics2DBody body2;


    public ScenePhysics2D_5_PhaseE_3() {
        renderer2D = new GraphicsRenderer2D();
    }

    @Override
    public void show() {
        camera = new GraphicsCamera(640f/128,480f/128, 1);
        camera.update();

        body1 = world.createBodyRectangle(null, Physics2DBody.MotionType.NEWTONIAN,
                0,0,0,
                0f,0f,0,
                1, 1, 1, false, 1,
                1, 1, 0);

        body2 = world.createBodyRectangle(null, Physics2DBody.MotionType.NEWTONIAN,
                0,0,0,
                0f,0f,0,
                1, 1, 1, false, 1,
                1, 1, 0);
    }


    @Override
    protected void refresh() {
        MathVector3 screen = new MathVector3(InputMouse.getCursorX(), InputMouse.getCursorY(), 0);
        camera.lens.unproject(screen);
        body1.setPosition(screen.x, screen.y);
        world.update(GraphicsUtils.getDeltaTime());

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.E)) {
            body1.shape.rot(3);
        }

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.Q)) {
            body1.shape.rot(-3);
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
