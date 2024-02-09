package org.example.game;

import org.example.engine.core.files.FileUtils;
import org.example.engine.core.graphics.*;
import org.lwjgl.opengl.GL11;

public class WindowScreenTest_1 implements WindowScreen {

    private Renderer3D renderer3D;
    private ModelBuilder modelBuilder;
    private Model model;

    private ShaderProgram shader;


    public WindowScreenTest_1() {
        this.renderer3D = new Renderer3D();
        this.modelBuilder = new ModelBuilder();
        final String vertexShaderSrc = FileUtils.getFileContent("assets/shaders/vertex.glsl");
        final String fragmentShaderSrc = FileUtils.getFileContent("assets/shaders/fragment.glsl");
        shader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);
    }

    @Override
    public void show() {
        System.out.println("show called");

        float[] data = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                -0.5f, 0.5f, 0f
        };

        float[] textureCoordinates = {
                0,0,
                0,1,
                1,1,
                1,0
        };

        int[] indices = {
                0,1,3,
                3,1,2
        };

        model = modelBuilder.build(data, textureCoordinates, indices);
    }

    @Override
    public void update(float delta) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);

        renderer3D.begin();
        renderer3D.render(model, null, shader);
        renderer3D.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        model.free();
        shader.free();
    }
}
