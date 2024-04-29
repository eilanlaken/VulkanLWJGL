package org.example.engine.core.graphics;

import org.example.engine.core.memory.MemoryResource;

public class GraphicsModelPart implements MemoryResource {

    public GraphicsModelPartMesh mesh;
    public GraphicsModelPartMaterial material;
    public final Class<? extends GraphicsShaderProgram> customShaderClass;

    public GraphicsModelPart(final GraphicsModelPartMesh mesh, final GraphicsModelPartMaterial material, final GraphicsShaderProgram shader) {
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
