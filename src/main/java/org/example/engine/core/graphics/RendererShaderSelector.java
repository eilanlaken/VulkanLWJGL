package org.example.engine.core.graphics;

import org.example.engine.core.files.FileUtils;

@Deprecated
public class RendererShaderSelector {

    private ShaderProgram defaultShader;

    public RendererShaderSelector() {
        final String vertexShaderSrc = FileUtils.getFileContent("assets/shaders/vertex.glsl");
        final String fragmentShaderSrc = FileUtils.getFileContent("assets/shaders/fragment.glsl");
        this.defaultShader = new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);
    }

    public ShaderProgram getDefaultShader() {
        return defaultShader;
    }
    public ShaderProgram get(final Model_old modelOld) {
        return null;
    }

}
