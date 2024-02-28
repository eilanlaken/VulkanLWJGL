package org.example.engine.core.graphics;

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
        materialAttributes.put("color", new Color(1,0,0,1));
        ModelPartMaterial material = new ModelPartMaterial(materialAttributes);
        return new ModelPart(mesh, material, null);
    }

    public static ModelPartMesh create(float[] positions, float[] textureCoordinates, float[] normals, int[] indices) {
        int vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
        storeIndicesBuffer(indices);
        int vboPositions = storeDataInAttributeList(0, 3, positions);
        int vboTextureCoordinates = storeDataInAttributeList(1, 2, textureCoordinates);
        int vboNormals = storeDataInAttributeList(2, 3, normals);
        GL30.glBindVertexArray(0);
        return new ModelPartMesh(vaoId, indices.length, vboPositions, vboTextureCoordinates, vboNormals);
    }

    private static ModelPartMaterial create(HashMap<String, Object> materialParams) {
        return new ModelPartMaterial(materialParams);
    }

    private static ModelPart build(final ModelPartMesh mesh, ModelPartMaterial material) {
        return new ModelPart(mesh, material, null);
    }

    public static Model build(ModelPart modelPart) {
        return new Model(modelPart);
    }

    public static Model buildTexturedCube(float width, float height, float depth, Texture texture) {

        return null;
    }

    public static Model build(float[] positions,
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
