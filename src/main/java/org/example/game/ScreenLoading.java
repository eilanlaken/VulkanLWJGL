package org.example.game;

import org.example.engine.core.application.Application;
import org.example.engine.core.application.ApplicationScreen;
import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.memory.MemoryResource;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class ScreenLoading extends ApplicationScreen {

    private ApplicationScreen screen = new SceneRendering2D_Shapes_6_capacity_bug();
    //private ApplicationScreen screen = new SceneRendering2D_3();
    //private WindowScreen screen = new SceneRendering2D_8();

    @Override
    public void show() {
        Map<String, Class<? extends MemoryResource>> requiredAssets = screen.getRequiredAssets();
        for (Map.Entry<String, Class<? extends MemoryResource>> requiredAsset : requiredAssets.entrySet()) {
            AssetStore.loadAsset(requiredAsset.getValue(), requiredAsset.getKey());
        }
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
    public void deleteAll() {

    }

}
