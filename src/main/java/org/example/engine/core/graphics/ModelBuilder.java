package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.memory.MemoryUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ModelBuilder {

    public static Model build() {
        return null;
    }

    private static ModelPartMesh create(float[] positions,
                                   float[] colors,
                                   float[] textureCoordinates0,
                                   float[] textureCoordinates1,
                                   float[] normals,
                                   float[] tangents,
                                   float[] biNormals,
                                   float[] boneWeights0,
                                   float[] boneWeights1,
                                   float[] boneWeights2,
                                   float[] boneWeights3,
                                   float[] boneWeights4,
                                   float[] boneWeights5,
                                   int[] indices) {
        int vaoId = GL30.glGenVertexArrays();
        Array<ModelVertexAttribute> attributes = new Array<>();
        GL30.glBindVertexArray(vaoId);
        storeIndicesBuffer(indices);
        int vboPositions = storeDataInAttributeList(ModelVertexAttribute.POSITION, positions, attributes);
        int vboColors = storeDataInAttributeList(ModelVertexAttribute.COLOR, colors, attributes);
        int vboTextureCoordinates0 = storeDataInAttributeList(ModelVertexAttribute.TEXTURE_COORDINATES0, textureCoordinates0, attributes);
        int vboTextureCoordinates1 = storeDataInAttributeList(ModelVertexAttribute.TEXTURE_COORDINATES1, textureCoordinates1, attributes);
        int vboNormals = storeDataInAttributeList(ModelVertexAttribute.NORMAL, normals, attributes);
        int vboTangents = storeDataInAttributeList(ModelVertexAttribute.TANGENT, tangents, attributes);
        int vboBiNormals = storeDataInAttributeList(ModelVertexAttribute.BI_NORMAL, biNormals, attributes);
        int vboBoneWeight0 = storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT0, boneWeights0, attributes);
        int vboBoneWeight1 = storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT1, boneWeights1, attributes);
        int vboBoneWeight2 = storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT2, boneWeights2, attributes);
        int vboBoneWeight3 = storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT3, boneWeights3, attributes);
        int vboBoneWeight4 = storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT4, boneWeights4, attributes);
        int vboBoneWeight5 = storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT5, boneWeights5, attributes);
        GL30.glBindVertexArray(0);
        final short bitmask = generateBitmask(attributes);
        //return new ModelPartMesh(vaoId, indices.length, vboPositions, vboTextureCoordinates, vboNormals);
        return null; // for now
    }

    private static void storeIndicesBuffer(int[] indices) {
        if (indices == null) return;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = MemoryUtils.store(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private static int storeDataInAttributeList(final ModelVertexAttribute attribute, final float[] data, Array<ModelVertexAttribute> attributes) {
        if (data == null) return -1;
        final int attributeNumber = attribute.ordinal();
        final int attributeUnitSize = attribute.length;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); // bind
        FloatBuffer buffer = MemoryUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, attributeUnitSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind
        attributes.add(attribute);
        return vbo;
    }

    private static short generateBitmask(final Array<ModelVertexAttribute> attributes) {
        short bitmask = 0b0000;
        for (final ModelVertexAttribute attribute : attributes) {
            bitmask |= attribute.bitmask;
        }
        return bitmask;
    }

}
