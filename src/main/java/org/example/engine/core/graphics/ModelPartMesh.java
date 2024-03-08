package org.example.engine.core.graphics;

import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.math.Shape3DAABB;
import org.example.engine.core.math.Shape3DSphere;
import org.example.engine.core.math.Vector3;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL30;

public class ModelPartMesh implements Resource {

    public final int vaoId;
    public final int vertexCount;
    public final short vertexAttributeBitmask;
    public final boolean indexed;
    public Shape3DSphere boundingSphere;
    public final int[] vbos;

    public ModelPartMesh(final int vaoId, final int vertexCount, final short bitmask, final boolean indexed, final Shape3DSphere boundingSphere, final int... vbos) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
        this.vertexAttributeBitmask = bitmask;
        this.indexed = indexed;
        this.boundingSphere = boundingSphere;
        this.vbos = vbos;
    }

    public boolean hasVertexAttribute(final ModelVertexAttribute attribute) {
        return (vertexAttributeBitmask & attribute.bitmask) != 0;
    }

    @Override
    // TODO: confirm it works
    public void free() {
        GL30.glDeleteVertexArrays(vaoId);
        for (int vbo : vbos) {
            GL30.glDeleteBuffers(vbo);
        }
    }
}
