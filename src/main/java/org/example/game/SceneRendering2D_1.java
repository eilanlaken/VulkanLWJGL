package org.example.game;

import org.example.engine.ecs.Component;
import org.example.engine.ecs.ComponentGraphics2DShape;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.graphics.*;
import org.example.engine.core.memory.MemoryResource;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_1 extends WindowScreen {

    private Renderer2D renderer2D;
    private Texture texture0;
    private TextureRegion region;
    private Camera camera;

    private ComponentGraphics2DShape shape;
    private ComponentGraphics2DShape shape2;


    public SceneRendering2D_1() {
        renderer2D = new Renderer2D();
    }

    @Override
    public Map<String, Class<? extends MemoryResource>> getRequiredAssets() {
        Map<String, Class<? extends MemoryResource>> requiredAssets = new HashMap<>();

        requiredAssets.put("assets/atlases/pack2_0.png", Texture.class);

        return requiredAssets;
    }

    @Override
    public void show() {
        texture0 = AssetStore.get("assets/atlases/pack2_0.png");
        region = new TextureRegion(texture0, 331, 25, 207, 236, 126,126, 400,400);

        // TODO: bug here: something is not right with the renderer: buffer limits etc.
        //shape = FactoryComponent.createShapeCircleHollow(200, 501, 30, new Color(1,0,1,1), null, null);
        //shape = FactoryComponent.createShapeCircleHollow(200, 500, 30, new Color(1,0,1,1), null, null);
        //shape = FactoryComponent.createShapeLine(0, 0, 100, 0, 2, new Color(1,0,1,1), null, null);
        //shape = FactoryComponent.createShapeRectangleFilled(100, 30, new Color(1,0,1,1), null, null);

        shape = Component.createShapeCircleFilled(30, 1500, new Color(0,0.5f,1,1), null, null);
        shape2 = Component.createShapeCircleFilled(30, 501, new Color(0,0.5f,1,1), null, null);

        camera = new Camera(640*2,480*2, 1);
        camera.update();
    }

    float time = 0;
    @Override
    protected void refresh() {
//        GL11.glViewport(0,0, 640, 480);
//        GL11.glEnable(GL11.GL_SCISSOR_TEST);
//        GL11.glScissor(200, 0, 640, 480);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,0);
        renderer2D.begin(camera);
        //renderer2D.pushTexture(texture0, new Color(1,1,1,1f), 0,0,1,1,0,0,256,256,256,256,0,0,0, 1, 1,null,null);
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                renderer2D.pushTextureRegion(region, new Color(1,1,1,1),-350 + i*10,350 - j*10,0,0,0,0.2f,0.2f,null,null);
            }
        }
        renderer2D.pushPolygon(shape.polygon, shape.tint, 0,0,0,0,0,1,1,null,null);
        renderer2D.pushPolygon(shape2.polygon, shape.tint,100,0,0,0,0,1,1,null,null);

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
