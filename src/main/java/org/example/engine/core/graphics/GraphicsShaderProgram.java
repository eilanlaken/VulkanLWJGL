package org.example.engine.core.graphics;

import org.example.engine.core.collections.CollectionsMapObjectInt;
import org.example.engine.core.math.MathMatrix4;
import org.example.engine.core.math.MathQuaternion;
import org.example.engine.core.math.MathVector3;
import org.example.engine.core.math.MathVector4;
import org.example.engine.core.memory.MemoryResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class GraphicsShaderProgram implements MemoryResource {

    public final String vertexShaderSource;
    public final String fragmentShaderSource;
    public final int program;
    protected final int vertexShaderId;
    protected final int fragmentShaderId;
    private String log;
    private boolean isCompiled;
    private final CollectionsMapObjectInt<String> uniformLocations;
    private final CollectionsMapObjectInt<String> uniformTypes;
    private final CollectionsMapObjectInt<String> uniformSizes;
    private String[] uniformNames;
    private final CollectionsMapObjectInt<String> attributeLocations;
    private final CollectionsMapObjectInt<String> attributeTypes;
    private final CollectionsMapObjectInt<String> attributeSizes;
    private String[] attributeNames;
    private Object[] uniformCache;

    public GraphicsShaderProgram(final String vertexShaderSource, final String fragmentShaderSource) {
        if (vertexShaderSource == null) throw new IllegalArgumentException("Vertex shader cannot be null.");
        if (fragmentShaderSource == null) throw new IllegalArgumentException("Fragment shader cannot be null.");
        this.vertexShaderSource = vertexShaderSource;
        this.fragmentShaderSource = fragmentShaderSource;
        // attributes
        this.attributeLocations = new CollectionsMapObjectInt<>();
        this.attributeTypes = new CollectionsMapObjectInt<>();
        this.attributeSizes = new CollectionsMapObjectInt<>();
        // uniforms
        this.uniformLocations = new CollectionsMapObjectInt<>();
        this.uniformTypes = new CollectionsMapObjectInt<>();
        this.uniformSizes = new CollectionsMapObjectInt<>();
        this.program = GL20.glCreateProgram();
        if (program == 0)
            throw new RuntimeException("Could not create shader");
        this.vertexShaderId = createVertexShader(vertexShaderSource);
        this.fragmentShaderId = createFragmentShader(fragmentShaderSource);
        link();
        registerAttributes();
        registerUniforms();
        this.uniformCache = new Object[uniformNames.length];
        validate();
    }

    private int createVertexShader(final String shaderCode) {
        int shaderId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        if (shaderId == 0)
            throw new RuntimeException("Error creating vertex shader.");
        GL20.glShaderSource(shaderId, shaderCode);
        GL20.glCompileShader(shaderId);
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0)
            throw new RuntimeException("Error compiling vertex shader: " + GL20.glGetShaderInfoLog(shaderId, 1024));
        GL20.glAttachShader(program, shaderId);
        return shaderId;
    }

    private int createFragmentShader(final String shaderCode) {
        int shaderId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        if (shaderId == 0)
            throw new RuntimeException("Error creating fragment shader.");
        GL20.glShaderSource(shaderId, shaderCode);
        GL20.glCompileShader(shaderId);
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0)
            throw new RuntimeException("Error compiling fragment shader: " + GL20.glGetShaderInfoLog(shaderId, 1024));
        GL20.glAttachShader(program, shaderId);
        return shaderId;
    }

    private void link() throws RuntimeException {
        GL20.glLinkProgram(program);
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == 0)
            throw new RuntimeException("Error linking shader code: " + GL20.glGetProgramInfoLog(program, 1024));

        if (vertexShaderId != 0)
            GL20.glDetachShader(program, vertexShaderId);

        if (fragmentShaderId != 0)
            GL20.glDetachShader(program, fragmentShaderId);

        GL20.glValidateProgram(program);
        if (GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) == 0)
            throw new RuntimeException("Could not validate shader code: " + GL20.glGetProgramInfoLog(program, 1024));
    }

    private void registerAttributes() {
        IntBuffer params = BufferUtils.createIntBuffer(1);
        IntBuffer type = BufferUtils.createIntBuffer(1);
        GL20.glGetProgramiv(this.program, GL20.GL_ACTIVE_ATTRIBUTES, params);
        int numAttributes = params.get(0);
        this.attributeNames = new String[numAttributes];
        for(int i = 0; i < numAttributes; ++i) {
            params.clear();
            params.put(0, 1);
            type.clear();
            String name = GL20.glGetActiveAttrib(this.program, i, params, type);
            int location = GL20.glGetAttribLocation(this.program, name);
            this.attributeLocations.put(name, location);
            this.attributeTypes.put(name, type.get(0));
            this.attributeSizes.put(name, params.get(0));
            this.attributeNames[i] = name;
        }
    }

    private void registerUniforms() {
        IntBuffer params = BufferUtils.createIntBuffer(1);
        IntBuffer type = BufferUtils.createIntBuffer(1);
        GL20.glGetProgramiv(this.program, GL20.GL_ACTIVE_UNIFORMS, params);
        int uniformSymbolsCount = params.get(0);
        for (int i = 0; i < uniformSymbolsCount; i++) {
            params.clear();
            params.put(0, 1);
            type.clear();
            String name = GL20.glGetActiveUniform(this.program, i, params, type);
            int size = params.get(0);
            final int location = GL20.glGetUniformLocation(this.program, name);
            this.uniformSizes.put(name, size);
            this.uniformTypes.put(name, type.get(0));
            this.uniformLocations.put(name, location);
            if (size > 1) { // array of uniforms.
                String prefix = name.replaceAll("\\[.*?]", "");;
                for (int k = 1; k < size; k++) {
                    String nextName = prefix + "[" + k + "]";
                    this.uniformSizes.put(nextName, size);
                    this.uniformTypes.put(nextName, type.get(0));
                    this.uniformLocations.put(nextName, location + k);
                }
            }
        }
        this.uniformNames = new String[uniformLocations.size];
        int i = 0;
        for(CollectionsMapObjectInt.Entry<String> entry : uniformLocations) {
            this.uniformNames[i] = entry.key;
            i++;
        }
    }

    protected final void bindUniforms(final HashMap<String, Object> uniforms) {
        if (uniforms == null) return;
        for (Map.Entry<String, Object> entry : uniforms.entrySet()) {
            final String name = entry.getKey();
            final Object value = uniforms.get(name);
            bindUniform(name, value);
        }
    }

    public void bindUniform(final String name, final Object value) {
        if (value == null) return;
        final int location = uniformLocations.get(name, -1);
        // TODO: remove. Good only for debugging, but prevents custom flexible shading.
        if (location == -1) throw new IllegalArgumentException("\n\nError: " + this.getClass().getSimpleName() +  " does not have a uniform named " + name + "." +
                "\nIf you have defined the uniform but have not used it, the GLSL compiler discarded it.\n");
        final int type = uniformTypes.get(name, -1);
        switch (type) {

            case GL20.GL_SAMPLER_2D:
                GraphicsTexture texture = (GraphicsTexture) value;
                int slot = GraphicsTextureBinder.bind(texture);
                if (isUniformIntegerCached(location, slot)) return;
                GL20.glUniform1i(location, slot);
                cacheUniformInteger(location, slot);
                break;

            case GL20.GL_INT:
                int i = (Integer) value;
                if (isUniformIntegerCached(location, i)) return;
                GL20.glUniform1i(location, i);
                cacheUniformInteger(location, i);
                break;

            case GL20.GL_FLOAT:
                float f = (Float) value;
                if (isUniformFloatCached(location, f)) return;
                GL20.glUniform1f(location, f);
                cacheUniformFloat(location, f);
                break;

            case GL20.GL_FLOAT_MAT4:
                MathMatrix4 matrix4 = (MathMatrix4) value;
                if (isUniformMatrix4Cached(location, matrix4)) return;
                GL20.glUniformMatrix4fv(location, false, matrix4.val);
                cacheUniformMatrix4(location, matrix4);
                break;

            case GL20.GL_FLOAT_VEC3:
                MathVector3 vector3 = (MathVector3) value;
                if (isUniformFloatTupleCached(location, vector3.x, vector3.y, vector3.z)) return;
                GL20.glUniform3f(location, vector3.x, vector3.y, vector3.z);
                cacheUniformFloatTuple(location, vector3.x, vector3.y, vector3.z);
                break;

            case GL20.GL_FLOAT_VEC4:
                if (value instanceof GraphicsColor) {
                    GraphicsColor color = (GraphicsColor) value;
                    if (isUniformFloatTupleCached(location, color.r, color.g, color.b, color.a)) return;
                    GL20.glUniform4f(location, color.r, color.g, color.b, color.a);
                    cacheUniformFloatTuple(location, color.r, color.g, color.b, color.a);
                } else if (value instanceof MathVector4) {
                    MathVector4 vector4 = (MathVector4) value;
                    if (isUniformFloatTupleCached(location, vector4.x, vector4.y, vector4.z, vector4.w)) return;
                    GL20.glUniform4f(location, vector4.x, vector4.y, vector4.z, vector4.w);
                    cacheUniformFloatTuple(location, vector4.x, vector4.y, vector4.z, vector4.w);
                } else if (value instanceof MathQuaternion) {
                    MathQuaternion quaternion = (MathQuaternion) value;
                    if (isUniformFloatTupleCached(location, quaternion.x, quaternion.y, quaternion.z, quaternion.w)) return;
                    GL20.glUniform4f(location, quaternion.x, quaternion.y, quaternion.z, quaternion.w);
                    cacheUniformFloatTuple(location, quaternion.x, quaternion.y, quaternion.z, quaternion.w);
                }
                break;

        }
    }

    private boolean isUniformIntegerCached(final int location, int value) {
        final IntegerCache cached = (IntegerCache) uniformCache[location];
        if (cached == null) return false;
        return cached.value == value;
    }

    private boolean isUniformBooleanCached(final int location, boolean value) {
        final BooleanCache cached = (BooleanCache) uniformCache[location];
        if (cached == null) return false;
        return cached.value == value;
    }

    private boolean isUniformFloatCached(final int location, float value) {
        final FloatCache cached = (FloatCache) uniformCache[location];
        if (cached == null) return false;
        return cached.value == value;
    }

    private boolean isUniformFloatTupleCached(final int location, float x, float y) {
        final FloatTuple2Cache cached = (FloatTuple2Cache) uniformCache[location];
        if (cached == null) return false;
        return cached.x == x && cached.y == y;
    }

    private boolean isUniformFloatTupleCached(final int location, float x, float y, float z) {
        final FloatTuple3Cache cached = (FloatTuple3Cache) uniformCache[location];
        if (cached == null) return false;
        return cached.x == x && cached.y == y && cached.z == z;
    }

    private boolean isUniformFloatTupleCached(final int location, float x, float y, float z, float w) {
        final FloatTuple4Cache cached = (FloatTuple4Cache) uniformCache[location];
        if (cached == null) return false;
        return cached.x == x && cached.y == y && cached.z == z && cached.w == w;
    }

    private boolean isUniformMatrix4Cached(final int location, final MathMatrix4 value) {
        final Matrix4Cache cached = (Matrix4Cache) uniformCache[location];
        if (cached == null) return false;
        return cached.value.equals(value);
    }

    private void cacheUniformInteger(final int location, final int value) {
        if (uniformCache[location] == null) uniformCache[location] = new IntegerCache();
        ((IntegerCache) uniformCache[location]).value = value;
    }

    private void cacheUniformBoolean(final int location, final boolean value) {
        if (uniformCache[location] == null) uniformCache[location] = new BooleanCache();
        ((BooleanCache) uniformCache[location]).value = value;
    }

    private void cacheUniformFloat(final int location, final float value) {
        if (uniformCache[location] == null) uniformCache[location] = new FloatCache();
        ((FloatCache) uniformCache[location]).value = value;
    }

    private void cacheUniformFloatTuple(final int location, float x, float y) {
        if (uniformCache[location] == null) uniformCache[location] = new FloatTuple2Cache();
        FloatTuple2Cache cache = (FloatTuple2Cache) uniformCache[location];
        cache.x = x;
        cache.y = y;
    }

    private void cacheUniformFloatTuple(final int location, float x, float y, float z) {
        if (uniformCache[location] == null) uniformCache[location] = new FloatTuple3Cache();
        FloatTuple3Cache cache = (FloatTuple3Cache) uniformCache[location];
        cache.x = x;
        cache.y = y;
        cache.z = z;
    }

    private void cacheUniformFloatTuple(final int location, float x, float y, float z, float w) {
        if (uniformCache[location] == null) uniformCache[location] = new FloatTuple4Cache();
        FloatTuple4Cache cache = (FloatTuple4Cache) uniformCache[location];
        cache.x = x;
        cache.y = y;
        cache.z = z;
        cache.w = w;
    }

    private void cacheUniformMatrix4(final int location, final MathMatrix4 value) {
        if (uniformCache[location] == null) uniformCache[location] = new Matrix4Cache();
        Matrix4Cache cache = (Matrix4Cache) uniformCache[location];
        cache.value.set(value);
    }

    private void validate() {
        // validate that the number of sampled textures does not exceed the allowed maximum on current GPU
        final int maxSampledTextures = GraphicsUtils.getMaxFragmentShaderTextureUnits();
        int sampledTextures = 0;
        for (CollectionsMapObjectInt.Entry<String> uniform : uniformTypes.entries()) {
            int type = uniform.value;
            if (type == GL20.GL_SAMPLER_2D) sampledTextures++;
        }
        if (sampledTextures > maxSampledTextures) throw new IllegalArgumentException("Error: shader code trying " +
                "to sample " + sampledTextures + ". The allowed maximum on this hardware is " + maxSampledTextures);
    }

    @Override
    public void delete() {
        GL20.glUseProgram(0);
        GL20.glDeleteProgram(vertexShaderId);
        GL20.glDeleteProgram(fragmentShaderId);
        GL20.glDeleteProgram(program);
    }

    private static class IntegerCache {
        private int value;
    }

    private static class BooleanCache {
        private boolean value;
    }

    private static class FloatCache {
        private float value;
    }

    private static class FloatTuple2Cache {
        private float x;
        private float y;
    }

    private static class FloatTuple3Cache {
        private float x;
        private float y;
        private float z;
    }

    private static class FloatTuple4Cache {
        private float x;
        private float y;
        private float z;
        private float w;
    }

    private static class Matrix4Cache {
        private MathMatrix4 value = new MathMatrix4();
    }

}
