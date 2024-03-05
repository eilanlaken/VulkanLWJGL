package org.example;

import org.example.engine.core.application.Application;
import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.collections.MapObjectInt;
import org.example.engine.core.graphics.*;
import org.example.game.ScreenLoading;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        WindowAttributes config = new WindowAttributes();
        Application.createSingleWindowApplication(config);
        Application.launch(new ScreenLoading());


//        try {
//            loadModel();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private static void loadModel() throws Exception {
        String path = "assets/models/cube.obj";

        try (AIScene aiScene = Assimp.aiImportFile(path, Assimp.aiProcess_Triangulate | Assimp.aiProcess_GenUVCoords | Assimp.aiProcess_GenNormals | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_CalcTangentSpace
                | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_ImproveCacheLocality | Assimp.aiProcess_RemoveRedundantMaterials)) {

            int numMaterials = aiScene.mNumMaterials();
            PointerBuffer aiMaterials = aiScene.mMaterials();
            Array<ModelPartMaterial> materials = new Array<>();
            for (int i = 0; i < numMaterials; i++) {
                AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
                processMaterial(aiMaterial, materials);
            }

            int numMeshes = aiScene.mNumMeshes();
            System.out.println("num meshes " + numMeshes);
            PointerBuffer aiMeshes = aiScene.mMeshes();
            for (int i = 0; i < numMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                processMesh(aiMesh, materials);
            }
        }
    }

    private static void processMaterial(AIMaterial aiMaterial, Array<ModelPartMaterial> materials) throws Exception {

        MapObjectInt<String> namedTextureTypes = new MapObjectInt<>();
        namedTextureTypes.put("textureBaseColor", Assimp.aiTextureType_BASE_COLOR);
        namedTextureTypes.put("textureNormal", Assimp.aiTextureType_NORMALS);
        namedTextureTypes.put("textureDiffuse", Assimp.aiTextureType_DIFFUSE);
        namedTextureTypes.put("textureNone", Assimp.aiTextureType_NONE);
        namedTextureTypes.put("textureAmbient", Assimp.aiTextureType_AMBIENT);
        namedTextureTypes.put("textureAmbientOcclusion", Assimp.aiTextureType_AMBIENT_OCCLUSION);
        namedTextureTypes.put("textureClearCoat", Assimp.aiTextureType_CLEARCOAT);
        namedTextureTypes.put("textureDiffuseRoughness", Assimp.aiTextureType_DIFFUSE_ROUGHNESS);
        namedTextureTypes.put("textureDisplacement", Assimp.aiTextureType_DISPLACEMENT);
        namedTextureTypes.put("textureEmissionColor", Assimp.aiTextureType_EMISSION_COLOR);
        namedTextureTypes.put("textureEmissive", Assimp.aiTextureType_EMISSIVE);
        namedTextureTypes.put("textureHeight", Assimp.aiTextureType_HEIGHT);
        namedTextureTypes.put("textureLightmap", Assimp.aiTextureType_LIGHTMAP);
        namedTextureTypes.put("textureMetallic", Assimp.aiTextureType_METALNESS);
        namedTextureTypes.put("textureReflection", Assimp.aiTextureType_REFLECTION);
        namedTextureTypes.put("textureSpecular", Assimp.aiTextureType_SPECULAR);
        namedTextureTypes.put("textureShininess", Assimp.aiTextureType_SHININESS);
        namedTextureTypes.put("textureNormalCamera", Assimp.aiTextureType_NORMAL_CAMERA);
        namedTextureTypes.put("textureSheen", Assimp.aiTextureType_SHEEN);
        namedTextureTypes.put("textureOpacity", Assimp.aiTextureType_OPACITY);
        namedTextureTypes.put("textureTransmission", Assimp.aiTextureType_TRANSMISSION);
        namedTextureTypes.put("textureUnknown", Assimp.aiTextureType_UNKNOWN);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            for (MapObjectInt.Entry<String> entry : namedTextureTypes) {
                int val = entry.value;
                if (val == Assimp.aiTextureType_DIFFUSE) System.out.println("diffuse");

                AIString path = AIString.calloc();
                IntBuffer mapping = stack.mallocInt(1);
                IntBuffer uvIndex = stack.mallocInt(1);
                IntBuffer op = stack.mallocInt(1);
                IntBuffer mapMode = stack.mallocInt(1);
                FloatBuffer blendMode = stack.mallocFloat(1);
                int result = Assimp.aiGetMaterialTexture(aiMaterial, entry.value, 0, path, (IntBuffer) mapping, uvIndex, blendMode, op, mapMode, null);

                // Assimp.aiTextureMapping_SPHERE;
                if (result == Assimp.aiReturn_SUCCESS) {
                    System.out.println("texture: " + entry.key);
                    System.out.println(path.dataString());
                    System.out.println("uv index: " + uvIndex.get(0));
                    System.out.println("mapping: " + mapping.get(0));
                    System.out.println("map mode: " + mapMode.get(0));
                    System.out.println("op: " + op.get(0));
                    System.out.println("blend mode: " + blendMode.get(0));
                }
            }
        }


        Map<String, String> namedColorTypes = new HashMap<>();
        namedColorTypes.put("colorAmbient", Assimp.AI_MATKEY_COLOR_AMBIENT);
        namedColorTypes.put("colorDiffuse", Assimp.AI_MATKEY_COLOR_DIFFUSE);
        namedColorTypes.put("colorEmissive", Assimp.AI_MATKEY_COLOR_EMISSIVE);
        namedColorTypes.put("colorReflective", Assimp.AI_MATKEY_COLOR_REFLECTIVE);
        namedColorTypes.put("colorSpecular", Assimp.AI_MATKEY_COLOR_SPECULAR);
        namedColorTypes.put("colorTransparent", Assimp.AI_MATKEY_COLOR_TRANSPARENT);

        AIColor4D colour = AIColor4D.create();
        for (Map.Entry<String, String> colorEntry : namedColorTypes.entrySet()) {
            int result = Assimp.aiGetMaterialColor(aiMaterial, colorEntry.getValue(), Assimp.aiTextureType_NONE, 0, colour);
            if (result == Assimp.aiReturn_SUCCESS) {
                System.out.println(colorEntry.getKey() + ": " + colour.r() + " " + colour.g() + " " + colour.b() + " " + colour.a());
            }
        }

        // create ModelPartMaterialData and add used dependencies.
        System.out.println("materials done");


    }

    private static void processMesh(AIMesh aiMesh, Array<ModelPartMaterial> materials) {
        System.out.println("mesh data:");
        System.out.println("vertices: " + aiMesh.mNumVertices());

        System.out.println("pos");
        float[] positions = getPositions(aiMesh);
        System.out.println(Arrays.toString(positions));

        System.out.println("textCoords0");
        float[] textCoords0 = getTextureCoords0(aiMesh);
        System.out.println(Arrays.toString(textCoords0));

        System.out.println("textCoords1");
        float[] textCoords1 = getTextureCoords1(aiMesh);
        System.out.println(Arrays.toString(textCoords1));

        System.out.println("normals");
        float[] normals = getNormals(aiMesh);
        System.out.println(Arrays.toString(normals));

        System.out.println("tangents");
        float[] tangents = getTangents(aiMesh);
        System.out.println(Arrays.toString(tangents));

        System.out.println("biNormals");
        float[] biNormals = getBiNormals(aiMesh);
        System.out.println(Arrays.toString(biNormals));

        System.out.println("indices");
        int[] indices = getIndices(aiMesh);
        System.out.println(Arrays.toString(indices));

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

    }



    private static float[] getPositions(AIMesh mesh) {
        AIVector3D.Buffer positionsBuffer = mesh.mVertices();
        float[] positions = new float[mesh.mVertices().limit() * 3];
        for (int i = 0; i < positionsBuffer.limit(); i++) {
            AIVector3D vector3D = positionsBuffer.get(i);
            positions[3*i] = vector3D.x();
            positions[3*i+1] = vector3D.y();
            positions[3*i+2] = vector3D.z();
        }
        return positions;
    }

    private static float[] getColors(AIMesh mesh) {
        AIColor4D.Buffer colorsBuffer = mesh.mColors(0);
        if (colorsBuffer == null) return null;
        float[] colors = new float[colorsBuffer.limit() * 4];
        for (int i = 0; i < colorsBuffer.limit(); i++) {
            AIColor4D color = colorsBuffer.get(i);
            colors[4*i] = color.r();
            colors[4*i+1] = color.g();
            colors[4*i+2] = color.b();
            colors[4*i+3] = color.a();
        }
        return colors;
    }

    private static float[] getTextureCoords0(AIMesh mesh) {
        AIVector3D.Buffer textureCoordinatesBuffer = mesh.mTextureCoords(0);
        if (textureCoordinatesBuffer == null) return null;
        float[] textureCoordinates0 = new float[mesh.mVertices().limit() * 2];
        for (int i = 0; i < textureCoordinatesBuffer.limit(); i++) {
            AIVector3D coordinates = textureCoordinatesBuffer.get(i);
            textureCoordinates0[2*i] = coordinates.x();
            textureCoordinates0[2*i+1] = coordinates.y();
        }
        return textureCoordinates0;
    }

    private static float[] getTextureCoords1(AIMesh mesh) {
        AIVector3D.Buffer textureCoords1Buffer = mesh.mTextureCoords(1);
        if (textureCoords1Buffer == null) return null;
        float[] textureCoordinates1 = new float[mesh.mVertices().limit() * 2];
        for (int i = 0; i < textureCoords1Buffer.limit(); i++) {
            AIVector3D coordinates = textureCoords1Buffer.get(i);
            textureCoordinates1[2*i] = coordinates.x();
            textureCoordinates1[2*i+1] = coordinates.y();
        }
        return textureCoordinates1;
    }

    private static float[] getNormals(AIMesh mesh) {
        AIVector3D.Buffer normalsBuffer = mesh.mNormals();
        if (normalsBuffer == null) return null;
        float[] normals = new float[normalsBuffer.limit() * 3];
        for (int i = 0; i < normalsBuffer.limit(); i++) {
            AIVector3D vector3D = normalsBuffer.get(i);
            normals[3*i] = vector3D.x();
            normals[3*i+1] = vector3D.y();
            normals[3*i+2] = vector3D.z();
        }
        return normals;
    }

    private static float[] getTangents(AIMesh mesh) {
        AIVector3D.Buffer tangentsBuffer = mesh.mTangents();
        if (tangentsBuffer == null) return null;
        float[] tangents = new float[tangentsBuffer.limit() * 3];
        for (int i = 0; i < tangentsBuffer.limit(); i++) {
            AIVector3D vector3D = tangentsBuffer.get(i);
            tangents[3*i] = vector3D.x();
            tangents[3*i+1] = vector3D.y();
            tangents[3*i+2] = vector3D.z();
        }
        return tangents;
    }

    private static float[] getBiNormals(AIMesh mesh) {
        AIVector3D.Buffer biNormalsBuffer = mesh.mBitangents();
        if (biNormalsBuffer == null) return null;
        float[] biNormals = new float[biNormalsBuffer.limit() * 3];
        for (int i = 0; i < biNormalsBuffer.limit(); i++) {
            AIVector3D vector3D = biNormalsBuffer.get(i);
            biNormals[3*i] = vector3D.x();
            biNormals[3*i+1] = vector3D.y();
            biNormals[3*i+2] = vector3D.z();
        }
        return biNormals;
    }


    // https://github.com/LWJGL/lwjgl3-demos/blob/main/src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java
    // line 393
    private static int[] getIndices(AIMesh mesh) {
        int faceCount = mesh.mNumFaces();
        if (faceCount <= 0) return null;
        AIFace.Buffer faces = mesh.mFaces();
        ArrayInt indices = new ArrayInt(faceCount * 3);
        for (int i = 0; i < faceCount; i++) {
            AIFace face = faces.get(i);
            if (face.mNumIndices() != 3) throw new IllegalArgumentException("Faces were not properly triangulated");
            IntBuffer buffer = face.mIndices();
            while (buffer.remaining() > 0) indices.add(buffer.get());
        }
        return indices.pack().items;
    }



}