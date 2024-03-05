package org.example.engine.core.graphics;

import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL30;

public class ModelPartMesh implements Resource {

    public final int vaoId;
    public final int vertexCount;
    public final short vertexAttributeBitmask;
    public final int[] vbos;

    public ModelPartMesh(final int vaoId, final int vertexCount, short bitmask, final int... vbos) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
        this.vertexAttributeBitmask = bitmask;
        this.vbos = vbos;
    }

    @Override
    public void free() {
        // TODO: implement
    }
}
