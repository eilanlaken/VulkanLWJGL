package org.example.engine.core.graphics;

import org.example.engine.core.collections.MapObjectInt;
import org.example.engine.core.memory.Resource;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ShaderProgram implements Resource {

    public final int program;
    protected final int vertexShaderId;
    protected final int fragmentShaderId;
    private String log;
    private boolean isCompiled;
    private final MapObjectInt<String> uniforms;
    private final MapObjectInt<String> uniformTypes;
    private final MapObjectInt<String> uniformSizes;
    private String[] uniformNames;
    private final MapObjectInt<String> attributes;
    private final MapObjectInt<String> attributeTypes;
    private final MapObjectInt<String> attributeSizes;
    private String[] attributeNames;
    private final String vertexShaderSource;
    private final String fragmentShaderSource;

    public ShaderProgram(final String vertexShaderSource, final String fragmentShaderSource) {
        if (vertexShaderSource == null) throw new IllegalArgumentException("Vertex shader cannot be null.");
        if (fragmentShaderSource == null) throw new IllegalArgumentException("Fragment shader cannot be null.");
        this.vertexShaderSource = vertexShaderSource;
        this.fragmentShaderSource = fragmentShaderSource;
        // attributes
        this.attributes = new MapObjectInt<>();
        this.attributeTypes = new MapObjectInt<>();
        this.attributeSizes = new MapObjectInt<>();
        // uniforms
        this.uniforms = new MapObjectInt<>();
        this.uniformTypes = new MapObjectInt<>();
        this.uniformSizes = new MapObjectInt<>();
        program = GL20.glCreateProgram();
        if (program == 0)
            throw new RuntimeException("Could not create shader");
        this.vertexShaderId = createVertexShader(vertexShaderSource);
        this.fragmentShaderId = createFragmentShader(fragmentShaderSource);
        link();
        fetchAttributes();
        fetchUniforms();
    }

    public int createVertexShader(final String shaderCode) {
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

    public int createFragmentShader(final String shaderCode) {
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
        GL20.glGetProgramiv(this.program, 35721, params);
        int numAttributes = params.get(0);
        this.attributeNames = new String[numAttributes];

        for(int i = 0; i < numAttributes; ++i) {
            params.clear();
            params.put(0, 1);
            type.clear();
            String name = GL20.glGetActiveAttrib(this.program, i, params, type);
            int location = GL20.glGetAttribLocation(this.program, name);
            this.attributes.put(name, location);
            this.attributeTypes.put(name, type.get(0));
            this.attributeSizes.put(name, params.get(0));
            this.attributeNames[i] = name;
        }
    }

    private void fetchUniforms() {
        IntBuffer params = BufferUtils.createIntBuffer(1);
        IntBuffer type = BufferUtils.createIntBuffer(1);
        GL20.glGetProgramiv(this.program, 35718, params);
        int numUniforms = params.get(0);
        this.uniformNames = new String[numUniforms];
        for(int i = 0; i < numUniforms; ++i) {
            params.clear();
            params.put(0, 1);
            type.clear();
            String name = GL20.glGetActiveUniform(this.program, i, params, type);
            int location = GL20.glGetUniformLocation(this.program, name);
            this.uniforms.put(name, location);
            this.uniformTypes.put(name, type.get(0));
            this.uniformSizes.put(name, params.get(0));
            this.uniformNames[i] = name;
        }
    }

    public void bind() {
        GL20.glUseProgram(program);
    }

    @Override
    public void free() {
        GL20.glUseProgram(0);
        GL20.glDeleteProgram(vertexShaderId);
        GL20.glDeleteProgram(fragmentShaderId);
        GL20.glDeleteProgram(program);
    }
}
