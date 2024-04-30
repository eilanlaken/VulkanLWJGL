package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.graphics.GraphicsCamera;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.graphics.GraphicsRenderer2D;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.physics2d_new.Physics2DBody;
import org.example.engine.core.physics2d_new.Physics2DWorld;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class ScenePhysics2D_5_PhaseA extends ApplicationScreen {

    private GraphicsRenderer2D renderer2D;
    private GraphicsCamera camera;
    private Physics2DWorld world = new Physics2DWorld();

    public ScenePhysics2D_5_PhaseA() {
        renderer2D = new GraphicsRenderer2D();
    }

    @Override
    public void show() {
        camera = new GraphicsCamera(640f/64,480f/64, 1);
        camera.update();
    }


    @Override
    protected void refresh() {
        world.update(GraphicsUtils.getDeltaTime());
        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.Q)) {
            world.createBodyCircle(null, Physics2DBody.MotionType.NEWTONIAN,
                    MathUtils.random() * 10 - 5,MathUtils.random() * 10 - 5,MathUtils.random() * 360,
                    0,0,0,
                    1, 1, 1, false, 1,
                    1);
        }

        if (InputKeyboard.isKeyPressed(InputKeyboard.Key.E)) {
            world.destroyBody(world.allBodies.getCyclic(0));
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
