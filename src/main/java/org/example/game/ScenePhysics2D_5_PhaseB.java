package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.graphics.Camera;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.input.InputMouse;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.MathVector3;
import org.example.engine.core.physics2d_new.Physics2DBody;
import org.example.engine.core.physics2d_new.Physics2DWorld;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class ScenePhysics2D_5_PhaseB extends ApplicationScreen {

    private Renderer2D renderer2D;
    private Camera camera;
    private Physics2DWorld world = new Physics2DWorld();
    private Physics2DBody body;


    public ScenePhysics2D_5_PhaseB() {
        renderer2D = new Renderer2D();
    }

    @Override
    public void show() {
        camera = new Camera(640f/64,480f/64, 1);
        camera.update();

        this.body = world.createBodyCircle(null, Physics2DBody.MotionType.LOGICAL,
                MathUtils.random() * 10 - 5,MathUtils.random() * 10 - 5,MathUtils.random() * 360,
                0f,0f,0,
                1, 1, 1, false, 1,
                1);
    }


    @Override
    protected void refresh() {
        world.update(GraphicsUtils.getDeltaTime());
        MathVector3 screen = new MathVector3(InputMouse.getCursorX(), InputMouse.getCursorY(), 0);
        camera.lens.unproject(screen);
        body.setPosition(screen.x, screen.y);

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.Q)) {
            world.createBodyCircle(null, Physics2DBody.MotionType.NEWTONIAN,
                    MathUtils.random() * 10 - 5,MathUtils.random() * 10 - 5,MathUtils.random() * 360,
                    0f,0f,0,
                    1, 1, 1, false, 1,
                    0.3f + MathUtils.random() * 0.3f);
        }

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.E)) {
            for (Physics2DBody body : world.allBodies) {
                body.applyForce(0.1f, 0.1f);
            }
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
