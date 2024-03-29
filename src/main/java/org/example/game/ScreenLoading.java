package org.example.game;

import org.example.engine.core.application.Application;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.graphics.WindowScreen;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class ScreenLoading extends WindowScreen {

    private WindowScreen screen = new SceneRendering2D_1();

    @Override
    public void show() {
        Map<String, Class<? extends Resource>> requiredAssets = screen.getRequiredAssets();
        for (Map.Entry<String, Class<? extends Resource>> requiredAsset : requiredAssets.entrySet()) {
            AssetStore.loadAsset(requiredAsset.getValue(), requiredAsset.getKey());
        }
    }

    @Override
    protected void refresh() {
        System.out.println(AssetStore.isLoaded("assets/models/cube-blue.fbx"));

        if (!AssetStore.isLoadingInProgress()) {
            System.out.println(AssetStore.isLoaded("assets/models/cube-blue.fbx"));
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
