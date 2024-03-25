package org.example.game;

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
public class SceneRendering2D_5 extends WindowScreen {

    private Renderer2D_4 renderer2D;
    private Texture texture0;
    private Camera camera;

    public SceneRendering2D_5() {
        renderer2D = new Renderer2D_4();
    }

    @Override
    public Map<String, Class<? extends Resource>> getRequiredAssets() {
        Map<String, Class<? extends Resource>> requiredAssets = new HashMap<>();

        requiredAssets.put("assets/textures/yellowSquare.png", Texture.class);

        return requiredAssets;
    }

    @Override
    public void show() {
        texture0 = AssetStore.get("assets/textures/yellowSquare.png");
        camera = new Camera(640*2,480*2, 1);
        camera.update();
    }


    @Override
    protected void refresh() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,1,0);

        renderer2D.begin(camera);
        renderer2D.pushTexture(texture0, new Color(1,1,1,1f), 0,0,1,1,0,0,256,256,256,256,0,0,0, 1, 1,null,null);
        renderer2D.end();
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
