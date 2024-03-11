package org.example.engine.core.graphics;

import org.lwjgl.opengl.GL11;

public class Renderer2D_old {

    private ShaderProgram currentShader;
    private CameraLens lens;
    private boolean drawing;

    // mesh data
    private int vertexIndex, triangleIndex;


    private float[] positionsBatch;
    private float[] colorsBatch;
    private float[] textCoords0Batch;
    private short[] triangles;

    public Renderer2D_old() {
        this.drawing = false;

        // create the dynamic mesh
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

    public void setLights() {
        // later
    }

    public void addToBatch() {

    }

    private void drawCurrentBatch() {

    }

    // TODO: see what is up
    public void end() {
        //this.currentShader.unbind();
        // do the actual drawing
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
    }


}
