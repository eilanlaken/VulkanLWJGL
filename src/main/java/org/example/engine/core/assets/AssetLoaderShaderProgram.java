package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.ShaderProgram;

public class AssetLoaderShaderProgram implements AssetLoader<ShaderProgram> {

    public static final String VERTEX_SHADER_FILE_SUFFIX = ".vert";
    public static final String FRAGMENT_SHADER_FILE_SUFFIX = ".frag";

    @Override
    public void asyncLoad(String path) {

    }

    @Override
    public ShaderProgram create(String path) {
        return null;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String path) {
        return null;
    }

}
