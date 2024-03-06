package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;
import org.example.engine.core.graphics.ShaderProgram;

public class AssetLoaderShaderProgram implements AssetLoader<ShaderProgram> {

    private String vertexShaderSrc;
    private String fragmentShaderSrc;

    @Override
    public void asyncLoad(String path) {
        vertexShaderSrc = AssetUtils.getFileContent(path + ".vert");
        fragmentShaderSrc = AssetUtils.getFileContent(path + ".frag");
    }

    @Override
    public ShaderProgram create() {
        return new ShaderProgram(vertexShaderSrc, fragmentShaderSrc);
    }

    @Override
    public Array<AssetDescriptor> getDependencies() {
        return null;
    }

}
