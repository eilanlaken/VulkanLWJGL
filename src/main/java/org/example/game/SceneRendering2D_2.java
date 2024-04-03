package org.example.game;

import org.example.engine.components.ComponentGraphics2DShape;
import org.example.engine.components.FactoryComponent;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.graphics.*;
import org.example.engine.core.math.Shape2DAABB;
import org.example.engine.core.math.Shape2DCircle;
import org.example.engine.core.math.Shape2DRectangle;
import org.example.engine.core.math.Shape2DSegment;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_2 extends WindowScreen {

    private Renderer2D renderer2D;
    private Camera camera;
    private ComponentGraphics2DShape shape;

    private Shape2DCircle circle;
    private Shape2DRectangle rectangle;
    private Shape2DAABB aabb;
    private Shape2DSegment segment;

    private Texture texture0;
    private TextureRegion region;


    public SceneRendering2D_2() {
        renderer2D = new Renderer2D();
    }

    @Override
    public Map<String, Class<? extends Resource>> getRequiredAssets() {
        Map<String, Class<? extends Resource>> requiredAssets = new HashMap<>();

        requiredAssets.put("assets/atlases/pack2_0.png", Texture.class);

        return requiredAssets;
    }

    @Override
    public void show() {
        texture0 = AssetStore.get("assets/atlases/pack2_0.png");
        region = new TextureRegion(texture0, 331, 25, 207, 236, 126,126, 400,400);

        shape = FactoryComponent.createShapeCircleHollow(30, 30,3, new Color(0,0.5f,1,1), null, null);
        shape = FactoryComponent.createShapePolygonFilled(new float[] {0,100, 0,0, 100,0, 200,200}, new Color(0,0.5f,1,1), null, null);
        shape = FactoryComponent.createShapePolygonHollow(new float[] {-50,50, -50,-50, 0,0}, 6, new Color(0,0.5f,1,1), null, null);
        //shape = FactoryComponent.createShapePolygonFilled(new float[] {-50,50, -50,-50, 50,-50, 50,50}, new Color(0,0.5f,1,1), null, null);
        //shape = FactoryComponent.createShapeRectangleHollow(30, 30,3, new Color(0,0.5f,1,1), null, null);

        circle = new Shape2DCircle(0,0,150);
        circle.setRotation(30);
        circle.update();

        rectangle = new Shape2DRectangle(200, 400);
        rectangle.setRotation(30);
        rectangle.update();

        aabb = new Shape2DAABB(40,40, 240, 240);
        aabb.update();

        segment = new Shape2DSegment(-100,-200,300,300);
        segment.update();
        System.out.println(segment);

        camera = new Camera(640*2,480*2, 1);
        camera.update();
    }

    float time = 0;
    @Override
    protected void refresh() {

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,0);
        renderer2D.begin(camera);
        renderer2D.pushTextureRegion(region, new Color(1,1,1,1),-350 + 10,10,0,0,0,0.2f,0.2f,null,null);

        circle.setRotation(time);
        renderer2D.pushPolygon(shape.polygon, shape.tint, 0,0,0,0,0,1,1,null,null);
        renderer2D.pushDebugShape(circle, null);
        renderer2D.pushDebugShape(rectangle, null);

        //renderer2D.pushDebugShape(rectangle, null);
        renderer2D.pushDebugShape(aabb, null);
        renderer2D.pushDebugShape(segment, null);


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
