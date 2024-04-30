package org.example.engine.core.graphics;

import org.example.engine.core.collections.CollectionsArray;
import org.example.engine.core.math.MathUtils;
import org.example.engine.ecs.ComponentTransform;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

// TODO: improve to handle whatever.
public class GraphicsRenderer3D {

    private boolean drawing;
    private GraphicsShaderProgram currentShader;
    private GraphicsCamera camera;

    public GraphicsRenderer3D() {
        this.drawing = false;
    }

    public void begin(GraphicsShaderProgram shader) {
        this.currentShader = shader;
        GraphicsShaderProgramBinder.bind(shader);
    }

    public void setCamera(final GraphicsCamera camera) {
        this.currentShader.bindUniform("u_camera_position", camera.position);
        this.currentShader.bindUniform("u_camera_combined", camera.lens.combined);
        this.camera = camera;
    }

    // TODO: implement. Don't forget about the lights transform.
    public void setEnvironment() {
        // bind all lights.
        // ambient light
        //this.currentShader.bindUniform("ambient", environment.getTotalAmbient());

    }

    public void draw(final GraphicsModelPart modelPart, final ComponentTransform transform) {
        modelPart.mesh.boundingSphere.translateAndScale(transform.x, transform.y, transform.z, MathUtils.max(transform.scaleX, transform.scaleY, transform.scaleZ));
        if (camera.lens.frustum.intersectsSphere(modelPart.mesh.boundingSphere)) {
            System.out.println("intersects");
        } else {
            System.out.println("CULLING");
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        // todo: see when it makes sense to compute the matrix transform
        currentShader.bindUniform("u_body_transform", transform.local);
        GraphicsModelPartMaterial material = modelPart.material;
        //currentShader.bindUniforms(material.materialParams);
        currentShader.bindUniform("colorDiffuse", material.uniformParams.get("colorDiffuse"));
        GraphicsModelPartMesh mesh = modelPart.mesh;
        System.out.println("ddddd " + mesh.vaoId);
        GL30.glBindVertexArray(mesh.vaoId);
        {
            for (GraphicsModelVertexAttribute attribute : GraphicsModelVertexAttribute.values()) {
                System.out.println("attrib: " + attribute.slot);
                if (mesh.hasVertexAttribute(attribute)) GL20.glEnableVertexAttribArray(attribute.slot);
            }
            if (mesh.indexed) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount);
            for (GraphicsModelVertexAttribute attribute : GraphicsModelVertexAttribute.values()) if (mesh.hasVertexAttribute(attribute)) GL20.glDisableVertexAttribArray(attribute.slot);
        }
        GL30.glBindVertexArray(0);
    }

    public void end() {
        //ShaderProgramBinder.unbind();
    }

    private void sort(CollectionsArray<GraphicsModelPart> modelParts) {
        // minimize: shader switching, camera binding, lights binding, material uniform binding
    }

}