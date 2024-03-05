package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;
import org.example.engine.core.collections.ArrayInt;
import org.example.engine.core.collections.MapObjectInt;
import org.example.engine.core.graphics.*;
import org.example.engine.core.memory.MemoryUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AssetLoaderModel implements AssetLoader<Model> {

    private ModelPartMaterialData[] materialsData;
    private ModelPartMeshData[] meshesData;
    private ModelPartData[] partsData;
    private ModelArmatureData armatureData;

    @Override
    public Array<AssetDescriptor> getDependencies() {
        // TODO: see if the path is relative etc.
        Array<AssetDescriptor> dependencies = new Array<>();
        for (ModelPartMaterialData materialData : materialsData) {
            Map<String, Object> attributesData = materialData.attributesData;
            for (Map.Entry<String, Object> entry : attributesData.entrySet()) {
                if (entry instanceof TextureParameters) dependencies.add(new AssetDescriptor(Texture.class, ((TextureParameters) entry).path));
            }
        }
        return dependencies;
    }

    @Override
    public Model create() {
        ModelPart[] parts = new ModelPart[partsData.length];
        for (int i = 0; i < parts.length; i++) {
            // create material
            ModelPartData partData = partsData[i];
            ModelPartMaterialData materialData = partData.materialData;
            ModelPartMeshData meshData = partData.meshData;
            HashMap<String, Object> materialAttributes = new HashMap<>();
            for (Map.Entry<String, Object> materialDataEntry : materialData.attributesData.entrySet()) {
                final String uniform = materialDataEntry.getKey();
                final Object dataValue = materialDataEntry.getValue();
                if (dataValue instanceof Color) {
                    Color color = (Color) dataValue;
                    materialAttributes.put(uniform, new Color(color.r, color.g, color.b, color.a));
                } else if (dataValue instanceof TextureParameters) {
                    TextureParameters params = (TextureParameters) dataValue;
                    Texture texture = AssetStore.get(params.path);
                    materialAttributes.put(uniform, texture);
                }
            }
            ModelPartMaterial material = new ModelPartMaterial(materialAttributes);
            ModelPartMesh mesh = create(meshData);
            parts[i] = new ModelPart(mesh, material, null);
        }
        final ModelArmature armature = new ModelArmature();
        return new Model(parts, armature);
    }

    @Override
    public void asyncLoad(final String path) {
        final int importFlags = Assimp.aiProcess_Triangulate | Assimp.aiProcess_GenUVCoords |
                Assimp.aiProcess_GenNormals | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_CalcTangentSpace
                | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_ImproveCacheLocality |
                Assimp.aiProcess_RemoveRedundantMaterials;
            try (AIScene aiScene = Assimp.aiImportFile(path, importFlags)) {
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

            partsData = new ModelPartData[numMeshes];
            for (int i = 0; i < partsData.length; i++) {
                partsData[i] = new ModelPartData();
                partsData[i].meshData = meshesData[i];
                partsData[i].materialData = materialsData[meshesData[i].materialIndex];
            }

            armatureData = new ModelArmatureData();
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
        meshData.materialIndex = aiMesh.mMaterialIndex();
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

    private int[] getIndices(final AIMesh aiMesh) {
        int faceCount = aiMesh.mNumFaces();
        System.out.println("face count: " + faceCount * 3);
        if (faceCount <= 0) return null;
        AIFace.Buffer faces = aiMesh.mFaces();
        ArrayInt indices = new ArrayInt(faceCount * 3);
        for (int i = 0; i < faceCount; i++) {
            AIFace aiFace = faces.get(i);
            if (aiFace.mNumIndices() != 3) throw new IllegalArgumentException("Faces were not properly triangulated");
            for (int j = 0; j < aiFace.mNumIndices(); j++)
                indices.add(aiFace.mIndices().get(j));
        }
        return indices.pack().items;
    }

    private ModelPartMesh create(final ModelPartMeshData meshData) {
        Array<ModelVertexAttribute> attributesCollector = new Array<>();
        ArrayInt vbosCollector = new ArrayInt();
        int vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
        {
            storeIndicesBuffer(meshData.indices, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.POSITION, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.COLOR, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.TEXTURE_COORDINATES0, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.TEXTURE_COORDINATES1, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.NORMAL, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.TANGENT, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.BI_NORMAL, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT0, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT1, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT2, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT3, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT4, meshData, attributesCollector, vbosCollector);
            storeDataInAttributeList(ModelVertexAttribute.BONE_WEIGHT5, meshData, attributesCollector, vbosCollector);
        }
        GL30.glBindVertexArray(0);
        final short bitmask = generateBitmask(attributesCollector);
        final int[] vbos = vbosCollector.pack().items;
        return new ModelPartMesh(vaoId, meshData.vertexCount, bitmask, meshData.indices != null, vbos);
    }

    private void storeIndicesBuffer(int[] indices, ArrayInt vbosCollector) {
        if (indices == null) return;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = MemoryUtils.store(indices);
        System.out.println(Arrays.toString(indices));
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        vbosCollector.add(vbo);
    }

    private void storeDataInAttributeList(final ModelVertexAttribute attribute, final ModelPartMeshData meshData, Array<ModelVertexAttribute> attributesCollector, ArrayInt vbosCollector) {
        final float[] data = (float[]) meshData.vertexBuffers.get(attribute);
        if (data == null) return;
        final int attributeNumber = attribute.ordinal();
        final int attributeUnitSize = attribute.length;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); // bind
        FloatBuffer buffer = MemoryUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, attributeUnitSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind
        attributesCollector.add(attribute);
        vbosCollector.add(vbo);
    }

    private short generateBitmask(final Array<ModelVertexAttribute> attributes) {
        short bitmask = 0b0000;
        for (final ModelVertexAttribute attribute : attributes) {
            bitmask |= attribute.bitmask;
        }
        return bitmask;
    }

    private static class ModelPartMeshData {
        public int vertexCount;
        public Map<ModelVertexAttribute, Object> vertexBuffers = new HashMap<>();
        public int materialIndex;
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

        ModelData(final ModelPartData[] partsData, final ModelArmatureData armatureData) {
            this.partsData = partsData;
            this.armatureData = armatureData;
        }

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
