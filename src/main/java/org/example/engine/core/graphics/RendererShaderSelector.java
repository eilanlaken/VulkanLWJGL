package org.example.engine.core.graphics;

@Deprecated
public class RendererShaderSelector {

    private Shader defaultShader;

    public RendererShaderSelector() {
//        final String vertexShaderSrc = FileUtils.getFileContent("assets/shaders/1_vertex.glsl");
//        final String fragmentShaderSrc = FileUtils.getFileContent("assets/shaders/1_fragment.glsl");
//        this.defaultShader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);
    }

    public Shader getDefaultShader() {
        return defaultShader;
    }
    public Shader get(final ModelPart part) {
        return null;
    }

}
