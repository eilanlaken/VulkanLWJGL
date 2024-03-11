package org.example.engine.core.graphics;

import org.example.engine.components.ComponentGraphicsCamera;
import org.example.engine.components.ComponentTransform;
import org.example.engine.core.collections.Array;
import org.example.engine.core.math.MathUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

// TODO: this is SystemRenderer territory.
@Deprecated public class Renderer3D {

    public final RendererFixedPipelineParamSetter paramSetter;
    private boolean drawing;
    private ShaderProgram currentShader;
    private ComponentGraphicsCamera camera;

    public Renderer3D() {
        this.paramSetter = new RendererFixedPipelineParamSetter();
        this.drawing = false;
    }

    public void begin(ShaderProgram shader) {
        this.currentShader = shader;
        this.currentShader.bind();
    }

    public void setCamera(final ComponentGraphicsCamera camera) {
        //this.currentShader.bindUniform("camera_position", camera.lens.position);
        this.currentShader.bindUniform("camera_combined", camera.lens.combined);
        this.camera = camera;
    }

    // TODO: implement. Don't forget about the lights transform.
    public void setEnvironment(final Lights lights) {
        // bind all lights.
        // ambient light
        //this.currentShader.bindUniform("ambient", environment.getTotalAmbient());
        this.currentShader.bindUniform("pointLightPos", lights.pointLights.get(0).position);
        this.currentShader.bindUniform("pointLightColor", lights.pointLights.get(0).color);
        this.currentShader.bindUniform("pointLightIntensity", lights.pointLights.get(0).intensity);
    }

    public void draw(final ModelPart modelPart, final ComponentTransform transform) {
        modelPart.mesh.boundingSphere.translateAndScale(transform.x, transform.y, transform.z, MathUtils.max(transform.scaleX, transform.scaleY, transform.scaleZ));
        if (camera.lens.frustum.intersectsSphere(modelPart.mesh.boundingSphere)) {
            System.out.println("intersects");
        } else {
            System.out.println("CULLING");
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        // todo: see when it makes sense to compute the matrix transform
        currentShader.bindUniform("body_transform", transform.local);
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

    public void end() {
        this.currentShader.unbind();
    }

    private void sort(Array<ModelPart> modelParts) {
        // minimize: shader switching, camera binding, lights binding, material uniform binding
    }

}
