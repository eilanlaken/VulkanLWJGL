package org.example.game;

import org.example.engine.components.Component;
import org.example.engine.components.ComponentGraphicsCamera;
import org.example.engine.components.ComponentTransform;
import org.example.engine.core.assets.AssetUtils;
import org.example.engine.core.graphics.GraphicsUtils;
import org.example.engine.core.graphics.Renderer2D;
import org.example.engine.core.graphics.ShaderProgram;
import org.example.engine.core.graphics.WindowScreen;
import org.lwjgl.opengl.GL11;

public class WindowScreenTest_Rendering_2D_prep extends WindowScreen {

    private Renderer2D renderer2D;
    private ShaderProgram shader;
    private ComponentTransform transform;
    private ComponentGraphicsCamera camera;

    // create and modify quad dynamically

    public WindowScreenTest_Rendering_2D_prep() {
        this.renderer2D = new Renderer2D();

        final String vertexShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d.vert");
        final String fragmentShaderSrc = AssetUtils.getFileContent("assets/shaders/default-2d.frag");
        this.shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);

        this.camera = Component.Factory.createCamera2D(GraphicsUtils.getWindowWidth(),GraphicsUtils.getWindowHeight());

    }

    @Override
    public void show() {

    }


    @Override
    protected void refresh() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);

        System.out.println("prep");

    }

    @Override
    public void resize(int width, int height) { }


    @Override
    public void hide() {
        shader.free();
    }

    @Override
    public void free() {

    }

}
