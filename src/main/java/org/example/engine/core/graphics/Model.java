package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL30;

public class Model implements Resource {

    public final int vaoId;
    int vbo;
    public final int vertexCount;

    public Model(final int vaoId, final int vertexCount, @Deprecated final int vbo) {
        this.vaoId = vaoId;
        this.vbo = vbo;
        this.vertexCount = vertexCount;
    }

    // later, TODO.
    public Array<ModelPart> modelParts;
    public ModelArmature armature;

    @Override
    public void free() {
        GL30.glDeleteVertexArrays(vaoId);
        GL30.glDeleteBuffers(vbo);
    }
}
