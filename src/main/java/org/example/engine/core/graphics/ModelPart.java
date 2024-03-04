package org.example.engine.core.graphics;

import org.example.engine.core.memory.Resource;

public class ModelPart implements Resource {

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
    public void free() {
        mesh.free();
    }
}
