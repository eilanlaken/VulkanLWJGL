package org.example.game;

import org.example.engine.components.ComponentGraphics2DShape;
import org.example.engine.components.FactoryComponent;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.graphics.*;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

// TODO: pretty good solution here:
//  TODO: http://forum.lwjgl.org/index.php?topic=5789.0
// TODO: orphaning - multi buffering:
// TODO: https://www.cppstories.com/2015/01/persistent-mapped-buffers-in-opengl/#persistence
// Note: glBufferData invalidates and reallocates the whole buffer. Use glBufferSubData to only update the data inside.
// https://stackoverflow.com/questions/72648980/opengl-sampler2d-array
// libGDX PolygonSpriteBatch.java line 772 draw()
public class SceneRendering2D_2 extends WindowScreen {

    private Renderer2D renderer2D;
    private Texture texture0;
    private Camera camera;

    private ComponentGraphics2DShape shape;

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

        shape = FactoryComponent.createShapeCircleFilled(200, 300, new Color(1,0,1,1), null, null);
        shape = FactoryComponent.createShapeRectangleHollow(200, 300, 10, new Color(1,0,1,1), null, null);

        // TODO: bug here: something is not right with the renderer: buffer limits etc.
        shape = FactoryComponent.createShapeCircleHollow(200, 300, 30, new Color(1,0,1,1), null, null);
        System.out.println("vertex array length: " + shape.polygon.localPoints.length);
        System.out.println("index array length: " + shape.polygon.indices.length);


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
        //renderer2D.pushTexture(region, new Color(1,1,1,1),time,0,0,0,0,1.5f,1.5f,null,null);
        renderer2D.pushShape(shape.polygon, shape.tint,0,0,0,0,0,1,1,null,null);

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
