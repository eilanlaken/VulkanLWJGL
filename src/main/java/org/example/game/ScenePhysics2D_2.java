package org.example.game;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.*;
import org.example.engine.core.math.Shape2D;
import org.example.engine.core.math.Shape2DCircle;
import org.example.engine.core.math.Vector2;
import org.example.engine.core.physics2d.Physics2DWorld;
import org.lwjgl.opengl.GL11;

public class ScenePhysics2D_2 extends WindowScreen {

    private Renderer2D renderer2D;
    private Camera camera;

    private Shape2D circle;
    private Shape2D other;
    private Color staleTint = new Color(1,0,0,1);

    Physics2DWorld world = new Physics2DWorld();

    public ScenePhysics2D_2() {
        renderer2D = new Renderer2D();
    }

    @Override
    public void show() {
        circle = new Shape2DCircle(1);
        other = new Shape2DCircle(1.5f,0,0);
        other.xy(3, 1.5f);
        other.update();

        camera = new Camera(640f/64,480f/64, 1);
        camera.update();

        world.createBody(circle, new Vector2(0,0), new Vector2(0.01f, 0));
    }


    @Override
    protected void refresh() {

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);
        renderer2D.begin(camera);

        renderer2D.pushDebugShape(circle, null);
        renderer2D.pushDebugShape(other, staleTint);

        renderer2D.end();

        world.update(GraphicsUtils.getDeltaTime());

//
//        float dx = 0;
//        float dy = 0;
//        if (Keyboard.isKeyPressed(Keyboard.Key.A)) dx -= 0.1f;
//        if (Keyboard.isKeyPressed(Keyboard.Key.D)) dx += 0.1f;
//        if (Keyboard.isKeyPressed(Keyboard.Key.W)) dy += 0.1f;
//        if (Keyboard.isKeyPressed(Keyboard.Key.S)) dy -= 0.1f;
//        circle.dx(dx);
//        circle.dy(dy);


    }


    private void renderBounds(Shape2D shape2D) {
        float r = shape2D.getBoundingRadius();
        Shape2DCircle bounds = new Shape2DCircle(r, shape2D.x(), shape2D.y());
        renderer2D.pushDebugShape(bounds,new Color(1,1,0,1));
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


    public static class Cell {

        public static float CELL_SIZE = 10;
        Array<Shape2D> shapes = new Array<>();

    }

}
