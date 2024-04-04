package org.example.game;

import org.example.engine.ecs.Component;
import org.example.engine.ecs.ComponentGraphics2DShape;
import org.example.engine.core.graphics.Camera;
import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.graphics.WindowScreen;
import org.example.engine.core.math.Shape2DRectangle;
import org.lwjgl.opengl.GL11;

public class ScenePhysics2D_1 extends WindowScreen {

    private Renderer2D renderer2D;
    private Camera camera;
    private ComponentGraphics2DShape shape;

    private Shape2DRectangle rect;

    private ComponentGraphics2DShape c1;
    private ComponentGraphics2DShape c2;
    private ComponentGraphics2DShape c3;
    private ComponentGraphics2DShape c4;


    public ScenePhysics2D_1() {
        renderer2D = new Renderer2D();
    }

    @Override
    public void show() {
        shape = Component.createShapeCircleHollow(30, 30,3, new Color(0,0.5f,1,1), null, null);
        rect = new Shape2DRectangle(200, 50);
        camera = new Camera(640*2,480*2, 1);
        camera.update();

        c1 = Component.createShapeCircleFilled(5, 30, new Color(0,0.5f,1,1), null, null);
        c2 = Component.createShapeCircleFilled(5, 30, new Color(0,0.5f,1,1), null, null);
        c3 = Component.createShapeCircleFilled(5, 30, new Color(0,0.5f,1,1), null, null);
        c4 = Component.createShapeCircleFilled(5, 30, new Color(0,0.5f,1,1), null, null);

    }

    float time = 0;
    @Override
    protected void refresh() {
        rect.setRotation(time);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,0);
        renderer2D.begin(camera);
        renderer2D.pushPolygon(shape.polygon, shape.tint, 0,0,0,0,0,1,1,null,null);

        renderer2D.pushPolygon(c1.polygon, shape.tint, rect.c1().x, rect.c1().y,0,0,0,1,1,null,null);
        renderer2D.pushPolygon(c2.polygon, shape.tint, rect.c2().x, rect.c2().y,0,0,0,1,1,null,null);
        renderer2D.pushPolygon(c3.polygon, shape.tint, rect.c3().x, rect.c3().y,0,0,0,1,1,null,null);
        renderer2D.pushPolygon(c4.polygon, shape.tint, rect.c4().x, rect.c4().y,0,0,0,1,1,null,null);


        renderer2D.end();
        time++;
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void hide() {
        renderer2D.free();
    }

    @Override
    public void free() {

    }



}
