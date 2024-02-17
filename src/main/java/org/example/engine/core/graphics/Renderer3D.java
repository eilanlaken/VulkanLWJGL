package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.math.Matrix4;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Renderer3D {

    public final RendererFixedPipelineParamSetter paramSetter;
    private boolean drawing;
    private RendererShaderSelector shaderSelector;

    public Renderer3D() {
        this.paramSetter = new RendererFixedPipelineParamSetter();
        this.shaderSelector = new RendererShaderSelector();
        this.drawing = false;
    }

    public void begin(final Camera camera) {

    }

    public void end() {

    }

    // TODO: maybe replace camera directly with the projection and transform of the lens.
    public void render(final Camera camera, final Model model, final Matrix4 transform, ShaderProgram shader) {
        // TODO: move to render context.
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // optimize shaders etc.
        shader.bind();
        shaderSelector.getDefaultShader().bindUniform("view", camera.lens.view);
        shaderSelector.getDefaultShader().bindUniform("projection", camera.lens.projection);

        shader.bindUniform("transform", transform);
        // TODO: optimize material binding
        shader.bindUniforms(model.get_material_debug());

        GL30.glBindVertexArray(model.vaoId);
        GL20.glEnableVertexAttribArray(0); // positions
        GL20.glEnableVertexAttribArray(1); // texture coordinates
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1); // texture coordinates
        GL30.glBindVertexArray(0);
        //shader.unbind();
    }

    private void sort(Array<ModelPart> modelParts) {
        // minimize: shader switching, camera binding, lights binding, material uniform binding
    }

}
