package org.example.engine.core.graphics;

import org.example.engine.core.files.FileUtils;

@Deprecated
public class RendererShaderSelector {

    private ShaderProgram defaultShader;

    public RendererShaderSelector() {
        final String vertexShaderSrc = FileUtils.getFileContent("assets/shaders/1_vertex.glsl");
        final String fragmentShaderSrc = FileUtils.getFileContent("assets/shaders/1_fragment.glsl");
        this.defaultShader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);
    }

    public ShaderProgram getDefaultShader() {
        return defaultShader;
    }
    public ShaderProgram get(final ModelPart part) {
        return null;
    }

}
