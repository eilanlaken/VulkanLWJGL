package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.graphics.*;
import org.example.game.ScreenLoading;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
//        WindowAttributes config = new WindowAttributes();
//        Application.createSingleWindowApplication(config);
//        Application.launch(new ScreenLoading());


        try {
            loadModel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadModel() throws Exception {
        String path = "assets/models/cube.fbx";
        Array<ModelPart> parts = new Array<>();

        try (AIScene aiScene = Assimp.aiImportFile(path, Assimp.aiProcess_Triangulate | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_FixInfacingNormals)) {
            int numMaterials = aiScene.mNumMaterials();
            PointerBuffer aiMaterials = aiScene.mMaterials();
            Array<ModelPartMaterial> materials = new Array<>();
            for (int i = 0; i < numMaterials; i++) {
                AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
                processMaterial(aiMaterial, materials);
            }

            int numMeshes = aiScene.mNumMeshes();
            PointerBuffer aiMeshes = aiScene.mMeshes();
            for (int i = 0; i < numMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                ModelPart part = processModelPart(aiMesh, materials);
                parts.add(part);
            }
        }
    }

    private static void processMaterial(AIMaterial aiMaterial, Array<ModelPartMaterial> materials) throws Exception {
        AIColor4D colour = AIColor4D.create();
        AIString path = AIString.calloc();


        Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
        String diffusePath = path.dataString();
        Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_NORMALS, 0, path, (IntBuffer) null, null, null, null, null, null);
        String normalPath = path.dataString();

        Color ambient = new Color();
        int result = Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, colour);
        if (result == 0) {
            ambient.set(colour.r(), colour.g(), colour.b(), colour.a());
        }

        System.out.println(diffusePath);

        HashMap<String, Object> materialAttributes = new HashMap<>();
        materialAttributes.put("color_ambient", ambient);
        ModelPartMaterial material = new ModelPartMaterial(materialAttributes);
        materials.add(material);
    }

    private static ModelPart processModelPart(AIMesh aiMesh, Array<ModelPartMaterial> materials) {
        float[] positions = getPositions(aiMesh);
        float[] textCoords0 = getTextureCoords0(aiMesh);
        float[] normals = getNormals(aiMesh);
        int[] indices = getIndices(aiMesh);

        System.out.println("positions");
        System.out.println(positions[0]);
        System.out.println("textCoords0");
        System.out.println(textCoords0);
        System.out.println("normals");
        System.out.println(normals);
        System.out.println("indices");
        System.out.println(indices[4]);

        ModelPartMesh mesh = null;//ModelBuilder.create(positions, textCoords0, normals, indices);
        ModelPartMaterial material;
        //System.out.println("ok");

        int materialIdx = aiMesh.mMaterialIndex();
        //System.out.println(materialIdx);

        if (materialIdx >= 0 && materialIdx < materials.size) {
            material = materials.get(materialIdx);
        } else {
            HashMap<String, Object> defaultMaterialAttributes = new HashMap<>();
            defaultMaterialAttributes.put("diffuse_color",  new Color(1.0f, 0.0f, 1.0f, 1.0f));
            material = new ModelPartMaterial(defaultMaterialAttributes);
        }

        return new ModelPart(mesh, material, null);
    }


    private static float[] getPositions(AIMesh mesh) {
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

    private static float[] getTextureCoords0(AIMesh mesh) {
        float[] textureCoordinates0 = new float[mesh.mVertices().limit() * 2];
        AIVector3D.Buffer textureCoordinatesBuffer = mesh.mTextureCoords(0);
        for (int i = 0; i < textureCoordinatesBuffer.limit(); i++) {
            AIVector3D coordinates = textureCoordinatesBuffer.get(i);
            textureCoordinates0[2*i] = coordinates.x();
            textureCoordinates0[2*i+1] = coordinates.y();
        }
        return textureCoordinates0;
    }

    private static float[] getTextureCoords1(AIMesh mesh) {
        float[] textureCoordinates1 = new float[mesh.mVertices().limit() * 2];
        AIVector3D.Buffer textureCoordinatesBuffer = mesh.mTextureCoords(1);
        for (int i = 0; i < textureCoordinatesBuffer.limit(); i++) {
            AIVector3D coordinates = textureCoordinatesBuffer.get(i);
            textureCoordinates1[2*i] = coordinates.x();
            textureCoordinates1[2*i+1] = coordinates.y();
        }
        return textureCoordinates1;
    }

    private static float[] getNormals(AIMesh mesh) {
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

    private static float[] getColors(AIMesh mesh) {
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

    private static int[] getIndices(AIMesh mesh) {
        int faceCount = mesh.mNumFaces();
        AIFace.Buffer faces = mesh.mFaces();
        ArrayInt indices = new ArrayInt();
        for (int i = 0; i < faceCount; i++) {
            AIFace face = faces.get(i);
            IntBuffer buffer = face.mIndices();
            while (buffer.remaining() > 0) {
                int index = buffer.get();
                indices.add(index);
            }
        }
        indices.pack();
        return indices.items;
    }

}