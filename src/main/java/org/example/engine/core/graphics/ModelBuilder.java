package org.example.engine.core.graphics;

import org.example.engine.core.memory.MemoryUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ModelBuilder {

    public static Model_old buildCube(float width, float height, float depth) {

        return null;
    }

    public static Model_old build(float[] positions,
                                  float[] colors,
                                  float[] textureCoordinates,
                                  float[] normals,
                                  float[] tangents,
                                  float[] biNormals,
                                  int[] indices) {

        return null;
    }

    public static Model_old build(float[] positions, float[] textureCoordinates, int[] indices) {
        int id = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(id);
        storeIndicesBuffer(indices);
        int vboPositions = storeDataInAttributeList(0, 3, positions);
        int vboTextureCoordinates = storeDataInAttributeList(1, 2, textureCoordinates);
        GL30.glBindVertexArray(0);
        return new Model_old(id, indices.length, vboPositions, vboTextureCoordinates);
    }

    private static void storeIndicesBuffer(int[] indices) {
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = MemoryUtils.store(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private static int storeDataInAttributeList(int attributeNumber, int attributeDataLength, float[] data) {
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); // bind
        FloatBuffer buffer = MemoryUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, attributeDataLength, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind
        return vbo;
    }


}
