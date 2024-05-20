package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.graphics.GraphicsCamera;
import org.example.engine.core.graphics.GraphicsRenderer2D;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.input.InputMouse;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector3;
import org.example.engine.core.physics2d_new.Physics2DBody;
import org.example.engine.core.physics2d_new.Physics2DWorld;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class ScenePhysics2D_5_PhaseE_19 extends ApplicationScreen {

    private GraphicsRenderer2D renderer2D;
    private GraphicsCamera camera;
    private Physics2DWorld world = new Physics2DWorld();

    public ScenePhysics2D_5_PhaseE_19() {
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
        //body2.shape.xy(screen.x, screen.y);

        if (InputMouse.isButtonPressed(InputMouse.Button.LEFT)) {

            world.createBodyCircle(null, Physics2DBody.MotionType.NEWTONIAN,
                    screen.x + 0.9f,screen.y + 0.9f,0,
                    0f,0f,0,
                    1, 1, 1, false, 1,
                    1, 0, 0);
            world.createBodyCircle(null, Physics2DBody.MotionType.NEWTONIAN,
                    screen.x - 0.9f,screen.y + 0.9f,0,
                    0f,0f,0,
                    1, 1, 1, false, 1,
                    1, 0, 0);
            world.createBodyCircle(null, Physics2DBody.MotionType.NEWTONIAN,
                    screen.x + 0.9f,screen.y - 0.9f,0,
                    0f,0f,0,
                    1, 1, 1, false, 1,
                    1, 0, 0);
            world.createBodyRectangle(null, Physics2DBody.MotionType.NEWTONIAN,
                    1, 2,0,
                    0f,0f,0,
                    1, 1, 1, false, 1,
                    1, 1f, 0, 0, MathUtils.random() * 360);
        }

        if (InputMouse.isButtonClicked(InputMouse.Button.RIGHT)) {
            System.out.println("fps: " + GraphicsUtils.getFps());
            System.out.println("b count: " + world.allBodies.size);
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

    }

    @Override
    public void deleteAll() {
        world.shutdown();
        renderer2D.deleteAll();
    }

}
