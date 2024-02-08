package org.example.game;

import org.example.engine.core.graphics.*;
import org.lwjgl.opengl.GL11;

public class WindowScreenTest_1 implements WindowScreen {

    private Renderer3D renderer3D;
    private ModelBuilder modelBuilder;
    private Model model;

    public WindowScreenTest_1() {
        this.renderer3D = new Renderer3D();
        this.modelBuilder = new ModelBuilder();
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
        model = modelBuilder.build(data);
    }

    @Override
    public void update(float delta) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);

        renderer3D.begin();
        renderer3D.render(model, null);
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

    }
}
