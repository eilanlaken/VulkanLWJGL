package org.example.engine.core.graphics;

import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.memory.MemoryUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public class ModelBuilder {

    private ArrayInt vaos = new ArrayInt();
    private ArrayInt vbos = new ArrayInt();

    public Model build(float[] data) {
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        //createVbo(0, data.length / 3, data); // TODO: bug, should be this?
        int vbo = createVbo(0, 3, data);
        GL30.glBindVertexArray(0);
        return new Model(id, data.length / 3, vbo);
    }

    private int createVbo(int attributeNumber, int vertexCount, float[] data) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); // bind
        FloatBuffer buffer = MemoryUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind
        return vbo;
    }


}
