package org.example.engine.core.files;

import org.example.engine.core.graphics.ShaderProgram;

public class AssetLoaderShaderProgram implements AssetLoader<ShaderProgram> {

    public static final String VERTEX_SHADER_FILE_SUFFIX = ".vert";
    public static final String FRAGMENT_SHADER_FILE_SUFFIX = ".frag";

    @Override
    public ShaderProgram load(String path) {
        return null;
    }
}
