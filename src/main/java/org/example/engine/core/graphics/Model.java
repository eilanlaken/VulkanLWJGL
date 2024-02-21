package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.memory.Resource;
import org.lwjgl.opengl.GL30;

public class Model implements Resource {

    public final int vaoId;
    public final ArrayInt vbos;
    public final int vertexCount;
    public final Array<ModelPart> parts;
    public final ModelArmature armature;

    public Model(int vaoId, ArrayInt vbos, int vertexCount, Array<ModelPart> parts, ModelArmature armature) {
        this.vaoId = vaoId;
        this.vbos = vbos;
        this.vertexCount = vertexCount;
        this.parts = parts;
        this.armature = armature;
    }

    @Override
    public void free() {
        GL30.glDeleteVertexArrays(vaoId);
        for (int vbo : vbos.items) {
            GL30.glDeleteBuffers(vbo);
        }
    }
}
