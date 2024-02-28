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
                -1.0f, -1.0f, -1.0f, // 0. Left-Bottom-Back
                1.0f, -1.0f, -1.0f, // 1. Right-Bottom-Back
                -1.0f,  1.0f, -1.0f, // 2. Left-Top-Back
                1.0f,  1.0f, -1.0f, // 3. Right-Top-Back
                -1.0f, -1.0f,  1.0f, // 4. Left-Bottom-Front
                1.0f, -1.0f,  1.0f, // 5. Right-Bottom-Front
                -1.0f,  1.0f,  1.0f, // 6. Left-Top-Front
                1.0f,  1.0f,  1.0f, // 7. Right-Top-Front
        };

        // Texture coordinates (6 faces, 4 corners each, 2 coordinates per corner)
        float[] textureCoordinates = {
                0.0f, 0.0f,  // back bottom left
                1.0f, 0.0f,  // back bottom right
                0.0f, 1.0f,   // back top left
                1.0f, 1.0f,  // back top right
                0.0f, 0.0f,  // front bottom left
                1.0f, 0.0f,  // front bottom right
                0.0f, 1.0f,  // front top left
                1.0f, 1.0f,  // front top right
        };

        // Normals of the vertices (each face has the same normal)
        float[] normals = {
                0.0f,  1.0f,  0.0f,  // front
                0.0f, -1.0f,  0.0f,  // back
                1.0f,  0.0f,  0.0f,  // right
                -1.0f,  0.0f,  0.0f,  // left
                0.0f,  0.0f,  1.0f,  // top
                0.0f,  0.0f, -1.0f,  // bottom
        };

        // Index buffer to specify the order of vertices to draw each face
        // (note: this uses triangle strips for efficiency)
        int[] indices = {
                // Front face
                4, 5, 7, 7, 6, 4,
                // Back face
                0, 2, 3, 3, 1, 0,
                // Left face
                0, 4, 6, 6, 2, 0,
                // Right face
                1, 3, 7, 7, 5, 1,
                // Top face
                2, 6, 7, 7, 3, 2,
                // Bottom face
                0, 1, 5, 5, 4, 0
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
