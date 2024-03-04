package org.example.engine.core.graphics;

import org.example.engine.core.assets.AssetStore;
import org.example.engine.core.memory.MemoryUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

public class ModelBuilder {

    public static ModelPart createRedCube() {
        // Positions of the vertices (8 vertices, 3 coordinates each)
        float[] positions = {
                -1.000f, 1.000f, 1.000f,
                -1.000f, -1.000f, 1.000f,
                1.000f, 1.000f, 1.000f,
                1.000f, -1.000f, 1.000f,
                -1.000f, -1.000f, 1.000f,
                1.000f, -1.000f, -1.000f,
                1.000f, -1.000f, 1.000f,
                -1.000f, -1.000f, -1.000f,
                -1.000f, 1.000f, 1.000f,
                -1.000f, -1.000f, -1.000f,
                -1.000f, -1.000f, 1.000f,
                -1.000f, 1.000f, -1.000f,
                1.000f, -1.000f, -1.000f,
                -1.000f, -1.000f, -1.000f,
                -1.000f, 1.000f, -1.000f,
                1.000f, 1.000f, -1.000f,
                1.000f, -1.000f, 1.000f,
                1.000f, 1.000f, -1.000f,
                1.000f, 1.000f, 1.000f,
                1.000f, -1.000f, -1.000f,
                1.000f, 1.000f, 1.000f,
                -1.000f, 1.000f, -1.000f,
                -1.000f, 1.000f, 1.000f,
                1.000f, 1.000f, -1.000f,
        };

        // Texture coordinates (6 faces, 4 corners each, 2 coordinates per corner)
        float[] textureCoordinates = {
                0.875f, 0.500f,
                0.875f, 0.750f,
                0.625f, 0.500f,
                0.625f, 0.750f,
                0.625f, 1.000f,
                0.375f, 0.750f,
                0.625f, 0.750f,
                0.375f, 1.000f,
                0.625f, 0.250f,
                0.375f, 0.000f,
                0.625f, 0.000f,
                0.375f, 0.250f,
                0.375f, 0.750f,
                0.125f, 0.750f,
                0.125f, 0.500f,
                0.375f, 0.500f,
                0.625f, 0.750f,
                0.375f, 0.500f,
                0.625f, 0.500f,
                0.375f, 0.750f,
                0.625f, 0.500f,
                0.375f, 0.250f,
                0.625f, 0.250f,
                0.375f, 0.500f,
        };

        // Normals of the vertices (each face has the same normal)
        float[] normals = {
                0.000f, 0.000f, 1.000f,
                0.000f, 0.000f, 1.000f,
                0.000f, 0.000f, 1.000f,
                0.000f, 0.000f, 1.000f,
                0.000f, -1.000f, 0.000f,
                0.000f, -1.000f, 0.000f,
                0.000f, -1.000f, 0.000f,
                0.000f, -1.000f, 0.000f,
                -1.000f, 0.000f, 0.000f,
                -1.000f, 0.000f, 0.000f,
                -1.000f, 0.000f, 0.000f,
                -1.000f, 0.000f, 0.000f,
                0.000f, 0.000f, -1.000f,
                0.000f, 0.000f, -1.000f,
                0.000f, 0.000f, -1.000f,
                0.000f, 0.000f, -1.000f,
                1.000f, 0.000f, 0.000f,
                1.000f, 0.000f, 0.000f,
                1.000f, 0.000f, 0.000f,
                1.000f, 0.000f, 0.000f,
                0.000f, 1.000f, 0.000f,
                0.000f, 1.000f, 0.000f,
                0.000f, 1.000f, 0.000f,
                0.000f, 1.000f, 0.000f,
        };

        // Index buffer to specify the order of vertices to draw each face
        // (note: this uses triangle strips for efficiency)
        int[] indices = {
                0,   1,   2,   2,   1,   3,   4,   5,   6,   5,   4,   7,
                8,   9,  10,   9,   8,  11,  12,  13,  14,  12,  14,  15,
                16,  17,  18,  17,  16,  19,  20,  21,  22,  21,  20,  23
        };

        ModelPartMesh mesh = create(positions, textureCoordinates, normals, indices);
        HashMap<String, Object> materialAttributes = new HashMap<>();
        //materialAttributes.put("albedo_map", debug_createTexture("assets/textures/yellowSquare.png"));
        materialAttributes.put("albedo_map", AssetStore.get("assets/textures/yellowSquare.png"));
        //materialAttributes.put("shineDamper", 0.8f);
        //materialAttributes.put("reflectivity", 0.05f);
        ModelPartMaterial material = new ModelPartMaterial(materialAttributes);

        return new ModelPart(mesh, material, null);
    }

    @Deprecated public static ModelPartMesh create(float[] positions, float[] textureCoordinates, float[] normals, int[] indices) {
        int vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
        storeIndicesBuffer(indices);
        int vboPositions = storeDataInAttributeList(5, 3, positions);
        int vboTextureCoordinates = storeDataInAttributeList(1, 2, textureCoordinates);
        int vboNormals = storeDataInAttributeList(2, 3, normals);
        GL30.glBindVertexArray(0);
        return new ModelPartMesh(vaoId, indices.length, (short) 0, vboPositions, vboTextureCoordinates, vboNormals);

    }


    public static Model_old create(float[] positions,
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
        GL30.glBindVertexArray(vaoId);
        if (indices != null) storeIndicesBuffer(indices);
        int vboPositions = storeDataInAttributeList(ModelVertexAttribute.POSITION, positions);
        int vboColors = storeDataInAttributeList(ModelVertexAttribute.COLOR, colors);
        int vboTextureCoordinates0 = storeDataInAttributeList(ModelVertexAttribute.TEXTURE_COORDINATES0, textureCoordinates0);
        int vboTextureCoordinates1 = storeDataInAttributeList(ModelVertexAttribute.TEXTURE_COORDINATES1, textureCoordinates1);
        int vboNormals = storeDataInAttributeList(ModelVertexAttribute.NORMAL, normals);
        int vboTangents = storeDataInAttributeList(ModelVertexAttribute.TANGENT, tangents);
        int vboBiNormals = storeDataInAttributeList(ModelVertexAttribute.BI_NORMAL, biNormals);
        int vboBoneWeight0 = storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT0, boneWeights0);
        int vboBoneWeight1 = storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT1, boneWeights1);
        int vboBoneWeight2 = storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT2, boneWeights2);
        int vboBoneWeight3 = storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT3, boneWeights3);
        int vboBoneWeight4 = storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT4, boneWeights4);
        int vboBoneWeight5 = storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT5, boneWeights5);
        GL30.glBindVertexArray(0);
        //return new ModelPartMesh(vaoId, indices.length, vboPositions, vboTextureCoordinates, vboNormals);
        return null; // for now
    }

    private static void storeIndicesBuffer(int[] indices) {
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = MemoryUtils.store(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private static int storeDataInAttributeList(final ModelVertexAttribute attribute, final float[] data) {
        if (data == null) return -1;
        final int attributeNumber = attribute.ordinal();
        final int attributeUnitSize = attribute.attributeUnitSize;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); // bind
        FloatBuffer buffer = MemoryUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, attributeUnitSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind
        return vbo;
    }

    @Deprecated private static int storeDataInAttributeList(int attributeNumber, int attributeUnitSize, float[] data) {
        if (data == null) return -1;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); // bind
        FloatBuffer buffer = MemoryUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, attributeUnitSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind
        return vbo;
    }

}
