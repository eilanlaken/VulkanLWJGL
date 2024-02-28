package org.example.engine.core.graphics;

import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL30;

public class ModelPartMesh implements Resource {

    public final int vaoId;
    public final ArrayInt vbos;
    public final int vertexCount;

    public ModelPartMesh(final int vaoId, final int vertexCount, final int... vbos) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
        this.vbos = new ArrayInt(vbos.length);
        for (int vbo : vbos) {
            this.vbos.add(vbo);
        }
    }

    @Override
    public void free() {
        GL30.glDeleteVertexArrays(vaoId);
        for (int vbo : vbos.items) {
            GL30.glDeleteBuffers(vbo);
        }
    }
}
