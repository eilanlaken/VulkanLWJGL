package org.example.engine.core.graphics;

import org.example.engine.core.memory.MemoryResource;

public class ModelPart implements MemoryResource {

    public ModelPartMesh mesh;
    public ModelPartMaterial material;
    public final Class<? extends ShaderProgram> customShaderClass;

    public ModelPart(final ModelPartMesh mesh, final ModelPartMaterial material, final ShaderProgram shader) {
        this.mesh = mesh;
        this.material = material;
        if (shader == null) this.customShaderClass = null;
        else customShaderClass = shader.getClass();
    }

    @Override
    public void delete() {
        mesh.delete();
    }
}
