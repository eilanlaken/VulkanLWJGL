package org.example.engine.core.graphics;

import org.example.engine.core.math.Shape2DPolygon;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Renderer2D_polyRenderer implements Resource {

    private ShaderProgram currentShader;
    private CameraLens lens;
    private boolean drawing;

    public Renderer2D_polyRenderer() {
        this.drawing = false;
    }

    public void begin(ShaderProgram shader) {
        this.currentShader = shader;
        this.currentShader.bind();
    }

    public void setCamera(final CameraLens lens) {
        //this.currentShader.bindUniform("camera_position", camera.lens.position);
        this.currentShader.bindUniform("u_camera_combined", lens.combined);
        this.lens = lens;
    }

    public void draw(final Shape2DPolygon polygon) {

    }

    // TODO: see what is up
    public void end() {
        //this.currentShader.unbind();
        // do the actual drawing
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    @Override
    public void free() {
        // free shader
        // free dynamic mesh
    }
}
