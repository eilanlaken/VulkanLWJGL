package org.example.game;

import org.example.engine.components.ComponentGraphics2DShape;
import org.example.engine.components.FactoryComponent;
import org.example.engine.core.graphics.Camera;
import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.graphics.WindowScreen;
import org.example.engine.core.math.Shape2DRectangle;
import org.lwjgl.opengl.GL11;

public class ScenePhysics2D_2 extends WindowScreen {

    private Renderer2D renderer2D;
    private Camera camera;
    private ComponentGraphics2DShape shape;

    public ScenePhysics2D_2() {
        renderer2D = new Renderer2D();
    }

    @Override
    public void show() {
        shape = FactoryComponent.createShapeCircleHollow(30, 30,3, new Color(0,0.5f,1,1), null, null);
        camera = new Camera(640*2,480*2, 1);
        camera.update();
    }

    float time = 0;
    @Override
    protected void refresh() {

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,0);
        renderer2D.begin(camera);
        renderer2D.pushShape(shape.polygon, shape.tint, 0,0,0,0,0,1,1,null,null);
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
