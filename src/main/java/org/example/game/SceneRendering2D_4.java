package org.example.game;

import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.graphics.*;
import org.example.engine.core.input.InputKeyboard;
import org.example.engine.core.memory.MemoryResource;
import org.example.engine.core.shape.*;
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
    private Shape2DComposite compound;

    private Texture texture0;
    private TextureRegion region;

    private Shape2DCircle bounds;

    public SceneRendering2D_4() {
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

        circle = new Shape2DCircle(150, 100,100);
        //circle.update();

        rectangle = new Shape2DRectangle(200,300);
        rectangle = new Shape2DRectangle(200,-100,200,300,-45);
        //rectangle.angle(30);
        //rectangle.update();

        aabb = new Shape2DAABB(400,400, 500, 500);
        //aabb.update();

        segment = new Shape2DSegment(0,0,150,200);
        segment.update();

        polygon = new Shape2DPolygon(new float[] {0,0, 200,0, 100,200, -300,200, -400,100});


        CollectionsArray<Shape2D> islands = new CollectionsArray<>();
        islands.add(circle);
        islands.add(polygon);

        compound = new Shape2DComposite(islands);
        bounds = new Shape2DCircle(compound.getBoundingRadius(), compound.x(), compound.y());

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
        renderer2D.pushDebugShape(compound, null);
        //renderer2D.pushDebugShape(polygon, null);

        renderBounds(compound);



        renderer2D.end();
        time++;

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



}
