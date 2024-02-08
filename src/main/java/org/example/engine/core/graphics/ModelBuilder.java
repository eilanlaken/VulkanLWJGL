package org.example.engine.core.graphics;

import org.example.engine.core.memory.MemoryUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ModelBuilder {

    public Model build(float[] positions, float[] textureCoordinates, int[] indices) {
        int id = GL30.glGenVertexArrays();
        storeIndicesBuffer(indices);
        GL30.glBindVertexArray(id);
        int vboPositions = storeDataInAttributeList(0, 3, positions);
        int vboTextureCoordinates = storeDataInAttributeList(1, 2, textureCoordinates);
        GL30.glBindVertexArray(0);
        return new Model(id, indices.length, vboPositions, vboTextureCoordinates);
    }

    private void storeIndicesBuffer(int[] indices) {
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = MemoryUtils.store(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private int storeDataInAttributeList(int attributeNumber, int vertexSize, float[] data) {
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); // bind
        FloatBuffer buffer = MemoryUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, vertexSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind
        return vbo;
    }


}
