package org.example.engine.core.graphics;

import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Renderer2D implements Resource {

    private ShaderProgram currentShader;
    private CameraLens lens;
    private boolean drawing;

    // mesh data
    private int batchSize = 10;
    private final int vaoId;
    private int vertexIndex, triangleIndex;
    private float[] positionsBatch = new float[batchSize * 3];
    private float[] colorsBatch = new float[batchSize * 4];
    private float[] textCoords0Batch = new float[batchSize * 2];
    private short[] triangles;

    public Renderer2D() {
        this.drawing = false;

        // generate dynamic mesh
        this.vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
        {
//            int vbo = GL15.glGenBuffers();
//            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
//            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);
//            storeDataInAttributeList(ModelVertexAttribute.POSITION, meshData, attributesCollector, vbosCollector);
//            storeDataInAttributeList(ModelVertexAttribute.COLOR, meshData, attributesCollector, vbosCollector);
//            storeDataInAttributeList(ModelVertexAttribute.TEXTURE_COORDINATES0, meshData, attributesCollector, vbosCollector);
        }
        GL30.glBindVertexArray(0);

    }

    public void drawBatch() {

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

    // flush()
    private void drawCurrentBatch() {
        GL30.glBindVertexArray(vaoId);
        {
            GL20.glEnableVertexAttribArray(ModelVertexAttribute.POSITION.slot);
            GL20.glEnableVertexAttribArray(ModelVertexAttribute.COLOR.slot);
            GL20.glEnableVertexAttribArray(ModelVertexAttribute.TEXTURE_COORDINATES0.slot);
            //GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(ModelVertexAttribute.POSITION.slot);
            GL20.glDisableVertexAttribArray(ModelVertexAttribute.COLOR.slot);
            GL20.glDisableVertexAttribArray(ModelVertexAttribute.TEXTURE_COORDINATES0.slot);
        }
        GL30.glBindVertexArray(0);
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
