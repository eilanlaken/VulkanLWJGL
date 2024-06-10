package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.*;
import org.example.engine.core.memory.MemoryResource;
import org.example.engine.core.shape.*;
import org.example.engine.ecs.ComponentGraphics2DShape;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_3 extends ApplicationScreen {

    private a_old_Renderer2D renderer2D;
    private Camera camera;
    private ComponentGraphics2DShape shape;

    private Shape2DCircle circle1;
    private Shape2DCircle circle2;

    private Shape2DRectangle rectangle;
    private Shape2DAABB aabb;
    private Shape2DSegment segment;
    private Shape2DPolygon polygon;

    private Texture texture0;
    private TextureRegion region;

    private Shape2DCircle bounds;

    public SceneRendering2D_3() {
        renderer2D = new a_old_Renderer2D();
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

        circle1 = new Shape2DCircle(150, 0,0);
        circle1.angle(30);
        circle1.update();

        circle2 = new Shape2DCircle(90, 0,0);
        circle2.angle(30);
        circle2.update();

        rectangle = new Shape2DRectangle(200,300);
        rectangle.angle(30);
        rectangle.update();

        aabb = new Shape2DAABB(40,40, 240, 240);
        aabb.update();

        segment = new Shape2DSegment(-100,-200,300,300);
        segment.update();

        polygon = new Shape2DPolygon(new float[] {0,0, 200,0, 100,200, -300,200, -400,100});
        polygon.angle(30);

        Array<Shape2D> islands = new Array<>();
        islands.add(circle1);
        islands.add(polygon);


        camera = new Camera(640*2,480*2, 1);
        camera.update();
    }

    float time = 0;
    @Override
    protected void refresh() {

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);
        renderer2D.begin(camera);
        renderer2D.pushTextureRegion(region, new Color(1,1,1,1),-350 + 10,10,0,0,0,0.2f,0.2f,null,null);

        //polygon.setRotation(time);
        //renderer2D.pushDebugShape(circle, null);
        //renderer2D.pushDebugShape(rectangle, null);
        //renderer2D.pushDebugShape(aabb, null);
        ////renderer2D.pushDebugShape(segment, null);
        //renderer2D.pushDebugShape(polygon, null);
        renderer2D.pushDebugShape(bounds, null);

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
        renderer2D.deleteAll();
    }



}
