package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.graphics.Camera;
import org.example.engine.core.graphics.a_old_Renderer2D;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.input.InputMouse;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.math.Vector3;
import org.example.engine.core.physics2d.Physics2DBody;
import org.example.engine.core.physics2d.Physics2DWorld;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class ScenePhysics2D_Rays_4 extends ApplicationScreen {

    private a_old_Renderer2D renderer2D;
    private Camera camera;
    private Physics2DWorld world = new Physics2DWorld();

    public ScenePhysics2D_Rays_4() {
        renderer2D = new a_old_Renderer2D();
    }

    @Override
    public void show() {
        camera = new Camera(640f/32,480f/32, 1);
        camera.update();

        for (int i = 0; i < 50; i++) {
            world.createBodyCircle(null, Physics2DBody.MotionType.STATIC,
                    20 * MathUtils.random() - 10, 20 * MathUtils.random() - 10, 360 * MathUtils.random(),
                    0f, 0f, 0,
                    1, 1, 1, 0.2f, false, 1,
                    0.5f);
        }

        for (int i = 0; i < 50; i++) {
            world.createBodyRectangle(null, Physics2DBody.MotionType.STATIC,
                    20 * MathUtils.random() - 10, 20 * MathUtils.random() - 10, 360 * MathUtils.random(),
                    0f, 0f, 0,
                    1, 1, 1, 0.2f, false, 1,
                    1 + 2 * MathUtils.random(), 1 + 2 * MathUtils.random(), 360 * MathUtils.random());
        }
    }


    @Override
    protected void refresh() {


        Vector3 screen = new Vector3(InputMouse.getCursorX(), InputMouse.getCursorY(), 0);
        camera.lens.unproject(screen);
        world.castRay((intersections) -> {

        }, 0,0, screen.x, screen.y, Float.POSITIVE_INFINITY);
        world.update(GraphicsUtils.getDeltaTime());

        if (InputMouse.isButtonClicked(InputMouse.Button.LEFT)) {

        }

        if (InputMouse.isButtonClicked(InputMouse.Button.RIGHT)) {

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
