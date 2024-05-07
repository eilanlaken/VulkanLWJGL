package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.graphics.GraphicsCamera;
import org.example.engine.core.graphics.GraphicsRenderer2D;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.input.InputMouse;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector3;
import org.example.engine.core.physics2d_new.Physics2DBody;
import org.example.engine.core.physics2d_new.Physics2DWorld;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class ScenePhysics2D_5_PhaseE_1 extends ApplicationScreen {

    private GraphicsRenderer2D renderer2D;
    private GraphicsCamera camera;
    private Physics2DWorld world = new Physics2DWorld();

    public ScenePhysics2D_5_PhaseE_1() {
        renderer2D = new GraphicsRenderer2D();
    }

    @Override
    public void show() {
        camera = new GraphicsCamera(640f/32,480f/32, 1);
        camera.update();
    }


    @Override
    protected void refresh() {
        world.update(GraphicsUtils.getDeltaTime());
        MathVector3 screen = new MathVector3(InputMouse.getCursorX(), InputMouse.getCursorY(), 0);
        camera.lens.unproject(screen);

        if (InputKeyboard.isKeyJustPressed(InputKeyboard.Key.E)) {
            screen.set(InputMouse.getCursorX(), InputMouse.getCursorY(), 0);
            camera.lens.unproject(screen);
            world.createBodyCircle(null, Physics2DBody.MotionType.NEWTONIAN,
                    screen.x,screen.y,0,
                    0f,0f,0,
                    1, 1, 1, false, 1,
                    1);
        }

        if (InputKeyboard.isKeyJustPressed(InputKeyboard.Key.Q)) {
            screen.set(InputMouse.getCursorX(), InputMouse.getCursorY(), 0);
            camera.lens.unproject(screen);
            world.createBodyCircle(null, Physics2DBody.MotionType.NEWTONIAN,
                    screen.x,screen.y,0,
                    MathUtils.random(),MathUtils.random(),0,
                    1, 1, 1, false, 1,
                    1);
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
