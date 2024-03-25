package org.example.game;

import org.example.engine.core.application.Application;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.graphics.WindowScreen;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class ScreenLoading extends WindowScreen {

    private WindowScreen screen = new SceneRendering2D_6();

    @Override
    public void show() {
        Map<String, Class<? extends Resource>> requiredAssets = screen.getRequiredAssets();
        for (Map.Entry<String, Class<? extends Resource>> requiredAsset : requiredAssets.entrySet()) {
            AssetStore.loadAsset(requiredAsset.getValue(), requiredAsset.getKey());
        }
        //AssetStore.loadAsset(Texture.class, "assets/textures/yellowSquare.png");
        //AssetStore.loadAsset(Texture.class, "assets/textures/pattern2.png");
        //AssetStore.loadAsset(Texture.class, "assets/textures/redGreenHalf.png");
        //AssetStore.loadAsset(Model.class, "assets/models/cube-blue.fbx");
    }

    @Override
    protected void refresh() {
        if (!AssetStore.isLoadingInProgress()) {
            Application.switchScreen(screen);
        }

        // frame update
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);
    }

    @Override
    public void resize(int width, int height) {

    }


    @Override
    public void hide() {

    }

    @Override
    public void free() {

    }

}
