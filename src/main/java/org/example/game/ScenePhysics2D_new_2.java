package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.graphics.Camera;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.graphics.a_old_Renderer2D_2;
import org.example.engine.core.input.InputMouse;
import org.example.engine.core.math.Vector3;
import org.example.engine.core.physics2d_new.Body;
import org.example.engine.core.physics2d_new.World;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class ScenePhysics2D_new_2 extends ApplicationScreen {

    private a_old_Renderer2D_2 renderer2D;
    private Camera camera;
    private World world = new World();

    private Body body_a;

    public ScenePhysics2D_new_2() {
        renderer2D = new a_old_Renderer2D_2();
    }

    @Override
    public void show() {
        camera = new Camera(640f/32,480f/32, 1);
        camera.update();

        body_a = world.createBodyRectangle(null, Body.MotionType.STATIC, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, false, 1, 3, 1, -2,2,30);

    }


    @Override
    protected void refresh() {
        world.update(GraphicsUtils.getDeltaTime());
        Vector3 screen = new Vector3(InputMouse.getCursorX(), InputMouse.getCursorY(), 0);
        camera.lens.unproject(screen);



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
