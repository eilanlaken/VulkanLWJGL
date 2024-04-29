package org.example.game;

import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.graphics.*;
import org.example.engine.core.memory.MemoryResource;
import org.example.engine.core.shape.*;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SceneRendering2D_5 extends ApplicationScreen {

    private GraphicsRenderer2D renderer2D;
    private GraphicsCamera camera;

    private Shape2DCircle circle;
    private Shape2DRectangle rectangle;
    private Shape2DAABB aabb;
    private Shape2DSegment segment;
    private Shape2DPolygon polygon;
    private Shape2DUnion compound;

    private GraphicsTexture texture0;
    private GraphicsTextureRegion region;

    private GraphicsShaderProgram custom = new GraphicsShaderProgram(
            AssetUtils.getFileContent("assets/shaders/black-silhouette.vert"),
            AssetUtils.getFileContent("assets/shaders/black-silhouette.frag"));


    public SceneRendering2D_5() {
        renderer2D = new GraphicsRenderer2D();
    }

    @Override
    public Map<String, Class<? extends MemoryResource>> getRequiredAssets() {
        Map<String, Class<? extends MemoryResource>> requiredAssets = new HashMap<>();

        requiredAssets.put("assets/atlases/pack2_0.png", GraphicsTexture.class);

        return requiredAssets;
    }

    @Override
    public void show() {
        texture0 = AssetStore.get("assets/atlases/pack2_0.png");
        region = new GraphicsTextureRegion(texture0, 331, 25, 207, 236, 126,126, 400,400);

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


        camera = new GraphicsCamera(640*2,480*2, 1);
        camera.update();
    }

    float time = 0;
    @Override
    protected void refresh() {

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);
        renderer2D.begin(camera);
        renderer2D.pushTextureRegion(region, new GraphicsColor(1,1,1,1),0,0,0,0,0,1f,1f,custom,null);

        //polygon.setRotation(time);
        //renderer2D.pushDebugShape(circle, null);
        //renderer2D.pushDebugShape(rectangle, null);
        //renderer2D.pushDebugShape(aabb, null);
        //renderer2D.pushDebugShape(polygon, null);

        renderBounds(compound);



        renderer2D.end();
        time++;
    }

    private void renderBounds(Shape2D shape2D) {
        float r = shape2D.getBoundingRadius();
        Shape2DCircle bounds = new Shape2DCircle(r, shape2D.x(), shape2D.y());
        renderer2D.pushDebugShape(bounds,new GraphicsColor(1,1,0,1));
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
