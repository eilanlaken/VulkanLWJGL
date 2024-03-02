package org.example.engine.core.assets;

import org.example.engine.core.collections.Array;

public class AssetLoaderString implements AssetLoader<String> {

    @Override
    public Array<AssetDescriptor> getDependencies(String path) {
        return null;
    }

    @Override
    public void asyncLoad(String path) {

    }

    @Override
    public String create(String path) {
        return null;
    }
}
