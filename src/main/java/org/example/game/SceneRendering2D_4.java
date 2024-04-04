package org.example.game;

import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.*;
import org.example.engine.core.math.*;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_4 extends WindowScreen {

    private Renderer2D renderer2D;
    private Camera camera;

    private Shape2DCircle circle;
    private Shape2DRectangle rectangle;
    private Shape2DAABB aabb;
    private Shape2DSegment segment;
    private Shape2DPolygon polygon;
    private Shape2DCompound compound;

    private Texture texture0;
    private TextureRegion region;

    private Shape2DCircle bounds;

    public SceneRendering2D_4() {
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

        circle = new Shape2DCircle(150, 0,0);
        circle.setRotation(30);
        circle.setTranslation(250,100);
        circle.applyTransform();
        circle.setScale(0.3f,0.3f);
        //circle.update();

        rectangle = new Shape2DRectangle(200,300);
        rectangle.setRotation(30);
        rectangle.update();

        aabb = new Shape2DAABB(40,40, 240, 240);
        aabb.update();

        segment = new Shape2DSegment(-100,-200,300,300);
        segment.update();

        polygon = new Shape2DPolygon(new float[] {0,0, 200,0, 100,200, -300,200, -400,100});
        polygon.setRotation(30);
        polygon.setScale(-1.2f,1.3f);

        Array<Shape2D> islands = new Array<>();
        islands.add(circle);
        islands.add(polygon);

        Array<Shape2D> holes = new Array<>();
        holes.add(new Shape2DCircle(90, 0,0));

        compound = new Shape2DCompound(islands, holes);
        //compound.update();
        bounds = new Shape2DCircle(compound.getBoundingRadius(), compound.getX(), compound.getY());

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
        renderer2D.pushDebugShape(circle, null);
        //renderer2D.pushDebugShape(rectangle, null);
        //renderer2D.pushDebugShape(aabb, null);
        ////renderer2D.pushDebugShape(segment, null);
        //renderer2D.pushDebugShape(polygon, null);

        renderBounds(circle);


        renderer2D.end();
        time++;
    }

    private void renderBounds(Shape2D shape2D) {
        float r = shape2D.getBoundingRadius();
        System.out.println(r);
        Shape2DCircle bounds = new Shape2DCircle(r, shape2D.getX(), shape2D.getY());
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



}