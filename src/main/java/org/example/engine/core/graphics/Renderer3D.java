package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Matrix4;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Renderer3D {

    public final RendererFixedPipelineParamSetter paramSetter;
    private boolean drawing;
    private RendererShaderSelector shaderSelector;
    private ShaderProgram currentShader;

    public Renderer3D() {
        this.paramSetter = new RendererFixedPipelineParamSetter();
        this.shaderSelector = new RendererShaderSelector();
        this.drawing = false;
    }

    public void begin(ShaderProgram shader) {
        this.currentShader = shader;
        this.currentShader.bind();
    }

    public void setCamera(final Camera camera) {
        this.currentShader.bindUniform("view", camera.lens.view);
        this.currentShader.bindUniform("projection", camera.lens.projection);
    }

    // TODO: implement. Don't forget about the lights transform.
    public void setLights() {

    }

    public void draw(final ModelPart modelPart, final Matrix4 transform) {
        // Enable depth testing (recommended for proper rendering)
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        // Disable backface culling
        GL11.glEnable(GL11.GL_CULL_FACE);

        currentShader.bindUniform("transform", transform);
        ModelPartMaterial material = modelPart.material;
        currentShader.bindUniforms(material.materialParams);
        ModelPartMesh mesh = modelPart.mesh;
        GL30.glBindVertexArray(mesh.vaoId);
        GL20.glEnableVertexAttribArray(0); // positions
        GL20.glEnableVertexAttribArray(1); // texture coordinates
        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1); // texture coordinates
        GL30.glBindVertexArray(0);
    }

    @Deprecated public void draw(final Model_old modelOld, final Matrix4 transform) {
        currentShader.bindUniform("transform", transform);
        currentShader.bindUniforms(modelOld.get_material_debug());
        GL30.glBindVertexArray(modelOld.vaoId);
        GL20.glEnableVertexAttribArray(0); // positions
        GL20.glEnableVertexAttribArray(1); // texture coordinates
        GL11.glDrawElements(GL11.GL_TRIANGLES, modelOld.vertexCount, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1); // texture coordinates
        GL30.glBindVertexArray(0);
    }

    public void end() {
        this.currentShader.unbind();
    }

    private void sort(Array<ModelPart> modelParts) {
        // minimize: shader switching, camera binding, lights binding, material uniform binding
    }

}
