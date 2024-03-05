package org.example.engine.core.graphics;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayInt;
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

    private static ModelPartMesh create(final int vertexCount,
                                       float[] positions,
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
        Array<ModelVertexAttribute> attributesCollector = new Array<>();
        ArrayInt vbosCollector = new ArrayInt();
        int vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
        {
            storeIndicesBuffer(indices, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.POSITION, positions, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.COLOR, colors, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.TEXTURE_COORDINATES0, textureCoordinates0, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.TEXTURE_COORDINATES1, textureCoordinates1, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.NORMAL, normals, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.TANGENT, tangents, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.BI_NORMAL, biNormals, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT0, boneWeights0, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT1, boneWeights1, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT2, boneWeights2, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT3, boneWeights3, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT4, boneWeights4, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT5, boneWeights5, attributesCollector, vbosCollector);
        }
        GL30.glBindVertexArray(0);
        final short bitmask = generateBitmask(attributesCollector);
        final int[] vbos = vbosCollector.pack().items;
        return new ModelPartMesh(vaoId, vertexCount, bitmask, true, vbos);
    }

    private static void storeIndicesBuffer(int[] indices, ArrayInt vbos) {
        if (indices == null) return;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = MemoryUtils.store(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        vbos.add(vbo);
    }

    private static void storeDataInAttributeList(final ModelVertexAttribute attribute, final float[] data, Array<ModelVertexAttribute> attributes, ArrayInt vbos) {
        if (data == null) return;
        final int attributeNumber = attribute.ordinal();
        final int attributeUnitSize = attribute.length;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); // bind
        FloatBuffer buffer = MemoryUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, attributeUnitSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind
        attributes.add(attribute);
        vbos.add(vbo);
    }

    private static short generateBitmask(final Array<ModelVertexAttribute> attributes) {
        short bitmask = 0b0000;
        for (final ModelVertexAttribute attribute : attributes) {
            bitmask |= attribute.bitmask;
        }
        return bitmask;
    }

}
