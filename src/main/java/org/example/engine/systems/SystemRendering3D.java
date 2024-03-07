package org.example.engine.systems;

import org.example.engine.core.graphics.*;
import org.example.engine.core.math.Matrix4;
import org.example.engine.entities.Entity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;
import java.util.Map;

public class SystemRendering3D {

    private Camera currentCamera;
    private Environment environment;
    private boolean drawing;
    private Map<ShaderProgram, Entity> shaderProgramEntities;

    private ShaderProgram currentShader;

    public SystemRendering3D() {
        currentCamera = null;
        environment = new Environment();
        shaderProgramEntities = new HashMap<>();
        drawing = false;
    }

    public void begin(Camera camera) {
        if (drawing) throw new IllegalStateException("Must call end(); Cannot nest begin() and end() blocks.");
        shaderProgramEntities.clear();
        drawing = true;
        currentCamera = camera;
    }

    // cull in this function - before adding model-part / light to render queue
    public void draw(final Entity entity) {
        // just storing logic!
        // entity: model, decal, light
    }

    public void end() {
        if (!drawing) throw new IllegalStateException("Must call begin() before call to end().; Cannot nest begin() and end() blocks.");
        // the ACTUAL rendering logic
        drawing = false;
    }

    public void setCamera(final Camera camera) {
        //this.currentShader.bindUniform("camera_position", camera.lens.position);
        this.currentShader.bindUniform("camera_combined", camera.lens.combined);
    }

    // TODO: implement. Don't forget about the lights transform.
    public void setEnvironment(final Environment environment) {
        this.currentShader.bindUniform("pointLightPos", environment.pointLights.get(0).position);
        this.currentShader.bindUniform("pointLightColor", environment.pointLights.get(0).color);
        this.currentShader.bindUniform("pointLightIntensity", environment.pointLights.get(0).intensity);
    }

    public void draw(final ModelPart modelPart, final Matrix4 transform) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        currentShader.bindUniform("body_transform", transform);
        ModelPartMaterial material = modelPart.material;
        //currentShader.bindUniforms(material.materialParams);
        currentShader.bindUniform("colorDiffuse", material.uniformParams.get("colorDiffuse"));
        ModelPartMesh mesh = modelPart.mesh;
        GL30.glBindVertexArray(mesh.vaoId);
        {
            for (ModelVertexAttribute attribute : ModelVertexAttribute.values()) if (mesh.hasVertexAttribute(attribute)) GL20.glEnableVertexAttribArray(attribute.slot);
            if (mesh.indexed) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount);
            for (ModelVertexAttribute attribute : ModelVertexAttribute.values()) if (mesh.hasVertexAttribute(attribute)) GL20.glDisableVertexAttribArray(attribute.slot);
        }
        GL30.glBindVertexArray(0);
    }

}
