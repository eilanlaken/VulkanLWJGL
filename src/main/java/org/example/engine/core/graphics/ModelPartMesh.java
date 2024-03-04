package org.example.engine.core.graphics;

import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL30;

public class ModelPartMesh implements Resource {

    public final int vaoId;
    public final int vertexCount;
    public final short vertexAttributeBitmask;
    public final ArrayInt vbos;
    // TODO: remove one
    public ModelPartMesh(final int vaoId, final int vertexCount, short bitmask, final int... vbos) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
        this.vbos = new ArrayInt(vbos.length);
        this.vertexAttributeBitmask = bitmask;
        for (int vbo : vbos) {
            this.vbos.add(vbo);
        }
    }
    // TODO: remove one
    public ModelPartMesh(final int vaoId, final int vertexCount, final int... vbos) {
        this.vertexAttributeBitmask = 0; // TODO: remove

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
