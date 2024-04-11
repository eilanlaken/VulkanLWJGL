package org.example.engine.core.assets;

import org.example.engine.core.collections.CollectionsArray;

public class AssetLoaderString implements AssetLoader<String> {

    @Override
    public CollectionsArray<AssetDescriptor> getDependencies() {
        return null;
    }

    @Override
    public void asyncLoad(String path) {

    }

    @Override
    public String create() {
        return null;
    }
}
