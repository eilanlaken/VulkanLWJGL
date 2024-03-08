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
    // see which is better - bounding aabb, bounding sphere or both.
    public Shape3DSphere boundingSphere;
    public Shape3DAABB boundingBox;
    public final int[] vbos;

    public ModelPartMesh(final int vaoId, final int vertexCount, final short bitmask, final boolean indexed, final Shape3DAABB boundingBox, final int... vbos) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
        this.vertexAttributeBitmask = bitmask;
        this.indexed = indexed;
        this.boundingBox = boundingBox;

        // TODO: test
        Vector3 center = new Vector3();
        center.add(boundingBox.min);
        center.add(boundingBox.max);
        center.scl(0.5f);
        float radius = Vector3.dst(boundingBox.min.x, boundingBox.min.y, boundingBox.min.z, boundingBox.max.x, boundingBox.max.y, boundingBox.max.z);
        this.boundingSphere = new Shape3DSphere(center, radius);

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
