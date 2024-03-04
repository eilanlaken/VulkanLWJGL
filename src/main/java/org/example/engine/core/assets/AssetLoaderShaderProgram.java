package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.Shader;

public class AssetLoaderShaderProgram implements AssetLoader<Shader> {

    public static final String VERTEX_SHADER_FILE_SUFFIX = ".vert";
    public static final String FRAGMENT_SHADER_FILE_SUFFIX = ".frag";

    @Override
    public void asyncLoad(String path) {

    }

    @Override
    public Shader create() {
        return null;
    }

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return null;
    }

}
