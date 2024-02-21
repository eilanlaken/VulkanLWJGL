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

    // TODO: refactor to ModelPart, which is the basic rendering unit.
    public void draw(final Model_old modelOld, final Matrix4 transform) {
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

    // TODO: maybe replace camera directly with the projection and transform of the lens.
    @Deprecated public void render(final Camera camera, final Model_old modelOld, final Matrix4 transform, ShaderProgram shader) {
        // TODO: move to render context.
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // optimize shaders etc.
        shader.bind();
        shaderSelector.getDefaultShader().bindUniform("view", camera.lens.view);
        shaderSelector.getDefaultShader().bindUniform("projection", camera.lens.projection);

        shader.bindUniform("transform", transform);
        // TODO: optimize material binding
        shader.bindUniforms(modelOld.get_material_debug());

        GL30.glBindVertexArray(modelOld.vaoId);
        GL20.glEnableVertexAttribArray(0); // positions
        GL20.glEnableVertexAttribArray(1); // texture coordinates
        GL11.glDrawElements(GL11.GL_TRIANGLES, modelOld.vertexCount, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1); // texture coordinates
        GL30.glBindVertexArray(0);
        //shader.unbind();
    }

    private void sort(Array<ModelPart> modelParts) {
        // minimize: shader switching, camera binding, lights binding, material uniform binding
    }

}
