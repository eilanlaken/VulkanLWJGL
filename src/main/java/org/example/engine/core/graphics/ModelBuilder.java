package org.example.engine.core.graphics;

import org.example.engine.core.memory.MemoryUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public final class ModelBuilder {

    public static int loadToVAO(float[] positions, float[] colors, int[] indices){
        int vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        bindIndicesBuffer(indices);
        storeDataInAttributeList(ModelVertexAttribute.POSITION_2D, positions);
        storeDataInAttributeList(ModelVertexAttribute.COLOR_PACKED, colors);


        GL30.glBindVertexArray(0);

        return vaoID;
    }

    private static int bindIndicesBuffer(int[] indices){
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = MemoryUtils.store(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        return vbo;
    }

    private static int storeDataInAttributeList(final ModelVertexAttribute attribute, float[] data){
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = MemoryUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribute.slot, attribute.length, attribute.type, attribute.normalized,0,0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    private static int storeDataInAttributeList(int slot, int length, int type, boolean normalized, float[] data){
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = MemoryUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(slot,length, type,normalized,0,0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vbo;
    }

}
