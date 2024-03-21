package org.example.game;

import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.graphics.*;
import org.example.engine.core.memory.Resource;

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
    private Texture texture1;
    private Texture texture2;
    private Texture texture3;

    public SceneRendering2D_2() {
        renderer2D = new Renderer2D();
    }

    @Override
    public Map<String, Class<? extends Resource>> getRequiredAssets() {
        Map<String, Class<? extends Resource>> requiredAssets = new HashMap<>();
        requiredAssets.put("assets/textures/yellowSquare.png", Texture.class);
        requiredAssets.put("assets/textures/pattern2.png", Texture.class);
        requiredAssets.put("assets/textures/redGreenHalf.png", Texture.class);
        return requiredAssets;
    }

    @Override
    public void show() {
        texture1 = AssetStore.get("assets/textures/yellowSquare.png");
        texture2 = AssetStore.get("assets/textures/pattern2.png");
        texture3 = AssetStore.get("assets/textures/redGreenHalf.png");

    }


    @Override
    protected void refresh() {
        System.out.println("HEEELLLOOOOO");

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
