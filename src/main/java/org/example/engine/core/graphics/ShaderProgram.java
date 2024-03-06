package org.example.engine.core.graphics;

import org.example.engine.core.collections.MapObjectInt;
import org.example.engine.core.math.Matrix4;
import org.example.engine.core.math.Quaternion;
import org.example.engine.core.math.Vector3;
import org.example.engine.core.math.Vector4;
import org.example.engine.core.memory.Resource;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class ShaderProgram implements Resource {

    public final String vertexShaderSource;
    public final String fragmentShaderSource;
    public final int program;
    protected final int vertexShaderId;
    protected final int fragmentShaderId;
    private String log;
    private boolean isCompiled;
    private final MapObjectInt<String> uniformLocations;
    private final MapObjectInt<String> uniformTypes;
    private final MapObjectInt<String> uniformSizes;
    private String[] uniformNames;
    private final MapObjectInt<String> attributeLocations;
    private final MapObjectInt<String> attributeTypes;
    private final MapObjectInt<String> attributeSizes;
    private String[] attributeNames;

    public ShaderProgram(final String vertexShaderSource, final String fragmentShaderSource) {
        if (vertexShaderSource == null) throw new IllegalArgumentException("Vertex shader cannot be null.");
        if (fragmentShaderSource == null) throw new IllegalArgumentException("Fragment shader cannot be null.");
        this.vertexShaderSource = vertexShaderSource;
        this.fragmentShaderSource = fragmentShaderSource;
        // attributes
        this.attributeLocations = new MapObjectInt<>();
        this.attributeTypes = new MapObjectInt<>();
        this.attributeSizes = new MapObjectInt<>();
        // uniforms
        this.uniformLocations = new MapObjectInt<>();
        this.uniformTypes = new MapObjectInt<>();
        this.uniformSizes = new MapObjectInt<>();
        this.program = GL20.glCreateProgram();
        if (program == 0)
            throw new RuntimeException("Could not create shader");
        this.vertexShaderId = createVertexShader(vertexShaderSource);
        this.fragmentShaderId = createFragmentShader(fragmentShaderSource);
        link();
        fetchAttributes();
        fetchUniforms();
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

    private void fetchAttributes() {
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

    private void fetchUniforms() {
        IntBuffer params = BufferUtils.createIntBuffer(1);
        IntBuffer type = BufferUtils.createIntBuffer(1);
        GL20.glGetProgramiv(this.program, GL20.GL_ACTIVE_UNIFORMS, params);
        int numUniforms = params.get(0);
        this.uniformNames = new String[numUniforms];
        for(int i = 0; i < numUniforms; ++i) {
            params.clear();
            params.put(0, 1);
            type.clear();
            String name = GL20.glGetActiveUniform(this.program, i, params, type);
            int location = GL20.glGetUniformLocation(this.program, name);
            this.uniformLocations.put(name, location);
            this.uniformTypes.put(name, type.get(0));
            this.uniformSizes.put(name, params.get(0));
            this.uniformNames[i] = name;
        }
    }

    protected final void bindUniforms(final HashMap<String, Object> uniforms) {
        if (uniforms == null) throw new IllegalArgumentException("Uniforms map cannot be null.");
        for (Map.Entry<String, Object> entry : uniforms.entrySet()) {
            final String name = entry.getKey();
            final Object value = uniforms.get(name);
            bindUniform(name, value);
        }
    }

    public void bindUniform(final String name, final Object value) {
        final int location = uniformLocations.get(name, -1);
        // TODO: remove. Good only for debugging, but prevents custom flexible shading.
        if (location == -1) throw new IllegalArgumentException("\n\nError: " + this.getClass().getSimpleName() +  " does not have a uniform named " + name + "." +
                "\nIf you have defined the uniform but have not used it, the GLSL compiler discarded it.\n");
        final int type = uniformTypes.get(name, -1);
        switch (type) {
            case GL20.GL_SAMPLER_2D:
                Texture texture = (Texture) value;
                int slot = TextureBinder.bindTexture(texture);
                GL20.glUniform1i(location, slot);
                break;
            case GL20.GL_FLOAT:
                float f = (Float) value;
                GL20.glUniform1f(location, f);
                break;
            case GL20.GL_FLOAT_MAT4:
                Matrix4 matrix4 = (Matrix4) value;
                GL20.glUniformMatrix4fv(location, false, matrix4.val);
                break;
            case GL20.GL_FLOAT_VEC3:
                Vector3 vector3 = (Vector3) value;
                GL20.glUniform3f(location, vector3.x, vector3.y, vector3.z);
                break;
            case GL20.GL_FLOAT_VEC4:
                if (value instanceof Color) {
                    Color color = (Color) value;
                    GL20.glUniform4f(location, color.r, color.g, color.b, color.a);
                    break;
                } else if (value instanceof Vector4) {
                    Vector4 vector4 = (Vector4) value;
                    GL20.glUniform4f(location, vector4.x, vector4.y, vector4.z, vector4.w);
                    break;
                } else if (value instanceof Quaternion) {
                    Quaternion quaternion = (Quaternion) value;
                    GL20.glUniform4f(location, quaternion.x, quaternion.y, quaternion.z, quaternion.w);
                    break;
                }
        }
    }

    // TODO: see if this constant: GL_MAX_TEXTURE_IMAGE_UNITS is the right one.
    private void validate() {
        // validate that the number of sampled textures does not exceed the allowed maximum on current GPU
        final int maxSampledTextures = GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS);
        int sampledTextures = 0;
        for (MapObjectInt.Entry<String> uniform : uniformTypes.entries()) {
            int type = uniform.value;
            if (type == GL20.GL_SAMPLER_2D) sampledTextures++;
        }
        if (sampledTextures > maxSampledTextures) throw new IllegalArgumentException("Error: shader code trying " +
                "to sample " + sampledTextures + ". The allowed maximum on this hardware is " + maxSampledTextures);
    }

    public void bind() {
        GL20.glUseProgram(program);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    @Override
    public void free() {
        GL20.glUseProgram(0);
        GL20.glDeleteProgram(vertexShaderId);
        GL20.glDeleteProgram(fragmentShaderId);
        GL20.glDeleteProgram(program);
    }
}
