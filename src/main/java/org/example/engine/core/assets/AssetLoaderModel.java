package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.collections.MapObjectInt;
import org.example.engine.core.graphics.Color;
import org.example.engine.core.graphics.Model;
import org.example.engine.core.graphics.ModelVertexAttribute;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AssetLoaderModel implements AssetLoader<Model> {

    private ModelPartMaterialData[] materialsData;
    private ModelPartMeshData[] meshesData;
    private ModelPartData[] modelPartsData;
    private ModelData modelData;

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return null;
    }

    @Override
    public Model create() {
        return null;
    }

    @Override
    public void asyncLoad(final String path) {
        try (AIScene aiScene = Assimp.aiImportFile(path,
                Assimp.aiProcess_Triangulate | Assimp.aiProcess_GenNormals | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_CalcTangentSpace
                       | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_ImproveCacheLocality | Assimp.aiProcess_RemoveRedundantMaterials)) {
            PointerBuffer aiMaterials  = aiScene.mMaterials();
            int numMaterials = aiScene.mNumMaterials();
            materialsData = new ModelPartMaterialData[numMaterials];
            for (int i = 0; i < numMaterials; i++) {
                final ModelPartMaterialData materialData = processMaterial(AIMaterial.create(aiMaterials.get(i)));
                materialsData[i] = materialData;
            }

            PointerBuffer aiMeshes = aiScene.mMeshes();
            int numMeshes = aiScene.mNumMeshes();
            meshesData = new ModelPartMeshData[numMeshes];
            for (int i = 0; i < numMeshes; i++) {
                final ModelPartMeshData meshData = processMesh(AIMesh.create(aiMeshes.get(i)));
                meshesData[i] = meshData;
            }


        }

    }

    private ModelPartMaterialData processMaterial(final AIMaterial aiMaterial) {
        ModelPartMaterialData modelPartMaterialData = new ModelPartMaterialData();

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
                final String uniform = entry.key;
                AIString path = AIString.calloc();
                IntBuffer mapping = stack.mallocInt(1);
                IntBuffer uvIndex = stack.mallocInt(1);
                IntBuffer op = stack.mallocInt(1);
                IntBuffer mapMode = stack.mallocInt(1);
                FloatBuffer blendMode = stack.mallocFloat(1);
                int result = Assimp.aiGetMaterialTexture(aiMaterial, entry.value, 0, path, (IntBuffer) mapping, uvIndex, blendMode, op, mapMode, null);
                if (result == Assimp.aiReturn_SUCCESS) {
                    TextureParameters params = new TextureParameters();
                    params.path = path.dataString();
                    params.mapMode = mapMode.get(0);
                    params.op = op.get(0);
                    params.blendMode = blendMode.get(0);
                    params.uvIndex = uvIndex.get(0);
                    params.mapping = mapping.get(0);
                    modelPartMaterialData.attributesData.put(uniform, params);
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
                modelPartMaterialData.attributesData.put(colorEntry.getKey(), new Color(colour.r(), colour.g(), colour.b(), colour.a()));
            }
        }

        // set dependencies
        return modelPartMaterialData;
    }

    private ModelPartMeshData processMesh(final AIMesh aiMesh) {
        ModelPartMeshData meshData = new ModelPartMeshData();
        meshData.vertexCount = aiMesh.mNumVertices();
        meshData.vertexBuffers.put(ModelVertexAttribute.POSITION, getPositions(aiMesh));
        meshData.vertexBuffers.put(ModelVertexAttribute.COLOR, getColors(aiMesh));
        meshData.vertexBuffers.put(ModelVertexAttribute.TEXTURE_COORDINATES0, getTextureCoords0(aiMesh));
        meshData.vertexBuffers.put(ModelVertexAttribute.TEXTURE_COORDINATES1, getTextureCoords1(aiMesh));
        meshData.vertexBuffers.put(ModelVertexAttribute.NORMAL, getNormals(aiMesh));
        meshData.vertexBuffers.put(ModelVertexAttribute.TANGENT, getTangents(aiMesh));
        meshData.vertexBuffers.put(ModelVertexAttribute.BI_NORMAL, getBiNormals(aiMesh));
        meshData.indices = getIndices(aiMesh);
        return meshData;
    }

    private float[] getPositions(final AIMesh mesh) {
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

    private float[] getColors(final AIMesh mesh) {
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

    private float[] getTextureCoords0(final AIMesh mesh) {
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

    private float[] getTextureCoords1(final AIMesh mesh) {
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

    private float[] getNormals(final AIMesh mesh) {
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

    private float[] getTangents(final AIMesh mesh) {
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

    private float[] getBiNormals(final AIMesh mesh) {
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

    private int[] getIndices(final AIMesh mesh) {
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
        return indices.items;
    }



    private static class ModelPartMeshData {
        public int vertexCount;
        public Map<ModelVertexAttribute, Object> vertexBuffers = new HashMap<>();
        public int[] indices;
    }

    private static class ModelPartMaterialData {
        public Map<String, Object> attributesData = new HashMap<>();
    }

    private static class ModelPartData {
        public ModelPartMeshData meshData;
        public ModelPartMaterialData materialData;
    }

    private static class ModelArmatureData {

    }

    private static class ModelData {
        public ModelPartData[] partsData;
        public ModelArmatureData armatureData;
    }

    private static class TextureParameters {
        public String path;
        public int uvIndex;
        public int mapping;
        public int mapMode;
        public int op;
        public float blendMode;
    }
}
