package org.example.engine.core.graphics;

public class ModelPart {

    public final Class shaderClass;
    public ModelPartMesh mesh;
    public ModelPartMaterial material;

    public ModelPart(final ShaderProgram shader, ModelPartMesh mesh, ModelPartMaterial material) {
        if (shader == null) this.shaderClass = null;
        else shaderClass = shader.getClass();
        this.mesh = mesh;
        this.material = material;
    }

}
