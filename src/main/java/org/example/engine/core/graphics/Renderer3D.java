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
        this.currentShader.bindUniform("cameraPosition", camera.lens.position);
        this.currentShader.bindUniform("cameraCombined", camera.lens.combined);
    }

    // TODO: implement. Don't forget about the lights transform.
    public void setEnvironment(final Environment environment) {
        // bind all lights.
        // ambient light
        //this.currentShader.bindUniform("ambient", environment.getTotalAmbient());

        this.currentShader.bindUniform("pointLightPosition", environment.pointLights.get(0).position);
        this.currentShader.bindUniform("pointLightColor", environment.pointLights.get(0).color);
        this.currentShader.bindUniform("pointLightIntensity", environment.pointLights.get(0).intensity);
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

        // TODO: bind by need
        GL20.glEnableVertexAttribArray(0); // positions
        GL20.glEnableVertexAttribArray(1); // texture coordinates
        GL20.glEnableVertexAttribArray(2); // normals
        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1); // texture coordinates
        GL20.glDisableVertexAttribArray(2); // normals


        GL30.glBindVertexArray(0);
    }

    public void end() {
        this.currentShader.unbind();
    }

    private void sort(Array<ModelPart> modelParts) {
        // minimize: shader switching, camera binding, lights binding, material uniform binding
    }

}
