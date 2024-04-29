package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.graphics.GraphicsCamera;
import org.example.engine.core.graphics.GraphicsColor;
import org.example.engine.core.graphics.GraphicsRenderer2D;
import org.example.engine.core.math.MathUtils;
import org.example.engine.core.memory.MemoryResource;
import org.example.engine.ecs.Component;
import org.example.engine.ecs.ComponentGraphics2DShape;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_8 extends ApplicationScreen {

    private GraphicsRenderer2D renderer2D;
    private GraphicsCamera camera;

    private ComponentGraphics2DShape shape;
    private ComponentGraphics2DShape shape2;
    private ComponentGraphics2DShape shape3;


    public SceneRendering2D_8() {
        renderer2D = new GraphicsRenderer2D();
    }

    @Override
    public Map<String, Class<? extends MemoryResource>> getRequiredAssets() {
        Map<String, Class<? extends MemoryResource>> requiredAssets = new HashMap<>();

        return requiredAssets;
    }

    @Override
    public void show() {

        shape = Component.createShapeCircleFilled(30, 400, 0,90, new GraphicsColor(0,0.5f,1,1), null, null);
        shape2 = Component.createShapeCircleHollow(300, 50, 4,0, 30, new GraphicsColor(0,0.5f,1,1), null, null);
        shape3 = Component.createShapeCurve(-300, 300, 20,100, x -> 400 * MathUtils.sinDeg(x), new GraphicsColor(0,0.5f,1,1), null, null);

        camera = new GraphicsCamera(640*2,480*2, 1);
        camera.update();
    }

    float time = 0;
    @Override
    protected void refresh() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,0);
        renderer2D.begin(camera);
        //renderer2D.pushPolygon(shape.polygon, shape.tint, 0,0,0,0,0,1,1,null,null);
        //renderer2D.pushPolygon(shape2.polygon, shape.tint, 0,0,0,0,0,1,1,null,null);
        renderer2D.pushPolygon(shape3.polygon, shape.tint, 0,0,0,0,0,1,1,null,null);
        renderer2D.end();
        time++;
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
