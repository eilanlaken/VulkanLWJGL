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
public class SceneRendering2D_2 extends WindowScreen {

    private Renderer2D_2 renderer2D;
    private Texture texture0;
    private Texture texture1;
    private Texture texture2;

    public SceneRendering2D_2() {
        renderer2D = new Renderer2D_2();
    }

    @Override
    public Map<String, Class<? extends Resource>> getRequiredAssets() {
        Map<String, Class<? extends Resource>> requiredAssets = new HashMap<>();
        requiredAssets.put("assets/textures/smile.png", Texture.class);
        requiredAssets.put("assets/textures/pattern2.png", Texture.class);
        requiredAssets.put("assets/textures/redGreenHalf.png", Texture.class);

        return requiredAssets;
    }

    @Override
    public void show() {
        texture0 = AssetStore.get("assets/textures/smile.png");
        texture1 = AssetStore.get("assets/textures/pattern2.png");
        texture2 = AssetStore.get("assets/textures/redGreenHalf.png");
    }


    @Override
    protected void refresh() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,1,0);

        renderer2D.begin(null);
        renderer2D.pushTexture(texture0, new Color(1,1,1,1f), 0,0,0,1,1,0,0,0,0,0,0,null,null);
        renderer2D.pushTexture(texture1, new Color(1,1,1,1f), 0,0,0,1,1,0,0,0,0,0,0,null,null);

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
