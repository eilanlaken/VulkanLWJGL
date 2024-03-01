package org.example.engine.core.files;

import org.example.engine.core.graphics.Model;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

public class AssetLoaderModel implements AssetLoader<Model> {

    // TODO: complete implementing.
    @Override
    public Model load(final String path) {
        try (AIScene scene = Assimp.aiImportFile(path, Assimp.aiProcess_Triangulate)) {
            PointerBuffer bufferMeshes = scene.mMeshes();
            PointerBuffer bufferMaterials = scene.mMaterials();
            PointerBuffer bufferTextures = scene.mTextures();

            for (int i = 0; i < bufferMeshes.limit(); i++) {
                AIMesh mesh = AIMesh.create(bufferMeshes.get(i));
                float[] positions = getPositions(mesh);
                System.out.println(positions);
            }

        }
        return null;
    }

    private float[] getPositions(AIMesh mesh) {
        float[] positions = new float[mesh.mVertices().limit() * 3];
        AIVector3D.Buffer positionsBuffer = mesh.mVertices();
        for (int i = 0; i < positionsBuffer.limit(); i++) {
            AIVector3D vector3D = positionsBuffer.get(i);
            positions[3*i] = vector3D.x();
            positions[3*i+1] = vector3D.y();
            positions[3*i+2] = vector3D.z();
        }
        return positions;
    }

    private float[] getTextureCoordinates(AIMesh mesh) {
        float[] textureCoordinates = new float[mesh.mVertices().limit() * 2];
        AIVector3D.Buffer textureCoordinatesBuffer = mesh.mTextureCoords(0);
        for (int i = 0; i < textureCoordinatesBuffer.limit(); i++) {
            AIVector3D coordinates = textureCoordinatesBuffer.get(i);
            textureCoordinates[2*i] = coordinates.x();
            textureCoordinates[2*i+1] = coordinates.y();
        }
        return textureCoordinates;
    }

    private float[] getNormals(AIMesh mesh) {
        float[] normals = new float[mesh.mVertices().limit() * 3];
        AIVector3D.Buffer normalsBuffer = mesh.mNormals();
        for (int i = 0; i < normalsBuffer.limit(); i++) {
            AIVector3D vector3D = normalsBuffer.get(i);
            normals[3*i] = vector3D.x();
            normals[3*i+1] = vector3D.y();
            normals[3*i+2] = vector3D.z();
        }
        return normals;
    }

    private float[] getColors(AIMesh mesh) {
        float[] colors = new float[mesh.mColors().limit() * 4];
        AIColor4D.Buffer colorsBuffer = mesh.mColors(0);
        for (int i = 0; i < colorsBuffer.limit(); i++) {
            AIColor4D color = colorsBuffer.get(i);
            colors[4*i] = color.r();
            colors[4*i+1] = color.g();
            colors[4*i+2] = color.b();
            colors[4*i+3] = color.a();
        }
        return colors;
    }
}
